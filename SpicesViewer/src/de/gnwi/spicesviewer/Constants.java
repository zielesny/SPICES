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

import java.awt.Color;
import java.awt.Dimension;
import java.util.regex.Pattern;

/**
 * The non language-related constants for the Spices Viewer application.
 * 
 * @author Jonas Schaub
 */
public abstract class Constants {

    //<editor-fold defaultstate="collapsed" desc="Enums">
    /**
     * An enum for the GraphStream node shape options
     * (see {@link org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape})
     * that combines the language-dependent representation of each 
     * option with the keyword that must be added as an 'ui.style' attribute to a node. 
     */
    public enum Shape {
        
        /**
         * Constant for a circular node shape.
         */
        CIRCLE (Language.getString("CIRCLE"), "circle"),
        
        /**
         * Constant for a rectangular node shape.
         */
        BOX (Language.getString("BOX"), "box"),
        
        /**
         * Constant for a rectangular node shape with round corners.
         */
        ROUNDED_BOX (Language.getString("ROUNDED_BOX"), "rounded-box"),
        
        /**
         * Constant for a rhombic node shape.
         */
        DIAMOND (Language.getString("DIAMOND"), "diamond"),
        
        /**
         * Constant for a cross-shaped node shape.
         */
        CROSS (Language.getString("CROSS"), "cross");
        
        /**
         * The language-dependent representation of each shape option determined 
         * by the actual 'Language_xx.properties' file in use.
         */
        private final String languageRepresentation;
        
        /**
         * The GraphStream style key of each node shape.
         */
        private final String gsKey;
        
        /**
         * Constructor:
         * It initialises a new Constant of type Constants.Shape.
         * 
         * @param aLanguageRepresentation The language-dependent representation 
         * of the new shape option determined by the actual 'Language_xx.properties' file in use
         * @param aGsKey The GraphStream style key of the new node shape
         */
        Shape(String aLanguageRepresentation, String aGsKey) {
            this.languageRepresentation = aLanguageRepresentation;
            this.gsKey = aGsKey;
        }
        
        /**
         * Returns the language-dependent representation of the node shape option.
         * 
         * @return the node shape's language-dependent representation
         */
        public String getLanguageRepresentation() {
            return this.languageRepresentation;
        }
        
        /**
         * Returns the GraphStream style key for the node shape.
         * 
         * @return the node shape's GraphStream style key
         */
        public String getGsKey() {
            return this.gsKey;
        }
    }
    
    /**
     * An enum for the different modes of adding a structure from the 
     * library to the {@link de.gnwi.spicesviewer.ViewerPanel}'s JTextArea 
     * that holds their language-dependent representations.
     */
    public enum AddStructureOptions {
        
        /**
         * Constant for replacing the current text of the text area with the selected
         * structure from the library.
         */
        REPLACE (Language.getString("REPLACE_INPUT_WITH_STRUCTURE")),
        
        /**
         * Constant for adding the selected structure to the already inserted structure
         * in the text area as a new part.
         */
        NEW_PART (Language.getString("ADD_AS_NEW_PART")),
        
        /**
         * Constant for connecting the selected structure from the library to 
         * the structure already inserted in the text area.
         */
        CONNECTING (Language.getString("ADD_SIMPLE_CONNECTION")),
        
        /**
         * Constant for adding the selected structure to the already inserted structure
         * in the text area as a new side chain.
         */
        SIDE_CHAIN (Language.getString("ADD_AS_SIDE_CHAIN"));
        
        /**
         * The language-dependent representation of each addition mode determined 
         * by the actual 'Language_xx.properties' file in use.
         */
        private final String languageRepresentation;
        
        /**
         * Constructor:
         * It initialises a new Constant of type Constants.AddStructureOptions.
         * 
         * @param aLanguageRepresentation The language-dependent representation 
         * of the new addition mode determined by the actual 'Language_xx.properties' file
         */
        AddStructureOptions(String aLanguageRepresentation) {
            this.languageRepresentation = aLanguageRepresentation;
        }
        
        /**
         * Returns the language-dependent representation of the addition mode.
         * 
         * @return the addition modes language-dependent representation
         */
        public String getLanguageRepresentation() {
            return this.languageRepresentation;
        }
    }
    
    /**
     * An enum for the four different modes of exporting the graph
     * or the whole main frame that holds their language-dependent
     * representations.
     */
    public enum ImageExportOptions {
        
        /**
         * Constant for exporting the graph by adding an 'ui.screenshot'
         * attribute to it.
         */
        GRAPH_ATTRIBUTE (Language.getString("IMAGE_EXPORT_BY_GRAPH_ATTRIBUTE")),
        
        /**
         * Constant for exporting the graph by GraphStream's 
         * {@link org.graphstream.stream.file.FileSinkImages}.
         */
        FILESINKIMAGES (Language.getString("IMAGE_EXPORT_BY_FILESINKIMAGES")),
        
        /**
         * Constant for exporting the graph by using the Swing paint()
         * method.
         */
        SWING (Language.getString("IMAGE_EXPORT_BY_SWING")),
        
        /**
         * Constant for screenshotting the whole main frame by using the Swing paint()
         * method.
         */
        WHOLE_FRAME (Language.getString("WHOLE_FRAME_SCREENSHOT"));
        
        /**
         * The language-dependent representation of each image export option determined 
         * by the actual 'Language_xx.properties' file in use.
         */
        private final String languageRepresentation;
        
        /**
         * Constructor:
         * It initialises a new Constant of type Constants.ImageExportOptions.
         * 
         * @param aLanguageRepresentation The language-dependent representation 
         * of the new image export option determined by the actual 
         * 'Language_xx.properties' file in use
         */
        ImageExportOptions(String aLanguageRepresentation) {
            this.languageRepresentation = aLanguageRepresentation;
        }
        
        /**
         * Returns the language-dependent representation of the image export option.
         * 
         * @return the addition modes language-dependent representation
         */
        public String getLanguageRepresentation() {
            return this.languageRepresentation;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public static final class variables">
    //<editor-fold defaultstate="collapsed" desc="Public static final int class variables">
    /**
     * A maximum length for doubles entered into the JTextFields for edge and node weight
     * in the {@link de.gnwi.spicesviewer.AdditionalSettingsDialog}.
     */
    public static final int WEIGHT_INPUT_MAX_LENGTH = 10;
    
    /**
     * Size for gaps between a frame's components and its bounds.
     */
    public static final int FRAMEWORK_GAP_SIZE = 20;
    
    /**
     * Size for gaps between a frame's components.
     */
    public static final int INTER_COMPONENT_GAP_SIZE = 5;
    
    /**
     * Initial number of rows for the {@link de.gnwi.spicesviewer.ViewerPanel}'s text area.
     */
    public static final int TEXTAREA_INITIAL_HEIGTH = 5;
    
    /**
     * Initial number of columns for the {@link de.gnwi.spicesviewer.ViewerPanel}'s text area.
     */
    public static final int TEXTAREA_INITIAL_WIDTH = 35;
   
    /**
     * Initial selected index for the {@link de.gnwi.spicesviewer.ViewerPanel}'s JComboBox.
     */
    public static final int COMBOBOX_INITIAL_SELECTED_INDEX = 0;
    
    /**
     * Initial number of columns for the edge weight and node weight text fields
     * in the {@link de.gnwi.spicesviewer.AdditionalSettingsDialog}.
     */
    public static final int WEIGHT_TEXTFIELD_COLUMNS = 5;
    
    /**
     * Minimum value of the node size slider in the 
     * {@link de.gnwi.spicesviewer.AdditionalSettingsDialog}.
     */
    public static final int NODE_SIZE_SLIDER_MIN = 10;
    
    /**
     * Maximum value of the node size slider in the 
     * {@link de.gnwi.spicesviewer.AdditionalSettingsDialog}.
     */
    public static final int NODE_SIZE_SLIDER_MAX = 100;
    
    /**
     * Initial value of the node size slider in the 
     * {@link de.gnwi.spicesviewer.AdditionalSettingsDialog}.
     */
    public static final int NODE_SIZE_SLIDER_INITIAL_VALUE = 10;
    
    /**
     * Major ticking space of the node size slider in the 
     * {@link de.gnwi.spicesviewer.AdditionalSettingsDialog}.
     */
    public static final int NODE_SIZE_SLIDER_MAJOR_TICK_SPACING = 10;
    
    /**
     * Minor ticking space of the node size slider in the 
     * {@link de.gnwi.spicesviewer.AdditionalSettingsDialog}.
     */
    public static final int NODE_SIZE_SLIDER_MINOR_TICK_SPACING = 1;
    
    /**
     * Width of all JToolTips in the application.
     */
    public static final int TOOL_TIP_WIDTH = 200;
    
    /**
     * Minimum width and height of the written image file
     */
    public static final int IMAGE_FILE_MIN_WIDTH_AND_HIGHT = 400;
    
    /**
     * A factor the graph's node count is multiplied by to determine 
     * the resolution of the written image file
     */
    public static final int NODE_COUNT_TO_RESOLUTION_FACTOR = 20;
    
    /**
     * Minimum node count of the graph from which on to use 
     * {@link org.graphstream.stream.file.FileSinkImages.Resolutions#UHD_4K} 
     * constant as resolution of the written image file
     */
    public static final int MIN_NODE_COUNT_FOR_UHD_4K = 190;
    
    /**
     * Minimum node count of the graph from which on to use 
     * {@link org.graphstream.stream.file.FileSinkImages.Resolutions#UHD_8K_16by9} 
     * constant as resolution of the written image file
     */
    public static final int MIN_NODE_COUNT_FOR_UHD_8K_16by9 = 300;
    
    /**
     * Minimum node count of the graph from which on to use 
     * {@link org.graphstream.stream.file.FileSinkImages.Resolutions#UHD_8K_1by1} 
     * constant as resolution of the written image file
     */
    public static final int MIN_NODE_COUNT_FOR_UHD_8K_1by1 = 500;
    
    /**
     * The number of steps the ScreenshotByGraphAttributeThread should take of testing 
     * whether the image file has been produced and waiting if it has not yet been produced.
     */
    public static final int WAITING_STEPS_FOR_IMAGE_EXPORT_BY_GRAPHATTRIBUTE = 10;
    
    /**
     * The time in milliseconds the ScreenshotByGraphAttributeThread should
     * wait for the GraphStream viewer to produce the image file.
     */
    public static final int WAITING_TIME_MILLIS_FOR_IMAGE_EXPORT_BY_GRAPHATTRIBUTE = 5000;
    
    /**
     * The time in milliseconds the ShowStatusMessageThread
     * should wait before resetting its label's text.
     */
    public static final int MESSAGE_SHOWING_TIME_MILLIS = 3000;
    
    /**
     * The number of allowed times the application has got to load the user preferences 
     * and fail before the application is shut down.
     */
    public static final int LOADING_TRIES = 3;
    
    /**
     * The width for all JButtons in the application.
     */
    public static final int BUTTON_WIDTH = 75;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public static final Color class variables">
    /**
     * The color black from Swing as default foreground color of the {@link de.gnwi.spicesviewer.MainFrame}'s 
     * status label.
     */
    public static final Color BLACK = Color.BLACK;
    
    /**
     * The default color for the application's error messages.
     */
    public static final Color DEFAULT_ERROR_COLOR = Color.RED;
    
    /**
     * The default color for the application's 'valid' message.
     */
    public static final Color DEFAULT_VALID_COLOR = Color.BLUE;
    
    /**
     * The default node color to be used when the graph's stylesheet does not 
     * define one.
     */
    public static final Color DEFAULT_NODE_COLOR = Color.WHITE;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public static final String class variables">
    /**
     * Initial text for the {@link de.gnwi.spicesviewer.ViewerPanel}'s text area. 
     */
    public static final String TEXTAREA_INITIAL_TEXT = "";
    
    /**
     * Initial text for the {@link de.gnwi.spicesviewer.ViewerPanel}'s message 
     * label and the {@link de.gnwi.spicesviewer.MainFrame}'s status label.
     */
    public static final String MESSAGE_LABEL_INITIAL_TEXT = " ";
    
    /**
     * An error message for the isListenerNull method.
     */
    public static final String LISTENER_IS_NULL = "The listener object is 'null'!";
    
    /**
     * The stylesheet to be added to the graph when the external resource 
     * can not be found.
     */
    public static final String DEFAULT_CSS_STYLESHEET = "node {\n" +
                                                        "	shape: rounded-box;\n" +
                                                        "       fill-mode: dyn-plain;\n" +
                                                        "	fill-color: white;\n" +
                                                        "	stroke-mode: plain;\n" +
                                                        "	stroke-width: 1;\n" +
                                                        "       size-mode: fit;\n" +
                                                        "	text-size: 14;\n" +
                                                        "       padding: 4, 1;\n" +
                                                        "}\n" +
                                                        "node.START {\n" +
                                                        "	shape: box;\n" +
                                                        "       icon-mode: at-left;\n" +
                                                        "	fill-mode: dyn-plain;\n" +
                                                        "       fill-color: white;\n" +
                                                        "       stroke-mode: dots;\n" +
                                                        "	stroke-width: 1;\n" +
                                                        "       size-mode: fit;\n" +
                                                        "       text-size: 14;\n" +
                                                        "       text-style: italic;\n" +
                                                        "}\n" +
                                                        "node.END {\n" +
                                                        "	shape: box;\n" +
                                                        "       icon-mode: at-left;\n" +
                                                        "	fill-mode: dyn-plain;\n" +
                                                        "       fill-color: white;\n" +
                                                        "       stroke-mode: dots;\n" +
                                                        "	stroke-width: 1;\n" +
                                                        "       size-mode: fit;\n" +
                                                        "       text-size: 14;\n" +
                                                        "       text-style: italic;\n" +
                                                        "}\n" +
                                                        "edge {\n" +
                                                        "       size: 1;\n" +
                                                        "}\n" +
                                                        "edge.START {\n" +
                                                        "       fill-mode: none; \n" +
                                                        "       stroke-mode: dots; \n" +
                                                        "       size: 0px;\n" +
                                                        "}\n" +
                                                        "edge.END {\n" +
                                                        "       fill-mode: none; \n" +
                                                        "       stroke-mode: dots; \n" +
                                                        "       size: 0px;\n" +
                                                        "}";
    
    /**
     * The default value for the directory the user set to save the image files to.
     */
    public static final String DEFAULT_USER_IMAGE_FILE_DIR = "";
    
    /**
     * The company distributing this application's name for naming the file folder for the user preferences.
     */
    public static final String VENDOR_NAME = "GNWI";
    
    /**
     * The application's name for naming the file folder for the user preferences.
     */
    public static final String APPLICATION_NAME = "SpicesViewer";
    
    /**
     * The filename of the serialized {@link de.gnwi.spicesviewer.UserPreferences} object.
     */
    public static final String USER_PREFERENCES_FILENAME = "User_Preferences.ser";
    
    /**
     * The filename of the produced image files.
     */
    public static final String EXPORTED_IMAGE_FILE_NAME = "Graph_Image";
    
    /**
     * The image files' extension/file format.
     */
    public static final String EXPORTED_IMAGE_FILE_EXTENSION = ".png";
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public static final double class variables">
    /**
     * Required java version.
     */
    public static final double REQUIRED_JAVA_VERSION = 1.8;
    
    /**
     * The zoom factor for {@link de.gnwi.spicesviewer.ViewerPanel} and 
     * {@link de.gnwi.spicesviewer.AlternativeViewerPanel}.
     */
    public static final double ZOOM_FACTOR = 0.1;
    
    /**
     * The resize weight for the applications split panes.
     */
    public static final double SPLIT_PANE_RESIZE_WEIGHT = 0.5;
    
    /**
     * The default node weight value for the {@link de.gnwi.spicesviewer.UserPreferences} 
     * when the user has not specified a value.
     * <p>
     * Note: There does not exist a real default value in GraphStream for this
     * (the default here is when there is no 'layout.weight' attribute stored 
     * on the graph) so this default value is defined as Double.NaN.
     */
    public static final Double DEFAULT_NODE_WEIGHT = Double.NaN;
    
    /**
     * The default edge weight value for the {@link de.gnwi.spicesviewer.UserPreferences}
     * when the user has not specified a value.
     * <p>
     * Note: There does not exist a real default value in GraphStream for this
     * (the default here is when there is no 'layout.weight' attribute stored 
     * on the graph) so this default value is defined as Double.NaN.
     */
    public static final Double DEFAULT_EDGE_WEIGHT = Double.NaN;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public static final Dimension class variables">
    /**
     * Preferred size for the {@link de.gnwi.spicesviewer.ViewerPanel}'s GraphStream 
     * {@link org.graphstream.ui.swingViewer.ViewPanel}.
     */
    public static final Dimension GRAPHSTREAM_VIEW_PANEL_PREFERRED_SIZE = new Dimension(400, 300);
    
    /**
     * The minimum size of the scroll panes in the 
     * {@link de.gnwi.spicesviewer.StructureLibraryPanel}'s JSplitPane.
     */
    public static final Dimension LIBRARY_PANEL_SCROLLPANES_MIN_SIZE = new Dimension(400, 100);
    
    /**
     * The minimum size of the {@link de.gnwi.spicesviewer.ViewerPanel}'s 
     * components in its JSplitPane.
     */
    public static final Dimension VIEWER_PANEL_SPLITPANE_COMPONENTS_MIN_SIZE = new Dimension(400, 100);
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public static final boolean class variables">
    /**
     * The default value for {@link de.gnwi.spicesviewer.UserPreferences}' field
     * specifying whether the particle names should be written out in full on the 
     * graph's nodes.
     */
    public static final boolean IS_FULL_PARTICLE_NAME_DISPLAY_DEFAULT = false;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public static final Pattern class variables">
    /**
     * A Pattern to filter all illegal inputs into the node and edge weight JTextFields
     * in the {@link de.gnwi.spicesviewer.AdditionalSettingsDialog}.
     */
    public static final Pattern DOUBLE_INPUT_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]*");
    //</editor-fold>
    //</editor-fold>

}
