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

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 * A JMenuBar for the application's {@link de.gnwi.spicesviewer.MainFrame}. 
 * It contains menus for setting 
 * graph properties (like node shape, node color, behavior for long particle
 * names and opening the {@link de.gnwi.spicesviewer.AdditionalSettingsDialog}), 
 * application properties (error color
 * and 'valid' color) and for screenshotting the graph or frame.
 * 
 * @author Jonas Schaub
 */
public class MenuBar extends JMenuBar {
    
    //<editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * A JMenu for the graph settings.
     */
    private final JMenu graphSettingsMenu;
    
    /**
     * A JMenu for the application settings.
     */
    private final JMenu appSettingsMenu;
    
    /**
     * A JMenu for the different image export options.
     */
    private final JMenu imageExportMenu;
    
    /**
     * A (sub-)menu to hold JRadioButtonMenuItems for setting the node shape.
     */
    private final JMenu nodeShapeSubmenu;
    
    /**
     * A (sub-)menu for setting and resetting the node color.
     */
    private final JMenu nodeColorSubmenu;
    
    /**
     * A checkbox to choose whether long particle names should be written out in full.
     */
    private final JCheckBoxMenuItem writeNamesOutMenuBox;
    
    /**
     * A JMenuItem to open the AdditionalSettingsDialog.
     */
    private final JMenuItem additionalSettingsMenuItem;
    
    /**
     * A JMenuItem to restore all graph settings.
     */
    private final JMenuItem restoreGraphSettingMenuItem;
    
    /**
     * A ButtonGroup for the node shape radio buttons.
     */
    private final ButtonGroup nodeShapeButtonGroup;
    
    /**
     * A String array to hold the language-dependent representations of the 
     * different node shape options.
     */
    private final String[] nodeShapeOptions;
    
    /**
     * An array of JRadioButtonMenuItems to hold the radio buttons for choosing 
     * the node shape.
     */
    private final JRadioButtonMenuItem[] nodeShapeRadioButtons;
    
    /**
     * A JMenuItem to restore the default node shape.
     */
    private final JMenuItem nodeShapeRestoreDefaultMenuItem;
    
    /**
     * A JMenuItem to change the node color.
     */
    private final JMenuItem nodeColorChangerMenuItem;
    
    /**
     * A JMenuItem to restore the default node color.
     */
    private final JMenuItem nodeColorRestoreDefaultMenuItem;
    
    /**
     * A JMenuItem to change the color of the application's error messages.
     */
    private final JMenuItem errorColorChangerMenuItem;
    
    /**
     * A JMenuItem to change the 'valid' color.
     */
    private final JMenuItem validColorChangerMenuItem;
    
    /**
     * A JMenuItem to restore the application's settings (error color and 'valid' color).
     */
    private final JMenuItem restoreAppSettingsMenuItem;
    
    /**
     * A JMenuItem to export the Spices graph as an image file via graph attribute.
     */
    private final JMenuItem imageExportByGraphAttributeMenuItem;
    
    /**
     * A JMenuItem to export the Spices graph as an image file via GraphStream's FileSinkImages.
     */
    private final JMenuItem imageExportByFileSinkImagesMenuItem;
    
    /**
     * A JMenuItem to export the Spices graph as an image file using Swing.
     */
    private final JMenuItem imageExportBySwingMenuItem;
    
    /**
     * A JMenuItem to screenshot the whole main frame using Swing.
     */
    private final JMenuItem screenshotWholeFrameMenuItem;
    
    /**
     * A JMenuItem to set the directory where the produced image files should be saved.
     */
    private final JMenuItem setImageFileDirMenuItem;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public constructor">
    /**
     * Constructor:
     * It initialises the new MenuBar's components,
     * adds them to the MenuBar and adds JToolTips to them. There are not event listeners added to any component.
     */
    public MenuBar() {
        super();
        //<editor-fold defaultstate="collapsed" desc="Initialisation">
        this.graphSettingsMenu = new JMenu(Language.getString("GRAPH_SETTINGS"));
        this.appSettingsMenu = new JMenu(Language.getString("APPLICATION_SETTINGS"));
        this.imageExportMenu = new JMenu(Language.getString("IMAGE_EXPORT"));
        this.nodeShapeSubmenu = new JMenu(Language.getString("NODE_SHAPE"));
        this.nodeColorSubmenu = new JMenu(Language.getString("NODE_COLOR"));
        this.writeNamesOutMenuBox = new JCheckBoxMenuItem(Language.getString("WRITE_PARTICLE_NAMES_OUT_IN_FULL"));
        this.additionalSettingsMenuItem = new JMenuItem(Language.getString("ADDITIONAL_SETTINGS"));
        this.restoreGraphSettingMenuItem = new JMenuItem(Language.getString("RESTORE_GRAPH_SETTINGS"));
        this.nodeShapeButtonGroup = new ButtonGroup();
        this.nodeShapeOptions = new String[Constants.Shape.values().length];
        for (int i = 0; i < Constants.Shape.values().length; i++) {
            this.nodeShapeOptions[i] = Constants.Shape.values()[i].getLanguageRepresentation();
        }
        this.nodeShapeRadioButtons = new JRadioButtonMenuItem[this.nodeShapeOptions.length];
        for (int i = 0; i < this.nodeShapeOptions.length; i++) {
            this.nodeShapeRadioButtons[i] = new JRadioButtonMenuItem(this.nodeShapeOptions[i]);
            this.nodeShapeButtonGroup.add(this.nodeShapeRadioButtons[i]);
        }
        this.nodeShapeRestoreDefaultMenuItem = new JMenuItem(Language.getString("RESTORE_DEFAULT"));
        this.nodeColorChangerMenuItem = new JMenuItem(Language.getString("CHOOSE_NODE_COLOR"));
        this.nodeColorRestoreDefaultMenuItem = new JMenuItem(Language.getString("RESTORE_DEFAULT"));
        this.errorColorChangerMenuItem = new JMenuItem(Language.getString("CHOOSE_ERROR_COLOR"));
        this.validColorChangerMenuItem = new JMenuItem(Language.getString("CHOOSE_VALID_COLOR"));
        this.restoreAppSettingsMenuItem = new JMenuItem(Language.getString("RESTORE_APPLICATION_SETTINGS"));
        this.imageExportByGraphAttributeMenuItem = new JMenuItem(Language.getString("IMAGE_EXPORT_BY_GRAPH_ATTRIBUTE"));
        this.imageExportByFileSinkImagesMenuItem = new JMenuItem(Language.getString("IMAGE_EXPORT_BY_FILESINKIMAGES"));
        this.imageExportBySwingMenuItem = new JMenuItem(Language.getString("IMAGE_EXPORT_BY_SWING"));
        this.screenshotWholeFrameMenuItem = new JMenuItem(Language.getString("WHOLE_FRAME_SCREENSHOT"));
        this.setImageFileDirMenuItem = new JMenuItem(Language.getString("SET_IMAGE_EXPORT_FILE_DIR"));
        //</editor-fold>
        
        this.createToolTips();
        this.addComponentsToMenuBar();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Adds JToolTips to some of the components.
     */
    private void createToolTips() {
        this.additionalSettingsMenuItem.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "ADDITIONAL_SETTINGS_MENUITEM_TIP")));
        this.appSettingsMenu.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "APPLICATION_SETTINGS_MENU_TIP")));
        this.graphSettingsMenu.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "GRAPH_SETTINGS_MENU_TIP")));
        this.nodeColorRestoreDefaultMenuItem.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "NODE_COLOR_RESTORE_MENUITEM_TIP")));
        this.nodeShapeRestoreDefaultMenuItem.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "NODE_SHAPE_RESTORE_MENUITEM_TIP")));
        this.restoreAppSettingsMenuItem.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "APPLICATION_SETTINGS_RESTORE_MENUITEM_TIP")));
        this.restoreGraphSettingMenuItem.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "GRAPH_SETTINGS_RESTORE_MENUITEM_TIP")));
        this.imageExportBySwingMenuItem.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "IMAGE_EXPORT_BY_SWING_MENUITEM_TIP")));
        this.imageExportByFileSinkImagesMenuItem.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "IMAGE_EXPORT_BY_FILESINK_MENUITEM_TIP")));
        this.imageExportByGraphAttributeMenuItem.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "IMAGE_EXPORT_BY_GRAPH_ATTRIBUTE_MENUITEM_TIP")));
        this.screenshotWholeFrameMenuItem.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "SCREENSHOT_WHOLE_FRAME_MENUITEM_TIP")));
        this.writeNamesOutMenuBox.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "WRITE_NAMES_OUT_MENUBOX_TIP")));
        this.setImageFileDirMenuItem.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "SET_IMAGE_EXPORT_FILE_DIR_MENUITEM_TIP")));
    }
    
    /**
     * Adds the menu items to the submenus and the submenus to the menu bar.
     */
    private void addComponentsToMenuBar() {
        this.add(this.appSettingsMenu);
        this.add(this.graphSettingsMenu);
        this.add(this.imageExportMenu);
        this.graphSettingsMenu.add(this.nodeShapeSubmenu);
        for (JRadioButtonMenuItem tmpButton : this.nodeShapeRadioButtons) {
            this.nodeShapeSubmenu.add(tmpButton);
        }
        this.nodeShapeSubmenu.addSeparator();
        this.nodeShapeSubmenu.add(this.nodeShapeRestoreDefaultMenuItem);
        this.graphSettingsMenu.add(this.nodeColorSubmenu);
        this.nodeColorSubmenu.add(this.nodeColorChangerMenuItem);
        this.nodeColorSubmenu.addSeparator();
        this.nodeColorSubmenu.add(this.nodeColorRestoreDefaultMenuItem);
        this.graphSettingsMenu.addSeparator();
        this.graphSettingsMenu.add(this.writeNamesOutMenuBox);
        this.graphSettingsMenu.addSeparator();
        this.graphSettingsMenu.add(this.additionalSettingsMenuItem);
        this.graphSettingsMenu.addSeparator();
        this.graphSettingsMenu.add(this.restoreGraphSettingMenuItem);
        this.appSettingsMenu.add(this.errorColorChangerMenuItem);
        this.appSettingsMenu.add(this.validColorChangerMenuItem);
        this.appSettingsMenu.addSeparator();
        this.appSettingsMenu.add(this.restoreAppSettingsMenuItem);
        this.imageExportMenu.add(this.imageExportByGraphAttributeMenuItem);
        this.imageExportMenu.add(this.imageExportByFileSinkImagesMenuItem);
        this.imageExportMenu.add(this.imageExportBySwingMenuItem);
        this.imageExportMenu.add(this.screenshotWholeFrameMenuItem);
        this.imageExportMenu.addSeparator();
        this.imageExportMenu.add(this.setImageFileDirMenuItem);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Returns every radio button in the menu bar's node shape submenu.
     * 
     * @return every radio button in the node shape submenu
     */
    public JRadioButtonMenuItem[] getNodeShapeRadioButtons() {
        return this.nodeShapeRadioButtons;
    }
    
    /**
     * Returns the menu bar's menu item that is supposed to restore the Spices graph's node shape to
     * its default value.
     * 
     * @return the menu item that should reset the node shape
     */
    public JMenuItem getRestoreNodeShapeMenuItem() {
        return this.nodeShapeRestoreDefaultMenuItem;
    }
    
    /**
     * Returns the menu bar's menu item that is supposed to let the user choose a new
     * node color for the Spices graph via a JColorChooser.
     * 
     * @return the menu item that should let the user choose a new node color
     */
    public JMenuItem getNodeColorChangerMenuItem() {
        return this.nodeColorChangerMenuItem;
    }
    
    /**
     * Returns the menu bar's menu item that is supposed to restore the Spices graph's node color to
     * its default value.
     * 
     * @return the menu item that should reset the node color
     */
    public JMenuItem getRestoreNodeColorMenuItem() {
        return this.nodeColorRestoreDefaultMenuItem;
    }
    
    /**
     * Returns the menu bar's menu item that is supposed to open the 
     * {@link de.gnwi.spicesviewer.AdditionalSettingsDialog}.
     * 
     * @return the menu item that should open the 
     * {@link de.gnwi.spicesviewer.AdditionalSettingsDialog}
     */
    public JMenuItem getAdditionalSettingsMenuItem() {
        return this.additionalSettingsMenuItem;
    }
    
    /**
     * Returns the menu bar's menu item that is supposed to restore the Spices graph's settings 
     * to their default values.
     * 
     * @return the menu item that should restore the graph settings
     */
    public JMenuItem getRestoreGraphSettingsMenuItem() {
        return this.restoreGraphSettingMenuItem;
    }
    
    /**
     * Returns the menu bar's menu item that is supposed to let the user choose a new 
     * error color via a JColorChooser for the application's error messages.
     * 
     * @return the menu item that should enable the user to set a new error color
     */
    public JMenuItem getErrorColorChangerMenuItem() {
        return this.errorColorChangerMenuItem;
    }
    
    /**
     * Returns the menu bar's menu item that is supposed to let the user choose a new 
     * valid color via a JColorChooser for the application's valid message.
     * 
     * @return the menu item that should enable the user to set a new valid color
     */
    public JMenuItem getValidColorChangerMenuItem() {
        return this.validColorChangerMenuItem;
    }
    
    /**
     * REturns the menu bar's menu item that is supposed to restore the application's 
     * settings (error color and valid color) to their default values.
     * 
     * @return the menu item that should restore the application's settings
     */
    public JMenuItem getRestoreAppSettingsMenuItem() {
        return this.restoreAppSettingsMenuItem;
    }
    
    /**
     * Returns the menu bar's menu item that is supposed to export the 
     * Spices graph as an image file by using a 'ui.screenshot' graph attribute.
     * 
     * @return the menu item that should export the Spices graph as an image file by graph attribute
     */
    public JMenuItem getImageExportByGraphAttributeMenuItem() {
        return this.imageExportByGraphAttributeMenuItem;
    }
    
    /**
     * Returns the menu bar's menu item that is supposed to export the 
     * Spices graph as an image file by using GraphStream's 
     * {@link org.graphstream.stream.file.FileSinkImages} class.
     * 
     * @return the menu item that should export the Spices graph as an image file by 
     * GraphStream's {@link org.graphstream.stream.file.FileSinkImages} class
     */
    public JMenuItem getImageExportByFileSinkImagesMenuItem() {
        return this.imageExportByFileSinkImagesMenuItem;
    }
    
    /**
     * Returns the menu bar's menu item that is supposed to export the 
     * Spices graph as an image file using Swing.
     * 
     * @return the menu item that should export the Spices graph as an image file using Swing
     */
    public JMenuItem getImageExportBySwingMenuItem() {
        return this.imageExportBySwingMenuItem;
    }
    
    /**
     * Returns the menu bar's menu item that is supposed to screenshot the whole main frame using Swing.
     * 
     * @return the menu item that should screenshot the whole main frame using Swing
     */
    public JMenuItem getScreenshotOfWholeFrameMenuItem() {
        return this.screenshotWholeFrameMenuItem;
    }
    
    /**
     * Returns the menu bar's menu item that is supposed to allow the user to set the directory
     * where the exported image files should be saved.
     * 
     * @return that should allow the user to set the directory where exported image files should be saved
     */
    public JMenuItem getImageExportFileDirMenuItem() {
        return this.setImageFileDirMenuItem;
    }
    
    /**
     * Returns the menu bar's checkbox menu item in the graph settings menu that is supposed to determine
     * whether the particle names should be written out in full on the Spices graph's nodes.
     * 
     * @return the checkbox menu item that should determine whether the particle names should be fully displayed
     */
    public JCheckBoxMenuItem getFullParticleNameDisplayMenuBox() {
        return this.writeNamesOutMenuBox;
    }
    
    /**
     * Returns the menu bar's image export menu.
     * 
     * @return the image export menu
     */
    public JMenu getImageExportMenu() {
        return this.imageExportMenu;
    }
    //</editor-fold>
}
