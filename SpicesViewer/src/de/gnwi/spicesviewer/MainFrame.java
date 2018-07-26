/**
 * SPICES (Simplified Particle Input ConnEction Specification) Viewer
 * Copyright (C) 2018  Achim Zielesny (achim.zielesny@googlemail.com)
 * 
 * Source code is available at <https://github.com/zielesny/SPICES>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.gnwi.spicesviewer;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

/**
 * The Spices Viewer application's main frame consisting of a JTabbedPane 
 * (that holds the {@link de.gnwi.spicesviewer.ViewerPanel}, the 
 * {@link de.gnwi.spicesviewer.AlternativeViewerPanel}, the 
 * {@link de.gnwi.spicesviewer.StructureLibraryPanel}) 
 * and a JPanel that serves as a status bar. A JMenuBar 
 * (see {@link de.gnwi.spicesviewer.MenuBar}) must be 
 * added externally to this frame.
 * 
 * @author Jonas Schaub
 */
public class MainFrame extends JFrame{
    
    //<editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * A tabbed pane to store the ViewerPanel, the AlternativeViewerPanel and the 
     * StructureLiraryPanel.
     */
    private final JTabbedPane tabbedPane;
    
    /**
     * An Instance of the ViewerPanel class for the first tab.
     */
    private final ViewerPanel viewerTab;
    
    /**
     * An instance of the AlternativeViewerPanel class for the second tab.
     */
    private final AlternativeViewerPanel alternativeViewerTab;
    
    /**
     * An instance of the StructureLibraryPanel class for the third tab.
     */
    private final StructureLibraryPanel structureLibraryTab;
    
    /**
     * A JPanel to be placed at the bottom of the frame to function as a 
     * status bar.
     */
    private final JPanel statusPanel;
    
    /**
     * A JLabel to display status messages in the statusPanel.
     */
    private final JLabel statusLabel;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public constructor">
    /**
     * Constructor:
     * Assigns the parameters to their respective object fields, initialises 
     * the rest and adds the components to the frame. 
     * The frame must be set visible externally. No event listeners are added to any component.
     *
     * @param aViewerPanel a {@link de.gnwi.spicesviewer.ViewerPanel} instance 
     * for the first tab
     * @param anAlternativeViewerPanel an {@link de.gnwi.spicesviewer.AlternativeViewerPanel} 
     * instance for the second tab
     * @param aStructureLibraryPanel a {@link de.gnwi.spicesviewer.StructureLibraryPanel}
     * instance for the third tab
     * @throws IllegalArgumentException if a parameter is 'null'
     */
    public MainFrame(ViewerPanel aViewerPanel,
            AlternativeViewerPanel anAlternativeViewerPanel,
            StructureLibraryPanel aStructureLibraryPanel)
            throws IllegalArgumentException {
        super();
        //<editor-fold defaultstate="collapsed" desc="Checks">
        if (aViewerPanel == null) { throw new IllegalArgumentException("aViewerPanel (instance of class ViewerPanel) is null."); }
        if (anAlternativeViewerPanel == null) { throw new IllegalArgumentException("anAlternativeViewerPanel (instance of class AlternativeViewerPanel) is null."); }
        if (aStructureLibraryPanel == null) { throw new IllegalArgumentException("aStructureLibraryPanel (instance of class StructureLibraryPanel) is null."); }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Initialisation">
        this.tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        this.viewerTab = aViewerPanel;
        this.alternativeViewerTab = anAlternativeViewerPanel;
        this.structureLibraryTab = aStructureLibraryPanel;
        this.statusPanel = new JPanel();
        this.statusLabel = new JLabel(Constants.MESSAGE_LABEL_INITIAL_TEXT);
        //</editor-fold>
        
        this.addComponentsToTabbedPane();
        this.layoutComponents();
        this.pack();
        this.setMinimumSize(this.getSize());
        this.setTitle(Language.getString("FRAME_TITLE"));
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Layouts the frame's components.
     */
    private void layoutComponents() {
        this.statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.statusPanel.setLayout(new BorderLayout());
        this.statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        this.statusPanel.add(this.statusLabel, BorderLayout.WEST);
        
        JPanel tmpContentPane = new JPanel();
        tmpContentPane.setLayout(new BorderLayout());
        tmpContentPane.add(this.tabbedPane, BorderLayout.CENTER);
        tmpContentPane.add(this.statusPanel, BorderLayout.SOUTH);
        this.setContentPane(tmpContentPane);
    }
    
    /**
     * Adds the three JPanel fields (excluding the statusPanel) to the tabbed
     * pane and gives them JToolTips.
     */
    private void addComponentsToTabbedPane() {
        this.tabbedPane.addTab(Language.getString("VIEWER_TAB_TITLE"),
                null, this.viewerTab,
                HTMLToolTip.createTip(Language.getString("VIEW_TAB_TIP")));
        this.tabbedPane.addTab(Language.getString("ALTERNATIVE_VIEW_TAB_TITLE"),
                null, this.alternativeViewerTab,
                HTMLToolTip.createTip(Language.getString("ALTERNATIVE_VIEW_TAB_TIP")));
        this.tabbedPane.addTab(Language.getString("LIBRARY_TAB_TITLE"),
                null, this.structureLibraryTab,
                HTMLToolTip.createTip(Language.getString("STRUCTURE_LIBRARY_TAB_TIP")));
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Returns the frame's JTabbedPane that holds the three tabs ({@link de.gnwi.spicesviewer.ViewerPanel}, 
     * {@link de.gnwi.spicesviewer.AlternativeViewerPanel} and {@link de.gnwi.spicesviewer.StructureLibraryPanel})
     * 
     * @return the frame's JTabbedPane
     */
    public JTabbedPane getTabbedPane() {
        return this.tabbedPane;
    }
    
    /**
     * Returns the frame's JLabel that serves as a status bar for displaying messages.
     * 
     * @return the frame's status bar JLabel
     */
    public JLabel getStatusLabel() {
        return this.statusLabel;
    }
    //</editor-fold>
}
