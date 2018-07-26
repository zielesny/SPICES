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

import de.gnwi.spices.Spices;
import de.gnwi.spices.SpicesConstants;
import de.gnwi.spicestographstream.SpicesToGraphStream;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Objects;
import java.util.regex.Matcher;
import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import org.apache.commons.io.FilenameUtils;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants;
import org.graphstream.ui.j2dviewer.J2DGraphRenderer;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

/**
 * The Spices Viewer application's controller. By instantiating this class 
 * (see {@link de.gnwi.spicesviewer.Main}) the application is started. 
 * It holds a {@link de.gnwi.spices.Spices} object, a GraphStream 
 * {@link org.graphstream.graph.Graph}, an instance of 
 * the {@link de.gnwi.spicestographstream.SpicesToGraphStream} and all 
 * other classes defined in this package.
 * 
 * @author Jonas Schaub
 */
public class Controller {
    
    //<editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * Instance of the Spices class whose according structure is displayed in the GraphStream graph.
     */
    private final Spices spices;
    
    /**
     * Instance of the application's main JFrame.
     */
    private final MainFrame mainFrame;
    
    /**
     * Instance of the application's main JFrame's main tab where the user can
     * enter and display a SPICES line notation.
     */
    private final ViewerPanel viewerPanel;
    
    /**
     * Instance of the JPanel (the second tab in the main JFrame) with an 
     * alternative view and zooming of the Spices graph.
     */
    private final AlternativeViewerPanel alternativeViewerPanel;
    
    /**
     * Instance of the JDialog where additional graph settings can be made.
     */
    private final AdditionalSettingsDialog additionalSettingsDialog;
    
    /**
     * The main JFrame's menu bar.
     */
    private final MenuBar menuBar;
    
    /**
     * Instance of the JPanel (the third tab in the main JFrame) where the user
     * can choose from some exemplary Spices structures and load them into the
     * application.
     */
    private final StructureLibraryPanel structureLibraryPanel;
    
    /**
     * Instance of the SpicesToGraphStream class for translating the
     * SPICES line notation into a GraphStream graph and setting the node color etc.
     */
    private final SpicesToGraphStream spicesToGraph;
    
    /**
     * Instance of GraphStream's SingleGraph class to turn into a Spices graph.
     */
    private final SingleGraph graph;
    
    /**
     * Instance of GraphStream's Viewer class to draw ViewPanels observing
     * the Spices graph from.
     */
    private final Viewer graphViewer;
    
    /**
     * Instance of GraphStream's ViewPanel class for instantiating the ViewerPanel
     * with.
     */
    private final ViewPanel graphViewPanel1;
    
    /**
     * Instance of GraphStream's ViewPanel class for instantiating the 
     * AlternativeViewerPanel with.
     */
    private final ViewPanel graphViewPanel2;
    
    /**
     * The graph's layout algorithm.
     */
    private final SpringBox graphLayout;
    
    /**
     * The user working directory of the JVM.
     */
    private final String appDir;
    
    /**
     * the default temp file path of the JVM.
     */
    private final String tempDir;
    
    /**
     * A JFileChooser instance for setting the directory where the exported image files should be saved.
     */
    private final JFileChooser fileChooser;
    
    /**
     * The text fields' default cursor.
     */
    private final Cursor textFieldDefaultCursor;
    
    /**
     * The text areas' default cursor.
     */
    private final Cursor textAreaDefaultCursor;
    
    /**
     * This point marks the original position of the ViewPanel in the JScrollPane in the AlternativeViewerPanel.
     */
    private final Point alternativeViewerPanelOriginalViewPosition;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * The user's settings that should be preserved for the next start of the
     * application.
     */
    private UserPreferences userPreferences;
    
    /**
     * When the GraphStream ViewPanel in the AlternativeViewerPanel is dragged this point marks the position where 
     * the mouse was pressed at the start.
     */
    private Point alternativeViewerPanelDragOrigin;
    
    /**
     * When the GraphStream ViewPanel in the AlternativeViewerPanel is dragged this point marks the position where 
     * the mouse is released.
     */
    private Point alternativeViewerPanelDragEnd;
    
    /**
     * When the Spices graph inside the ViewerPanel's GraphStream ViewPanel is dragged this point marks the position where 
     * the mouse was pressed at the start.
     */
    private Point3 viewerPanelDragOrigin;
    
    /**
     * When the Spices graph inside the ViewerPanel's GraphStream ViewPanel is dragged this point marks the position where 
     * the mouse is released.
     */
    private Point3 viewerPanelDragEnd;
    
    /**
     * The ViewerPanel's GraphStream ViewPanel's Camera's initial view center position to go back to by zooming out or 
     * resetting.
     */
    private final Point3 viewerPanelOriginalViewCenterPos;
    
    /**
     * The currently running ShowStatusMessageWorker thread to display a message in the MainFrame's
     * status bar.
     */
    private ShowStatusMessageWorker messageThread;
    
    /**
     * A boolean to indicate cases where the ItemListener attached to the combo box controlling the Spices graph's 
     * part display should be disabled. It is implemented because the ItemListener is also called when the list of 
     * items in the combo box is adjusted.
     */
    private boolean isPartDisplayComboBoxListenerToBeDisabled;
    
    /**
     * The additionalSettingsDialog's user input or displayed value for the Spices graph's node weight when the 
     * additionalSettingsDialog is opened.
     */
    private String nodeWeightInputAtOpening;
    
    /**
     * The additionalSettingsDialog's user input or displayed value for the Spices graph's edge weight when the 
     * additionalSettingsDialog is opened.
     */
    private String edgeWeightInputAtOpening;
    
    /**
     * The additionalSettingsDialog's user input or displayed value for the Spices graph's node size when the 
     * additionalSettingsDialog is opened.
     */
    private int nodeSizeAtOpening;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public Constructor">
    /**
     * Constructor:
     * It initialises the Controller's fields, adds various listeners 
     * to its Swing components, loads the {@link de.gnwi.spicesviewer.UserPreferences} 
     * from a '.ser' file and starts the application by setting the JFrame 
     * {@link de.gnwi.spicesviewer.MainFrame} visible.
     * 
     * @param anAppDir the directory where the application should save its data (the user preferences file)
     * @param aTempDir the default temp file path of the JVM
     * @throws IllegalArgumentException if one of the parameters is 'null' is no directory
     * or does not exists
     */
    public Controller(String anAppDir, String aTempDir) {
        //<editor-fold defaultstate="collapsed" desc="Checks">
        if (anAppDir == null) { throw new IllegalArgumentException("anAppDir (instance of class String) is null."); }
        if (aTempDir == null) { throw new IllegalArgumentException("aTempDir (instance of class String) is null."); }
        File tmpAppDirFile = new File(anAppDir);
        File tmpTempDirFile = new File(aTempDir);
        if (!tmpAppDirFile.isDirectory() || !tmpAppDirFile.exists() || 
                !tmpTempDirFile.isDirectory() || !tmpTempDirFile.exists())
            throw new IllegalArgumentException("The given directories are either "
                    + "no directories or do not exist!");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Initialisations">
        this.appDir = anAppDir;
        this.tempDir = aTempDir;
        this.spicesToGraph = new SpicesToGraphStream();
        this.userPreferences = new UserPreferences(this.spicesToGraph.getStylesheet());
        this.spices = new Spices("");
        this.graph = new SingleGraph("SpicesGraph");
        this.graphLayout = new SpringBox();
        this.graphViewer = new Viewer(this.graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        this.graphViewer.enableAutoLayout(this.graphLayout);
        this.graphViewPanel1 = this.graphViewer.addView("graphViewPanel1", 
                new J2DGraphRenderer(), false);
        this.graphViewPanel2 = this.graphViewer.addView("graphViewPanel2", 
                new J2DGraphRenderer(), false);
        this.viewerPanel = new ViewerPanel(this.graphViewPanel1);
        this.alternativeViewerPanel = new AlternativeViewerPanel(this.graphViewPanel2);
        this.structureLibraryPanel = new StructureLibraryPanel();
        this.additionalSettingsDialog = new AdditionalSettingsDialog();
        this.menuBar = new MenuBar();
        this.mainFrame = new MainFrame(this.viewerPanel, 
                this.alternativeViewerPanel, this.structureLibraryPanel);
        this.mainFrame.setJMenuBar(this.menuBar);
        this.fileChooser = new JFileChooser();
        this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.fileChooser.setAcceptAllFileFilterUsed(false);
        this.fileChooser.setDialogTitle(Language.getString("FILE_CHOOSER_TITLE"));
        this.fileChooser.setCurrentDirectory(new File(this.tempDir));
        this.textFieldDefaultCursor = this.additionalSettingsDialog.getEdgeWeightTextField().getCursor();
        this.textAreaDefaultCursor = this.viewerPanel.getLineNotationTextArea().getCursor();
        this.alternativeViewerPanelOriginalViewPosition = this.alternativeViewerPanel.getScrollPane().getViewport().getViewPosition();
        this.viewerPanelOriginalViewCenterPos = this.viewerPanel.getGraphStreamViewPanel().getCamera().getViewCenter();
        this.messageThread = null;
        this.isPartDisplayComboBoxListenerToBeDisabled = false;
        this.edgeWeightInputAtOpening = this.getEdgeWeightTextFieldInput();
        this.nodeWeightInputAtOpening = this.getNodeWeightTextFieldInput();
        this.nodeSizeAtOpening = this.additionalSettingsDialog.getNodeSizeSlider().getValue();
        //</editor-fold>
        
        this.loadUserPreferences();
        this.initialiseListeners();
        
        this.additionalSettingsDialog.setDefaultCloseOperation(javax.swing.JFrame.HIDE_ON_CLOSE);
        this.mainFrame.setDefaultCloseOperation(javax.swing.JFrame.HIDE_ON_CLOSE);
        this.mainFrame.setLocationRelativeTo(null);
        this.mainFrame.setVisible(true);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Deserialises a UserPreferences object from a file named like 
     * {@link de.gnwi.spicesviewer.Constants#USER_PREFERENCES_FILENAME}
     * when the application starts and adjusts the application's settings accordingly.
     * If deserialisation fails a new UserPreferences object is instantiated (after 
     * a JOptionPane message for the user). If adjusting the application's settings 
     * fails more than {@link de.gnwi.spicesviewer.Constants#LOADING_TRIES} times the application is exited.
     */
    private void loadUserPreferences() {
        FileInputStream tmpFileInputStream = null;
        ObjectInputStream tmpObjectInputStream = null;
        try {
            tmpFileInputStream = new FileInputStream(this.appDir + File.separator + Constants.USER_PREFERENCES_FILENAME);
            tmpObjectInputStream = new ObjectInputStream(tmpFileInputStream);
            this.userPreferences = (UserPreferences) tmpObjectInputStream.readObject();
            this.userPreferences.setStylesheet(this.spicesToGraph.getStylesheet());
            this.userPreferences.loadDefaultsFromCSS();
        } catch (FileNotFoundException aFileNotFoundException) {
            this.userPreferences = new UserPreferences(this.spicesToGraph.getStylesheet());
            String tmpErrorMessage = Language.getString("PROBLEM_LOADING_PREFERENCES");
            JOptionPane.showMessageDialog(null, tmpErrorMessage, 
                    Language.getString("NOTIFICATION"), JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException anException) {
            this.userPreferences = new UserPreferences(this.spicesToGraph.getStylesheet());
            String tmpErrorMessage = Language.getString("PROBLEM_LOADING_PREFERENCES");
            JOptionPane.showMessageDialog(null, tmpErrorMessage, 
                    Language.getString("NOTIFICATION"), JOptionPane.INFORMATION_MESSAGE);
        } finally {
            try {
                if (tmpFileInputStream != null) 
                    tmpFileInputStream.close();
                if (tmpObjectInputStream != null) 
                    tmpObjectInputStream.close();
            } catch (IOException anIOException) {
            }
        }
        boolean tmpWasLoadingSuccessfull = false;
        int tmpCounter = 0;
        do {            
            try {
                if (!this.userPreferences.getCSSNodeSizeMode().equals(StyleConstants.SizeMode.DYN_SIZE)) {
                    this.additionalSettingsDialog.getNodeSizeSlider().setEnabled(false);
                    this.additionalSettingsDialog.getRestoreNodeSizeButton().setEnabled(false);
                } else {
                    this.setNodeSize(this.userPreferences.getCurrentNodeSize(), false);
                    this.setNodeSizeSliderValue(this.userPreferences.getCurrentNodeSize());
                }
                this.menuBar.getFullParticleNameDisplayMenuBox().setSelected(this.userPreferences.isFullParticleNameDisplay());
                this.setFullParticleNameDisplay(this.userPreferences.isFullParticleNameDisplay(), false);
                this.setNodeShape(this.userPreferences.getCurrentNodeShapeKey(), false);
                this.setNodeShapeRadioButtonsSelection(this.userPreferences.getCurrentNodeShapeKey());
                this.setNodeColor(this.userPreferences.getCurrentNodeColor(), false);
                if (!Objects.equals(this.userPreferences.getCurrentEdgeWeight(), Constants.DEFAULT_EDGE_WEIGHT)) {
                    this.setEdgeWeight(this.userPreferences.getCurrentEdgeWeight().toString(), false);
                    this.additionalSettingsDialog.getEdgeWeightTextField().setText(
                            this.userPreferences.getCurrentEdgeWeight().toString());
                }
                if (!Objects.equals(this.userPreferences.getCurrentNodeWeight(), Constants.DEFAULT_NODE_WEIGHT)) {
                    this.setNodeWeight(this.userPreferences.getCurrentNodeWeight().toString(), false);
                    this.additionalSettingsDialog.getNodeWeightTextField().setText(
                            this.userPreferences.getCurrentNodeWeight().toString());
                }
                File tmpFile = new File(this.userPreferences.getImageFileDir());
                if (this.userPreferences.getImageFileDir().equals(Constants.DEFAULT_USER_IMAGE_FILE_DIR)
                        || !tmpFile.isDirectory() || !tmpFile.exists())
                    this.userPreferences.setImageFileDir(this.tempDir);
                this.fileChooser.setCurrentDirectory(tmpFile);
                tmpWasLoadingSuccessfull = true;
            } catch (IllegalArgumentException anIllegalArgumentException) {
                if (tmpCounter > Constants.LOADING_TRIES) {
                    String tmpErrorMessage = Language.getString("PROBLEM_LOADING_PREFERENCES_SHUTDOWN");
                    this.userPreferences = new UserPreferences(this.spicesToGraph.getStylesheet());
                    JOptionPane.showMessageDialog(null, tmpErrorMessage,
                            Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
                String tmpErrorMessage = Language.getString("PROBLEM_LOADING_PREFERENCES");
                this.userPreferences = new UserPreferences(this.spicesToGraph.getStylesheet());
                JOptionPane.showMessageDialog(null, tmpErrorMessage,
                        Language.getString("NOTIFICATION"), JOptionPane.INFORMATION_MESSAGE);
                tmpCounter++;
            }
        } while (!tmpWasLoadingSuccessfull);
    }
    
    /**
     * Serialises the UserPreferences instance in a '.ser' file when the 
     * application is terminated to save them for a restart in the future.
     */
    private void saveUserPreferences() {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        FileOutputStream tmpFileOutputStream = null;
        ObjectOutputStream tmpObjectOutputStream = null;
        try {
            tmpFileOutputStream = new FileOutputStream(this.appDir + File.separator + Constants.USER_PREFERENCES_FILENAME);
            tmpObjectOutputStream = new ObjectOutputStream(tmpFileOutputStream);
            tmpObjectOutputStream.writeObject(this.userPreferences);
        } catch (FileNotFoundException aFileNotFoundException) {
            String tmpErrorMessage = Language.getString("ERROR_MESSAGE_SAVING_PREFERENCES");
            JOptionPane.showMessageDialog(null, tmpErrorMessage,
                    Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
        } catch (IOException anIOException) {
            String tmpErrorMessage = Language.getString("ERROR_MESSAGE_SAVING_PREFERENCES");
            JOptionPane.showMessageDialog(null, tmpErrorMessage,
                    Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (tmpFileOutputStream != null) 
                    tmpFileOutputStream.close();
                if (tmpObjectOutputStream != null) {
                    tmpObjectOutputStream.flush();
                    tmpObjectOutputStream.close();
                }
            } catch (IOException anIOException) {
            }
            this.resetApplicationCursor();
        }
    }
    
    /**
     * Initialises various listeners as anonymous subclasses and adds them to the 
     * designated Swing components in the controller's JPanel and JFrame extending
     * objects.
     */
    private void initialiseListeners() {
        //<editor-fold defaultstate="collapsed" desc="viewerPanel">
        ((AbstractDocument) this.viewerPanel.getLineNotationTextArea().getDocument()).setDocumentFilter(new DocumentFilter() {
            
            @Override
            public void insertString(DocumentFilter.FilterBypass aFilterBypass, int anOffset,
                    String aStringToInsert, AttributeSet anAttributeSet) 
                    throws BadLocationException {
                Matcher tmpMatcher = SpicesConstants.INPUTSTRUCTURE_PATTERN.matcher(aStringToInsert);
                if (tmpMatcher.matches()) {
                    super.insertString(aFilterBypass, anOffset, aStringToInsert, anAttributeSet);
                }
            }
            
            @Override
            public void replace(DocumentFilter.FilterBypass aFilterBypass, int anOffset,
                   int aLength, String aText, AttributeSet anAttributeSet)
                   throws BadLocationException {
                Matcher tmpMatcher = SpicesConstants.INPUTSTRUCTURE_PATTERN.matcher(aText);
                if (tmpMatcher.matches()) {
                    super.replace(aFilterBypass, anOffset, aLength, aText, anAttributeSet);
                }
            }
        });
        
        this.viewerPanel.getLineNotationTextArea().getDocument().addDocumentListener(new DocumentListener() {
            
            @Override
            public void insertUpdate(DocumentEvent aDocumentEvent) {
                Controller.this.updateSpicesAndView();
            }
            
            
            @Override
            public void removeUpdate(DocumentEvent aDocumentEvent) {
                Controller.this.updateSpicesAndView();
            }
            
            @Override
            public void changedUpdate(DocumentEvent aDocumentEvent) {
                Controller.this.updateSpicesAndView();
            }
        });
        
        this.viewerPanel.getPartDisplayComboBox().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                if (Controller.this.viewerPanel.getPartDisplayComboBox().getSelectedItem() == null || 
                        Controller.this.isPartDisplayComboBoxListenerToBeDisabled)
                    return;
                Controller.this.updateView();
            }
        });
        
        this.viewerPanel.getResetOfGraphStreamViewPanelButton().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.viewerPanel.getGraphStreamViewPanel().getCamera().resetView();
            }
        });
        
        MouseAdapter tmpMouseAdapter = new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent aMouseEvent) {
                Controller.this.setViewerPanelDragOrigin(aMouseEvent);
            }
            
            @Override
            public void mouseDragged(MouseEvent aMouseEvent) {
                Controller.this.dragViewerPanelGSViewPanel(aMouseEvent);
            }
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent aMouseWheelEvent) {
                int tmpNotches = aMouseWheelEvent.getWheelRotation();
                if (tmpNotches > 0)
                    Controller.this.zoomViewerPanelOut();
                else
                    Controller.this.zoomViewerPanelIn(aMouseWheelEvent);
            }
        };
        this.viewerPanel.getGraphStreamViewPanel().addMouseListener(tmpMouseAdapter);
        this.viewerPanel.getGraphStreamViewPanel().addMouseMotionListener(tmpMouseAdapter);
        this.viewerPanel.getGraphStreamViewPanel().addMouseWheelListener(tmpMouseAdapter);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="alternativeViewerPanel">
        MouseAdapter tmpMouseAdapter2 = new MouseAdapter() {
            
            @Override
            public void mousePressed(MouseEvent aMouseEvent) {
                Controller.this.setAlternativeViewerPanelDragOrigin(aMouseEvent);
            }
            
            @Override
            public void mouseDragged(MouseEvent aMouseEvent) {
                Controller.this.dragAlternativeViewerPanelGSViewPanel(aMouseEvent);
            }
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent aMouseWheelEvent) {
                int tmpNotches = aMouseWheelEvent.getWheelRotation();
                if (tmpNotches > 0)
                    Controller.this.zoomAlternativeViewerPanelOut(aMouseWheelEvent);
                else
                    Controller.this.zoomAlternativeViewerPanelIn(aMouseWheelEvent);
            }
        };
        this.alternativeViewerPanel.getGraphStreamViewPanel().addMouseListener(tmpMouseAdapter2);
        this.alternativeViewerPanel.getGraphStreamViewPanel().addMouseMotionListener(tmpMouseAdapter2);
        this.alternativeViewerPanel.getScrollPane().getViewport().addMouseWheelListener(tmpMouseAdapter2);
        
        this.alternativeViewerPanel.getResetButton().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.resetAlternativeViewerPanelZoom();
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="structureLibraryPanel">
        this.structureLibraryPanel.getStructureNameList().addListSelectionListener(new ListSelectionListener() {
            
            @Override
            public void valueChanged(ListSelectionEvent aListSelectionEvent) {
                Controller.this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                String tmpStructureName = (String) Controller.this.structureLibraryPanel.getStructureNameList().getSelectedValue();
                String tmpStructure = StructurePropertiesReader.getString(tmpStructureName);
                Controller.this.structureLibraryPanel.getStructureTextArea().setText(tmpStructure);
                if (!Controller.this.structureLibraryPanel.getAdditionOfStructureToViewButton().isEnabled()) {
                    Controller.this.structureLibraryPanel.getAdditionOfStructureToViewButton().setEnabled(true);
                }
                Controller.this.resetApplicationCursor();
            }
        });
        
        this.structureLibraryPanel.getAdditionOfStructureToViewButton().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.addStructure();
            }
        });
        
        this.structureLibraryPanel.getStructureNameList().addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent aMouseEvent) {
                if (aMouseEvent.getClickCount() == 2)
                    Controller.this.addStructure();
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="menuBar">
        this.menuBar.getNodeColorChangerMenuItem().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Color tmpColor = JColorChooser.showDialog(Controller.this.mainFrame,
                        Language.getString("COLOR_CHOOSER_DIALOG_TITLE"),
                        Controller.this.userPreferences.getCurrentNodeColor());
                if (tmpColor != null) {
                    Controller.this.setNodeColor(tmpColor, true);
                }
                Controller.this.resetApplicationCursor();
            }
        });
        
        ActionListener tmpShapeChangerListener = new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                JRadioButtonMenuItem tmpSource = (JRadioButtonMenuItem) anActionEvent.getSource();
                String tmpComboBoxItem = tmpSource.getText();
                String tmpShapeKey = Constants.Shape.ROUNDED_BOX.getGsKey();
                for (Constants.Shape tmpShape : Constants.Shape.values()) {
                    if (tmpShape.getLanguageRepresentation().equals(tmpComboBoxItem)) {
                        tmpShapeKey = tmpShape.getGsKey();
                        break;
                    }
                }
                Controller.this.setNodeShape(tmpShapeKey, true);
            }
        };
        for (JRadioButtonMenuItem tmpButton : this.menuBar.getNodeShapeRadioButtons()) {
            tmpButton.addActionListener(tmpShapeChangerListener);
        }
        
        this.menuBar.getImageExportMenu().addMenuListener(new MenuListener() {
            
            @Override
            public void menuSelected(MenuEvent aMenuEvent) {
                Component tmpComponent = Controller.this.mainFrame.getTabbedPane().getSelectedComponent();
                int tmpNodeCount = Controller.this.graph.getNodeCount();
                if (tmpComponent.equals(Controller.this.structureLibraryPanel) || tmpNodeCount == 0) {
                    Controller.this.menuBar.getImageExportBySwingMenuItem().setEnabled(false);
                    Controller.this.menuBar.getImageExportByFileSinkImagesMenuItem().setEnabled(false);
                    Controller.this.menuBar.getImageExportByGraphAttributeMenuItem().setEnabled(false);
                } else if (tmpComponent.equals(Controller.this.alternativeViewerPanel)) {
                    Controller.this.menuBar.getImageExportByGraphAttributeMenuItem().setEnabled(false);
                }
            }
            
            @Override
            public void menuDeselected(MenuEvent aMenuEvent) {
                Controller.this.menuBar.getImageExportBySwingMenuItem().setEnabled(true);
                Controller.this.menuBar.getImageExportByFileSinkImagesMenuItem().setEnabled(true);
                Controller.this.menuBar.getImageExportByGraphAttributeMenuItem().setEnabled(true);
            }
            
            @Override
            public void menuCanceled(MenuEvent aMenuEvent) {}
        });
        
        this.menuBar.getErrorColorChangerMenuItem().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Color tmpColor = JColorChooser.showDialog(Controller.this.mainFrame,
                        Language.getString("COLOR_CHOOSER_DIALOG_TITLE"),
                        Controller.this.userPreferences.getCurrentErrorColor());
                if (tmpColor != null) {
                    Controller.this.setErrorColor(tmpColor, true);
                }
                Controller.this.resetApplicationCursor();
            }
        });
        
        this.menuBar.getValidColorChangerMenuItem().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Color tmpColor = JColorChooser.showDialog(Controller.this.mainFrame,
                        Language.getString("COLOR_CHOOSER_DIALOG_TITLE"),
                        Controller.this.userPreferences.getCurrentValidColor());
                if (tmpColor != null) {
                    Controller.this.setValidColor(tmpColor, true);
                }
                Controller.this.resetApplicationCursor();
            }
        });
        
        this.menuBar.getRestoreNodeShapeMenuItem().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.resetNodeShape();
            }
        });
        
        this.menuBar.getRestoreNodeColorMenuItem().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.resetNodeColor();
            }
        });
        
        this.menuBar.getRestoreGraphSettingsMenuItem().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.resetGraphSettings();
            }
        });
        
        this.menuBar.getRestoreAppSettingsMenuItem().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.resetApplicationSettings();
            }
        });
        
        this.menuBar.getFullParticleNameDisplayMenuBox().addItemListener(new ItemListener() {
            
            @Override
            public void itemStateChanged(ItemEvent anItemEvent) {
                boolean tmpIsBoxSelected;
                int tmpStateChange = anItemEvent.getStateChange();
                switch (tmpStateChange) {
                    case ItemEvent.DESELECTED:
                        tmpIsBoxSelected = false;
                        break;
                    case ItemEvent.SELECTED:
                        tmpIsBoxSelected = true;
                        break;
                    default:
                        return;
                }
                Controller.this.setFullParticleNameDisplay(tmpIsBoxSelected, true);
            }
        });
        
        this.menuBar.getAdditionalSettingsMenuItem().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.edgeWeightInputAtOpening = Controller.this.getEdgeWeightTextFieldInput();
                Controller.this.nodeWeightInputAtOpening = Controller.this.getNodeWeightTextFieldInput();
                Controller.this.nodeSizeAtOpening = Controller.this.additionalSettingsDialog.getNodeSizeSlider().getValue();
                Controller.this.additionalSettingsDialog.setLocationRelativeTo(Controller.this.mainFrame);
                Controller.this.additionalSettingsDialog.setVisible(true);
            }
        });
        
        ActionListener tmpImageExportListener = new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                JMenuItem tmpSource = (JMenuItem) anActionEvent.getSource();
                String tmpKey = tmpSource.getText();
                Controller.this.imageExport(tmpKey);
            }
        };
        this.menuBar.getImageExportByGraphAttributeMenuItem().addActionListener(tmpImageExportListener);
        this.menuBar.getImageExportByFileSinkImagesMenuItem().addActionListener(tmpImageExportListener);
        this.menuBar.getImageExportBySwingMenuItem().addActionListener(tmpImageExportListener);
        this.menuBar.getScreenshotOfWholeFrameMenuItem().addActionListener(tmpImageExportListener);
        
        this.menuBar.getImageExportFileDirMenuItem().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                File tmpOldDir = new File(Controller.this.userPreferences.getImageFileDir());
                Controller.this.fileChooser.setCurrentDirectory(tmpOldDir);
                int tmpReturnValue;
                tmpReturnValue = Controller.this.fileChooser.showDialog(Controller.this.mainFrame, 
                        Language.getString("OK"));
                if (tmpReturnValue == JFileChooser.APPROVE_OPTION) {
                    File tmpNewDir = Controller.this.fileChooser.getSelectedFile();
                    if (tmpNewDir.isDirectory() && tmpNewDir.exists()) {
                        Controller.this.userPreferences.setImageFileDir(tmpNewDir.getPath());
                    } else {
                        Controller.this.showStatusMessage(Language.getString("DIRECTORY_ERROR"), 
                                Controller.this.userPreferences.getCurrentErrorColor());
                    }
                }
                Controller.this.resetApplicationCursor();
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="additionalSettingsDialog">
        DocumentFilter tmpFilter = new DocumentFilter() {            
            
            @Override
            public void insertString(DocumentFilter.FilterBypass aFilterBypass, int anOffset,
                    String aStringToInsert, AttributeSet anAttributeSet) 
                    throws BadLocationException {
                String tmpOldText = aFilterBypass.getDocument().getText(0, aFilterBypass.getDocument().getLength());
                int length = aStringToInsert.length();
                String tmpNewText = tmpOldText.substring(0, anOffset) + aStringToInsert 
                        + tmpOldText.substring(anOffset+length, tmpOldText.length());
                Matcher tmpMatcher = Constants.DOUBLE_INPUT_PATTERN.matcher(tmpNewText);
                if (tmpMatcher.matches() && tmpNewText.length() <= Constants.WEIGHT_INPUT_MAX_LENGTH) {
                    if (tmpNewText.equals("."))
                        aStringToInsert = "0.";
                    if (tmpNewText.equals("-.")) {
                        aStringToInsert = "0.";
                    }
                    if (tmpNewText.equals("+.")) {
                        aStringToInsert = "0.";
                    }
                    super.insertString(aFilterBypass, anOffset, aStringToInsert, anAttributeSet);
                }
            }
            
            @Override
            public void replace(DocumentFilter.FilterBypass aFilterBypass, int anOffset,
                   int aLength, String aText, AttributeSet anAttributeSet)
                   throws BadLocationException {
                String tmpOldText = aFilterBypass.getDocument().getText(0, aFilterBypass.getDocument().getLength());
                String tmpNewText = tmpOldText.substring(0, anOffset) + aText 
                        + tmpOldText.substring(anOffset+aLength, tmpOldText.length());
                Matcher tmpMatcher = Constants.DOUBLE_INPUT_PATTERN.matcher(tmpNewText);
                if (tmpMatcher.matches() && tmpNewText.length() <= Constants.WEIGHT_INPUT_MAX_LENGTH) {
                    if (tmpNewText.equals("."))
                        aText = "0.";
                    if (tmpNewText.equals("-.")) {
                        aText = "0.";
                    }
                    if (tmpNewText.equals("+.")) {
                        aText = "0.";
                    }
                    super.replace(aFilterBypass, anOffset, aLength, aText, anAttributeSet);
                }
            }
        };
        ((AbstractDocument) this.additionalSettingsDialog.getNodeWeightTextField().getDocument()).setDocumentFilter(tmpFilter);
        ((AbstractDocument) this.additionalSettingsDialog.getEdgeWeightTextField().getDocument()).setDocumentFilter(tmpFilter);
        
        this.additionalSettingsDialog.getRestoreNodeWeightButton().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.additionalSettingsDialog.getNodeWeightTextField().setText("");
            }
        });
        
        this.additionalSettingsDialog.getRestoreEdgeWeightButton().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.additionalSettingsDialog.getEdgeWeightTextField().setText("");
            }
        });
        
        this.additionalSettingsDialog.getRestoreNodeSizeButton().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.additionalSettingsDialog.getNodeSizeSlider().setValue(Controller.this.userPreferences.getCSSNodeSize());
            }
        });
        
        this.additionalSettingsDialog.getOkButton().addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                if (!Controller.this.edgeWeightInputAtOpening.equals(Controller.this.getEdgeWeightTextFieldInput())) {
                    Controller.this.setEdgeWeight(Controller.this.getEdgeWeightTextFieldInput(), true);
                }
                if (!Controller.this.nodeWeightInputAtOpening.equals(Controller.this.getNodeWeightTextFieldInput())) {
                    Controller.this.setNodeWeight(Controller.this.getNodeWeightTextFieldInput(), true);
                }
                if (Controller.this.nodeSizeAtOpening != Controller.this.additionalSettingsDialog.getNodeSizeSlider().getValue()) {
                    Controller.this.setNodeSize(Controller.this.additionalSettingsDialog.getNodeSizeSlider().getValue(), true);
                }
                Controller.this.resetApplicationCursor();
                Controller.this.additionalSettingsDialog.setVisible(false);
            }
        });
        
        this.additionalSettingsDialog.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent anActionEvent) {
                Controller.this.additionalSettingsDialog.setVisible(false);
                Controller.this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                if (!Controller.this.edgeWeightInputAtOpening.equals(Controller.this.getEdgeWeightTextFieldInput())) {
                    Controller.this.additionalSettingsDialog.getEdgeWeightTextField().setText(Controller.this.edgeWeightInputAtOpening);
                }
                if (!Controller.this.nodeWeightInputAtOpening.equals(Controller.this.getNodeWeightTextFieldInput())) {
                    Controller.this.additionalSettingsDialog.getNodeWeightTextField().setText(Controller.this.nodeWeightInputAtOpening);
                }
                if (Controller.this.nodeSizeAtOpening != Controller.this.additionalSettingsDialog.getNodeSizeSlider().getValue()) {
                    Controller.this.additionalSettingsDialog.getNodeSizeSlider().setValue(Controller.this.nodeSizeAtOpening);
                }
                Controller.this.resetApplicationCursor();
            }
            
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="mainFrame">
        WindowAdapter tmpWindowAdapter = new WindowAdapter() {
            /* A failed attempt to battle the problem that sections of the graph in the ViewerPanel's ViewPanel sometimes 
            diappear when the ViewPanel or the application loses focus. 'Controller.this.graphLayout.compute()' did that 
            quite successfull but led to ConcurrentModificationExceptions in the LayoutRunner thread if the layout 
            was not stabilized before. Testing this by 'Controller.this.graphLayout.getNodeMovedCount() == 0' did not work.
            
            @Override
            public void windowActivated(WindowEvent aWindowEvent) {
                Controller.this.mainFrame.revalidate();
                Controller.this.mainFrame.repaint();
                Controller.this.graphViewPanel1.revalidate();
                Controller.this.graphViewPanel1.repaint();
                Controller.this.graphLayout.compute();
            }
            @Override
            public void windowDeiconified(WindowEvent aWindowEvent) {
                Controller.this.mainFrame.revalidate();
                Controller.this.mainFrame.repaint();
                Controller.this.graphViewPanel1.revalidate();
                Controller.this.graphViewPanel1.repaint();
                Controller.this.graphLayout.compute();
            }
            @Override
            public void windowGainedFocus(WindowEvent aWindowEvent) {
                Controller.this.mainFrame.revalidate();
                Controller.this.mainFrame.repaint();
                Controller.this.graphViewPanel1.revalidate();
                Controller.this.graphViewPanel1.repaint();
                Controller.this.graphLayout.compute();
            }
            @Override
            public void windowStateChanged(WindowEvent aWindowEvent) {
                Controller.this.mainFrame.revalidate();
                Controller.this.mainFrame.repaint();
                Controller.this.graphViewPanel1.revalidate();
                Controller.this.graphViewPanel1.repaint();
                Controller.this.graphLayout.compute();
            }*/
            @Override
            public void windowClosing(WindowEvent aWindowEvent) {
                Controller.this.saveUserPreferences();
                System.exit(0);
            }
        };
        this.mainFrame.addWindowListener(tmpWindowAdapter);
        this.mainFrame.addWindowFocusListener(tmpWindowAdapter);
        this.mainFrame.addWindowStateListener(tmpWindowAdapter);
        
        /* An attempt to make the graph use additional space properly when the ViewPanel is resized. It did work but 
        'compute()' caused ConcurrentModificationExceptions sometimes (see above) which is why this approach has been 
        discarded.
        
        ComponentAdapter tmpComponentAdapter = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent aComponentEvent) {
                Controller.this.graphLayout.compute();
            }
        };
        this.mainFrame.addComponentListener(tmpComponentAdapter);*/
        
        /* A failed attempt to solve the problem, that dragging a node in the ViewerPanel's ViewPanel fails sometimes after 
        reactivating the ViewerPanel tab.
        
        this.mainFrame.getTabbedPane().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent aChangeEvent) {
                Component tmpComponent = Controller.this.mainFrame.getSelectedTab();
                if (tmpComponent.equals(Controller.this.viewerPanel)) {
                    Controller.this.graphViewPanel1.revalidate();
                    Controller.this.graphViewPanel1.repaint();
                    Controller.this.graphLayout.compute();
                }
            }
        });*/
        //</editor-fold>
    }
    
    /**
     * Updates the Spices object's input structure, sets the ViewerPanel's message
     * label's text according to the new error message given by the Spices
     * object, updates the ViewerPanel's combo box for part choosing
     * and updates the ViewerPanel's GraphStream ViewPanel when the ViewerPanel's 
     * TextArea's document is changed.
     */
    private void updateSpicesAndView() {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String tmpInputStructure;
        String tmpErrorMessage;
        try {
            tmpInputStructure = this.viewerPanel.getLineNotationTextArea().getText();
            this.spices.setInputStructure(tmpInputStructure);
        } catch (Exception anException) {
            tmpErrorMessage = Language.getString("UNEXPECTED_ERROR");
            JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage, 
                    Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
            this.resetApplicationCursor();
            return;
        }
        tmpErrorMessage = this.spices.getErrorMessage();
        if (tmpErrorMessage == null) {
            tmpErrorMessage = Language.getString("VALID");
            this.viewerPanel.getMessageLabel().setForeground(this.userPreferences.getCurrentValidColor());
            this.viewerPanel.getMessageLabel().setText(tmpErrorMessage);
            this.viewerPanel.getResetOfGraphStreamViewPanelButton().setEnabled(true);
            this.isPartDisplayComboBoxListenerToBeDisabled = true;
            this.viewerPanel.getPartDisplayComboBox().removeAllItems();
            String tmpAll = Language.getString("ALL");
            this.viewerPanel.getPartDisplayComboBox().addItem(tmpAll);
            this.viewerPanel.getPartDisplayComboBox().setSelectedItem(tmpAll);
            if (this.spices.hasMultipleParts()) {
                for (int i = 1; i <= this.spices.getPartsOfSpices().length; i++) {
                    this.viewerPanel.getPartDisplayComboBox().addItem(Integer.toString(i));
                    this.viewerPanel.getPartDisplayComboBox().setEnabled(true);
                }
            } else {
                this.viewerPanel.getPartDisplayComboBox().setEnabled(false);
            }
            this.isPartDisplayComboBoxListenerToBeDisabled = false;
            this.spicesToGraph.updateSpicesGraph(this.graph, this.spices);
            this.viewerPanel.getGraphStreamViewPanel().getCamera().resetView();
            this.resetAlternativeViewerPanelZoom();
        } else {
            this.viewerPanel.getMessageLabel().setForeground(this.userPreferences.getCurrentErrorColor());
            this.viewerPanel.getMessageLabel().setText(tmpErrorMessage);
            this.graph.clear();
            this.viewerPanel.getPartDisplayComboBox().setEnabled(false);
            this.viewerPanel.getResetOfGraphStreamViewPanelButton().setEnabled(false);
            this.viewerPanel.getGraphStreamViewPanel().getCamera().resetView();
            this.resetAlternativeViewerPanelZoom();
        }
        this.resetApplicationCursor();
    }
    
    /**
     * Updates the GraphStream graph when another part is selected to be shown
     * in the ViewerPanel's combo box. The Spices objects error message has 
     * to be 'null'!
     */
    private void updateView() {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        this.viewerPanel.getGraphStreamViewPanel().getCamera().resetView();
        this.resetAlternativeViewerPanelZoom();
        String tmpErrorMessage;
        tmpErrorMessage = this.spices.getErrorMessage();
        if (tmpErrorMessage == null) {
            String tmpSelectedPart = (String) this.viewerPanel.getPartDisplayComboBox().getSelectedItem();
            if (tmpSelectedPart.equals(Language.getString("ALL"))) {
                this.spicesToGraph.updateSpicesGraph(this.graph, this.spices);
            } else {
                int tmpPart = Integer.parseInt((String) this.viewerPanel.getPartDisplayComboBox().getSelectedItem());
                this.spicesToGraph.updateSpicesGraph(this.graph, this.spices, tmpPart - 1);
            }
        }
        this.resetApplicationCursor();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Image export related methods">
    /**
     * Exports the given JComponent (intended for the ViewerPanel's 
     * GraphStream ViewPanel or the AlternativeViewerPanel's scroll pane above its
     * GraphStream ViewPanel) using its paint() method to generate a buffered image
     * and then writing it to the given file. If anything goes wrong a JOptionPane
     * message is shown to the user. If everything works well a 'Image file exported'
     * message is shown in the main frame's status bar.
     * 
     * @param aViewPanelOrScrollPane the JComponent to export as an image
     * @param anImageFile to write the produced image to; it has to be of a type javax.imageio.ImageIO supports!
     */
    private void exportGraphstreamViewBySwingMethod(JComponent aViewPanelOrScrollPane, File anImageFile) {
        //Moving this functionality to an own thread (like the two GraphStream image export options) resulted in odd behaviour
        // The wait cursor is set and discarded in the calling imageExport() method
        if (aViewPanelOrScrollPane == null || anImageFile == null || anImageFile.exists()) {
            String tmpErrorMessage = Language.getString("IMAGE_EXPORT_ERROR");
            JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage,
                    Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        String tmpFileExtension = FilenameUtils.getExtension(anImageFile.getName());
        String[] tmpSupportedExtensions = ImageIO.getWriterFormatNames();
        boolean tmpIsExtensionSupported = false;
        for (String tmpArrayValue : tmpSupportedExtensions) {
            if (tmpArrayValue.equals(tmpFileExtension)) {
                tmpIsExtensionSupported = true;
                break;
            }
        }
        if (!tmpIsExtensionSupported) {
            String tmpErrorMessage = Language.getString("IMAGE_EXPORT_ERROR");
            JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage,
                    Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        BufferedImage tmpImage = new BufferedImage(
                aViewPanelOrScrollPane.getWidth(),
                aViewPanelOrScrollPane.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        aViewPanelOrScrollPane.paint(tmpImage.getGraphics());
        try {
            ImageIO.write(tmpImage, tmpFileExtension, anImageFile);
        } catch (IOException anIOException) {
            String tmpErrorMessage = Language.getString("IMAGE_EXPORT_ERROR");
            JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage,
                    Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.showStatusMessage(Language.getString("IMAGE_EXPORTED")
                + " (" + anImageFile.getAbsolutePath() + ")", 
                this.userPreferences.getCurrentValidColor());
    }
    
    /**
     * Screenshots the main frame whatever tab is currently active in the tabbed pane
     * using its paint() method to generate a buffered image
     * and then writing it to the given file. If anything goes wrong a JOptionPane
     * message is shown to the user. If everything works well a 'Image file exported'
     * message is shown in the main frame's status bar.
     * Known bug: After zooming or dragging a section of the graph in the ViewerPanel's
     * ViewPanel might be blank in a screenshot of the whole main frame.
     * 
     * @param aScreenshotFile to write the produced image to; it has to be of a type javax.imageio.ImageIO supports!
     */
    private void screenshotMainFrame(File aScreenshotFile) {
        //Moving this functionality to an own thread (like the two GraphStream image export options) resulted in odd behaviour
        // The wait cursor is set and discarded in the calling imageExport() method
        if (aScreenshotFile == null) {
            String tmpErrorMessage = Language.getString("IMAGE_EXPORT_ERROR");
            JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage,
                    Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        String tmpFileExtension = FilenameUtils.getExtension(aScreenshotFile.getName());
        String[] tmpSupportedExtensions = ImageIO.getWriterFormatNames();
        boolean tmpIsExtensionSupported = false;
        for (String tmpArrayValue : tmpSupportedExtensions) {
            if (tmpArrayValue.equals(tmpFileExtension)) {
                tmpIsExtensionSupported = true;
                break;
            }
        }
        if (!tmpIsExtensionSupported) {
            String tmpErrorMessage = Language.getString("IMAGE_EXPORT_ERROR");
            JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage,
                    Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        BufferedImage tmpImage = new BufferedImage(
                this.mainFrame.getWidth(),
                this.mainFrame.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        this.mainFrame.paint(tmpImage.getGraphics());
        try {
            ImageIO.write(tmpImage, tmpFileExtension, aScreenshotFile);
        } catch (IOException anIOException) {
            String tmpErrorMessage = Language.getString("IMAGE_EXPORT_ERROR");
            JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage,
                    Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.showStatusMessage(Language.getString("IMAGE_EXPORTED") 
                + " (" + aScreenshotFile.getAbsolutePath() + ")", 
                this.userPreferences.getCurrentValidColor());
    }
    
    /**
     * Gives four different methods to export the GraphStream ViewPanel
     * or the whole main frame as an image file depending on the given key. If anything goes wrong a JOptionPane
     * message is shown to the user. If everything works well a 'Image file exported'
     * message is shown in the main frame's status bar. The resulting file is stored 
     * in the UserPreference's image file directory (as set by the user),
     * the default temp path, or the user working directory (the application's 
     * working directory) if anything goes wrong with the previous paths. 
     * Some options do not work if the structure library tab is currently active,
     * the Spices graph is not showing because of an error or the alternativeViewerPanel is activated.
     * 
     * @param tmpKey a language-dependent representation from the enum Constants.ImageExportOptions
     */
    private void imageExport(String aKey) {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String tmpPath = "";
        File tmpUserDirFile = new File(this.userPreferences.getImageFileDir());
        File tmpTempDirFile = new File(this.tempDir);
        File tmpAppDirFile = new File(this.appDir);
        if (tmpUserDirFile.isDirectory() && tmpUserDirFile.exists()) {
            tmpPath = tmpUserDirFile.getAbsolutePath();
        } else if (tmpTempDirFile.isDirectory() && tmpTempDirFile.exists()) {
            tmpPath = tmpTempDirFile.getAbsolutePath();
        } else if (tmpAppDirFile.isDirectory() && tmpAppDirFile.exists()) {
            tmpPath = tmpAppDirFile.getAbsolutePath();
        } else {
            String tmpErrorMessage = Language.getString("IMAGE_EXPORT_ERROR");
            JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage,
                    Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
            this.resetApplicationCursor();
            return;
        }
        File tmpImageFile = null;
        for (int i = 0; i <= Integer.MAX_VALUE; i++) {
            tmpImageFile = new File(tmpPath + File.separator + Constants.EXPORTED_IMAGE_FILE_NAME + i 
                    + Constants.EXPORTED_IMAGE_FILE_EXTENSION);
            if (!tmpImageFile.exists()) {
                break;
            }
            if (i == Integer.MAX_VALUE) {
                String tmpErrorMessage = Language.getString("IMAGE_FILE_NUMBER_ERROR");
                JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage,
                        Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
                this.resetApplicationCursor();
                return;
            }
        }
        if (aKey.equals(Constants.ImageExportOptions.GRAPH_ATTRIBUTE.getLanguageRepresentation())) {
            //takes .png, .jpg and .bmp files
            try {
                new ImageExportGraphAttributeWorker(tmpImageFile,
                        this.graph,
                        this.mainFrame,
                        this.userPreferences,
                        this).execute();
            } catch (IllegalArgumentException anIllegalArgumentException) {
                String tmpErrorMessage = Language.getString("IMAGE_EXPORT_ERROR");
                JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage,
                        Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
                this.resetApplicationCursor();
                return;
            }
        } else if (aKey.equals(Constants.ImageExportOptions.FILESINKIMAGES.getLanguageRepresentation())) {
            //takes .png and .jpeg files
            try {
                new ImageExportFileSinkImagesWorker(tmpImageFile,
                        this.graph,
                        this.mainFrame,
                        this.userPreferences,
                        this).execute();
            } catch (IllegalArgumentException anIllegalArgumentException) {
                String tmpErrorMessage = Language.getString("IMAGE_EXPORT_ERROR");
                JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage,
                        Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
                this.resetApplicationCursor();
                return;
            }
        } else if (aKey.equals(Constants.ImageExportOptions.SWING.getLanguageRepresentation())) {
            Component tmpComponent = this.mainFrame.getTabbedPane().getSelectedComponent();
            if (tmpComponent.equals(this.viewerPanel)) {
                this.exportGraphstreamViewBySwingMethod(this.graphViewPanel1, tmpImageFile);
            } else if (tmpComponent.equals(this.alternativeViewerPanel)) {
                this.exportGraphstreamViewBySwingMethod(this.alternativeViewerPanel.getScrollPane().getViewport(), 
                        tmpImageFile);
            } else {
                String tmpErrorMessage = Language.getString("IMAGE_EXPORT_ERROR");
                JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage,
                        Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
            }
        } else if (aKey.equals(Constants.ImageExportOptions.WHOLE_FRAME.getLanguageRepresentation())) {
            this.screenshotMainFrame(tmpImageFile);
        } else {
            String tmpErrorMessage = Language.getString("IMAGE_EXPORT_ERROR");
            JOptionPane.showMessageDialog(this.mainFrame, tmpErrorMessage,
                    Language.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
        }
        this.resetApplicationCursor();
    }
    //</editor-fold>
    
    /**
     * Sets the cursor for the whole application.
     * Note: This does not include the split panes in the application.
     */
    private void setApplicationCursor(Cursor aCursor) {
        if (aCursor == null)
            return;
        this.mainFrame.setCursor(aCursor);
        this.additionalSettingsDialog.setCursor(aCursor);
        this.additionalSettingsDialog.getEdgeWeightTextField().setCursor(aCursor);
        this.additionalSettingsDialog.getNodeWeightTextField().setCursor(aCursor);
        this.viewerPanel.getLineNotationTextArea().setCursor(aCursor);
    }
    
    /**
     * Resets the cursor of the application to the default cursor.
     */
    private void resetApplicationCursor() {
        this.mainFrame.setCursor(Cursor.getDefaultCursor());
        this.viewerPanel.getLineNotationTextArea().setCursor(this.textAreaDefaultCursor);
        this.additionalSettingsDialog.setCursor(Cursor.getDefaultCursor());
        this.additionalSettingsDialog.getEdgeWeightTextField().setCursor(this.textFieldDefaultCursor);
        this.additionalSettingsDialog.getNodeWeightTextField().setCursor(this.textFieldDefaultCursor);
    }
    
    /**
     * Sets the node shape of the Spices graph.
     * 
     * @param aShapeKey a gsKey field of a Constants.Shape enum object; if 'null'
     * or empty the node shape will be reset to its default
     * @param aShouldPreferencesBeUpdated specifying whether the user preferences should be 
     * updated accordingly
     */
    private void setNodeShape(String aShapeKey, boolean aShouldPreferencesBeUpdated) {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            this.spicesToGraph.setNodeShape(this.graph, aShapeKey);
            if (aShouldPreferencesBeUpdated) {
                this.userPreferences.setCurrentNodeShapeKey(aShapeKey);
            }
        } catch (IllegalArgumentException anIllegalArgumentException) {
            String tmpErrorMessage = Language.getString("PROBLEM_SETTING_NODE_SHAPE");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
        }
        this.resetApplicationCursor();
    }
    
    /**
     * Resets the node shape to its default value.
     */
    private void resetNodeShape() {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            this.spicesToGraph.resetNodeShape(this.graph);
            this.setNodeShapeRadioButtonsSelection(this.userPreferences.getCSSNodeShapeKey());
        } catch (IllegalArgumentException anIllegalArgumentException) {
            String tmpErrorMessage = Language.getString("PROBLEM_RESETTING_NODE_SHAPE");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
            this.resetApplicationCursor();
            return;
        }
        this.userPreferences.resetNodeShapeKey();
        this.updateView();
        this.resetApplicationCursor();
    }
    
    /**
     * Sets the node weight of the Spices graph.
     * 
     * @param aNodeWeight the String representation of a double; If empty the
     * node weight will be reset
     * @param aShouldPreferencesBeUpdated specifying whether the user preferences should be 
     * updated accordingly
     */
    private void setNodeWeight(String aNodeWeight, boolean aShouldPreferencesBeUpdated) {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (aNodeWeight.trim().isEmpty() || aNodeWeight.trim().equals("")) {
            this.resetNodeWeight();
            this.resetApplicationCursor();
            return;
        }
        if (aNodeWeight.trim().equals(".") || aNodeWeight.trim().equals("-.") || 
                aNodeWeight.trim().equals("-.") || aNodeWeight.trim().equals("-") || aNodeWeight.trim().equals("+")) {
            this.additionalSettingsDialog.getNodeWeightTextField().setText(String.valueOf(this.spicesToGraph.getCurrentNodeWeight()));
            System.out.println(this.additionalSettingsDialog.getNodeWeightTextField().getText());
            this.resetApplicationCursor();
            return;
        }
        double tmpNodeWeight;
        try {
            tmpNodeWeight = Double.parseDouble(aNodeWeight);
        } catch (NumberFormatException aNumberFormatException) {
            String tmpErrorMessage = Language.getString("PROBLEM_SETTING_NODE_WEIGHT");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
            this.additionalSettingsDialog.getNodeWeightTextField().setText(String.valueOf(this.spicesToGraph.getCurrentNodeWeight()));
            this.resetApplicationCursor();
            return;
        } catch (NullPointerException aNullPointerException) {
            this.resetApplicationCursor();
            return;
        }
        try {
            this.spicesToGraph.setNodeWeight(this.graph, tmpNodeWeight);
            if (aShouldPreferencesBeUpdated) {
                this.userPreferences.setCurrentNodeWeight(tmpNodeWeight);
            }
        } catch (IllegalArgumentException anIllegalArgumentException) {
            String tmpErrorMessage = Language.getString("PROBLEM_SETTING_NODE_WEIGHT");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
            this.additionalSettingsDialog.getNodeWeightTextField().setText(String.valueOf(this.spicesToGraph.getCurrentNodeWeight()));
            this.resetApplicationCursor();
            return;
        }
        this.resetApplicationCursor();
    }
    
    /**
     * Resets the node weight to its default value.
     */
    private void resetNodeWeight() {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            this.spicesToGraph.resetNodeWeight(this.graph);
        } catch (IllegalArgumentException anIllegalArgumentException) {
            String tmpErrorMessage = Language.getString("PROBLEM_RESETTING_NODE_WEIGHT");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
            this.additionalSettingsDialog.getNodeWeightTextField().setText(String.valueOf(this.spicesToGraph.getCurrentNodeWeight()));
            this.resetApplicationCursor();
            return;
        }
        this.userPreferences.resetNodeWeight();
        this.additionalSettingsDialog.getNodeWeightTextField().setText("");
        this.resetApplicationCursor();
    }
    
    /**
     * Sets the edge weight of the Spices graph.
     * 
     * @param anEdgeWeight the String representation of a double; If empty the
     * edge weight will be reset
     * @param aShouldPreferencesBeUpdated specifying whether the user preferences should be 
     * updated accordingly
     */
    private void setEdgeWeight(String anEdgeWeight, boolean aShouldPreferencesBeUpdated) {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (anEdgeWeight.trim().isEmpty() || anEdgeWeight.trim().equals("")) {
            this.resetEdgeWeight();
            this.resetApplicationCursor();
            return;
        }
        if (anEdgeWeight.trim().equals(".") || anEdgeWeight.trim().equals("-.") || 
                anEdgeWeight.trim().equals("+.") || anEdgeWeight.trim().equals("-") || anEdgeWeight.trim().equals("+")) {
            this.additionalSettingsDialog.getEdgeWeightTextField().setText(String.valueOf(this.spicesToGraph.getCurrentEdgeWeight()));
            this.resetApplicationCursor();
            return;
        }
        double tmpEdgeWeight;
        try {
            tmpEdgeWeight = Double.parseDouble(anEdgeWeight);
        } catch (NumberFormatException aNumberFormatException) {
            String tmpErrorMessage = Language.getString("PROBLEM_SETTING_EDGE_WEIGHT");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
            this.additionalSettingsDialog.getEdgeWeightTextField().setText(String.valueOf(this.spicesToGraph.getCurrentEdgeWeight()));
            this.resetApplicationCursor();
            return;
        } catch (NullPointerException aNullPointerException) {
            this.resetApplicationCursor();
            return;
        }
        try {
            this.spicesToGraph.setEdgeWeight(this.graph, tmpEdgeWeight);
            if (aShouldPreferencesBeUpdated) {
                this.userPreferences.setCurrentEdgeWeight(tmpEdgeWeight);
            }
        } catch (IllegalArgumentException anIllegalArgumentException) {
            String tmpErrorMessage = Language.getString("PROBLEM_SETTING_EDGE_WEIGHT");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
            this.additionalSettingsDialog.getEdgeWeightTextField().setText(String.valueOf(this.spicesToGraph.getCurrentEdgeWeight()));
        }
        this.resetApplicationCursor();
    }
    
    /**
     * Resets the edge weight to its default value.
     */
    private void resetEdgeWeight() {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            this.spicesToGraph.resetEdgeWeight(this.graph);
        } catch (IllegalArgumentException anIllegalArgumentException) {
            String tmpErrorMessage = Language.getString("PROBLEM_RESETTING_EDGE_WEIGHT");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
            this.additionalSettingsDialog.getEdgeWeightTextField().setText(String.valueOf(this.spicesToGraph.getCurrentEdgeWeight()));
            this.resetApplicationCursor();
            return;
        }
        this.userPreferences.resetEdgeWeight();
        this.additionalSettingsDialog.getEdgeWeightTextField().setText("");
        this.resetApplicationCursor();
    }
    
    /**
     * Sets the node color of the Spices graph.
     * 
     * @param aColor to give to the graph's nodes
     * @param aShouldPreferencesBeUpdated specifying whether the user preferences should be 
     * updated accordingly
     */
    private void setNodeColor(Color aColor, boolean aShouldPreferencesBeUpdated) {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            this.spicesToGraph.setNodeColor(Controller.this.graph, aColor);
            if (aShouldPreferencesBeUpdated) {
                this.userPreferences.setCurrentNodeColor(aColor);
            }
        } catch (IllegalArgumentException anIllegalArgumentException) {
            String tmpErrorMessage = Language.getString("PROBLEM_SETTING_NODE_COLOR");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
        }
        this.resetApplicationCursor();
    }
    
    /**
     * Resets the node color to its default value.
     */
    private void resetNodeColor() {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            this.spicesToGraph.resetNodeColor(this.graph);
        } catch (IllegalArgumentException anIllegalArgumentException) {
            String tmpErrorMessage = Language.getString("PROBLEM_RESETTING_NODE_COLOR");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
            this.resetApplicationCursor();
            return;
        }
        this.userPreferences.resetNodeColor();
        this.resetApplicationCursor();
    }
    
    /**
     * Sets the node size of the Spices graph.
     * 
     * @param aNodeSize to give to the graph's nodes
     * @param aShouldPreferencesBeUpdated specifying whether the user preferences should be 
     * updated accordingly
     */
    private void setNodeSize(int aNodeSize, boolean aShouldPreferencesBeUpdated) {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int tmpOldValue = this.spicesToGraph.getCurrentNodeSize();
        try {
            this.spicesToGraph.setNodeSize(this.graph, aNodeSize);
            if (aShouldPreferencesBeUpdated) {
                this.userPreferences.setCurrentNodeSize(aNodeSize);
            }
        } catch (IllegalArgumentException anIllegalArgumentException) {
            String tmpErrorMessage = Language.getString("PROBLEM_SETTING_NODE_SIZE");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
            this.setNodeSizeSliderValue(tmpOldValue);
            this.resetNodeSize();
        }
        this.resetApplicationCursor();
    }
    
    /**
     * Resets the node size to its default value by starting a new anonymous 
     * SwingWorker thread. This is done because without starting a new thread
     * this operation fails sometimes. The Spices graph is locked for all other 
     * threads during this operation.
     */
    private void resetNodeSize() {
        SwingWorker tmpResetNodeSizeThread = new SwingWorker() {
            
            @Override
            protected Object doInBackground() {
                //Controller.this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                int tmpOldValue = Controller.this.spicesToGraph.getCurrentNodeSize();
                synchronized(Controller.this.graph) {
                    try {
                        Controller.this.spicesToGraph.resetNodeSize(Controller.this.graph);
                    } catch (IllegalArgumentException anIllegalArgumentException) {
                        String tmpErrorMessage = Language.getString("PROBLEM_RESETTING_NODE_SIZE");
                        Controller.this.showStatusMessage(tmpErrorMessage, 
                                Controller.this.userPreferences.getCurrentErrorColor());
                        Controller.this.setNodeSizeSliderValue(tmpOldValue);
                        //Controller.this.resetApplicationCursor();
                        return null;
                    }
                }
                Controller.this.userPreferences.resetNodeSize();
                try {
                    Controller.this.setNodeSizeSliderValue(Controller.this.userPreferences.getCurrentNodeSize());
                } catch (IllegalArgumentException anIllegalArgumentException) {
                    String tmpErrorMessage = Language.getString("PROBLEM_RESETTING_NODE_SIZE");
                    Controller.this.showStatusMessage(tmpErrorMessage, Controller.this.userPreferences.getCurrentErrorColor());                    
                }
                //Controller.this.resetApplicationCursor();
                return null;
            }
        };
        SwingUtilities.invokeLater(tmpResetNodeSizeThread);
    }
    
    /**
     * Resets all graph settings (node shape, node weight, edge weight, node color, 
     * node size and whether the particle names should be written out in full) to
     * their default values.
     */
    private void resetGraphSettings() {
        this.resetNodeShape();
        this.resetNodeWeight();
        this.resetEdgeWeight();
        this.resetNodeColor();
        /*There is no own reset method for the following because it has got no "default" button 
        and does only consist of two lines*/
        this.setFullParticleNameDisplay(Constants.IS_FULL_PARTICLE_NAME_DISPLAY_DEFAULT, true);
        this.menuBar.getFullParticleNameDisplayMenuBox().setSelected(Constants.IS_FULL_PARTICLE_NAME_DISPLAY_DEFAULT);
        this.resetNodeSize();
        Controller.this.resetApplicationCursor();
    }
    
    /**
     * Sets the color of the application's error messages. The wait cursor is set in the calling event listener.
     * 
     * @param aColor for the application's error messages
     * @param aShouldPreferencesBeUpdated specifying whether the user preferences should be 
     * updated accordingly
     */
    private void setErrorColor(Color aColor, boolean aShouldPreferencesBeUpdated) {
        try {
            if (this.viewerPanel.getMessageLabel().getForeground().equals(this.userPreferences.getCurrentErrorColor())) {
                this.viewerPanel.getMessageLabel().setForeground(aColor);
            }
            if (aShouldPreferencesBeUpdated) {
                this.userPreferences.setCurrentErrorColor(aColor);
            }
        } catch (IllegalArgumentException anIllegalArgumentException) {
            String tmpErrorMessage = Language.getString("PROBLEM_SETTING_ERROR_COLOR");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
        }
    }
    
    /**
     * Sets the color of the application's 'valid' message. The wait cursor is set in the calling event listener.
     * 
     * @param aColor for the application's 'valid' message
     * @param aShouldPreferencesBeUpdated specifying whether the user preferences should be 
     * updated accordingly
     */
    private void setValidColor(Color aColor, boolean aShouldPreferencesBeUpdated) {
        try {
            if (this.viewerPanel.getMessageLabel().getForeground().equals(this.userPreferences.getCurrentValidColor())) {
                this.viewerPanel.getMessageLabel().setForeground(aColor);
            }
            if (aShouldPreferencesBeUpdated) {
                this.userPreferences.setCurrentValidColor(aColor);
            }
        } catch (IllegalArgumentException anIllegalArgumentException) {
            String tmpErrorMessage = Language.getString("PROBLEM_SETTING_VALID_COLOR");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
        }
    }
    
    /**
     * Resets the application's error and 'valid' color to their defaults. The wait cursor is set in the 
     * calling event listener.
     */
    private void resetApplicationSettings() {
        try {
            if (this.viewerPanel.getMessageLabel().getForeground().equals(this.userPreferences.getCurrentErrorColor())) {
                this.viewerPanel.getMessageLabel().setForeground(Constants.DEFAULT_ERROR_COLOR);
            }
            this.userPreferences.resetErrorColor();
            
            if (this.viewerPanel.getMessageLabel().getForeground().equals(this.userPreferences.getCurrentValidColor())) {
                this.viewerPanel.getMessageLabel().setForeground(Constants.DEFAULT_VALID_COLOR);
            }
            this.userPreferences.resetValidColor();
        } catch (IllegalArgumentException anIllegalArgumentException) {
            String tmpErrorMessage = Language.getString("PROBLEM_RESETTING_APPLICATION_SETTINGS");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
        }
    }
    
    /**
     * Adds the selected Spices structure from the structureLibraryPanel to
     * the ViewerPanel's text area using the addition mode set in the structurLibraryPanel's
     * combo box.
     */
    private void addStructure() {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String tmpStructureName = (String) this.structureLibraryPanel.getStructureNameList().getSelectedValue();
        if (tmpStructureName == null) {
            String tmpErrorMessage = Language.getString("PROBLEM_ADDING_STRUCTURE");
            this.showStatusMessage(tmpErrorMessage, this.userPreferences.getCurrentErrorColor());
        }
        String tmpStructure = StructurePropertiesReader.getString(tmpStructureName);
        String tmpCurrentInput = this.viewerPanel.getLineNotationTextArea().getText();
        String tmpSelectedAdditionMode = (String) this.structureLibraryPanel.getAdditionModeBox().getSelectedItem();
        if (tmpSelectedAdditionMode.equals(Constants.AddStructureOptions.REPLACE.getLanguageRepresentation())) {
            this.viewerPanel.getLineNotationTextArea().setText(tmpStructure);
        } else if (tmpSelectedAdditionMode.equals(Constants.AddStructureOptions.NEW_PART.getLanguageRepresentation())) {
            if (tmpCurrentInput.trim().isEmpty()) {
                this.viewerPanel.getLineNotationTextArea().setText("<" + tmpStructure + ">");
            } else if (tmpCurrentInput.trim().startsWith("<") && tmpCurrentInput.trim().endsWith(">")) {
                this.viewerPanel.getLineNotationTextArea().setText(tmpCurrentInput + "<" + tmpStructure + ">");
            } else {
                this.viewerPanel.getLineNotationTextArea().setText("<" + tmpCurrentInput + "><" + tmpStructure + ">");
            }
        } else if (tmpSelectedAdditionMode.equals(Constants.AddStructureOptions.CONNECTING.getLanguageRepresentation())) {
            if (tmpCurrentInput.trim().isEmpty())
                this.viewerPanel.getLineNotationTextArea().setText(tmpStructure);
            else
                this.viewerPanel.getLineNotationTextArea().setText(tmpCurrentInput + "-" + tmpStructure);
        } else if (tmpSelectedAdditionMode.equals(Constants.AddStructureOptions.SIDE_CHAIN.getLanguageRepresentation())) {
            this.viewerPanel.getLineNotationTextArea().setText(tmpCurrentInput + "(" + tmpStructure + ")");
        } else {
            if (tmpCurrentInput.trim().isEmpty())
                this.viewerPanel.getLineNotationTextArea().setText(tmpStructure);
            else
                this.viewerPanel.getLineNotationTextArea().setText(tmpCurrentInput + "-" + tmpStructure);
        }
        this.resetApplicationCursor();
    }
    
    /**
     * Determines whether the particle names should be written out 
     * in full on the Spices graph's nodes.
     * 
     * @param aShouldBeFullParticleNameDisplay specifying whether the particle names should be written out 
     * in full on the Spices graph's nodes
     * @param anUpdatePreferences specifying whether the user preferences should be 
     * updated accordingly
     */
    private void setFullParticleNameDisplay(boolean aShouldBeFullParticleNameDisplay, boolean aShouldPreferencesBeUpdated) {
        this.setApplicationCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        this.spicesToGraph.setFullParticleNameDisplay(aShouldBeFullParticleNameDisplay);
        if (aShouldPreferencesBeUpdated)
            this.userPreferences.setFullParticleNameDisplay(aShouldBeFullParticleNameDisplay);
        this.updateView();
        this.resetApplicationCursor();
    }
    
    //<editor-fold defaultstate="collapsed" desc="AdditionalSettingsDialog related methods">
    /**
     * Returns the user's input into the AdditionalSettingsDialog's JTextField for the node weight.
     *
     * @return text of the JTextField for the node weight (may be an empty String!)
     */
    private String getNodeWeightTextFieldInput() {
        try {
            return this.additionalSettingsDialog.getNodeWeightTextField().getText();
        } catch (NullPointerException aNullPointerException) {
            return "";
        }
    }
    
    /**
     * Returns the user's input into the AdditionalSettingsDialog's JTextField for the edge weight.
     *
     * @return text of the JTextField for the edge weight (may be an empty String!)
     */
    private String getEdgeWeightTextFieldInput() {
        try {
            return this.additionalSettingsDialog.getEdgeWeightTextField().getText();
        } catch (NullPointerException aNullPointerException) {
            return "";
        }
    }
    
    /**
     * Sets the value of the AdditionalSettingsDialog's JSlider for setting the Spices graph's node size.
     * Intended for displaying loaded user preferences.
     *
     * @param aValue an integer between
     * {@link de.gnwi.spicesviewer.Constants#NODE_SIZE_SLIDER_MIN} and
     * {@link de.gnwi.spicesviewer.Constants#NODE_SIZE_SLIDER_MAX}
     * @throws IllegalArgumentException if the given value is out of the slider's
     * bounds (as described in the parameter section)
     */
    private void setNodeSizeSliderValue(int aValue) throws IllegalArgumentException {
        if (aValue > Constants.NODE_SIZE_SLIDER_MAX || aValue < Constants.NODE_SIZE_SLIDER_MIN)
            throw new IllegalArgumentException("The given value is too small or too big!");
        this.additionalSettingsDialog.getNodeSizeSlider().setValue(aValue);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="AlternativeViewerPanel related methods">
    /**
     * Resets the JScrollPane's view of the GraphStream 
     * {@link org.graphstream.ui.swingViewer.ViewPanel}
     * displaying the {@link de.gnwi.spices.Spices} graph after zooming and 
     * dragging.
     */
    private void resetAlternativeViewerPanelZoom() {
        this.alternativeViewerPanel.getGraphStreamViewPanel().setPreferredSize(this.alternativeViewerPanel.getScrollPane().getViewport().getSize());
        this.alternativeViewerPanel.getScrollPane().getViewport().setViewPosition(this.alternativeViewerPanelOriginalViewPosition);
        this.alternativeViewerPanel.getGraphStreamViewPanel().getCamera().resetView();
    }
    
    /**
     * Sets the point where the mouse is pressed in the JScrollPane for future 
     * calculations.
     * 
     * @param aMouseEvent that gives coordinates relative to the GraphStream 
     * {@link org.graphstream.ui.swingViewer.ViewPanel}
     * displaying the {@link de.gnwi.spices.Spices} graph
     * @throws IllegalArgumentException if aMouseEvent is 'null'
     */
    private void setAlternativeViewerPanelDragOrigin(MouseEvent aMouseEvent) throws IllegalArgumentException {
        if (aMouseEvent == null) { throw new IllegalArgumentException("aMouseEvent (instance of class MouseEvent) is null."); }
        this.alternativeViewerPanelDragOrigin = aMouseEvent.getPoint();
    }
    
    /**
     * Drags the GraphStream {@link org.graphstream.ui.swingViewer.ViewPanel}
     * displaying the {@link de.gnwi.spices.Spices} graph by adjusting the view's position through the viewport
     * when the mouse is pressed and then moved inside the JScrollPane
     * 
     * @param aMouseEvent that gives coordinates relative to the GraphStream 
     * {@link org.graphstream.ui.swingViewer.ViewPanel}
     * displaying the {@link de.gnwi.spices.Spices} graph
     * @throws IllegalArgumentException is aMouseEvent is 'null'
     */
    private void dragAlternativeViewerPanelGSViewPanel(MouseEvent aMouseEvent) throws IllegalArgumentException {
        if (aMouseEvent == null) { throw new IllegalArgumentException("aMouseEvent (instance of class MouseEvent) is null."); }
        this.alternativeViewerPanelDragEnd = aMouseEvent.getPoint();
        Point tmpRectangleCornerPos = new Point(this.alternativeViewerPanelDragEnd.x, this.alternativeViewerPanelDragOrigin.y);
        Point tmpVectorDragOriginToRectangleCorner = new Point(tmpRectangleCornerPos.x - this.alternativeViewerPanelDragOrigin.x,
                tmpRectangleCornerPos.y - this.alternativeViewerPanelDragOrigin.y);
        Point tmpVectorRectangelCornerToDragEnd = new Point(this.alternativeViewerPanelDragEnd.x - tmpRectangleCornerPos.x,
                this.alternativeViewerPanelDragEnd.y - tmpRectangleCornerPos.y);
        Point tmpCurrentViewPosition = this.alternativeViewerPanel.getScrollPane().getViewport().getViewPosition();
        if (tmpCurrentViewPosition.x - tmpVectorDragOriginToRectangleCorner.x > 0) {
            this.alternativeViewerPanel.getScrollPane().getViewport().setViewPosition(new Point(tmpCurrentViewPosition.x - tmpVectorDragOriginToRectangleCorner.x,
                    tmpCurrentViewPosition.y));
        }
        tmpCurrentViewPosition = this.alternativeViewerPanel.getScrollPane().getViewport().getViewPosition();
        if (tmpCurrentViewPosition.y - tmpVectorRectangelCornerToDragEnd.y > 0) {
            this.alternativeViewerPanel.getScrollPane().getViewport().setViewPosition(new Point(tmpCurrentViewPosition.x, 
                    tmpCurrentViewPosition.y - tmpVectorRectangelCornerToDragEnd.y));
        }
    }
    
    /**
     * Zooms mouse position specifically into the GraphStream 
     * {@link org.graphstream.ui.swingViewer.ViewPanel}
     * displaying the {@link de.gnwi.spices.Spices} graph when the mouse 
     * wheel is turned inside the JScrollPane above it by adjusting the 
     * {@link org.graphstream.ui.swingViewer.ViewPanel}'s 
     * preferred size and the view's position of the viewport.
     * <p>
     * Reference:
     * <a href="https://stackoverflow.com/questions/13155382/jscrollpane-zoom-relative-to-mouse-position">stackoverflow.com</a>
     * 
     * @param aMouseWheelEvent that gives coordinates relative to the viewport
     * @throws IllegalArgumentException if aMouseWheelEvent is 'null'
     */
    private void zoomAlternativeViewerPanelIn(MouseWheelEvent aMouseWheelEvent) throws IllegalArgumentException {
        if (aMouseWheelEvent == null) { throw new IllegalArgumentException("aMouseWheelEvent (instance of class MouseWheelEvent) is null."); }
        Dimension tmpCurrentViewSize = this.alternativeViewerPanel.getScrollPane().getViewport().getViewSize();
        this.alternativeViewerPanel.getGraphStreamViewPanel().setPreferredSize(new Dimension((int)(tmpCurrentViewSize.width*(1.0+Constants.ZOOM_FACTOR)),
                (int)(tmpCurrentViewSize.height*(1.0+Constants.ZOOM_FACTOR))));
        this.alternativeViewerPanel.getGraphStreamViewPanel().revalidate();
        this.alternativeViewerPanel.getGraphStreamViewPanel().repaint();
        
        Point tmpViewPos = this.alternativeViewerPanel.getScrollPane().getViewport().getViewPosition();
        Point tmpMousePos = aMouseWheelEvent.getPoint();
        
        int tmpNewX = (int)(tmpMousePos.x*(Constants.ZOOM_FACTOR) + (1.0+Constants.ZOOM_FACTOR)*tmpViewPos.x);
        int tmpNewY = (int)(tmpMousePos.y*(Constants.ZOOM_FACTOR) + (1.0+Constants.ZOOM_FACTOR)*tmpViewPos.y);
        
        this.alternativeViewerPanel.getScrollPane().getViewport().setViewPosition(new Point(tmpNewX, tmpNewY));
        this.alternativeViewerPanel.getGraphStreamViewPanel().revalidate();
        this.alternativeViewerPanel.getGraphStreamViewPanel().repaint();
    }
    
    /**
     * Zooms mouse position specifically out of the GraphStream 
     * {@link org.graphstream.ui.swingViewer.ViewPanel}
     * displaying the {@link de.gnwi.spices.Spices} graph when the mouse 
     * wheel is turned inside the JScrollPane above it by adjusting the 
     * {@link org.graphstream.ui.swingViewer.ViewPanel}'s 
     * preferred size and the view's position of the viewport
     * <p>
     * Reference:
     * <a href="https://stackoverflow.com/questions/13155382/jscrollpane-zoom-relative-to-mouse-position">stackoverflow.com</a>
     * 
     * @param aMouseWheelEvent that gives coordinates relative to the JScrollPane's Viewport
     * @throws IllegalArgumentException if aMouseWheelEvent is 'null'
     */
    private void zoomAlternativeViewerPanelOut(MouseWheelEvent aMouseWheelEvent) throws IllegalArgumentException {
        if (aMouseWheelEvent == null) { throw new IllegalArgumentException("aMouseWheelEvent (instance of class MouseWheelEvent) is null."); }
        Dimension tmpCurrentViewSize = this.alternativeViewerPanel.getScrollPane().getViewport().getViewSize();
        if (this.alternativeViewerPanel.getScrollPane().getViewport().getHeight() > this.alternativeViewerPanel.getGraphStreamViewPanel().getHeight() || 
                this.alternativeViewerPanel.getScrollPane().getViewport().getWidth() > this.alternativeViewerPanel.getGraphStreamViewPanel().getWidth()) {
            return;
        }
        this.alternativeViewerPanel.getGraphStreamViewPanel().setPreferredSize(new Dimension((int)(tmpCurrentViewSize.width*(1.0-Constants.ZOOM_FACTOR)),
                (int)(tmpCurrentViewSize.height*(1.0-Constants.ZOOM_FACTOR))));
        this.alternativeViewerPanel.getGraphStreamViewPanel().revalidate();
        this.alternativeViewerPanel.getGraphStreamViewPanel().repaint();
        
        Point tmpViewPos = this.alternativeViewerPanel.getScrollPane().getViewport().getViewPosition();
        Point tmpMousePos = aMouseWheelEvent.getPoint();
        
        int tmpNewX = (int)(tmpMousePos.x*(-Constants.ZOOM_FACTOR) + (1.0-Constants.ZOOM_FACTOR)*tmpViewPos.x);
        int tmpNewY = (int)(tmpMousePos.y*(-Constants.ZOOM_FACTOR) + (1.0-Constants.ZOOM_FACTOR)*tmpViewPos.y);
        
        this.alternativeViewerPanel.getScrollPane().getViewport().setViewPosition(new Point(tmpNewX, tmpNewY));
        this.alternativeViewerPanel.getGraphStreamViewPanel().revalidate();
        this.alternativeViewerPanel.getGraphStreamViewPanel().repaint();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="MenuBar related methods">
    /**
     * Sets the selection of the node shape radio buttons in the node shape submenu of the MenuBar.
     * Intended for displaying loaded user preferences.
     *
     * @param aShapeKey the language-dependent representation of a GraphStream
     * shape key (to be found in the {@link de.gnwi.spicesviewer.Constants.Shape} enum)
     * @throws IllegalArgumentException if the given aShapeKey can not be found
     * in the {@link de.gnwi.spicesviewer.Constants.Shape} enum's language-dependent
     * representations
     */
    private void setNodeShapeRadioButtonsSelection(String aShapeKey)
            throws IllegalArgumentException {
        String tmpLanguageKey = null;
        for (Constants.Shape tmpShape : Constants.Shape.values()) {
            if (tmpShape.getGsKey().equals(aShapeKey)) {
                tmpLanguageKey = tmpShape.getLanguageRepresentation();
                break;
            }
        }
        if (tmpLanguageKey == null) {
            throw new IllegalArgumentException("The given shape key can not" +
                    "be found in the languageRepresentations of the ViewerConstants.Shape enum!");
        }
        for (JRadioButtonMenuItem tmpNodeShapeRadioButton : this.menuBar.getNodeShapeRadioButtons()) {
            if (tmpLanguageKey.equals(tmpNodeShapeRadioButton.getText())) {
                tmpNodeShapeRadioButton.setSelected(true);
                return;
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="ViewerPanel related methods">
    /**
     * Drags the Spices graph inside the ViewerPanel's GraphStream ViewPanel. This only works if 
     * the mouse is placed next to the graph to let the 
     * GraphStream DefaultMouseManager take over when 
     * the mouse is placed on a node or edge.
     * Known bug: After zooming or dragging part of the graph might disappear 
     * from the GraphStream ViewPanel but it can be 
     * restored by simply clicking on it.
     * 
     * @param aMouseEvent that gives the coordinates of the dragging end in 
     * the ViewPanel's coordinates
     * @throws IllegalArgumentException if aMouseEvent is 'null'
     */
    private void dragViewerPanelGSViewPanel(MouseEvent aMouseEvent) {
        if (aMouseEvent == null) {
            return;
        }
        GraphicElement tmpElement = this.viewerPanel.getGraphStreamViewPanel().findNodeOrSpriteAt(aMouseEvent.getX(), 
                aMouseEvent.getY());
        if (tmpElement != null)
            return;
        this.viewerPanelDragEnd = this.viewerPanel.getGraphStreamViewPanel().getCamera().transformPxToGu(aMouseEvent.getX(), 
                aMouseEvent.getY());
        Point3 tmpRectangleCornerPos = new Point3(this.viewerPanelDragEnd.x, this.viewerPanelDragOrigin.y, 0);
        Point3 tmpVectorDragOriginToRectangleCorner = new Point3(tmpRectangleCornerPos.x - this.viewerPanelDragOrigin.x, 
                tmpRectangleCornerPos.y - this.viewerPanelDragOrigin.y, 0);
        Point3 tmpVectorRectangelCornerToDragEnd = new Point3(this.viewerPanelDragEnd.x - tmpRectangleCornerPos.x, 
                this.viewerPanelDragEnd.y - tmpRectangleCornerPos.y, 0);
        Point3 tmpCurrentViewPosition = this.viewerPanel.getGraphStreamViewPanel().getCamera().getViewCenter();
        this.viewerPanel.getGraphStreamViewPanel().getCamera().setViewCenter(tmpCurrentViewPosition.x - tmpVectorDragOriginToRectangleCorner.x, 
                    tmpCurrentViewPosition.y - tmpVectorRectangelCornerToDragEnd.y, 0);
    }
    
    /**
     * Saves the point where the mouse is pressed next to a graph component 
     * and then dragged in the ViewerPanel's GraphStream ViewPanel.
     * It also keeps the ViewPanel's 
     * DefaultMouseManager from drawing a 
     * selecting rectangle.
     * 
     * @param aMouseEvent that gives the coordinates of the point where the 
     * mouse was pressed in the ViewPanel's 
     * coordinates
     */
    private void setViewerPanelDragOrigin(MouseEvent aMouseEvent) {
        if (aMouseEvent == null) { 
            return;
        }
        GraphicElement tmpElement = this.viewerPanel.getGraphStreamViewPanel().findNodeOrSpriteAt(aMouseEvent.getX(), 
                aMouseEvent.getY());
        if (tmpElement != null) 
            return;
        this.viewerPanel.getGraphStreamViewPanel().endSelectionAt(aMouseEvent.getX(), aMouseEvent.getY());
        this.viewerPanelDragOrigin = this.viewerPanel.getGraphStreamViewPanel().getCamera().transformPxToGu(aMouseEvent.getX(), 
                aMouseEvent.getY());
    }
    
    /**
     * Zooms into the ViewerPanel's GraphStream ViewPanel 
     * in relation to the mouse's position.
     * Known bug: After zooming or dragging part of the graph might disappear 
     * from the ViewPanel but it can be 
     * restored by simply clicking on it.
     * 
     * @param aMouseWheelEvent that gives the coordinates of the point where 
     * the mouse wheel was moved in the 
     * ViewPanel's coordinates
     */
    private void zoomViewerPanelIn(MouseWheelEvent aMouseWheelEvent) {
        if (aMouseWheelEvent == null) { 
            return;
        }
        Point3 tmpMousePosBefore = this.viewerPanel.getGraphStreamViewPanel().getCamera().transformPxToGu(aMouseWheelEvent.getX(), 
                aMouseWheelEvent.getY());
        Point3 tmpViewCenterPosBefore = this.viewerPanel.getGraphStreamViewPanel().getCamera().getViewCenter();
        Point3 tmpRectangleCornerPosBefore = new Point3(tmpViewCenterPosBefore.x, 
                tmpMousePosBefore.y, 0);
        Point3 tmpVectorMouseToCorner = new Point3(tmpRectangleCornerPosBefore.x-tmpMousePosBefore.x, 
                tmpRectangleCornerPosBefore.y-tmpMousePosBefore.y, 0);
        Point3 tmpVectorCornerToViewCenter = new Point3(tmpViewCenterPosBefore.x-tmpRectangleCornerPosBefore.x, 
                tmpViewCenterPosBefore.y-tmpRectangleCornerPosBefore.y, 0);
        
        this.viewerPanel.getGraphStreamViewPanel().getCamera().setViewPercent(
                this.viewerPanel.getGraphStreamViewPanel().getCamera().getViewPercent()*(1.0-Constants.ZOOM_FACTOR));
        
        Point3 tmpMousePosAfter = this.viewerPanel.getGraphStreamViewPanel().getCamera().transformPxToGu(aMouseWheelEvent.getX(), 
                aMouseWheelEvent.getY());
        tmpVectorMouseToCorner.scale(1.0-Constants.ZOOM_FACTOR);
        tmpVectorCornerToViewCenter.scale(1.0-Constants.ZOOM_FACTOR);
        Point3 tmpRectangleCornerPosAfter = new Point3(tmpMousePosAfter.x+tmpVectorMouseToCorner.x, 
                tmpMousePosAfter.y+tmpVectorMouseToCorner.y, 0);
        Point3 tmpViewCenterPosAfter = new Point3(tmpRectangleCornerPosAfter.x+tmpVectorCornerToViewCenter.x, 
                tmpRectangleCornerPosAfter.y+tmpVectorCornerToViewCenter.y, 0);

        this.viewerPanel.getGraphStreamViewPanel().getCamera().setViewCenter(tmpViewCenterPosAfter.x, 
                tmpViewCenterPosAfter.y, tmpViewCenterPosAfter.z);
        this.viewerPanel.getGraphStreamViewPanel().revalidate();
    }
    
    /**
     * Zooms out of the Spices graph in the ViewerPanel's GraphStream ViewPanel and 
     * sets the view center nearer to its original position to compensate the 
     * mouse position specific zooming in.
     */
    private void zoomViewerPanelOut() {
        Point3 tmpViewCenterPosBefore = this.viewerPanel.getGraphStreamViewPanel().getCamera().getViewCenter();
        Point3 tmpRectangleCornerPosBefore = new Point3(tmpViewCenterPosBefore.x, 
                this.viewerPanelOriginalViewCenterPos.y, 0);
        Point3 tmpVectorOriginalPosToCorner = new Point3(tmpRectangleCornerPosBefore.x-this.viewerPanelOriginalViewCenterPos.x, 
                tmpRectangleCornerPosBefore.y-this.viewerPanelOriginalViewCenterPos.y, 0);
        Point3 tmpVectorCornerToViewCenter = new Point3(tmpViewCenterPosBefore.x-tmpRectangleCornerPosBefore.x, 
                tmpViewCenterPosBefore.y-tmpRectangleCornerPosBefore.y, 0);

        this.viewerPanel.getGraphStreamViewPanel().getCamera().setViewPercent(
                this.viewerPanel.getGraphStreamViewPanel().getCamera().getViewPercent()*(1.0+Constants.ZOOM_FACTOR));
        
        tmpVectorOriginalPosToCorner.scale(1.0-Constants.ZOOM_FACTOR);
        tmpVectorCornerToViewCenter.scale(1.0-Constants.ZOOM_FACTOR);
        Point3 tmpRectangleCornerPosAfter = new Point3(this.viewerPanelOriginalViewCenterPos.x+tmpVectorOriginalPosToCorner.x, 
                this.viewerPanelOriginalViewCenterPos.y+tmpVectorOriginalPosToCorner.y, 0);
        Point3 tmpViewCenterPosAfter = new Point3(tmpRectangleCornerPosAfter.x+tmpVectorCornerToViewCenter.x, 
                tmpRectangleCornerPosAfter.y+tmpVectorCornerToViewCenter.y, 0);

        this.viewerPanel.getGraphStreamViewPanel().getCamera().setViewCenter(tmpViewCenterPosAfter.x, 
                tmpViewCenterPosAfter.y, tmpViewCenterPosAfter.z);
        this.viewerPanel.getGraphStreamViewPanel().revalidate();
    }
    //</editor-fold>
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Starts a new {@link de.gnwi.spicesviewer.ShowStatusMessageWorker} 
     * with the given parameters to display a message in the MainFrame's status bar for a 
     * certain time. If there is a message currently on display it will be 
     * overridden by canceling the running 
     * {@link de.gnwi.spicesviewer.ShowStatusMessageWorker}. This method is public because 
     * ImageExportGraphAttributeWorker and ImageExportFileSinkImagesWorker need to call it 
     * upon completion of their tasks.
     * 
     * @param aMessage the message to display
     * @param aColor the color in which to paint the message's text
     * @throws IllegalArgumentException if a parameter is 'null'
     */
    public void showStatusMessage(String aMessage, Color aColor) 
            throws IllegalArgumentException {
        if (aMessage == null) { throw new IllegalArgumentException("aMessage (instance of class String) is null."); }
        if (aColor == null) { throw new IllegalArgumentException("aColor (instance of class Color) is null."); }
        if (this.messageThread != null) {
            this.messageThread.cancel(true);
            if (this.mainFrame.getStatusLabel().getForeground() != Constants.BLACK) {
                this.mainFrame.getStatusLabel().setForeground(Constants.BLACK);
                this.mainFrame.getStatusLabel().setText(Constants.MESSAGE_LABEL_INITIAL_TEXT);
                this.mainFrame.getStatusLabel().revalidate();
                this.mainFrame.getStatusLabel().repaint();
            }
        }
        this.messageThread = new ShowStatusMessageWorker(this.mainFrame.getStatusLabel(), aMessage, aColor);
        this.messageThread.execute();
    }
    //</editor-fold>

}
