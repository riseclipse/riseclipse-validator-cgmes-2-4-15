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

import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;

import fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.cim.CimPackage;
import fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.cim.util.CimConstants;
import fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.cim.util.CimResourceFactoryImpl;
import fr.centralesupelec.edf.riseclipse.cim.headerModel.ModelDescription.ModelDescriptionPackage;
import fr.centralesupelec.edf.riseclipse.util.IRiseClipseConsole;
import fr.centralesupelec.edf.riseclipse.util.RiseClipseModelLoader;
import fr.centralesupelec.edf.riseclipse.util.TextRiseClipseConsole;


public class EntsoeCim16ModelLoader extends RiseClipseModelLoader {
    
    public EntsoeCim16ModelLoader( IRiseClipseConsole console ) {
        super( console );
    }
    
    @Override
    public void reset() {
        super.reset();
        
        // Register the appropriate resource factory to handle all file
        // extensions.
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put( Resource.Factory.Registry.DEFAULT_EXTENSION, new CimResourceFactoryImpl() );

        // Register the package to ensure it is available during loading.
        resourceSet.getPackageRegistry().put( CimPackage.eNS_URI, CimPackage.eINSTANCE );
        resourceSet.getPackageRegistry().put( CimConstants.entsoe_URI, CimPackage.eINSTANCE );
        resourceSet.getPackageRegistry().put( ModelDescriptionPackage.eNS_URI, ModelDescriptionPackage.eINSTANCE );
    }

    public Resource loadWithoutValidation( String name ) {
        Object eValidator = EValidator.Registry.INSTANCE.remove( CimPackage.eINSTANCE );

        Resource resource = load( name );
        
        if( eValidator != null ) {
            EValidator.Registry.INSTANCE.put( CimPackage.eINSTANCE, eValidator );
        }
        return resource;
    }
    
    public static void main( String[] args ) {
        IRiseClipseConsole console = new TextRiseClipseConsole();
        EntsoeCim16ModelLoader loader = new EntsoeCim16ModelLoader( console );
        
        for( int i = 0; i < args.length; ++i ) {
            @SuppressWarnings( "unused" )
            Resource resource = loader.load( args[i] );
        }
    }

}
