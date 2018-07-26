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

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.*;
import de.gnwi.spices.Spices;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * Class to generate GraphStream objects from Spices objects.
 * 
 * @author Mirco Daniel, Jonas Schaub
 */
public class SpicesToGraphStream {
    
    //<editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * The current node color for graphs created with this class' non-static
     * methods.
     */
    private Color currentNodeColor;
    
    /**
     * The current node shape key for graphs created with this class' non-static
     * methods; Must be able to be found in Constants.Shape's gsKeys.
     */
    private String currentNodeShapeKey;
    
    /**
     * The current node weight for graphs created with this class' non-static
     * methods.
     */
    private double currentNodeWeight;
    
    /**
     * The current edge weight for graphs created with this class' non-static
     * methods.
     */
    private double currentEdgeWeight;
    
    /**
     * Specifies whether the current node weight for graphs created with this
     * class' non-static methods is different from its default.
     */
    private boolean isNodeWeightUpdated;
    
    /**
     * Specifies whether the current edge weight for graphs created with this
     * class' non-static methods is different from its default.
     */
    private boolean isEdgeWeightUpdated;
    
    /**
     * Specifies whether the current node color for graphs created with this 
     * class' non-static methods is different from its default.
     */
    private boolean isNodeColorUpdated;
    
    /**
     * Specifies whether the current node shape for graphs created with this 
     * class' non-static methods is different from its default.
     */
    private boolean isNodeShapeUpdated;
    
    /**
     * The current node size for graphs created with this class' non-static
     * methods.
     */
    private Integer currentNodeSize;
    
    /**
     * Specifies whether the current node size for graphs created with this 
     * class' non-static methods is different from its default.
     */
    private boolean isNodeSizeUpdated;
    
    /**
     * True: Full particles name display, false: Abbreviated particle name display
     */
    private boolean isFullParticleNameDisplay;
    
    /**
     * The String representation of the '.css' file added to the graph as stylesheet.
     */
    private String stylesheet;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public constructor">
    /**
     * Constructor:
     * It creates a new instance and initialises all private fields of this new
     * object with their designated default values.
     * 
     * @author Jonas Schaub
     */
    public SpicesToGraphStream() {
        this.currentNodeColor = null;
        this.currentNodeShapeKey = null;
        this.currentEdgeWeight = Constants.DEFAULT_EDGE_WEIGHT;
        this.currentNodeWeight = Constants.DEFAULT_NODE_WEIGHT;
        this.currentNodeSize = null;
        this.isNodeWeightUpdated = false;
        this.isEdgeWeightUpdated = false;
        this.isNodeColorUpdated = false;
        this.isNodeShapeUpdated = false;
        this.isNodeSizeUpdated = false;
        this.isFullParticleNameDisplay = Constants.IS_FULL_PARTICLE_NAME_DISPLAY_DEFAULT;
        this.stylesheet = "";
        /*getClass().getResource() doesn't work if a '.jar' file is executed.
        Following code works in IDE as well as in '.jar' file.*/
        try {
            InputStream tmpInputStream = this.getClass().getResourceAsStream(Constants.GRAPHSTREAM_STYLESHEET);
            BufferedReader tmpReader = new BufferedReader(new InputStreamReader(tmpInputStream));
            this.stylesheet = tmpReader.lines().collect(Collectors.joining("\n"));
        }
        catch (Exception anException) {
            this.stylesheet = Constants.DEFAULT_CSS_STYLESHEET;
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Method to convert an adjacent array to a nonredundant adjacent array
     * 
     * @param anAdjacentArray to be converted to a nonredundant adjacent array
     * @return nonredundant adjacent array
     * @author Mirco Daniel
     */
    private int[][] convertToNoneRedundantAdjacentArray(int[][] anAdjacentArray) {
        int[][] tmpAdjacentArray = new int[anAdjacentArray.length][];
        int tmpCountArrayItems;
        for (int i = 0; i < anAdjacentArray.length; i++) {
            tmpCountArrayItems = 0;
            for (int j = 0; j < anAdjacentArray[i].length; j++) {
                if (anAdjacentArray[i][j] > i) {
                    tmpCountArrayItems++;
                }
            }
            if (tmpCountArrayItems > 0) {
                tmpAdjacentArray[i] = new int[tmpCountArrayItems];
                int tmpCounter = 0;
                for (int j = 0; j < anAdjacentArray[i].length; j++) {
                    if (anAdjacentArray[i][j] > i) {
                        tmpAdjacentArray[i][tmpCounter++] = anAdjacentArray[i][j];
                    }
                }
            }
        }
        return tmpAdjacentArray;
    }
    
    /**
     * Adds a '.css' stylesheet file and other attributes to the graph. If the 
     * file can not be loaded Constants.DEFAULT_CSS_STYLESHEET is used instead.
     * 
     * @param aGraph to add the attributes to
     * @throws IllegalArgumentException if aGraph is 'null'
     * @author Jonas Schaub
     */
    private void addAttributes(Graph aGraph) throws IllegalArgumentException {
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        aGraph.addAttribute("ui.stylesheet",  this.stylesheet);
        aGraph.addAttribute("ui.quality");
        aGraph.addAttribute("ui.antialias");
    }
    
    /**
     * Builds nodes and edges in the graph corresponding to the 
     * {@link de.gnwi.spices.Spices} object.
     * Also adds special nodes to the particles with [START]  and [END] tags.
     * 
     * @param aGraph a GraphStream graph that should display the 
     * SPICES structure
     * @param aSpices a {@link de.gnwi.spices.Spices} object that should be 
     * displayed in the graph
     * @param aPartNumber the part of the {@link de.gnwi.spices.Spices} 
     * object to be displayed
     * @throws IllegalArgumentException if aGraph or aSpices is 'null' or aPartNumber
     * is invalid
     * @author Jonas Schaub
     */
    private void buildNodesAndEdges(Spices aSpices, Graph aGraph, int aPartNumber) {
        if (aSpices == null) { throw new IllegalArgumentException("aSpices (instance of class Spices) is null."); }
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        if (aSpices.getErrorMessage() != null) {
            return;
        }
        int tmpPartsNumber = aSpices.getPartsOfSpices().length;
        if (aPartNumber > tmpPartsNumber || aPartNumber < 0) {
            throw new IllegalArgumentException("The Spices object has got no "
                    + aPartNumber + "th part!");
        }
        int tmpParticleNumber;
        String[][] tmpNodeID = new String[tmpPartsNumber][];
        String tmpLabel;
        StringBuilder tmpNodeIDBuilder;
        StringBuilder tmpEdgeIDBuilder;
        Node tmpNode;
        URL tmpStartFileName = null;
        URL tmpEndFileName = null;
        tmpParticleNumber = aSpices.getPartsOfSpices()[aPartNumber].getInnerParticles().length;
        tmpNodeID[aPartNumber] = new String[tmpParticleNumber];
        for (int j = 0; j < tmpParticleNumber; j++) {
            // Build nodes
            tmpNodeIDBuilder = new StringBuilder(aSpices.getPartsOfSpices()[aPartNumber].getInnerParticles()[j]);
            tmpNodeIDBuilder.append('(');
            tmpNodeIDBuilder.append(aPartNumber);
            tmpNodeIDBuilder.append(',');
            tmpNodeIDBuilder.append(j);
            tmpNodeIDBuilder.append(')');
            tmpNodeID[aPartNumber][j] = tmpNodeIDBuilder.toString();
            aGraph.addNode(tmpNodeID[aPartNumber][j]);
            tmpNode = aGraph.getNode(tmpNodeID[aPartNumber][j]);
            tmpNode.setAttribute("ui.class", aSpices.getPartsOfSpices()[aPartNumber].getInnerParticles()[j]);
            if (!this.isFullParticleNameDisplay) {
                if (aSpices.getPartsOfSpices()[aPartNumber].getInnerBackboneIndices()[j] != 0) {
                    tmpLabel = aSpices.getPartsOfSpices()[aPartNumber].getInnerParticles()[j].replaceAll("([A-Z][a-zA-Z0-9])[a-zA-Z0-9]{2,8}", "$1.");
                } else {
                    tmpLabel =  aSpices.getPartsOfSpices()[aPartNumber].getInnerParticles()[j].replaceAll("([A-Z][a-zA-Z0-9])[a-zA-Z0-9]{2,6}([a-zA-Z0-9][a-zA-Z0-9])", "$1.$2");
                } 
            } else {
                tmpLabel = aSpices.getPartsOfSpices()[aPartNumber].getInnerParticles()[j];
            }
            if (aSpices.getPartsOfSpices()[aPartNumber].getInnerBackboneIndices()[j] != 0) {
                    tmpLabel = tmpLabel + "'" + aSpices.getPartsOfSpices()[aPartNumber].getInnerBackboneIndices()[j] + "'";
            }
            tmpNode.addAttribute("ui.label", tmpLabel);
        }
        // Build edges
        if (tmpParticleNumber > 1) {
            int[][] tmpAdjacentArray = this.convertToNoneRedundantAdjacentArray(
                    aSpices.getPartsOfSpices()[aPartNumber].getAdjacentArray());
            for (int j = 0; j < tmpAdjacentArray.length; j++) {
                if (tmpAdjacentArray[j] != null) {
                    for (int k = 0; k < tmpAdjacentArray[j].length; k++) {
                        tmpEdgeIDBuilder = new StringBuilder(tmpNodeID[aPartNumber][j]);
                        tmpEdgeIDBuilder.append(tmpNodeID[aPartNumber][tmpAdjacentArray[j][k]]);
                        aGraph.addEdge(tmpEdgeIDBuilder.toString(), tmpNodeID[aPartNumber][j], 
                                tmpNodeID[aPartNumber][tmpAdjacentArray[j][k]]);
                    }
                }
            }
        }
        // Build nodes for annotations
        if (aSpices.getPartsOfSpices()[aPartNumber].getStartParticleIndex() > -1) {
            int tmpStartIndex = aSpices.getPartsOfSpices()[aPartNumber].getStartParticleIndex();
            int tmpEndIndex = aSpices.getPartsOfSpices()[aPartNumber].getEndParticleIndex();
            
            Node tmpStartNode = aGraph.addNode(tmpNodeID[aPartNumber][tmpStartIndex]+"_START");
            Edge tmpEdgeToStartNode = aGraph.addEdge(tmpNodeID[aPartNumber][tmpStartIndex]+"_START_edge", 
                    tmpNodeID[aPartNumber][tmpStartIndex]+"_START", tmpNodeID[aPartNumber][tmpStartIndex]);
            tmpEdgeToStartNode.addAttribute("ui.class", "START");
            if (this.isFullParticleNameDisplay) {
                tmpStartNode.addAttribute("ui.label", "START");
                tmpStartFileName = this.getClass().getResource(Constants.GRAPHSTREAM_START_TAG);
                tmpStartNode.addAttribute("ui.style", "icon: url('" + tmpStartFileName + "');");
                tmpStartNode.addAttribute("ui.class", "START");
            } else {
                tmpStartNode.addAttribute("ui.label", " ");
                tmpStartFileName = this.getClass().getResource(Constants.GRAPHSTREAM_START_TAG_WITH_BORDERS);
                tmpStartNode.addAttribute("ui.style", "fill-image: url('" + tmpStartFileName + "');");
                tmpStartNode.addAttribute("ui.class", "REDSTART");
            }
            
            Node tmpEndNode = aGraph.addNode(tmpNodeID[aPartNumber][tmpEndIndex]+"_END");
            Edge tmpEdgeToEndNode = aGraph.addEdge(tmpNodeID[aPartNumber][tmpEndIndex]+"_END_edge", 
                    tmpNodeID[aPartNumber][tmpEndIndex]+"_END", tmpNodeID[aPartNumber][tmpEndIndex]);
            tmpEdgeToEndNode.addAttribute("ui.class", "END");
            if (this.isFullParticleNameDisplay) {
                tmpEndNode.addAttribute("ui.label", "END");
                tmpEndFileName = this.getClass().getResource(Constants.GRAPHSTREAM_END_TAG);
                tmpEndNode.addAttribute("ui.style", "icon: url('" + tmpEndFileName + "');");
                tmpEndNode.addAttribute("ui.class", "END");
            } else {
                tmpEndNode.addAttribute("ui.label", " ");
                tmpEndFileName = this.getClass().getResource(Constants.GRAPHSTREAM_END_TAG_WITH_BORDERS);
                tmpEndNode.addAttribute("ui.style", "fill-image: url('" + tmpEndFileName + "');");
                tmpEndNode.addAttribute("ui.class", "REDEND");
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Convert a Spices object to match GraphStream Graph object (more parts will separated in more Graph objects)
     * 
     * @param aSpices SPICES object
     * @return GraphStream Graph object(s)
     * @author Mirco Daniel
     */
    public Graph[] convertSpicesToStreamGraph(Spices aSpices) {
        if (aSpices == null || aSpices.getErrorMessage() != null) {
            return new Graph[0];
        }
        int tmpPartsNumber = aSpices.getPartsOfSpices().length;
        int tmpParticleNumber;
        String[][] tmpNodeID = new String[tmpPartsNumber][];
        String tmpLabel;
        StringBuilder tmpNodeIDBuilder;
        StringBuilder tmpEdgeIDBuilder;
        Graph[] tmpGraph = new Graph[tmpPartsNumber];
        SpriteManager spriteManager;
        Node tmpNode;
        URL tmpStartFileName = null;
        URL tmpEndFileName = null;
        String tmpStylesheet = "";
        // getClass.getResource doesn't work, if jar-file is executed.
        // Following code works in IDE as well as in jar-file.
        try {
            InputStream tmpInputStream = this.getClass().getResourceAsStream(Constants.GRAPHSTREAM_STYLESHEET);
            BufferedReader tmpReader = new BufferedReader(new InputStreamReader(tmpInputStream));
            tmpStylesheet = tmpReader.lines().collect(Collectors.joining("\n"));
        }
        catch (Exception anException) {
            tmpStylesheet = Constants.DEFAULT_CSS_STYLESHEET;
        }
        for (int i = 0; i < tmpPartsNumber; i++) {
            tmpGraph[i] = new SingleGraph("Graph" + i);
            tmpGraph[i].addAttribute("ui.stylesheet",  tmpStylesheet);
            tmpGraph[i].addAttribute("ui.quality");
            tmpGraph[i].addAttribute("ui.antialias");
            tmpParticleNumber = aSpices.getPartsOfSpices()[i].getInnerParticles().length;
            tmpNodeID[i] = new String[tmpParticleNumber];
            for (int j = 0; j < tmpParticleNumber; j++) {
                // Build nodes
                tmpNodeIDBuilder = new StringBuilder(aSpices.getPartsOfSpices()[i].getInnerParticles()[j]);
                tmpNodeIDBuilder.append('(');
                tmpNodeIDBuilder.append(i);
                tmpNodeIDBuilder.append(',');
                tmpNodeIDBuilder.append(j);
                tmpNodeIDBuilder.append(')');
                tmpNodeID[i][j] = tmpNodeIDBuilder.toString();
                tmpGraph[i].addNode(tmpNodeID[i][j]);
                tmpNode = tmpGraph[i].getNode(tmpNodeID[i][j]);
                tmpNode.setAttribute("ui.class", aSpices.getPartsOfSpices()[i].getInnerParticles()[j]);
                if (aSpices.getPartsOfSpices()[i].getInnerParticles()[j].length() > 5) {
                    tmpLabel =  aSpices.getPartsOfSpices()[i].getInnerParticles()[j].substring(0, 3) + "..";
                } else {
                    tmpLabel = aSpices.getPartsOfSpices()[i].getInnerParticles()[j];
                }
                if (aSpices.getPartsOfSpices()[i].getInnerBackboneIndices()[j] != 0) {
                    tmpLabel = tmpLabel + "'" + aSpices.getPartsOfSpices()[i].getInnerBackboneIndices()[j] + "'";
                }
                tmpNode.addAttribute("ui.label", tmpLabel);
            }
            // Build edges
            if (tmpParticleNumber > 1) {
                int[][] tmpAdjacentArray = this.convertToNoneRedundantAdjacentArray(aSpices.getPartsOfSpices()[i].getAdjacentArray());
                for (int j = 0; j < tmpAdjacentArray.length; j++) {
                    if (tmpAdjacentArray[j] != null) {
                        for (int k = 0; k < tmpAdjacentArray[j].length; k++) {
                            tmpEdgeIDBuilder = new StringBuilder(tmpNodeID[i][j]);
                            tmpEdgeIDBuilder.append(tmpNodeID[i][tmpAdjacentArray[j][k]]);
                            tmpGraph[i].addEdge(tmpEdgeIDBuilder.toString(), tmpNodeID[i][j], tmpNodeID[i][tmpAdjacentArray[j][k]]);
                        }
                    }
                }
            }
        }
        // Link START- and END-Tags with corrensponding nodes
        for (int i = 0; i < tmpPartsNumber; i++) {
            if (aSpices.getPartsOfSpices()[i].getStartParticleIndex() > -1) {
                int tmpStartIndex = aSpices.getPartsOfSpices()[i].getStartParticleIndex();
                int tmpEndIndex = aSpices.getPartsOfSpices()[i].getEndParticleIndex();
                spriteManager = new SpriteManager(tmpGraph[i]);
                Sprite spriteStart = spriteManager.addSprite("START");
                spriteStart.attachToNode(tmpNodeID[i][tmpStartIndex]);
                spriteStart.setPosition(-0.1, 0.0, 0.0);
                tmpStartFileName = this.getClass().getResource(Constants.GRAPHSTREAM_START_TAG);
                spriteStart.addAttribute("ui.style", "fill-image: url('" + tmpStartFileName + "');");
                Sprite spriteEnd = spriteManager.addSprite("END");
                spriteEnd.attachToNode(tmpNodeID[i][tmpEndIndex]);
                spriteEnd.setPosition(-0.1, 0.0, 0.0);
                tmpEndFileName = this.getClass().getResource(Constants.GRAPHSTREAM_END_TAG);
                spriteEnd.addAttribute("ui.style", "fill-image: url('" + tmpEndFileName + "');");
            }
        }
        return tmpGraph;
    }
    
    /**
     * Gives the current node color.
     * 
     * @return the current node color
     * @author Jonas Schaub
     */
    public Color getCurrentDefaultNodeColor() {
        return this.currentNodeColor;
    }
    
    /**
     * Gives the current node shape key.
     * 
     * @return the current node shape key
     * @author Jonas Schaub
     */
    public String getCurrentNodeShapeKey() {
        return this.currentNodeShapeKey;
    }
    
    /**
     * Gives the current edge weight.
     * 
     * @return the current edge weight
     * @author Jonas Schaub
     */
    public double getCurrentEdgeWeight() {
        return this.currentEdgeWeight;
    }
    
    /**
     * Gives the current node weight.
     * 
     * @return the current node weight
     * @author Jonas Schaub
     */
    public double getCurrentNodeWeight() {
        return this.currentNodeWeight;
    }
    
    /**
     * Gives the current node size.
     * 
     * @return the current node size
     * @author Jonas Schaub
     */
    public Integer getCurrentNodeSize() {
        return this.currentNodeSize;
    }
    
    /**
     * Gives the String representation of the '.css' file added to the graph as stylesheet.
     * 
     * @return the String representation of the '.css' file added to the graph as stylesheet.
     */
    public String getStylesheet() {
        return this.stylesheet;
    }
    
    /**
     * Resets the node size of aGraph to its default value (as specified in the 
     * stylesheet) and resets the according field in this object to its default
     * value.
     * 
     * @param aGraph whose node size has been dynamically altered
     * @throws IllegalArgumentException if aGraph is 'null'
     * @author Jonas Schaub
     */
    public void resetNodeSize(Graph aGraph) throws IllegalArgumentException {
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        for (Node tmpNode : aGraph.getEachNode()) {
            if (tmpNode.hasAttribute("ui.size")) {
                tmpNode.removeAttribute("ui.size");
            }
        }
        this.isNodeSizeUpdated = false;
        this.currentNodeSize = null;
    }
    
    /**
     * Resets the node color of aGraph to its default value (as specified in the 
     * stylesheet) and resets the according field in this object to its default
     * value.
     * 
     * @param aGraph whose node color has been dynamically altered
     * @throws IllegalArgumentException if aGraph is 'null'
     * @author Jonas Schaub
     */
    public void resetNodeColor(Graph aGraph) throws IllegalArgumentException {
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        for (Node tmpNode : aGraph) {
            if (tmpNode.hasAttribute("ui.color"))
                tmpNode.removeAttribute("ui.color");
        }
        this.isNodeColorUpdated = false;
        this.currentNodeColor = null;
    }
    
    /**
     * Resets the node shape of aGraph to its default value (as specified in the 
     * stylesheet) and resets the according field in this object to its default
     * value.
     * 
     * @param aGraph whose node shape has been dynamically altered
     * @throws IllegalArgumentException if aGraph is 'null'
     * @author Jonas Schaub
     */
    public void resetNodeShape(Graph aGraph) throws IllegalArgumentException {
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        for (Node tmpNode : aGraph) {
            if (tmpNode.hasAttribute("ui.style"))
                tmpNode.removeAttribute("ui.style");
        }
        this.isNodeShapeUpdated = false;
        this.currentNodeShapeKey = null;
    }
    
    /**
     * Resets the node weight of aGraph to its default value (as specified in the 
     * stylesheet) and resets the according field in this object to its default
     * value.
     * 
     * @param aGraph whose node weight has been dynamically altered
     * @throws IllegalArgumentException if aGraph is 'null'
     * @author Jonas Schaub
     */
    public void resetNodeWeight(Graph aGraph) throws IllegalArgumentException {
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        for (Node tmpNode : aGraph) {
            if (tmpNode.hasAttribute("layout.weight"))
                tmpNode.removeAttribute("layout.weight");
        }
        this.isNodeWeightUpdated = false;
        this.currentNodeWeight = Constants.DEFAULT_NODE_WEIGHT;
    }
    
    /**
     * Resets the edge weight of aGraph to its default value (as specified in the 
     * stylesheet) and resets the according field in this object to its default
     * value.
     * 
     * @param aGraph whose edge weight has been dynamically altered
     * @throws IllegalArgumentException if aGraph is 'null'
     * @author Jonas Schaub
     */
    public void resetEdgeWeight(Graph aGraph) throws IllegalArgumentException {
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        for (Edge tmpEdge : aGraph.getEachEdge()) {
            if (tmpEdge.hasAttribute("layout.weight"))
                tmpEdge.removeAttribute("layout.weight");
        }
        this.isEdgeWeightUpdated = false;
        this.currentEdgeWeight = Constants.DEFAULT_EDGE_WEIGHT;
    }
    
    /**
     * Sets the node shape of aGraph and sets the according field in this object.
     * 
     * @param aGraph whose node shape should be altered
     * @param aKey a GraphStream node shape key from a 
     * {@link de.gnwi.spicestographstream.Constants.Shape} enum object; If 'null'
     * or empty the node shape will be reset to its default
     * @throws IllegalArgumentException if aGraph is 'null' or aKey can not be found 
     * in {@link de.gnwi.spicestographstream.Constants.Shape}
     * @author Jonas Schaub
     */
    public void setNodeShape(Graph aGraph, String aKey) throws IllegalArgumentException {
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        if (aKey == null || aKey.isEmpty()) {
            this.resetNodeShape(aGraph);
            return;
        }
        boolean tmpIsInShape = false;
        for (Constants.Shape tmpShape : Constants.Shape.values()) {
            if (aKey.equals(tmpShape.getGsKey())) {
                tmpIsInShape = true;
                break;
            }
        }
        if (!tmpIsInShape) {
            throw new IllegalArgumentException("Given shape key is not in Constants.Shape!");
        }
        for (Node tmpNode : aGraph) {
            tmpNode.addAttribute("ui.style", "shape: " + aKey + ";");
        }
        this.isNodeShapeUpdated = true;
        this.currentNodeShapeKey = aKey;
    }
    
    /**
     * Sets the node color of aGraph and sets the according field in this object.
     * 
     * @param aGraph whose node color should be altered
     * @param aColor to paint the graph's nodes in; If it is 'null' the node color
     * of aGraph will be reset to its default value.
     * @throws IllegalArgumentException if aGraph is 'null'
     * @author Jonas Schaub
     */
    public void setNodeColor(Graph aGraph, Color aColor) throws IllegalArgumentException {
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        if (aColor == null) {
            this.resetNodeColor(aGraph);
            return;
        }
        for (Node tmpNode : aGraph) {
            tmpNode.addAttribute("ui.color", aColor);
        }
        this.isNodeColorUpdated = true;
        this.currentNodeColor = aColor;
    }
    
    /**
     * Sets the node weight of aGraph and sets the according field in this object.
     * 
     * @param aGraph whose node weight should be altered
     * @param aWeight to give to the graph's nodes
     * @throws IllegalArgumentException if aGraph is 'null'
     * @author Jonas Schaub
     */
    public void setNodeWeight(Graph aGraph, double aWeight) throws IllegalArgumentException {
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        for (Node tmpNode : aGraph) {
            tmpNode.addAttribute("layout.weight", aWeight);
        }
        this.isNodeWeightUpdated = true;
        this.currentNodeWeight = aWeight;
    }
    
    /**
     * Sets the edge weight of aGraph and sets the according field in this object.
     * 
     * @param aGraph whose edge weight should be altered
     * @param aWeight to give to the graph's edges
     * @throws IllegalArgumentException if aGraph is 'null'
     * @author Jonas Schaub
     */
    public void setEdgeWeight(Graph aGraph, double aWeight) throws IllegalArgumentException {
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        for (Edge tmpEdge : aGraph.getEachEdge()) {
            tmpEdge.addAttribute("layout.weight", aWeight);
        }
        this.isEdgeWeightUpdated = true;
        this.currentEdgeWeight = aWeight;
    }
    
    /**
     * Sets the node size of aGraph and sets the according field in this object.
     * Note: This will not work unless 'size-mode: dyn-size;' is specified
     * for the nodes in the graph's stylesheet!
     * 
     * @param aGraph whose edge weight should be altered
     * @param aSize to give to the graph's nodes
     * @throws IllegalArgumentException if aGraph is 'null' or aSize is zero or
     * negative
     * @author Jonas Schaub
     */
    public void setNodeSize(Graph aGraph, int aSize) throws IllegalArgumentException {
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        if (aSize < 1)
            throw new IllegalArgumentException("The node size can not be zero or negative!");
        for (Node tmpNode : aGraph) {
            tmpNode.addAttribute("ui.size", aSize);
        }
        this.isNodeSizeUpdated = true;
        this.currentNodeSize = aSize;
    }
    
    /**
     * Specifies whether the particle names should be written out in full on the
     * graph's nodes.
     * 
     * @param aShouldBeFullParticleNameDisplay that determines whether the particle names should be written out
     * in full on the graph's nodes
     * @author Jonas Schaub
     */
    public void setFullParticleNameDisplay(boolean aShouldBeFullParticleNameDisplay) {
        this.isFullParticleNameDisplay = aShouldBeFullParticleNameDisplay;
    }
    
    /**
     * Changes a GraphStream {@link org.graphstream.graph.Graph} object to match the given
     * {@link de.gnwi.spices.Spices} object. The graph is not reinstantiated but cleared and then
     * filled with elements and attributes according to the {@link de.gnwi.spices.Spices} object so that
     * a GraphStream {@link org.graphstream.ui.view.Viewer} observing the 
     * graph does not loose connection to it.
     * The different parts of the {@link de.gnwi.spices.Spices} object are all combined in one graph
     * (but unconnected). If the {@link de.gnwi.spices.Spices}' error message is not 'null' the graph
     * is only cleared. Node color, node shape, edge weight, node weight and
     * node size are set according to the matching object fields if they
     * have been updated.
     *
     * @param aGraph a GraphStream {@link org.graphstream.graph.Graph} that 
     * should display the {@link de.gnwi.spices.Spices} structure
     * @param aSpices a {@link de.gnwi.spices.Spices} object that should be displayed in the graph
     * @throws IllegalArgumentException if aGraph or aSpices is 'null'
     * @author Jonas Schaub
     */
    public void updateSpicesGraph(Graph aGraph, Spices aSpices) throws IllegalArgumentException {
        if (aSpices == null) { throw new IllegalArgumentException("aSpices (instance of class Spices) is null."); }
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        aGraph.clear();
        if (aSpices.getErrorMessage() != null) {
            return;
        }
        int tmpPartsNumber = aSpices.getPartsOfSpices().length;
        this.addAttributes(aGraph);
        for (int i = 0; i < tmpPartsNumber; i++) {
            this.buildNodesAndEdges(aSpices, aGraph, i);
        }
        if (this.isNodeColorUpdated) {
            for (Node tmpNode : aGraph) {
                tmpNode.addAttribute("ui.color", this.currentNodeColor);
            }
        }
        if (this.isNodeShapeUpdated) {
            for (Node tmpNode : aGraph) {
                tmpNode.addAttribute("ui.style", "shape: " + this.currentNodeShapeKey + ";");
            }
        }
        if (this.isEdgeWeightUpdated) {
            for (Edge tmpEdge : aGraph.getEachEdge()) {
                tmpEdge.addAttribute("layout.weight", this.currentEdgeWeight);
            }
        }
        if (this.isNodeWeightUpdated) {
            for (Node tmpNode : aGraph) {
                tmpNode.addAttribute("layout.weight", this.currentNodeWeight);
            }
        }
        if (this.isNodeSizeUpdated) {
            for (Node tmpNode : aGraph) {
                tmpNode.addAttribute("ui.size", this.currentNodeSize);
            }
        }
    }
    
    /**
     * Changes a GraphStream {@link org.graphstream.graph.Graph} object to match the given
     * {@link de.gnwi.spices.Spices} object. The graph is not reinstantiated but cleared and then
     * filled with elements and attributes according to the {@link de.gnwi.spices.Spices} object so that
     * a GraphStream {@link org.graphstream.ui.view.Viewer} observing the 
     * graph does not loose connection to it.
     * Only the part specified by aPartNumber is displayed in the graph.
     * If the {@link de.gnwi.spices.Spices}' error message is not 'null' the graph
     * is only cleared. Node color, node shape, edge weight, node weight and
     * node size are set according to the matching object fields if they
     * have been updated.
     *
     * @param aGraph a GraphStream {@link org.graphstream.graph.Graph} 
     * that should display the {@link de.gnwi.spices.Spices} structure
     * @param aSpices a {@link de.gnwi.spices.Spices} object that should be displayed in the graph
     * @param aPartNumber the part of the {@link de.gnwi.spices.Spices} object to be displayed
     * @throws IllegalArgumentException if aGraph or aSpices is 'null' or aPartNumber
     * is invalid
     * @author Jonas Schaub
     */
    public void updateSpicesGraph(Graph aGraph, Spices aSpices, int aPartNumber)
            throws IllegalArgumentException {
        if (aSpices == null) { throw new IllegalArgumentException("aSpices (instance of class Spices) is null."); }
        if (aGraph == null) { throw new IllegalArgumentException("aGraph (instance of class Graph) is null."); }
        aGraph.clear();
        if (aSpices.getErrorMessage() != null) {
            return;
        }
        int tmpPartsNumber = aSpices.getPartsOfSpices().length;
        if (aPartNumber > tmpPartsNumber || aPartNumber < 0) {
            throw new IllegalArgumentException("The Spices object has got no "
                    + aPartNumber + "th part!");
        }
        this.addAttributes(aGraph);
        this.buildNodesAndEdges(aSpices, aGraph, aPartNumber);
        if (this.isNodeColorUpdated) {
            for (Node tmpNode : aGraph) {
                tmpNode.addAttribute("ui.color", this.currentNodeColor);
            }
        }
        if (this.isNodeShapeUpdated) {
            for (Node tmpNode : aGraph) {
                tmpNode.addAttribute("ui.style", "shape: " + this.currentNodeShapeKey + ";");
            }
        }
        if (this.isEdgeWeightUpdated) {
            for (Edge tmpEdge : aGraph.getEachEdge()) {
                tmpEdge.addAttribute("layout.weight", this.currentEdgeWeight);
            }
        }
        if (this.isNodeWeightUpdated) {
            for (Node tmpNode : aGraph) {
                tmpNode.addAttribute("layout.weight", this.currentNodeWeight);
            }
        }
        if (this.isNodeSizeUpdated) {
            for (Node tmpNode : aGraph) {
                tmpNode.addAttribute("ui.size", this.currentNodeSize);
            }
        }
    }
    //</editor-fold>

}