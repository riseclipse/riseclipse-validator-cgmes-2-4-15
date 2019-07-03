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
package fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.validator.component;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.validator.RiseClipseEntsoeCim16Validator;
import fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.validator.ui.application.RiseClipseEntsoeCim16ValidatorApplication;
import fr.centralesupelec.edf.riseclipse.util.AbstractRiseClipseConsole;
import fr.centralesupelec.edf.riseclipse.util.IRiseClipseConsole;

@SuppressWarnings( "serial" )
public class CgmesFilePane extends JPanel implements ActionListener {

    private JButton btnAddCgmesFile;
    private JButton btnValidate;
    private CgmesFileList cgmesFilesList;
    private RiseClipseEntsoeCim16ValidatorApplication application;

    public CgmesFilePane( RiseClipseEntsoeCim16ValidatorApplication application ) {
        this.application = application;
        
        setLayout( new BorderLayout( 0, 0 ));

        JPanel btnPanel = new JPanel();
        add( btnPanel, BorderLayout.SOUTH );

        btnAddCgmesFile = new JButton( "Add CGMES file" );
        btnAddCgmesFile.addActionListener( this );
        btnPanel.add( btnAddCgmesFile );

        btnValidate = new JButton( "Validate" );
        btnValidate.addActionListener( this );
        btnPanel.add( btnValidate );

        JScrollPane sclFilesPane = new JScrollPane();
        add( sclFilesPane, BorderLayout.CENTER );
        
        cgmesFilesList = new CgmesFileList();
        sclFilesPane.setViewportView( cgmesFilesList );
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        Object source = e.getSource();

        if( source == btnAddCgmesFile ) {
            JFileChooser fileChooser = new JFileChooser();
            if( fileChooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION ) {
                cgmesFilesList.add( fileChooser.getSelectedFile() );
            }
            return;
        }

        if( source == btnValidate ) {
            ArrayList< File > oclFiles = application.getOclFiles();
            ArrayList< String > sclFiles = cgmesFilesList.getCgmesFiles();
            
            ResultFrame result = new ResultFrame();
            
            IRiseClipseConsole console = result.getMainConsole();
            AbstractRiseClipseConsole.changeConsole( console );
            RiseClipseEntsoeCim16Validator.displayLegal( console );
            RiseClipseEntsoeCim16Validator.prepare( oclFiles );
            result.repaint();
            for( int i = 0; i < sclFiles.size(); ++i ) {
                console = result.getConsoleFor( sclFiles.get( i ));
                AbstractRiseClipseConsole.changeConsole( console );
                RiseClipseEntsoeCim16Validator.resetLoadFinalize( console, sclFiles.get( i ));
                RiseClipseEntsoeCim16Validator.run( console );
                result.repaint();
            }

            return;
        }
    }
}
