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
 */
package fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.validator.ui.application;

import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.validator.component.CgmesFilePane;
import fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.validator.component.OCLFilePane;

import javax.swing.JScrollPane;
import javax.swing.JPanel;

public class RiseClipseEntsoeCim16ValidatorApplication {

    private JFrame frame;
    private OCLFilePane oclTree;

    /**
     * Launch the application.
     */
    public static void main( String[] args ) {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                try {
                    RiseClipseEntsoeCim16ValidatorApplication window = new RiseClipseEntsoeCim16ValidatorApplication();
                    window.frame.setVisible( true );
                }
                catch( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    /**
     * Create the application.
     */
    public RiseClipseEntsoeCim16ValidatorApplication() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle( "RiseClipseEntsoeCim16ValidatorApplication" );
        frame.setBounds( 100, 100, 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        JTabbedPane tabbedPane = new JTabbedPane( JTabbedPane.TOP );
        frame.getContentPane().add( tabbedPane );

        JPanel cgmesPanel = new CgmesFilePane( this );
        tabbedPane.addTab( "CGMES Files", null, cgmesPanel, null );

        JScrollPane oclPane = new JScrollPane();
        tabbedPane.addTab( "OCL Files", null, oclPane, null );

        File fileRoot = new File( System.getProperty( "user.dir" ) + "/OCL" );
        oclTree = new OCLFilePane( fileRoot );
        oclPane.setViewportView( oclTree );

    }

    public ArrayList< File > getOclFiles() {
        ArrayList< File > oclFiles = new ArrayList<>();
        oclTree.getOclFiles( oclFiles );
        return oclFiles;
    }

}
