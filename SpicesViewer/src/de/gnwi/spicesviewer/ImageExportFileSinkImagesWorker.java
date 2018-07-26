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

import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.apache.commons.io.FilenameUtils;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;

/**
 * A SwingWorker extending class to export the 
 * graph by GraphStream's 
 * {@link org.graphstream.stream.file.FileSinkImages} as an image file.
 * 
 * @author Jonas Schaub
 */
public class ImageExportFileSinkImagesWorker extends SwingWorker {

    //<editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * A File instance that provides the path where the image file should be saved.
     */
    private final File imageFile;
    
    /**
     * The Spices graph that should be exported.
     */
    private final SingleGraph graph;
    
    /**
     * A MainFrame instance where a JOptionPane message should be shown when the image export 
     * fails.
     */
    private final MainFrame mainFrame;
    
    /**
     * A UserPreferences instance from which to draw the messages' colors.
     */
    private final UserPreferences userPreferences;
    
    /**
     * The instantiating Controller instance for showing a message in the MainFrame's status bar 
     * when the image export is successful.
     */
    private final Controller controller;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public constructor">
    /**
     * Constructor:
     * Tests the parameters and assigns them to the object's fields.
     *
     * @param anImageFile to which the image should be saved; It must not
     * exist on the hard drive already and must be of type .png or .jpeg
     * @param aSingleGraph the GraphStream 
     * {@link org.graphstream.graph.implementations.SingleGraph} 
     * to be exported. Its node count determines the resolution of the 
     * written image file.
     * @param aMainFrame the {@link de.gnwi.spicesviewer.MainFrame} 
     * where to write a message when succeeded or failed
     * @param aUserPreferences to draw the color of the message in 
     * the {@link de.gnwi.spicesviewer.MainFrame} from
     * @param aController the instantiating Controller instance for showing a message in the MainFrame's status bar 
     * when the image export is successful
     * @throws IllegalArgumentException if one parameter is 'null', the image file
     * already exists on the hard drive, the image file is not of type .png or .jpeg,
     * the graph has no nodes or no stylesheet, or the 
     * {@link de.gnwi.spicesviewer.UserPreferences}' 'valid' color is 'null'
     */
    public ImageExportFileSinkImagesWorker(File anImageFile,
            SingleGraph aSingleGraph,
            MainFrame aMainFrame,
            UserPreferences aUserPreferences,
            Controller aController)
            throws IllegalArgumentException {
        super();
        //<editor-fold defaultstate="collapsed" desc="Checks">
        if (anImageFile == null) { throw new IllegalArgumentException("anImageFile (instance of class File) is null."); }
        if (aSingleGraph == null) { throw new IllegalArgumentException("aSingleGraph (instance of class SingleGraph) is null."); }
        if (aMainFrame == null) { throw new IllegalArgumentException("aMainFrame (instance of class MainFrame) is null."); }
        if (aUserPreferences == null) { throw new IllegalArgumentException("aUserPreferences (instance of class UserPreferences) is null."); }
        if (aController == null) { throw new IllegalArgumentException("aController (instance of class Controller) is null."); }
        if (anImageFile.exists()) {
            throw new IllegalArgumentException("The given File does already exist!");
        }
        String tmpFileExtension = FilenameUtils.getExtension(anImageFile.getName());
        if (!(tmpFileExtension.equals("png") || tmpFileExtension.equals("jpeg"))) {
            throw new IllegalArgumentException("The given File has to be of type .png or .jpeg!");
        }
        if (aSingleGraph.getNodeCount() == 0) {
            throw new IllegalArgumentException("The Spices graph does not have any nodes!");
        }
        if (aSingleGraph.getAttribute("ui.stylesheet") == null) {
            throw new IllegalArgumentException("graph's stylesheet is 'null'!");
        }
        if (aUserPreferences.getCurrentValidColor() == null) {
            throw new IllegalArgumentException("The given UserPreferences object's 'valid' color is 'null'!");
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Assignment to object fields">
        this.imageFile = anImageFile;
        this.graph = aSingleGraph;
        this.mainFrame = aMainFrame;
        this.userPreferences = aUserPreferences;
        this.controller = aController;
        //</editor-fold>
    }
    //</editor-fold>
    
    /**
     * Exports the given graph by using GraphStream's 
     * {@link org.graphstream.stream.file.FileSinkImages} as an image file.
     * <p>
     * Settings of FileSinkImages:
     * {@link org.graphstream.stream.file.FileSinkImages.LayoutPolicy#COMPUTED_FULLY_AT_NEW_IMAGE};
     * {@link org.graphstream.stream.file.FileSinkImages.OutputPolicy#BY_STEP};
     * {@link org.graphstream.stream.file.FileSinkImages.Quality#HIGH};
     * {@link org.graphstream.stream.file.FileSinkImages.RendererType#SCALA};
     * <p>
     * The stylesheet from the graph is used for the image export so that it gives
     * an exact representation of the graph. The resolution of the image file 
     * is dependent on the node count of the graph 
     * (node count times 
     * {@link de.gnwi.spicesviewer.Constants#NODE_COUNT_TO_RESOLUTION_FACTOR}) 
     * but has a minimum as set in 
     * {@link de.gnwi.spicesviewer.Constants#IMAGE_FILE_MIN_WIDTH_AND_HIGHT}.
     * At higher node counts (margins are set in the different constants in the 
     * {@link de.gnwi.spicesviewer.Constants} class)
     * the existing 'UHD_XK' resolutions from 
     * {@link org.graphstream.stream.file.FileSinkImages.Resolutions} 
     * are taken for the resolution.
     * At high node counts and depending on the operating system this method can 
     * take very long to compute! At the beginning a dummy file is created on 
     * the hard drive to reserve the filename for this operation. If an exception 
     * occurs in the writing process a JOptionPane message dialog is opened.
     * If no error occurs and the image file is written successfully
     * the {@link de.gnwi.spicesviewer.MainFrame} displays a message in its status bar.
     * 
     * 
     * @return null
     */
    @Override
    protected Object doInBackground() {
        synchronized (this.graph) {
            FileSinkImages tmpFileSinkImages = new FileSinkImages();
            tmpFileSinkImages.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
            tmpFileSinkImages.setOutputPolicy(FileSinkImages.OutputPolicy.BY_STEP);
            tmpFileSinkImages.setQuality(FileSinkImages.Quality.HIGH);
            tmpFileSinkImages.setRenderer(FileSinkImages.RendererType.SCALA);
            tmpFileSinkImages.setStyleSheet(this.graph.getAttribute("ui.stylesheet"));

            int tmpNodeCount = this.graph.getNodeCount();
            int tmpMinWidthAndHeight = Constants.IMAGE_FILE_MIN_WIDTH_AND_HIGHT;
            int tmpImageWidthAndHeight = tmpNodeCount*Constants.NODE_COUNT_TO_RESOLUTION_FACTOR;
            tmpFileSinkImages.setResolution(new FileSinkImages.CustomResolution(tmpImageWidthAndHeight, 
                    tmpImageWidthAndHeight));
            if (tmpImageWidthAndHeight < tmpMinWidthAndHeight) {
                tmpFileSinkImages.setResolution(new FileSinkImages.CustomResolution(tmpMinWidthAndHeight, 
                        tmpMinWidthAndHeight));
            }
            if (tmpNodeCount > Constants.MIN_NODE_COUNT_FOR_UHD_4K) {
                tmpFileSinkImages.setResolution(FileSinkImages.Resolutions.UHD_4K);
            }
            if (tmpNodeCount > Constants.MIN_NODE_COUNT_FOR_UHD_8K_16by9) {
                tmpFileSinkImages.setResolution(FileSinkImages.Resolutions.UHD_8K_16by9);
            } 
            if (tmpNodeCount > Constants.MIN_NODE_COUNT_FOR_UHD_8K_1by1) {
                tmpFileSinkImages.setResolution(FileSinkImages.Resolutions.UHD_8K_1by1);
            }
            try {
                /*A dummy file is created here because the writing process can take a long time 
                and the filename should be reserved for this thread*/
                boolean tmpWasFileSuccessfullyCreated = this.imageFile.createNewFile();
                if (tmpWasFileSuccessfullyCreated) {
                    tmpFileSinkImages.writeAll(this.graph, this.imageFile.getAbsolutePath());
                } else {
                    String tmpErrorMessage = Language.getString("IMAGE_EXPORT_ERROR");
                    JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage, 
                            Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            } catch (IOException anIOException) {
                String tmpErrorMessage = Language.getString("IMAGE_EXPORT_ERROR");
                JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage, 
                        Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        /*Moving this section to the done() method where it belongs resulted in 
        strange behaviour of the MainFrame*/
        this.controller.showStatusMessage(Language.getString("IMAGE_EXPORTED")
                + " (" + this.imageFile.getAbsolutePath() + ")", 
                this.userPreferences.getCurrentValidColor());
        return null;
    }
    
}
