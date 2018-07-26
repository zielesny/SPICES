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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

/**
 * A JDialog extending class to change settings of the application that can not
 * be set in the {@link de.gnwi.spicesviewer.MenuBar}, such as 
 * node and edge weight and the node size.
 * It consists of two JTextFields where the user can enter node and edge weight,
 * a JSlider to adjust the node size, three JButtons to set the three properties to their default values, 
 * a 'cancel' button and an 'ok' button.
 * 
 * @author Jonas Schaub
 */
public class AdditionalSettingsDialog extends JDialog {

    //<editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * A JLabel to request a node weight from the user.
     */
    private final JLabel nodeWeightLabel;
    
    /**
     * A JTextField where the user can enter a node weight.
     */
    private final JTextField nodeWeightTextField;
    
    /**
     * A JButton to restore the node weight to its original value.
     */
    private final JButton restoreNodeWeightButton;
    
    /**
     * A JLabel to request an edge weight from the user.
     */
    private final JLabel edgeWeightLabel;
    
    /**
     * A JTextField where the user can enter an edge weight.
     */
    private final JTextField edgeWeightTextField;
    
    /**
     * A JButton to restore the edge weight to its original value.
     */
    private final JButton restoreEdgeWeightButton;
    
    /**
     * A JLabel to request a node size from the user.
     */
    private final JLabel nodeSizeLabel;
    
    /**
     * A JSlider through which the user can set the node size.
     */
    private final JSlider nodeSizeSlider;
    
    /**
     * A JButton to restore the node size to its original value.
     */
    private final JButton restoreNodeSizeButton;
    
    /**
     * A JButton to hide this dialog and adjust the settings altered by the user.
     */
    private final JButton okButton;
    
    /**
     * A JButton to hide the dialog without adjusting any settings.
     */
    private final JButton cancelButton;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public constructor">
    /**
     * Constructor:
     * It initialises and layouts the new AdditionalSettingsDialog's components 
     * and adds JToolTips to them. The dialog is not set visible and no event listeners are added to its components.
     */
    public AdditionalSettingsDialog() {
        super();
        //<editor-fold defaultstate="collapsed" desc="Initialisation">
        this.nodeWeightLabel = new JLabel(Language.getString("NODE_WEIGHT"));
        this.nodeWeightTextField = new JTextField(Constants.WEIGHT_TEXTFIELD_COLUMNS);
        this.restoreNodeWeightButton = new JButton(Language.getString("RESTORE_DEFAULT"));
        this.edgeWeightLabel = new JLabel(Language.getString("EDGE_WEIGHT"));
        this.edgeWeightTextField = new JTextField(Constants.WEIGHT_TEXTFIELD_COLUMNS);
        this.restoreEdgeWeightButton = new JButton(Language.getString("RESTORE_DEFAULT"));
        this.nodeSizeLabel = new JLabel(Language.getString("NODE_SIZE"));
        this.nodeSizeSlider = new JSlider(JSlider.HORIZONTAL, Constants.NODE_SIZE_SLIDER_MIN, 
                Constants.NODE_SIZE_SLIDER_MAX, 
                Constants.NODE_SIZE_SLIDER_INITIAL_VALUE);
        this.restoreNodeSizeButton = new JButton(Language.getString("RESTORE_DEFAULT"));
        this.okButton = new JButton(Language.getString("OK"));
        this.cancelButton = new JButton(Language.getString("CANCEL"));
        
        this.okButton.setPreferredSize(new Dimension(Constants.BUTTON_WIDTH, 
                this.okButton.getPreferredSize().height));
        this.cancelButton.setPreferredSize(new Dimension(Constants.BUTTON_WIDTH, 
                this.cancelButton.getPreferredSize().height));
        this.restoreEdgeWeightButton.setPreferredSize(new Dimension(Constants.BUTTON_WIDTH, 
                this.restoreEdgeWeightButton.getPreferredSize().height));
        this.restoreNodeSizeButton.setPreferredSize(new Dimension(Constants.BUTTON_WIDTH, 
                this.restoreNodeSizeButton.getPreferredSize().height));
        this.restoreNodeWeightButton.setPreferredSize(new Dimension(Constants.BUTTON_WIDTH, 
                this.restoreNodeWeightButton.getPreferredSize().height));
        this.nodeSizeSlider.setMajorTickSpacing(Constants.NODE_SIZE_SLIDER_MAJOR_TICK_SPACING);
        this.nodeSizeSlider.setMinorTickSpacing(Constants.NODE_SIZE_SLIDER_MINOR_TICK_SPACING);
        this.nodeSizeSlider.setPaintTicks(true);
        this.nodeSizeSlider.setPaintLabels(true);
        //</editor-fold>
        
        this.createToolTips();
        this.layoutComponents();
        this.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        this.pack();
        this.setResizable(false);
        this.setTitle(Language.getString("ADDITIONAL_SETTINGS"));
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Adds JToolTips to some of the components.
     */
    private void createToolTips() {
        this.edgeWeightTextField.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "EDGE_WEIGHT_TEXTFIELD_TIP")));
        this.nodeSizeLabel.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "NODE_SIZE_LABEL_TIP")));
        this.nodeWeightTextField.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "NODE_WEIGHT_TEXTFIELD_TIP")));
        this.restoreEdgeWeightButton.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "RESTORE_EDGE_WEIGHT_BUTTON_TIP")));
        this.restoreNodeSizeButton.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "RESTORE_NODE_SIZE_BUTTON_TIP")));
        this.restoreNodeWeightButton.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "RESTORE_NODE_WEIGHT_BUTTON_TIP")));
    }
    
    /**
     * Layouts the container's components.
     */
    private void layoutComponents() {
        JPanel tmpContentPane = new JPanel();
        GridBagLayout tmpGridBagLayout = new GridBagLayout();
        tmpContentPane.setLayout(tmpGridBagLayout);
        GridBagConstraints tmpConstraints = new GridBagConstraints();
        tmpConstraints.gridx = 0;
        tmpConstraints.gridy = 0;
        tmpConstraints.fill = GridBagConstraints.HORIZONTAL;
        tmpConstraints.insets = new Insets(Constants.FRAMEWORK_GAP_SIZE,
                Constants.FRAMEWORK_GAP_SIZE,
                Constants.INTER_COMPONENT_GAP_SIZE,
                Constants.INTER_COMPONENT_GAP_SIZE);
        tmpContentPane.add(this.nodeWeightLabel, tmpConstraints);
        
        tmpConstraints.gridx = 1;
        tmpConstraints.insets = new Insets(Constants.FRAMEWORK_GAP_SIZE,
                0,
                Constants.INTER_COMPONENT_GAP_SIZE,
                Constants.INTER_COMPONENT_GAP_SIZE);
        tmpContentPane.add(this.nodeWeightTextField, tmpConstraints);
        
        tmpConstraints.gridx = 2;
        tmpConstraints.insets = new Insets(Constants.FRAMEWORK_GAP_SIZE,
                0,
                Constants.INTER_COMPONENT_GAP_SIZE,
                Constants.FRAMEWORK_GAP_SIZE);
        tmpContentPane.add(this.restoreNodeWeightButton, tmpConstraints);
        
        tmpConstraints.gridx = 0;
        tmpConstraints.gridy = 1;
        tmpConstraints.insets = new Insets(0,
                Constants.FRAMEWORK_GAP_SIZE,
                Constants.INTER_COMPONENT_GAP_SIZE,
                Constants.INTER_COMPONENT_GAP_SIZE);
        tmpContentPane.add(this.edgeWeightLabel, tmpConstraints);
        
        tmpConstraints.gridx = 1;
        tmpConstraints.insets = new Insets(0,
                0,
                Constants.INTER_COMPONENT_GAP_SIZE,
                Constants.INTER_COMPONENT_GAP_SIZE);
        tmpContentPane.add(this.edgeWeightTextField, tmpConstraints);
        
        tmpConstraints.gridx = 2;
        tmpConstraints.insets = new Insets(0,
                0,
                Constants.INTER_COMPONENT_GAP_SIZE,
                Constants.FRAMEWORK_GAP_SIZE);
        tmpContentPane.add(this.restoreEdgeWeightButton, tmpConstraints);
        
        tmpConstraints.gridx = 0;
        tmpConstraints.gridy = 2;
        tmpConstraints.insets = new Insets(0,
                Constants.FRAMEWORK_GAP_SIZE,
                0,
                Constants.INTER_COMPONENT_GAP_SIZE);
        tmpContentPane.add(this.nodeSizeLabel, tmpConstraints);
        
        tmpConstraints.gridx = 1;
        tmpConstraints.insets = new Insets(0,
                0,
                0,
                Constants.INTER_COMPONENT_GAP_SIZE);
        tmpContentPane.add(this.nodeSizeSlider, tmpConstraints);
        
        tmpConstraints.gridx = 2;
        tmpConstraints.insets = new Insets(0,
                0,
                Constants.INTER_COMPONENT_GAP_SIZE,
                Constants.FRAMEWORK_GAP_SIZE);
        tmpContentPane.add(this.restoreNodeSizeButton, tmpConstraints);
        
        tmpConstraints.gridy = 3;
        tmpConstraints.gridx = 1;
        tmpConstraints.fill = GridBagConstraints.NONE;
        tmpConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
        tmpConstraints.insets = new Insets(Constants.FRAMEWORK_GAP_SIZE,
                0,
                Constants.FRAMEWORK_GAP_SIZE,
                Constants.INTER_COMPONENT_GAP_SIZE);
        tmpContentPane.add(this.okButton, tmpConstraints);
        
        tmpConstraints.gridx = 2;
        tmpConstraints.anchor = GridBagConstraints.PAGE_START;
        tmpConstraints.insets = new Insets(Constants.FRAMEWORK_GAP_SIZE,
                0,
                Constants.FRAMEWORK_GAP_SIZE,
                Constants.FRAMEWORK_GAP_SIZE);
        tmpContentPane.add(this.cancelButton, tmpConstraints);
        
        this.setContentPane(tmpContentPane);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Returns the dialog's JButton that is supposed to restore the Spices graph's node weight to its default value.
     * 
     * @return the dialog's JButton that should restore the node weight
     * to its default value
     */
    public JButton getRestoreNodeWeightButton() {
        return this.restoreNodeWeightButton;
    }
    
    /**
     * Returns the dialog's JButton that is supposed to restore the Spices graph's edge weight to its default value.
     * 
     * @return the dialog's JButton that should restore the edge weight
     * to its default value
     */
    public JButton getRestoreEdgeWeightButton() {
        return this.restoreEdgeWeightButton;
    }
    
    /**
     * Returns the dialog's 'OK' JButton that is supposed to hide the dialog and adjust settings altered by the user.
     * 
     * @return the dialog's 'OK' JButton
     */
    public JButton getOkButton() {
        return this.okButton;
    }
    
    /**
     * Returns the dialog's 'Cancel' JButton that is supposed to hide the dialog without adjusting any settings.
     * 
     * @return the dialog's 'Cancel' JButton
     */
    public JButton getCancelButton() {
        return this.cancelButton;
    }
    
    /**
     * Returns the dialog's JButton that is supposed to restore the Spices graph's node size to its default value.
     * 
     * @return the dialog's JButton that should restore the node size
     * to its default value
     */
    public JButton getRestoreNodeSizeButton() {
        return this.restoreNodeSizeButton;
    }
    
    /**
     * Returns the dialog's JSlider that is supposed to adjust the Spices graph's node size. 
     * 
     * @return the dialog's JSlider that should adjust the node size
     */
    public JSlider getNodeSizeSlider() {
        return this.nodeSizeSlider;
    }
    
    /**
     * Returns the dialog's JTextField where the user can enter a node weight for the Spices graph.
     * 
     * @return the dialog's JTextField designated for node weights
     */
    public JTextField getNodeWeightTextField() {
        return this.nodeWeightTextField;
    }
    
    /**
     * Returns the dialog's JTextField where the user can enter an edge weight for the Spices graph.
     * 
     * @return the dialog's JTextField designated for edge weights
     */
    public JTextField getEdgeWeightTextField() {
        return this.edgeWeightTextField;
    }
    //</editor-fold>
}
