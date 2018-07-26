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
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.graphstream.ui.graphicGraph.stylesheet.Style;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.graphicGraph.stylesheet.StyleSheet;
import org.graphstream.ui.graphicGraph.stylesheet.Values;

/**
 * A serializable class that preserves application and graph settings made by the user
 * for the next start of the application. It also reads the stylesheet attached 
 * to the graph to get some necessary informations for the application.
 * 
 * @author Jonas Schaub
 */
public class UserPreferences implements Serializable {
    
    //<editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * The graph's node color as set in the stylesheet.
     */
    private Color cssNodeColor;
    
    /**
     * The graph's node shape key as set in the stylesheet. It must be able
     * to be found in the Constants.Shape enum's gsKeys.
     */
    private String cssNodeShapeKey;
    
    /**
     * The current node color as set by the user or the stylesheet (after
     * a reset).
     */
    private Color currentNodeColor;
    
    /**
     * The current node shape key as set by the user or the stylesheet (after
     * a reset). It must be able to be found in the Constants.Shape enum's gsKeys.
     */
    private String currentNodeShapeKey;
    
    /**
     * The current error color as set by the user or Constants.DEFAULT_ERROR_COLOR
     * (after a reset).
     */
    private Color currentErrorColor;
    
    /**
     * The current 'valid' color as set by the user or Constants.DEFAULT_VALID_COLOR
     * (after a reset).
     */
    private Color currentValidColor;
    
    /**
     * The current node weight as set by the user or Constants.DEFAULT_NODE_WEIGHT
     * (after a reset).
     */
    private Double currentNodeWeight;
    
    /**
     * The current edge weight as set by the user or Constants.DEFAULT_NODE_WEIGHT
     * (after a reset).
     */
    private Double currentEdgeWeight;
    
    /**
     * The graph's node size as set in the stylesheet.
     */
    private int cssNodeSize;
    
    /**
     * The current node size as set by the user or the stylesheet (after
     * a reset).
     */
    private int currentNodeSize;
    
    /**
     * Determines whether the particle names should be written out in full in the graph
     * as set by the user or Constants.IS_FULL_PARTICLE_NAME_DISPLAY_DEFAULT (after a reset).
     */
    private boolean isFullParticleNameDisplay;
    
    /**
     * The graph's node size mode as set by the stylesheet. Determines whether
     * the node size can be set dynamically by the user (in case of DYN_SIZE) or
     * if the nodes fit automatically to their labels (particle names) (in case of
     * FIT).
     */
    private StyleConstants.SizeMode cssNodeSizeMode;
    
    /**
     * The directory the user set to save the exported image files to.
     */
    private String imageFileDir;
    
    /**
     * The String representation of the css file added to the Spices graph as stylesheet.
     */
    private String stylesheet;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public constructor">
    /**
     * Constructor:
     * It invokes 
     * {@link de.gnwi.spicesviewer.UserPreferences#loadDefaultsFromCSS()} 
     * first and then sets the not stylesheet-dependent fields to their default 
     * values. 
     * 
     * @param aStylesheet the String representation of the '.css' file added to the 
     * Spices graph as stylesheet in the SpicesToGraphStreamModel; If its 'null'
     * the stylesheet will be set to a default css stylesheet.
     */
    public UserPreferences(String aStylesheet) {
        if (aStylesheet == null)
            aStylesheet = Constants.DEFAULT_CSS_STYLESHEET;
        this.stylesheet = aStylesheet;
        this.loadDefaultsFromCSS();
        this.currentNodeColor = this.cssNodeColor;
        this.currentNodeShapeKey = this.cssNodeShapeKey;
        this.currentNodeSize = this.cssNodeSize;
        this.currentNodeWeight = Constants.DEFAULT_NODE_WEIGHT;
        this.currentEdgeWeight = Constants.DEFAULT_EDGE_WEIGHT;
        this.currentErrorColor = Constants.DEFAULT_ERROR_COLOR;
        this.currentValidColor = Constants.DEFAULT_VALID_COLOR;
        this.isFullParticleNameDisplay = Constants.IS_FULL_PARTICLE_NAME_DISPLAY_DEFAULT;
        this.imageFileDir = Constants.DEFAULT_USER_IMAGE_FILE_DIR;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Sets the fields of this object holding different default values 
     * according to the '.css' stylesheet in use in the application.
     * If loading the '.css' file fails 
     * {@link de.gnwi.spicesviewer.Constants#DEFAULT_CSS_STYLESHEET} will 
     * be used. If any other step of reading the stylesheet fails the currently
     * read field will be set to a default value.
     * The loaded node size can not be higher than 
     * {@link de.gnwi.spicesviewer.Constants#NODE_SIZE_SLIDER_MAX}
     * or lower than {@link de.gnwi.spicesviewer.Constants#NODE_SIZE_SLIDER_MIN}.
     */
    public void loadDefaultsFromCSS() {
        StyleSheet tmpStyleSheetObject = null;
        try {
            tmpStyleSheetObject = new StyleSheet();
            tmpStyleSheetObject.parseFromString(this.stylesheet);
        }
        catch (Exception anException) {
            String tmpErrorMessage = Language.getString("PROBLEM_LOADING_CSS");
            JOptionPane.showMessageDialog(null, tmpErrorMessage,
                    Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
            this.stylesheet = Constants.DEFAULT_CSS_STYLESHEET;
            tmpStyleSheetObject = new StyleSheet();
            try {
                tmpStyleSheetObject.parseFromString(this.stylesheet);
            } catch (IOException anIOException) {
                /*As the stylesheet is loaded from a proven Constants value
                there should be no exception thrown here*/
            }
        }
        Style tmpDefaultNodeStyle = tmpStyleSheetObject.getDefaultNodeStyle();
        Color tmpFillColor = null;
        String tmpShapeKey = "";
        StyleConstants.Shape tmpNodeShape = null;
        StyleConstants.SizeMode tmpNodeSizeMode = null;
        int tmpMinSizeValue = 0;
        if (tmpDefaultNodeStyle == null) {
            tmpFillColor = Constants.DEFAULT_NODE_COLOR;
            tmpShapeKey = Constants.Shape.ROUNDED_BOX.getGsKey();
            tmpNodeSizeMode = StyleConstants.SizeMode.FIT;
            tmpMinSizeValue = Constants.NODE_SIZE_SLIDER_MIN;
        } else {
            tmpFillColor = tmpDefaultNodeStyle.getFillColor(0);
            if (tmpFillColor == null) {
                tmpFillColor = Constants.DEFAULT_NODE_COLOR;
            }
            tmpNodeShape = tmpDefaultNodeStyle.getShape();
            if (tmpNodeShape == null) {
                tmpShapeKey = Constants.Shape.ROUNDED_BOX.getGsKey();
            } else {
                tmpShapeKey = tmpNodeShape.toString();
            }
            for (Constants.Shape tmpShape : Constants.Shape.values()) {
                if (tmpShape.name().equals(tmpShapeKey)) {
                    tmpShapeKey = tmpShape.getGsKey();
                    break;
                }
                tmpShapeKey = Constants.Shape.ROUNDED_BOX.getGsKey();
            }
            tmpNodeSizeMode = tmpDefaultNodeStyle.getSizeMode();
            if (tmpNodeSizeMode == null) {
                tmpNodeSizeMode = StyleConstants.SizeMode.FIT;
            }
            Values tmpSizeValues = tmpDefaultNodeStyle.getSize();
            if (tmpSizeValues.getValueCount() > 0) {
                ArrayList<Double> tmpSizeValuesList = new ArrayList<>();
                Iterator tmpIterator = tmpSizeValues.iterator();
                while (tmpIterator.hasNext()) {
                    tmpSizeValuesList.add((Double) tmpIterator.next());
                }
                int tmpIndexOfMin = tmpSizeValuesList.indexOf(Collections.min(tmpSizeValuesList));
                tmpMinSizeValue = tmpSizeValuesList.get(tmpIndexOfMin).intValue();
                if (tmpMinSizeValue < Constants.NODE_SIZE_SLIDER_MIN) {
                    tmpMinSizeValue = Constants.NODE_SIZE_SLIDER_MIN;
                }
                if (tmpMinSizeValue > Constants.NODE_SIZE_SLIDER_MAX) {
                    tmpMinSizeValue = Constants.NODE_SIZE_SLIDER_MAX;
                }
            } else {
                tmpMinSizeValue = Constants.NODE_SIZE_SLIDER_MIN;
            }
        }
        this.cssNodeColor = tmpFillColor;
        this.cssNodeShapeKey = tmpShapeKey;
        this.cssNodeSizeMode = tmpNodeSizeMode;
        this.cssNodeSize = tmpMinSizeValue;
    }
    
    /**
     * Gives the current node color .
     * 
     * @return the current node color
     */
    public Color getCurrentNodeColor() {
        return this.currentNodeColor;
    }
    
    /**
     * Gives the current node shape key.
     * 
     * @return the current node shape key (not 'null')
     */
    public String getCurrentNodeShapeKey() {
        return this.currentNodeShapeKey;
    }
    
    /**
     * Gives the current error color.
     * 
     * @return the current error color (not 'null')
     */
    public Color getCurrentErrorColor() {
        return this.currentErrorColor;
    }
    
    /**
     * Gives the current 'valid' color.
     * 
     * @return the current 'valid' color (not 'null')
     */
    public Color getCurrentValidColor() {
        return this.currentValidColor;
    }
    
    /**
     * Gives the current node weight.
     *  
     * @return the current node weight (may be Double.NaN, see 
     * {@link de.gnwi.spicesviewer.Constants#DEFAULT_NODE_WEIGHT})
     */
    public Double getCurrentNodeWeight() {
        return this.currentNodeWeight;
    }
    
    /**
     * Gives the current edge weight.
     * 
     * @return the current edge weight (may be Double.NaN, see 
     * {@link de.gnwi.spicesviewer.Constants#DEFAULT_EDGE_WEIGHT})
     */
    public Double getCurrentEdgeWeight() {
        return this.currentEdgeWeight;
    }
    
    /**
     * Gives the current node size.
     * 
     * @return the current node size
     */
    public int getCurrentNodeSize() {
        return this.currentNodeSize;
    }
    
    /**
     * Gives the node size mode as set in the stylesheet.
     * 
     * @return the node size mode as set in the stylesheet as a GraphStream
     * {@link org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.SizeMode}
     */
    public StyleConstants.SizeMode getCSSNodeSizeMode() {
        return this.cssNodeSizeMode;
    }
    
    /**
     * Gives the directory the user set to save the image files to.
     * 
     * @return the directory the user set to save the image files to
     */
    public String getImageFileDir() {
        return this.imageFileDir;
    }
    
    /**
     * Gives the Spices graph's node size as set in its stylesheet.
     * 
     * @return the default node size
     */
    public int getCSSNodeSize() {
        return this.cssNodeSize;
    }
    
    /**
     * Gives the Spices graph's node shape as set in its stylesheet.
     * 
     * @return the default node shape
     */
    public String getCSSNodeShapeKey() {
        return this.cssNodeShapeKey;
    }
    
    /**
     * Specifies Whether the particle names should be written out in full.
     * 
     * @return the current value of the field specifying whether the particle 
     * names should be written out in full on the graph's nodes
     */
    public boolean isFullParticleNameDisplay() {
        return this.isFullParticleNameDisplay;
    }
    
    /**
     * Resets the current node color to its default value as set in the loaded 
     * stylesheet.
     */
    public void resetNodeColor() {
        this.currentNodeColor = this.cssNodeColor;
    }
    
    /**
     * Resets the current node shape key to its default value as set in the loaded 
     * stylesheet.
     */
    public void resetNodeShapeKey() {
        this.currentNodeShapeKey = this.cssNodeShapeKey;
    }
    
    /**
     * Resets the current error color to 
     * {@link de.gnwi.spicesviewer.Constants#DEFAULT_ERROR_COLOR}.
     */
    public void resetErrorColor() {
        this.currentErrorColor = Constants.DEFAULT_ERROR_COLOR;
    }
    
    /**
     * Resets the current 'valid' color to 
     * {@link de.gnwi.spicesviewer.Constants#DEFAULT_VALID_COLOR}.
     */
    public void resetValidColor() {
        this.currentValidColor = Constants.DEFAULT_VALID_COLOR;
    }
    
    /**
     * Resets the current node weight to 
     * {@link de.gnwi.spicesviewer.Constants#DEFAULT_NODE_WEIGHT}.
     */
    public void resetNodeWeight() {
        this.currentNodeWeight = Constants.DEFAULT_NODE_WEIGHT;
    }
    
    /**
     * Resets the current edge weight to 
     * {@link de.gnwi.spicesviewer.Constants#DEFAULT_EDGE_WEIGHT}.
     */
    public void resetEdgeWeight() {
        this.currentEdgeWeight = Constants.DEFAULT_EDGE_WEIGHT;
    }
    
    /**
     * Resets the current node size to its default value as set in the loaded 
     * stylesheet.
     */
    public void resetNodeSize() {
        this.currentNodeSize = this.cssNodeSize;
    }
    
    /**
     * Resets the field specifying whether the particle 
     * names should be written out in full on the graph's nodes to 
     * {@link de.gnwi.spicesviewer.Constants#IS_FULL_PARTICLE_NAME_DISPLAY_DEFAULT}.
     */
    public void resetParticleNamesWrittenOutInFull() {
        this.isFullParticleNameDisplay = Constants.IS_FULL_PARTICLE_NAME_DISPLAY_DEFAULT;
    }
    
    /**
     * Resets the directory the user set to save the exported image files to back to
     * {@link de.gnwi.spicesviewer.Constants#DEFAULT_USER_IMAGE_FILE_DIR}.
     */
    public void resetImageFileDir() {
        this.imageFileDir = Constants.DEFAULT_USER_IMAGE_FILE_DIR;
    }
    
    /**
     * Sets the current node color.
     * 
     * @param aColor a Color to be the current node color
     * @throws IllegalArgumentException if aColor is 'null'
     */
    public void setCurrentNodeColor(Color aColor) throws IllegalArgumentException {
        if (aColor == null) { throw new IllegalArgumentException("aColor (instance of class Color) is null."); }
        this.currentNodeColor = aColor;
    }
    
    /**
     * Sets the current node shape key.
     * 
     * @param aShapeKey a GraphStream shape key from one of the enum objects 
     * defined in {@link de.gnwi.spicesviewer.Constants.Shape}
     * @throws IllegalArgumentException if aShapeKey can not be found in 
     * {@link de.gnwi.spicesviewer.Constants.Shape} or is 'null'
     */
    public void setCurrentNodeShapeKey(String aShapeKey) throws IllegalArgumentException {
        if (aShapeKey == null) { throw new IllegalArgumentException("aShapeKey (instance of class String) is null."); }
        boolean tmpIsInShape = false;
        for (Constants.Shape tmpShape : Constants.Shape.values()) {
            if (aShapeKey.equals(tmpShape.getGsKey())) {
                tmpIsInShape = true;
                break;
            }
        }
        if (!tmpIsInShape) {
            throw new IllegalArgumentException("Given shape key is not in Constants.Shape!");
        }
        this.currentNodeShapeKey = aShapeKey;
    }
    
    /**
     * Sets the current error color.
     * 
     * @param aColor a Color to be the current error color
     * @throws IllegalArgumentException if aColor is 'null'
     */
    public void setCurrentErrorColor(Color aColor) throws IllegalArgumentException {
        if (aColor == null) { throw new IllegalArgumentException("aColor (instance of class Color) is null."); }
        this.currentErrorColor = aColor;
    }
    
    /**
     * Sets the current 'valid' color.
     * 
     * @param aColor a Color to be the current 'valid' color
     * @throws IllegalArgumentException if aColor is 'null'
     */
    public void setCurrentValidColor(Color aColor) throws IllegalArgumentException {
        if (aColor == null) { throw new IllegalArgumentException("aColor (instance of class Color) is null."); }
        this.currentValidColor = aColor;
    }
    
    /**
     * Sets the current node weight.
     * 
     * @param aDouble to be the current node weight
     * @throws IllegalArgumentException if aDouble is 'null', or NaN, or infinite
     */
    public void setCurrentNodeWeight(Double aDouble) throws IllegalArgumentException {
        if (aDouble == null) { throw new IllegalArgumentException("aDouble (instance of class Double) is null."); }
        if (aDouble.isInfinite() || aDouble.isNaN())
            throw new IllegalArgumentException("aDouble is infinite or NaN!");
        this.currentNodeWeight = aDouble;
    }
    
    /**
     * Sets the current edge weight.
     * 
     * @param aDouble to be the current edge weight
     * @throws IllegalArgumentException if aDouble is 'null', or NaN, or infinite
     */
    public void setCurrentEdgeWeight(Double aDouble) throws IllegalArgumentException {
        if (aDouble == null) { throw new IllegalArgumentException("aDouble (instance of class Double) is null."); }
        if (aDouble.isInfinite() || aDouble.isNaN())
            throw new IllegalArgumentException("aDouble is infinite or NaN!");
        this.currentEdgeWeight = aDouble;
    }
    
    /**
     * Sets the current node size.
     * 
     * @param anInt to be the current node size
     * @throws IllegalArgumentException if anInt is higher than 
     * {@link de.gnwi.spicesviewer.Constants#NODE_SIZE_SLIDER_MAX}
     * or lower than {@link de.gnwi.spicesviewer.Constants#NODE_SIZE_SLIDER_MIN}
     */
    public void setCurrentNodeSize(int anInt) throws IllegalArgumentException {
        if (anInt < Constants.NODE_SIZE_SLIDER_MIN || anInt > Constants.NODE_SIZE_SLIDER_MAX) {
            throw new IllegalArgumentException("The given node size is too high or too low!");
        }
        this.currentNodeSize = anInt;
    }
    
    /**
     * Sets the field that determines whether the particle names should be written out
     * in full on the graph's nodes.
     * 
     * @param aShouldBeFullParticleNameDisplay true if the particle names should be written out
     * in full 
     */
    public void setFullParticleNameDisplay(boolean aShouldBeFullParticleNameDisplay) {
        this.isFullParticleNameDisplay = aShouldBeFullParticleNameDisplay;
    }
    
    /**
     * Sets the directory where exported image files are saved.
     * 
     * @param aDirectory the String representation of the directorie's path,
     * where the image files should be saved
     * @throws IllegalArgumentException if aDirectory is 'null', is no directory,
     * or does not exists
     */
    public void setImageFileDir(String aDirectory) {
        if (aDirectory == null) { throw new IllegalArgumentException("aDirectory (instance of class String) is null."); }
        File tmpFile = new File(aDirectory);
        if (!tmpFile.isDirectory() || !tmpFile.exists())
            throw new IllegalArgumentException("The given directory is either "
                    + "no directory or does not exist!");
        this.imageFileDir = aDirectory;
    }
    
    /**
     * Sets the stylesheet to be added to the graph. To make some changes visible 
     * loadDefaultsFromCSS() needs to be invoked afterwards and some values reset to their defaults.
     * 
     * @param aStylesheet the String representation of the '.css' file added to the 
     * Spices graph as stylesheet in the SpicesToGraphStreamModel; If its 'null'
     * the stylesheet will be set to a default CSS stylesheet.
     */
    public void setStylesheet(String aStylesheet) {
        if (aStylesheet == null)
            aStylesheet = Constants.DEFAULT_CSS_STYLESHEET;
        this.stylesheet = aStylesheet;
    }
    //</editor-fold>

}
