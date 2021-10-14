/*
*************************************************************************
**  Copyright (c) 2016-2021 CentraleSupélec & EDF.
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
**      https://riseclipse.github.io
*************************************************************************
*/
package fr.centralesupelec.edf.riseclipse.cim.cim16.entsoe_v2_4_15.validator.component;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/*
 * Adapted from http://www.devx.com/tips/Tip/5342
 */
@SuppressWarnings( "serial" )
public class CgmesFileList extends JList< CgmesFileCheckBox > {
    
    protected static Border noFocusBorder = new EmptyBorder( 1, 1, 1, 1 );
    
    private DefaultListModel< CgmesFileCheckBox > model;

    public CgmesFileList() {
        model = new DefaultListModel< CgmesFileCheckBox >();
        setModel( model );
        
        setCellRenderer( new CgmesFileCellRenderer() );

        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                int index = locationToIndex( e.getPoint() );

                if( index != -1 ) {
                    JCheckBox checkbox = getModel().getElementAt( index ).getCheckBox();
                    checkbox.setSelected( !checkbox.isSelected() );
                    repaint();
                }
            }
        } );

        setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
    }

    public void add( File file ) {
        for( int i = 0; i < model.size(); ++i ) {
            if( model.getElementAt( i ).getFile().getAbsolutePath().equals( file.getAbsolutePath() )) {
                return;
            }
        }
        
        CgmesFileCheckBox check = new CgmesFileCheckBox( file );
        model.addElement( check );
    }

    protected class CgmesFileCellRenderer implements ListCellRenderer< CgmesFileCheckBox > {

        @Override
        public Component getListCellRendererComponent( JList< ? extends CgmesFileCheckBox > list, CgmesFileCheckBox file, int index,
                boolean isSelected, boolean cellHasFocus ) {
            JCheckBox checkbox = file.getCheckBox();
            checkbox.setBackground( isSelected ? getSelectionBackground() : getBackground() );
            checkbox.setForeground( isSelected ? getSelectionForeground() : getForeground() );
            checkbox.setEnabled( isEnabled() );
            checkbox.setFont( getFont() );
            checkbox.setFocusPainted( false );
            checkbox.setBorderPainted( true );
            checkbox.setBorder( isSelected ? UIManager.getBorder( "List.focusCellHighlightBorder" ) : noFocusBorder );
            return checkbox;
        }
    }

    public ArrayList< String > getCgmesFiles() {
        ArrayList< String > cgmesFiles = new ArrayList< String >();
        
        for( int i = 0; i < model.size(); ++i ) {
            if( model.getElementAt( i ).getCheckBox().isSelected() ) {
                cgmesFiles.add( model.getElementAt( i ).getFile().getAbsolutePath() );
            }
        }
        
        return cgmesFiles;
    }

}


