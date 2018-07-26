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
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.apache.commons.io.FilenameUtils;
import org.graphstream.graph.implementations.SingleGraph;

/**
 * A SwingWorker extending class to export the 
 * graph as an image file by adding a
 * 'ui.screenshot' attribute to it that the GraphStream Viewer intercepts causing it
 * to export the graph (the visible section if it has been dragged and/or 
 * zoomed)
 * 
 * @author Jonas Schaub
 */
public class ImageExportGraphAttributeWorker extends SwingWorker {

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
     * exist on the hard drive already and must be of type .bmp, .png or .jpg
     * @param aSingleGraph the graph to be exported
     * @param aMainFrame The {@link de.gnwi.spicesviewer.MainFrame} where to 
     * write a message at success or failure
     * @param aUserPreferences to draw the color of the message in 
     * the {@link de.gnwi.spicesviewer.MainFrame} from
     * @param aController the instantiating Controller instance for showing a message in the MainFrame's status bar 
     * when the image export is successful
     * @throws IllegalArgumentException if one parameter is 'null', the image file
     * already exists on the hard drive, the image file is not of type .bmp, .png or .jpg,
     * the graph has no nodes or the {@link de.gnwi.spicesviewer.UserPreferences}'
     * 'valid' color is 'null'
     */
    public ImageExportGraphAttributeWorker(File anImageFile,
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
        if (!(tmpFileExtension.equals("png") || tmpFileExtension.equals("jpg") || tmpFileExtension.equals("bmp"))) {
            throw new IllegalArgumentException("The given File has to be of type .png, .bmp or .jpg!");
        }
        if (aSingleGraph.getNodeCount() == 0) {
            throw new IllegalArgumentException("The Spices graph does not have any nodes!");
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
     * Exports the given graph by adding a
     * 'ui.screenshot' attribute to it that the GraphStream Viewer intercepts causing it
     * to export the graph (the visible section if it has been dragged and/or 
     * zoomed). The thread sleeps for 
     * {@link de.gnwi.spicesviewer.Constants#WAITING_TIME_MILLIS_FOR_IMAGE_EXPORT_BY_GRAPHATTRIBUTE} divided by
     * {@link de.gnwi.spicesviewer.Constants#WAITING_STEPS_FOR_IMAGE_EXPORT_BY_GRAPHATTRIBUTE}
     * milliseconds and then checks afterwards if the image file has been 
     * successfully written to the hard drive. If it has not the thread waits again and tries anew afterwards until 
     * {@link de.gnwi.spicesviewer.Constants#WAITING_STEPS_FOR_IMAGE_EXPORT_BY_GRAPHATTRIBUTE} steps have been gone through.
     * If the image file does not exist until then a JOptionPane message is shown.
     * 
     * @return null
     * @throws UnsupportedOperationException if the thread is interrupted while it sleeps
     */
    @Override
    protected Object doInBackground() throws UnsupportedOperationException {
        synchronized (this.graph) {
            this.graph.addAttribute("ui.screenshot", this.imageFile.getAbsolutePath());
        }
        for (int i = 0; i < Constants.WAITING_STEPS_FOR_IMAGE_EXPORT_BY_GRAPHATTRIBUTE; i++) {
            try {
                Thread.sleep(Constants.WAITING_TIME_MILLIS_FOR_IMAGE_EXPORT_BY_GRAPHATTRIBUTE / 
                        Constants.WAITING_STEPS_FOR_IMAGE_EXPORT_BY_GRAPHATTRIBUTE);
            } catch (InterruptedException anInterruptedException) {
                throw new UnsupportedOperationException("Interrupts not supported.", anInterruptedException);
            }
            if (this.imageFile.exists()) {
            this.controller.showStatusMessage(Language.getString("IMAGE_EXPORTED")
                    + " (" + this.imageFile.getAbsolutePath() + ")",
                    this.userPreferences.getCurrentValidColor());
            return null;
            }
        }
        String tmpErrorMessage = Language.getString("IMAGE_EXPORT_TIME_ERROR");
        JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage, 
                Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
        return null;
    }
    
}
