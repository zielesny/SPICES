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

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * An abstract library to read a 'Language_xx.properties' file containing the 
 * language-dependent constants of the application.
 * 
 * @author Jonas Schaub
 */
public abstract class Language {
    
    //<editor-fold defaultstate="collapsed" desc="Private static final class variables">
    /**
     * Resource bundle name and package location.
     */
    private static final String BUNDLE_NAME = "de.gnwi.spicesviewer.Language";
    
    /**
     * The system's locale; determines which 'Language_xx.properties' file 
     * is to be used.
     */
    private static final Locale LOCALE_DEFAULT = Locale.getDefault();
    
    /**
     * Resource bundle for the given 'Language_xx.properties' file and system-specific 
     * locale.
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, LOCALE_DEFAULT);
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public static methods">
    /**
     * Returns the resource's string for the given key. Does not throw a
     * MissingResourceException if the string could not be found but returns 
     * "[language resource for the key " + aKey + "could not be found!]" instead.
     * 
     * @param aKey the resource's key in the Language_xx.properties file
     * @return the resource's string in the Language_xx.properties file for 
     * the given key
     */
    public static String getString(String aKey) {
        try {
            return RESOURCE_BUNDLE.getString(aKey).trim();
        } catch (MissingResourceException aMissingResourceException) {
            return "[language resource for the key " + aKey + "could not be found!]";
        }
    }
    //</editor-fold>
}
