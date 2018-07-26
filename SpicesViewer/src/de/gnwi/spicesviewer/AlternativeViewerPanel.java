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
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.util.DefaultMouseManager;

/**
 * A JPanel extending object to display the graph as in the 
 * {@link de.gnwi.spicesviewer.ViewerPanel} 
 * but giving the user an alternative zoom experience by using a JScrollPane.
 * 
 * @author Jonas Schaub
 */
public class AlternativeViewerPanel extends JPanel {
    
    //<editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * A JScrollPane under which to place the GraphStream 
     * ViewPanel displaying the Spices graph.
     */
    private final JScrollPane scrollPane;
    
    /**
     * A GraphStream ViewPanel that displays the Spices graph.
     */
    private final ViewPanel gsViewPanel;
    
    /**
     * A button for resetting the scroll pane after zooming and dragging.
     */
    private final JButton resetButton;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public constructor">
    /**
     * Constructor:
     * It initialises the new AlternativeViewerPanel's components, layouts them and gives them JToolTips. No event 
     * listeners are added to any component.
     * 
     * @param aViewPanel a GraphStream {@link org.graphstream.ui.swingViewer.ViewPanel} 
     * that displays the graph;
     * Its GraphStream {@link org.graphstream.ui.view.util.DefaultMouseManager}
     * will be removed to control all its movements from 
     * the scroll pane above it
     * @throws IllegalArgumentException if aViewPanel is 'null'
     */
    public AlternativeViewerPanel(ViewPanel aViewPanel) throws IllegalArgumentException {
        super();
        if (aViewPanel == null) { throw new IllegalArgumentException("aViewPanel (instance of class ViewPanel) is null."); }
        //<editor-fold defaultstate="collapsed" desc="Initialisation">
        this.gsViewPanel = aViewPanel;
        this.scrollPane = new JScrollPane(this.gsViewPanel, 
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.resetButton = new JButton(Language.getString("RESET_VIEW"));
        
        for (MouseListener tmpListener : this.gsViewPanel.getMouseListeners()) {
            if (tmpListener instanceof DefaultMouseManager)
                this.gsViewPanel.removeMouseListener(tmpListener);
        }
        this.gsViewPanel.setPreferredSize(Constants.GRAPHSTREAM_VIEW_PANEL_PREFERRED_SIZE);
        this.scrollPane.setMinimumSize(this.gsViewPanel.getPreferredSize());
        this.resetButton.setPreferredSize(new Dimension(Constants.BUTTON_WIDTH, 
                this.resetButton.getPreferredSize().height));
        //</editor-fold>
        
        this.layoutComponents();
        this.createToolTips();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Layouts the container's components.
     */
    private void layoutComponents() {
        SpringLayout tmpSpringLayout = new SpringLayout();
        this.setLayout(tmpSpringLayout);
        this.add(this.scrollPane);
        this.add(this.resetButton);
        
        tmpSpringLayout.putConstraint(SpringLayout.NORTH, this.scrollPane,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.NORTH, this);
        tmpSpringLayout.putConstraint(SpringLayout.WEST, this.scrollPane,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.WEST, this);
        
        tmpSpringLayout.putConstraint(SpringLayout.NORTH, this.resetButton,
                Constants.INTER_COMPONENT_GAP_SIZE, SpringLayout.SOUTH, this.scrollPane);
        tmpSpringLayout.putConstraint(SpringLayout.WEST, this.resetButton,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.WEST, this);
        
        tmpSpringLayout.putConstraint(SpringLayout.EAST, this,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.EAST, this.scrollPane);
        tmpSpringLayout.putConstraint(SpringLayout.SOUTH, this,
                Constants.FRAMEWORK_GAP_SIZE, SpringLayout.SOUTH, this.resetButton);
    }
    
    /**
     * Adds JToolTips to some of the components.
     */
    private void createToolTips() {
        this.resetButton.setToolTipText(HTMLToolTip.createTip(Language.getString(
                "RESET_VIEW_PANEL_BUTTON_TIP")));
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Returns the panel's JButton that is supposed to reset the JScrollPane's view
     * of the GraphStream {@link org.graphstream.ui.swingViewer.ViewPanel}
     * displaying the graph after zooming and
     * dragging.
     *
     * @return the panel's reset button
     */
    public JButton getResetButton() {
        return this.resetButton;
    }
    
    /**
     * Returns the panel's GraphStream {@link org.graphstream.ui.swingViewer.ViewPanel}
     * displaying the graph.
     * 
     * @return the panel's GraphStream {@link org.graphstream.ui.swingViewer.ViewPanel}
     */
    public ViewPanel getGraphStreamViewPanel() {
        return this.gsViewPanel;
    }
    
    /**
     * Returns the panel's JScrollPane that holds the GraphStream {@link org.graphstream.ui.swingViewer.ViewPanel}
     * displaying the graph and controls its zooming and dragging.
     * 
     * @return the panel's JScrollPane
     */
    public JScrollPane getScrollPane() {
        return this.scrollPane;
    }
    //</editor-fold>
}
