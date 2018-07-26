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
import javax.swing.JLabel;
import javax.swing.SwingWorker;

/**
 * A SwingWorker extending class to present a status message in the 
 * {@link de.gnwi.spicesviewer.MainFrame}'s 
 * status bar for a defined amount of time.
 * 
 * @author Jonas Schaub
 */
public class ShowStatusMessageWorker extends SwingWorker {

    //<editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * The JLabel on which to show a message.
     */
    private final JLabel messageLabel;
    
    /**
     * The message to present.
     */
    private final String message;
    
    /**
     * The color in which the message's text should be drawn.
     */
    private final Color color;
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public constructor">
    /**
     * Constructor:
     * Tests the parameters and assigns them to the object's fields.
     *
     * @param aStatusLabel The status label on which to show a message
     * @param aMessage The message to present (can be empty)
     * @param aColor The color in which the message's text should be drawn
     * @throws IllegalArgumentException if one of the parameters is 'null'
     */
    public ShowStatusMessageWorker(JLabel aStatusLabel,
            String aMessage,
            Color aColor)
            throws IllegalArgumentException {
        super();
        //<editor-fold defaultstate="collapsed" desc="Checks">
        if (aStatusLabel == null) { throw new IllegalArgumentException("aStatusLabel (instance of class JLabel) is null."); }
        if (aMessage == null) { throw new IllegalArgumentException("aMessage (instance of class String) is null."); }
        if (aColor == null) { throw new IllegalArgumentException("aColor (instance of class Color) is null."); }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Assignment to object fields">
        this.messageLabel = aStatusLabel;
        this.message = aMessage;
        this.color = aColor;
        //</editor-fold>
    }
    //</editor-fold>
    
    /**
     * Presents the given message in the given status label (painted in the given color) 
     * and resets the label's text after a defined amount of time 
     * ({@link de.gnwi.spicesviewer.Constants#MESSAGE_SHOWING_TIME_MILLIS}). 
     * The given JLabel is locked for all other threads during this time. The thread may be 
     * interrupted without raising an exception.
     * 
     * @return null
     */
    @Override
    protected Object doInBackground() {
        synchronized (this.messageLabel) {
            this.messageLabel.setForeground(this.color);
            this.messageLabel.setText(this.message);
            this.messageLabel.revalidate();
            this.messageLabel.repaint();
            try {
                Thread.sleep(Constants.MESSAGE_SHOWING_TIME_MILLIS);
            } catch (InterruptedException anInterruptedException) {}
            this.messageLabel.setForeground(Constants.BLACK);
            this.messageLabel.setText(Constants.MESSAGE_LABEL_INITIAL_TEXT);
            this.messageLabel.revalidate();
            this.messageLabel.repaint();
            return null;
        }
    }
}
