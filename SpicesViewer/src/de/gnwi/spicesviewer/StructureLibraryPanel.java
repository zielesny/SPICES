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

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;

/**
 * A JPanel to present the user some exemplary SPICES 
 * structures (loaded from a 'Structure.properties' file)
 * that can be displayed in the {@link de.gnwi.spicesviewer.ViewerPanel}.
 * It consists of a JList where the structure names are displayed, a JTextArea
 * where the selected SPICES line notation is displayed, 
 * a JComboBox to choose in which way the selected structure should be added to the 
 * {@link de.gnwi.spicesviewer.ViewerPanel} and a JButton to add the structure.
 * 
 * @author Jonas Schaub
 */
public class StructureLibraryPanel extends JPanel {
    
    //<editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * A JLabel requesting the user to choose a structure name from the list
     */
    private final JLabel selectNameLabel;
    
    /**
     * A JList for displaying the name keys of the SPICES structures.
     */
    private final JList structureNameList;
    
    /**
     * A JScrollPane to be placed above the structureNameList.
     */
    private final JScrollPane structureNameListScrollPane;
    
    /**
     * A JScrollPane to be placed above the structureArea.
     */
    private final JScrollPane structureAreaScrollPane;
    
    /**
     * A JTextArea to display the SPICES structure linked to the selected
     * structure name in the structureNameList.
     */
    private final JTextArea structureArea;
    
    /**
     * A JSplitPane to be placed between the two scroll panes
     */
    private final JSplitPane splitPane;
    
    /**
     * A JLabel requesting the user to choose an addition mode for adding
     * the selected structure to the ViewerPanel.
     */
    private final JLabel selectAnAdditionModeLabel;
    
    /**
     * A JComboBox to hold the different addition modes described in
     * Constants.AddStructureOptions.
     */
    private final JComboBox selectAdditionModeBox;
    
    /**
     * A JButton that adds the selected structure to the ViewerPanel (using the
     * selected addition mode).
     */
    private final JButton addStructureToViewButton;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public constructor">
    /**
     * Constructor:
     * It initialises the new StructureLibraryPanel's components, layouts them 
     * and gives them tool tips. No event listeners are added to any component.
     */
    public StructureLibraryPanel() {
        super();
        //<editor-fold defaultstate="collapsed" desc="Initialisation">
        this.structureNameList = new JList<>(StructurePropertiesReader.getKeySetStringArray());
        this.structureNameListScrollPane = new JScrollPane(this.structureNameList,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.structureArea = new JTextArea(Constants.TEXTAREA_INITIAL_TEXT);
        this.structureAreaScrollPane = new JScrollPane(this.structureArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                this.structureNameListScrollPane, this.structureAreaScrollPane);
        this.addStructureToViewButton = new JButton(Language.getString("ADD_STRUCTURE_TO_VIEW"));
        this.addStructureToViewButton.setEnabled(false);
        this.selectNameLabel = new JLabel(Language.getString("SELECT_STRUCTURE_NAME"));
        this.selectAnAdditionModeLabel = new JLabel(Language.getString("SELECT_ADDITION_MODE"));
        int tmpLength = Constants.AddStructureOptions.values().length;
        String[] tmpAddStructureOptionsLanguageRepresentations = new String[tmpLength];
        for (int i = 0; i < tmpLength; i++) {
            String tmpLanguageRepresentation = Constants.AddStructureOptions.values()[i].getLanguageRepresentation();
            tmpAddStructureOptionsLanguageRepresentations[i] = tmpLanguageRepresentation;
        }
        this.selectAdditionModeBox = new JComboBox<>(tmpAddStructureOptionsLanguageRepresentations);
        
        this.structureArea.setEditable(false);
        this.structureArea.setLineWrap(true);
        this.structureArea.setWrapStyleWord(true);
        this.structureNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.structureNameList.setLayoutOrientation(JList.VERTICAL);
        this.splitPane.setOneTouchExpandable(true);
        this.splitPane.setContinuousLayout(true);
        this.structureNameListScrollPane.setMinimumSize(Constants.LIBRARY_PANEL_SCROLLPANES_MIN_SIZE);
        this.structureNameListScrollPane.setPreferredSize(Constants.LIBRARY_PANEL_SCROLLPANES_MIN_SIZE);
        this.structureAreaScrollPane.setMinimumSize(Constants.LIBRARY_PANEL_SCROLLPANES_MIN_SIZE);
        this.splitPane.setResizeWeight(Constants.SPLIT_PANE_RESIZE_WEIGHT);
        this.selectAdditionModeBox.setEditable(false);
        this.addStructureToViewButton.setPreferredSize(new Dimension(Constants.BUTTON_WIDTH, 
                this.addStructureToViewButton.getPreferredSize().height));
        //</editor-fold>
        
        this.createToolTips();
        this.layoutComponents();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Adds JToolTips to some of the components.
     */
    private void createToolTips() {
        this.addStructureToViewButton.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "ADD_STRUCTURE_FROM_LIBRARY_BUTTON_TIP")));
        this.selectAdditionModeBox.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "ADDITION_MODE_BOX_TIP")));
        this.structureNameList.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "STRUCTURE_NAME_LIST_TIP")));
    }
    
    /**
     * Layouts the container's components.
     */
    private void layoutComponents() {
        JPanel tmpAdditionModePanel = new JPanel();
        FlowLayout tmpFlowLayout = new FlowLayout(FlowLayout.LEADING,
                Constants.INTER_COMPONENT_GAP_SIZE, 0);
        tmpAdditionModePanel.setLayout(tmpFlowLayout);
        tmpAdditionModePanel.add(this.selectAnAdditionModeLabel);
        tmpAdditionModePanel.add(this.selectAdditionModeBox);
        tmpAdditionModePanel.setMaximumSize(tmpAdditionModePanel.getSize());
        
        SpringLayout tmpSpringLayout = new SpringLayout();
        this.setLayout(tmpSpringLayout);
        this.add(this.selectNameLabel);
        this.add(this.splitPane);
        this.add(tmpAdditionModePanel);
        this.add(this.addStructureToViewButton);
        
        tmpSpringLayout.putConstraint(SpringLayout.NORTH, this.selectNameLabel,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.NORTH, this);
        tmpSpringLayout.putConstraint(SpringLayout.WEST, this.selectNameLabel,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.WEST, this);
        
        tmpSpringLayout.putConstraint(SpringLayout.NORTH, this.splitPane,
                Constants.INTER_COMPONENT_GAP_SIZE, SpringLayout.SOUTH, this.selectNameLabel);
        tmpSpringLayout.putConstraint(SpringLayout.WEST, this.splitPane,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.WEST, this);
        
        tmpSpringLayout.putConstraint(SpringLayout.NORTH, tmpAdditionModePanel,
                Constants.INTER_COMPONENT_GAP_SIZE, SpringLayout.SOUTH, this.splitPane);
        tmpSpringLayout.putConstraint(SpringLayout.WEST, tmpAdditionModePanel,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.WEST, this);
        
        tmpSpringLayout.putConstraint(SpringLayout.NORTH, this.addStructureToViewButton,
                Constants.INTER_COMPONENT_GAP_SIZE, SpringLayout.SOUTH, tmpAdditionModePanel);
        tmpSpringLayout.putConstraint(SpringLayout.WEST, this.addStructureToViewButton,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.WEST, this);
        
        tmpSpringLayout.putConstraint(SpringLayout.EAST, this,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.EAST, this.splitPane);
        tmpSpringLayout.putConstraint(SpringLayout.SOUTH, this,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.SOUTH, this.addStructureToViewButton);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Returns the panel's JButton that is supposed to add the 
     * selected SPICES structure to the text area of the 
     * {@link de.gnwi.spicesviewer.ViewerPanel} using the 
     * selected addition mode.
     * 
     * @return the JButton that should transmit the 
     * selected SPICES structure to the ViewerPanel
     */
    public JButton getAdditionOfStructureToViewButton() {
        return this.addStructureToViewButton;
    }
    
    /**
     * Returns the panel's JList that displays the exemplary 
     * SPICES structures' names.
     * 
     * @return the JList that displays the SPICES structures' names
     */
    public JList getStructureNameList() {
        return this.structureNameList;
    }
    
    /**
     * Returns the panel's JComboBox that displays the different addition 
     * modes described in {@link de.gnwi.spicesviewer.Constants.AddStructureOptions}.
     * 
     * @return the JComboBox that displays the different addition 
     * modes
     */
    public JComboBox getAdditionModeBox() {
        return this.selectAdditionModeBox;
    }
    
    /**
     * Returns the panel's JTextArea that is supposed to display the SPICES line notation 
     * linked to the selected structure name in the list above.
     * 
     * @return the JTextArea that should display the SPICES line notation upon selection of a structure name 
     */
    public JTextArea getStructureTextArea() {
        return this.structureArea;
    }
    //</editor-fold>
}
