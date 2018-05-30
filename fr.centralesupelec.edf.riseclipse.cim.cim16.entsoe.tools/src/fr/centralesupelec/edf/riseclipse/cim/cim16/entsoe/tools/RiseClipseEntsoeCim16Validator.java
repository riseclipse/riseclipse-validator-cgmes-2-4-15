/**
 *  Copyright (c) 2018 CentraleSupélec & EDF.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  This file is part of the RiseClipse tool
 *  
 *  Contributors:
 *      Computer Science Department, CentraleSupélec
 *      EDF R&D
 *  Contacts:
 *      dominique.marcadet@centralesupelec.fr
 *      aurelie.dehouck-neveu@edf.fr
 *  Web site:
 *      http://wdi.supelec.fr/software/RiseClipse/
 * 
 */
package fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe.tools;

import java.io.File;
import java.util.ArrayList;
import fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.cim.CimPackage;
import fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.cim.provider.CimItemProviderAdapterFactory;
import fr.centralesupelec.edf.riseclipse.util.IRiseClipseConsole;
import fr.centralesupelec.edf.riseclipse.util.TextRiseClipseConsole;
import fr.centralesupelec.edf.riseclipse.validation.ocl.OCLValidator;

import org.eclipse.emf.ecore.resource.Resource;

public class RiseClipseEntsoeCim16Validator {

	private static OCLValidator ocl;
    private static CimItemProviderAdapterFactory adapter;
    private static EntsoeCim16ModelLoader loader;

    public static void usage( IRiseClipseConsole console ) {
        console.setLevel( IRiseClipseConsole.INFO_LEVEL );
		console.info( "java -jar RiseClipseEntsoeCim16Validator.jar [--verbose] [--merge] [<oclFile> | <cimFile>]*" );
		console.info( "Files ending with \".ocl\" are considered OCL files, all others are considered ENTSOE v2.4.15 files" );
		console.info( "If --merge, all ENTSOE v2.4.15 files are merged before OCL validation" );
		System.exit( -1 );
	}

    public static void main( String[] args ) {
    	
        final IRiseClipseConsole console = new TextRiseClipseConsole();
        
        console.setLevel( IRiseClipseConsole.INFO_LEVEL );
        displayLegal( console );
        
        console.setLevel( IRiseClipseConsole.WARNING_LEVEL );
        
        if( args.length == 0 ) usage( console );
        
        boolean merge = false;

        int posFiles = 0;
        for( int i = 0; i < args.length; ++i ) {
            if( args[i].startsWith( "--" )) {
                posFiles = i + 1;
                if( "--verbose".equals( args[i] )) {
                    console.setLevel( IRiseClipseConsole.INFO_LEVEL );
                }
                else if( "--merge".equals( args[i] )) {
                    merge = true;
                }
                else {
                    console.error( "Unrecognized option " + args[i] );
                    usage( console );
                }
            }
        }

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
        
        prepare( console, oclFiles );
        if( merge ) {
            for( int i = 0; i < cimFiles.size(); ++i ) {
                load( console, cimFiles.get( i ));
            }
            loader.finalizeLoad();
            run( console );
        }
        else {
            for( int i = 0; i < cimFiles.size(); ++i ) {
                loader.reset();
                load( console, cimFiles.get( i ));
                loader.finalizeLoad();
                run( console );
            }
        }
    }
    
    public static void displayLegal( IRiseClipseConsole console ) {
        console.info( "Copyright (c) 2018 CentraleSupélec & EDF." );
        console.info( "All rights reserved. This program and the accompanying materials" );
        console.info( "are made available under the terms of the Eclipse Public License v1.0" );
        console.info( "which accompanies this distribution, and is available at" );
        console.info( "http://www.eclipse.org/legal/epl-v10.html" );
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
        console.info( "RiseClipseEntsoeCim16Validator version: 1.0.0" );
        console.info( "" );
    }

    public static void prepare( IRiseClipseConsole console, ArrayList< File > oclFiles ) {
        CimPackage cimPkg = CimPackage.eINSTANCE;
        ocl = new OCLValidator( cimPkg, true );

        for( int i = 0; i < oclFiles.size(); ++i ) {
            console.info( "Loading ocl: " + oclFiles.get( i ));
            // workaround for bug 486872
//          File file = new File( oclFiles.get( i ));
//          URI uri = file.isFile() ? URI.createFileURI( file.getAbsolutePath() ) : URI.createURI( oclFiles.get( i ));
//          oclFiles.add( uri );
//          ocl.addOCLDocument( uri, console );
            ocl.addOCLDocument( oclFiles.get( i ), console );
        }

        loader = new EntsoeCim16ModelLoader( console );
        adapter = new CimItemProviderAdapterFactory();
    }

    public static void run( IRiseClipseConsole console ) {
        for( Resource resource : loader.getResourceSet().getResources() ) {
            // Some empty resources may be created when other URI are present
            if( resource.getContents().size() > 0 ) {
                console.info( "Validating file: " + resource.getURI().lastSegment() );
                ocl.validate( resource, adapter, console );
            }
        }
    }

    private static void load( IRiseClipseConsole console, String cimFile ) {
        loader.loadWithoutValidation( cimFile );
    }

    public static void resetLoadFinalize( IRiseClipseConsole console, String cimFile ) {
        loader.reset();
        load( console, cimFile );
        loader.finalizeLoad();
    }

}

