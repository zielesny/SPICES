/**
 * SpicesToGraphStream (SPICES to GraphStream)
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

package de.gnwi.spicestographstream;

/**
 * Constants
 * 
 * @author Jonas Schaub
 */
public abstract class Constants {
    
    //<editor-fold defaultstate="collapsed" desc="Enums">
    /**
     * An enum for the GraphStream node shape options
     * (see {@link org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Shape})
     * which gives the keyword that must be added as a 'ui.style' attribute to a node.
     */
    public enum Shape {
        
        /**
         * Constant for a circular node shape.
         */
        CIRCLE ("circle"),
        
        /**
         * Constant for a rectangular node shape.
         */
        BOX ("box"),
        
        /**
         * Constant for a rectangular node shape with round corners.
         */
        ROUNDED_BOX ("rounded-box"),
        
        /**
         * Constant for a rhombic node shape.
         */
        DIAMOND ("diamond"),
        
        /**
         * Constant for a cross-shaped node shape.
         */
        CROSS ("cross");
        
        /**
         * The GraphStream style key of each node shape.
         */
        private final String gsKey;
        
        /**
         * Constructor:
         * It initialises a new Constant of type Constants.Shape.
         *
         * @param aGsKey The GraphStream style key of the new node shape
         */
        Shape(String aGsKey) {
            this.gsKey = aGsKey;
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
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="String constants">
    /**
     * The stylesheet to be added to the graph when the external resource
     * can not be found.
     */
    public static final String DEFAULT_CSS_STYLESHEET = 
        "node {\n" +
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
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="Double constants">
    /**
     * The default node weight value.
     * Note: There does not exist a real default value in GraphStream for this
     * (the default here is when there is no 'layout.weight' attribute stored
     * on the graph) so this default value is defined as Double.NaN.
     */
    public static final Double DEFAULT_NODE_WEIGHT = Double.NaN;
    
    /**
     * The default edge weight value.
     * Note: There does not exist a real default value in GraphStream for this
     * (the default here is when there is no 'layout.weight' attribute stored
     * on the graph) so this default value is defined as Double.NaN.
     */
    public static final Double DEFAULT_EDGE_WEIGHT = Double.NaN;
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="Boolean constants">
    /**
     * The default value for the field specifying whether the particle names
     * should be written out in full on the graph's nodes.
     */
    public static final boolean IS_FULL_PARTICLE_NAME_DISPLAY_DEFAULT = false;
    //</editor-fold>
    //
    //<editor-fold defaultstate="collapsed" desc="Resource related strings">
    /**
     * Pathname of the cascading style sheet file for the graph attributes
     */
    public static final String GRAPHSTREAM_STYLESHEET = "/de/gnwi/spicestographstream/resource/SpicesToGraphStream.css";

    /**
     * A png file to fill the start node
     */
    public static final String GRAPHSTREAM_START_TAG = "/de/gnwi/spicestographstream/resource/START.png";

    /**
     * A png file to fill the start node when the particle names are reduced
     */
    public static final String GRAPHSTREAM_START_TAG_WITH_BORDERS = "/de/gnwi/spicestographstream/resource/START_border.png";
    
    /**
     * A png file to fill the end node
     */
    public static final String GRAPHSTREAM_END_TAG = "/de/gnwi/spicestographstream/resource/END.png";
    
    /**
     * A png file to fill the end node when the particle names are reduced
     */
    public static final String GRAPHSTREAM_END_TAG_WITH_BORDERS = "/de/gnwi/spicestographstream/resource/END_border.png";
    //</editor-fold>

}
