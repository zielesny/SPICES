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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import org.graphstream.ui.swingViewer.ViewPanel;

/**
 * A JPanel extending class where the user can enter a 
 * SPICES line notation 
 * into a text area that will be displayed in a GraphStream 
 * {@link org.graphstream.ui.swingViewer.ViewPanel} underneath the text area.
 * It consists of a JTextArea where the user can enter a 
 * SPICES line notation,
 * a JLabel that displays a specific error message should the 
 * SPICES line notation be invalid, a GraphStream 
 * {@link org.graphstream.ui.swingViewer.ViewPanel} where the graph is displayed,
 * a JComboBox where the user can choose a specific part of the 
 * SPICES structure for individual
 * display and a JButton to reset the {@link org.graphstream.ui.swingViewer.ViewPanel}
 * after zooming, dragging and rotating.
 * 
 * @author Jonas Schaub
 */
public class ViewerPanel extends JPanel {

    //<editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * A JLabel to request an input structure from the user.
     */
    private final JLabel requestLabel;
    
    /**
     * A JTextArea where the user can enter an input structure.
     */
    private final JTextArea textArea;
    
    /**
     * A JLabel for displaying the Spices' error message or 'valid' if there is
     * no error in the input structure.
     */
    private final JLabel messageLabel;
    
    /**
     * A JScrollPane to hold the textArea.
     */
    private final JScrollPane scrollPane;
    
    /**
     * A JSplitPane to place the textArea and the gsViewPanel in.
     */
    private final JSplitPane splitPane;
    
    /**
     * A JLabel to request the user to choose which part of the SPICES structure 
     * (if it has multiple parts) should be displayed.
     */
    private final JLabel choosePartLabel;
    
    /**
     * A JComboBox for the user to choose which part of the Spices is to be shown.
     */
    private final JComboBox<String> comboBox;
    
    /**
     * A GraphStream ViewPanel to display the Spices graph.
     */
    private final ViewPanel gsViewPanel;
    
    /**
     * A JButton to reset the gsViewPanel after zooming and dragging.
     */
    private final JButton resetViewPanelButton;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public constructor">
    /**
     * Constructor:
     * It initialises and layouts the container's components and adds tool tips 
     * to them. No event listeners are added to any component.
     * 
     * @param aViewPanel a GraphStream 
     * {@link org.graphstream.ui.swingViewer.ViewPanel} that presents the 
     * graph
     * @throws IllegalArgumentException if aViewPanel is 'null'
     */
    public ViewerPanel(ViewPanel aViewPanel) throws IllegalArgumentException {
        super();
        if (aViewPanel == null) { throw new IllegalArgumentException("aViewPanel (instance of class ViewPanel) is null."); }
        //<editor-fold defaultstate="collapsed" desc="Initialisations">
        this.requestLabel = new JLabel(Language.getString("REQUEST_INPUTSTRUCTURE"));
        this.textArea = new JTextArea(Constants.TEXTAREA_INITIAL_TEXT,
                Constants.TEXTAREA_INITIAL_HEIGTH,
                Constants.TEXTAREA_INITIAL_WIDTH);
        this.scrollPane = new JScrollPane(this.textArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.messageLabel = new JLabel(Constants.MESSAGE_LABEL_INITIAL_TEXT);
        this.gsViewPanel = aViewPanel;
        this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.choosePartLabel = new JLabel(Language.getString("CHOOSE_PART"));
        this.comboBox = new JComboBox<>(new String[] {Language.getString("ALL")});
        this.resetViewPanelButton = new JButton(Language.getString("RESET_VIEW"));
        this.resetViewPanelButton.setEnabled(false);
        
        this.messageLabel.setForeground(Constants.BLACK);
        this.comboBox.setSelectedIndex(Constants.COMBOBOX_INITIAL_SELECTED_INDEX);
        this.comboBox.setEditable(false);
        this.comboBox.setEnabled(false);
        this.comboBox.setMaximumSize(new Dimension(this.comboBox.getSize()));
        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(true);
        this.textArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        this.textArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        this.scrollPane.setMinimumSize(Constants.VIEWER_PANEL_SPLITPANE_COMPONENTS_MIN_SIZE);
        this.scrollPane.setPreferredSize(Constants.VIEWER_PANEL_SPLITPANE_COMPONENTS_MIN_SIZE);
        this.gsViewPanel.setPreferredSize(Constants.GRAPHSTREAM_VIEW_PANEL_PREFERRED_SIZE);
        this.gsViewPanel.setMinimumSize(Constants.VIEWER_PANEL_SPLITPANE_COMPONENTS_MIN_SIZE);
        this.splitPane.setOneTouchExpandable(true);
        this.splitPane.setContinuousLayout(true);
        this.resetViewPanelButton.setPreferredSize(new Dimension(Constants.BUTTON_WIDTH, 
                this.resetViewPanelButton.getPreferredSize().height));
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
        this.choosePartLabel.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "PART_BOX_TIP")));
        this.resetViewPanelButton.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "RESET_VIEW_PANEL_BUTTON_TIP")));
        this.textArea.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "TEXT_AREA_TIP")));
    }
    
    /**
     * Layouts the container's components.
     */
    private void layoutComponents() {
        JPanel tmpButtonPanel = new JPanel();
        FlowLayout tmpFlowLayout = new FlowLayout(FlowLayout.LEADING, 
                Constants.INTER_COMPONENT_GAP_SIZE, 0);
        tmpButtonPanel.setLayout(tmpFlowLayout);
        tmpButtonPanel.add(this.choosePartLabel);
        tmpButtonPanel.add(this.comboBox);
        tmpButtonPanel.setMaximumSize(new Dimension(tmpButtonPanel.getSize()));
        
        JPanel tmpGSViewPanelBackgroundPanel = new JPanel();
        tmpGSViewPanelBackgroundPanel.setLayout(new BorderLayout());
        tmpGSViewPanelBackgroundPanel.add(this.gsViewPanel, BorderLayout.CENTER);
        
        JPanel tmpScrollPaneAndMessageLabelPanel = new JPanel();
        SpringLayout tmpSpringLayout1 = new SpringLayout();
        tmpScrollPaneAndMessageLabelPanel.setLayout(tmpSpringLayout1);
        tmpScrollPaneAndMessageLabelPanel.add(this.requestLabel);
        tmpScrollPaneAndMessageLabelPanel.add(this.scrollPane);
        tmpScrollPaneAndMessageLabelPanel.add(this.messageLabel);
        
        tmpSpringLayout1.putConstraint(SpringLayout.NORTH, this.requestLabel, 
                0, SpringLayout.NORTH, tmpScrollPaneAndMessageLabelPanel);
        tmpSpringLayout1.putConstraint(SpringLayout.WEST, this.requestLabel, 
                0, SpringLayout.WEST, tmpScrollPaneAndMessageLabelPanel);
        
        tmpSpringLayout1.putConstraint(SpringLayout.NORTH, this.scrollPane, 
                Constants.INTER_COMPONENT_GAP_SIZE, SpringLayout.SOUTH, this.requestLabel);
        tmpSpringLayout1.putConstraint(SpringLayout.WEST, this.scrollPane, 
                0, SpringLayout.WEST, tmpScrollPaneAndMessageLabelPanel);
        
        tmpSpringLayout1.putConstraint(SpringLayout.NORTH, this.messageLabel, 
                Constants.INTER_COMPONENT_GAP_SIZE, SpringLayout.SOUTH, this.scrollPane);
        tmpSpringLayout1.putConstraint(SpringLayout.WEST, this.messageLabel, 
                0, SpringLayout.WEST, tmpScrollPaneAndMessageLabelPanel);
        
        tmpSpringLayout1.putConstraint(SpringLayout.EAST, tmpScrollPaneAndMessageLabelPanel,
                0, SpringLayout.EAST, this.scrollPane);
        tmpSpringLayout1.putConstraint(SpringLayout.SOUTH, tmpScrollPaneAndMessageLabelPanel,
                0, SpringLayout.SOUTH, this.messageLabel);
        
        this.splitPane.setTopComponent(tmpScrollPaneAndMessageLabelPanel);
        this.splitPane.setBottomComponent(tmpGSViewPanelBackgroundPanel);
        this.splitPane.setResizeWeight(Constants.SPLIT_PANE_RESIZE_WEIGHT);
        
        SpringLayout tmpSpringLayout = new SpringLayout();
        this.setLayout(tmpSpringLayout);
        this.add(this.splitPane);
        this.add(tmpButtonPanel);
        this.add(this.resetViewPanelButton);
        
        tmpSpringLayout.putConstraint(SpringLayout.NORTH, this.splitPane, 
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.NORTH, this);
        tmpSpringLayout.putConstraint(SpringLayout.WEST, this.splitPane, 
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.WEST, this);
        
        tmpSpringLayout.putConstraint(SpringLayout.NORTH, tmpButtonPanel,
                Constants.INTER_COMPONENT_GAP_SIZE, SpringLayout.SOUTH, this.splitPane);
        tmpSpringLayout.putConstraint(SpringLayout.WEST, tmpButtonPanel,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.WEST, this);
        
        tmpSpringLayout.putConstraint(SpringLayout.NORTH, this.resetViewPanelButton,
                Constants.INTER_COMPONENT_GAP_SIZE, SpringLayout.SOUTH, tmpButtonPanel);
        tmpSpringLayout.putConstraint(SpringLayout.WEST, this.resetViewPanelButton,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.WEST, this);
        
        tmpSpringLayout.putConstraint(SpringLayout.EAST, this,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.EAST, this.splitPane);
        tmpSpringLayout.putConstraint(SpringLayout.SOUTH, this,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.SOUTH, this.resetViewPanelButton);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Returns the panel's JButton that is supposed to reset the Spices graph displaying GraphStream
     * view panel after zooming and dragging.
     *
     * @return Button
     */
    public JButton getResetOfGraphStreamViewPanelButton() {
        return this.resetViewPanelButton;
    }
    
    /**
     * Returns the panel's JTextArea where the user is supposed to enter 
     * a SPICES line notation for the represented structure to be displayed.
     * 
     * @return the JTextArea where a SPICES line notation should be entered by the user
     */
    public JTextArea getLineNotationTextArea() {
        return this.textArea;
    }
    
    /**
     * Returns the panel's JComboBox where the user is supposed to choose 
     * which part of the Spices structure to display.
     * 
     * @return the JComboBox for selecting individual parts
     */
    public JComboBox getPartDisplayComboBox() {
        return this.comboBox;
    }
    
    /**
     * Returns the panel's GraphStream {@link org.graphstream.ui.swingViewer.ViewPanel} that displays the Spices graph.
     * 
     * @return the GraphStream ViewPanel displaying the Spices graph
     */
    public ViewPanel getGraphStreamViewPanel() {
        return this.gsViewPanel;
    }
    
    /**
     * Returns the panel's JLabel that is supposed to display "valid" if the entered SPICES line notation is 
     * syntactically correct and an error message otherwise.
     * 
     * @return the JLabel that should inform the user about the correctness of their entered line notation
     */
    public JLabel getMessageLabel() {
        return this.messageLabel;
    }
    //</editor-fold>
}
