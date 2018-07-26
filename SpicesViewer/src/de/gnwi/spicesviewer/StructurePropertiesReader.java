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

import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * An abstract library to read a 'Structures.properties' file containing some
 * exemplary SPICES structures and to load them into the 
 * Spices Viewer application.
 * 
 * @author Jonas Schaub
 */
public abstract class StructurePropertiesReader {
    
    //<editor-fold defaultstate="collapsed" desc="Private static final class variables">
    /**
     * Resource bundle name and package location.
     */
    private static final String BUNDLE_NAME = "de.gnwi.spicesviewer.Structures";
    
    /**
     * Resource bundle for the given 'Structures.properties' file.
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Public static methods">
    /**
     * Generates the key set of the ResourceBundle 
     * (generated from the given 'Structures.properties' file) and turns it into 
     * a sorted array of Strings. No key can be 
     * linked to more than one structure in the file.
     * 
     * @return the ResourceBundle's key set as a sorted array of Strings
     */
    public static String[] getKeySetStringArray() {
        Object[] tmpKeySetArray = RESOURCE_BUNDLE.keySet().toArray();
        int tmpKeySetArraySize = tmpKeySetArray.length;
        String[] tmpKeyStringArray = new String[tmpKeySetArraySize];
        for (int i = 0; i < tmpKeySetArraySize; i++) {
            tmpKeyStringArray[i] = tmpKeySetArray[i].toString();
        }
        Arrays.sort(tmpKeyStringArray);
        return tmpKeyStringArray;
    }
    
    /**
     * Returns the resource's SPICES structure for the given key. Does not throw a
     * MissingResourceException if the string could not be found but returns 
     * "[Spices structure for the key " + aKey + "could not be found!]" instead.
     * 
     * @param aKey the SPICES structure's key in the 
     * 'Structures.properties' file
     * @return the SPICES structure linked to the given aKey
     */
    public static String getString(String aKey) {
        try {
            return RESOURCE_BUNDLE.getString(aKey).trim();
        } catch (MissingResourceException aMissingResourceException) {
            return "[Spices structure for the key " + aKey + "could not be found!]";
        }
    }
    //</editor-fold>
}
