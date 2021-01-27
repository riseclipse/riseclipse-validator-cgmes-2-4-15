/*
*************************************************************************
**  Copyright (c) 2019 CentraleSupélec & EDF.
**  All rights reserved. This program and the accompanying materials
**  are made available under the terms of the Eclipse Public License v2.0
**  which accompanies this distribution, and is available at
**  https://www.eclipse.org/legal/epl-v20.html
** 
**  This file is part of the RiseClipse tool
**  
**  Contributors:
**      Computer Science Department, CentraleSupélec
**      EDF R&D
**  Contacts:
**      dominique.marcadet@centralesupelec.fr
**      aurelie.dehouck-neveu@edf.fr
**  Web site:
**      http://wdi.supelec.fr/software/RiseClipse/
*************************************************************************
*/
package fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.validator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.cim.CimPackage;
import fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.cim.provider.CimItemProviderAdapterFactory;
import fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.utilities.EntsoeCim16ModelLoader;
import fr.centralesupelec.edf.riseclipse.cim.headerModel.ModelDescription.ModelDescriptionPackage;
import fr.centralesupelec.edf.riseclipse.util.AbstractRiseClipseConsole;
import fr.centralesupelec.edf.riseclipse.util.FileRiseClipseConsole;
import fr.centralesupelec.edf.riseclipse.util.IRiseClipseConsole;
import fr.centralesupelec.edf.riseclipse.util.RiseClipseFatalException;
import fr.centralesupelec.edf.riseclipse.util.TextRiseClipseConsole;
import fr.centralesupelec.edf.riseclipse.validation.ocl.OCLValidator;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.EValidator.SubstitutionLabelProvider;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.ocl.pivot.validation.ComposedEValidator;

public class RiseClipseValidatorCGMES {

    private static OCLValidator oclValidator;
    private static CimItemProviderAdapterFactory adapter;
    private static EntsoeCim16ModelLoader loader;
    private static String outputFile;

    public static void usage() {
        IRiseClipseConsole console = AbstractRiseClipseConsole.getConsole();
        
        console.setLevel( IRiseClipseConsole.INFO_LEVEL );
		console.info( "java -jar RiseClipseValidatorCGMES.jar [--verbose | --info | --warning | --error] [--output <file>] [--merge] [<oclFile> | <cimFile>]*" );
		console.info( "Files ending with \".ocl\" are considered OCL files, all others are considered ENTSOE CGMES v2.4.15 files" );
		console.info( "If --merge, all ENTSOE CGMES v2.4.15 files are merged before OCL validation" );
		System.exit( -1 );
	}

    public static void main( String[] args ) {
    	
        int consoleLevel = IRiseClipseConsole.WARNING_LEVEL;
        
        
        if( args.length == 0 ) usage();
        
        boolean merge = false;

        int posFiles = 0;
        for( int i = 0; i < args.length; ++i ) {
            if( args[i].startsWith( "--" )) {
                posFiles = i + 1;
                if( "--verbose".equals( args[i] ) ) {
                    consoleLevel = IRiseClipseConsole.VERBOSE_LEVEL;
                }
                else if( "--info".equals( args[i] ) ) {
                    consoleLevel = IRiseClipseConsole.INFO_LEVEL;
                }
                else if( "--warning".equals( args[i] ) ) {
                    consoleLevel = IRiseClipseConsole.WARNING_LEVEL;
                }
                else if( "--error".equals( args[i] ) ) {
                    consoleLevel = IRiseClipseConsole.ERROR_LEVEL;
                }
                else if( "--output".equals( args[i] ) ) {
                    if( ++i < args.length ) {
                        outputFile = args[i];
                        ++posFiles;
                    }
                    else usage();
                }
                else if( "--merge".equals( args[i] )) {
                    merge = true;
                }
                else {
                    AbstractRiseClipseConsole.getConsole().error( "Unrecognized option " + args[i] );
                    usage();
                }
            }
        }

        IRiseClipseConsole console = ( outputFile == null ) ? new TextRiseClipseConsole() : new FileRiseClipseConsole( outputFile );
        AbstractRiseClipseConsole.changeConsole( console );
        console.setLevel( IRiseClipseConsole.INFO_LEVEL );
        displayLegal( console );
        console.setLevel( consoleLevel );

        ArrayList< File > oclFiles = new ArrayList<>();
        ArrayList< String > cimFiles = new ArrayList<>();
        for( int i = posFiles; i < args.length; ++i ) {
            if( args[i].endsWith( ".ocl" ) ) {
                oclFiles.add( new File( args[i] ));
            }
            else {
                cimFiles.add( args[i] );
            }
        }
        
        prepare( oclFiles );
        if( merge ) {
            for( int i = 0; i < cimFiles.size(); ++i ) {
                load( console, cimFiles.get( i ));
            }
            loader.finalizeLoad( console );
            run( console );
        }
        else {
            for( int i = 0; i < cimFiles.size(); ++i ) {
                loader.reset();
                load( console, cimFiles.get( i ));
                loader.finalizeLoad( console );
                run( console );
            }
        }
    }
    
    public static void displayLegal( IRiseClipseConsole console ) {
        console.info( "Copyright (c) 2019 CentraleSupélec & EDF." );
        console.info( "All rights reserved. This program and the accompanying materials" );
        console.info( "are made available under the terms of the Eclipse Public License v2.0" );
        console.info( "which accompanies this distribution, and is available at" );
        console.info( "https://www.eclipse.org/legal/epl-v20.html" );
        console.info( "" );
        console.info( "This file is part of the RiseClipse tool." );
        console.info( "" );
        console.info( "Contributors:" );
        console.info( "    Computer Science Department, CentraleSupélec" );
        console.info( "    EDF R&D" );
        console.info( "Contacts:" );
        console.info( "    dominique.marcadet@centralesupelec.fr" );
        console.info( "    aurelie.dehouck-neveu@edf.fr" );
        console.info( "Web site:" );
        console.info( "    http://wdi.supelec.fr/software/RiseClipse/" );
        console.info( "" );
        console.info( "RiseClipseValidatorCGMES version: 1.1.0 a2 (3 July 2019)" );
        console.info( "" );
    }

    public static void prepare( ArrayList< File > oclFiles ) {
        IRiseClipseConsole console = AbstractRiseClipseConsole.getConsole();
        
        CimPackage cimPkg = CimPackage.eINSTANCE;
        if( cimPkg == null ) {
            throw new RiseClipseFatalException( "CIM package not found", null );
        }
        ModelDescriptionPackage modelPkg = ModelDescriptionPackage.eINSTANCE;
        if( modelPkg == null ) {
            throw new RiseClipseFatalException( "ModelDescription package not found", null );
        }

        ComposedEValidator validator = ComposedEValidator.install( cimPkg );

        if(( oclFiles != null ) && ( ! oclFiles.isEmpty() )) {
            oclValidator = new OCLValidator( cimPkg, console );

            for( int i = 0; i < oclFiles.size(); ++i ) {
                oclValidator.addOCLDocument( oclFiles.get( i ), console );
            }
            oclValidator.prepare( validator, console );
        }

        loader = new EntsoeCim16ModelLoader( );
        adapter = new CimItemProviderAdapterFactory();
    }

    public static void run( IRiseClipseConsole console ) {
        for( Resource resource : loader.getResourceSet().getResources() ) {
            // Some empty resources may be created when other URI are present
            if( resource.getContents().size() > 0 ) {
                console.info( "Validating file: " + resource.getURI().lastSegment() );
                validate( resource, adapter );
            }
        }
    }

    private static void validate( @NonNull Resource resource, final AdapterFactory adapter ) {
        IRiseClipseConsole console = AbstractRiseClipseConsole.getConsole();
        
        Map< Object, Object > context = new HashMap< Object, Object >();
        SubstitutionLabelProvider substitutionLabelProvider = new EValidator.SubstitutionLabelProvider() {

            @Override
            public String getValueLabel( EDataType eDataType, Object value ) {
                return Diagnostician.INSTANCE.getValueLabel( eDataType, value );
            }

            @Override
            public String getObjectLabel( EObject eObject ) {
                IItemLabelProvider labelProvider = ( IItemLabelProvider ) adapter.adapt( eObject,
                        IItemLabelProvider.class );
                return labelProvider.getText( eObject );
            }

            @Override
            public String getFeatureLabel( EStructuralFeature eStructuralFeature ) {
                return Diagnostician.INSTANCE.getFeatureLabel( eStructuralFeature );
            }
        };
        context.put( EValidator.SubstitutionLabelProvider.class, substitutionLabelProvider );

        for( int n = 0; n < resource.getContents().size(); ++n ) {
            Diagnostic diagnostic = Diagnostician.INSTANCE.validate( resource.getContents().get( n ), context );

            for( Iterator< Diagnostic > i = diagnostic.getChildren().iterator(); i.hasNext(); ) {
                Diagnostic childDiagnostic = i.next();
                
                List< ? > data = childDiagnostic.getData();
                EObject object = ( EObject ) data.get( 0 );
                String message = childDiagnostic.getMessage();
                if(( data.size() > 1 ) && ( data.get( 1 ) instanceof EAttribute )) {
                    EAttribute attribute = ( EAttribute ) data.get( 1 );
                    if( attribute == null ) continue;
                    message = "\tAttribute " + attribute.getName() + " of "
                                + substitutionLabelProvider.getObjectLabel( object ) + " : "
                                + childDiagnostic.getChildren().get( 0 ).getMessage();
                }

                switch( childDiagnostic.getSeverity() ) {
                case Diagnostic.INFO:
                    console.info( message );
                    break;
                case Diagnostic.WARNING:
                    console.warning( message );
                    break;
                case Diagnostic.ERROR:
                    console.error( message );
                    break;
                }
            }
        }
    }

    private static void load( IRiseClipseConsole console, String cimFile ) {
        loader.loadWithoutValidation( cimFile );
    }

    public static void resetLoadFinalize( IRiseClipseConsole console, String cimFile ) {
        loader.reset();
        load( console, cimFile );
        loader.finalizeLoad( console );
    }

}

