/**
 * SPICES (Simplified Particle Input ConnEction Specification)
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
package de.gnwi.spices;

import java.util.regex.Pattern;

/**
 * Constants for Spices
 * 
 * @author Mirco Daniel, Achim Zielesny
 */
public final class SpicesConstants {

    /**
     * Default Arraydeque capacity
     */
    public static final int DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY = 1000;
    
    /**
     * Default HashMap initial capacity
     */
    public static final int DEFAULT_HASHMAP_INITIAL_CAPACITY = 500;

    /**
     * Default number of all particles of all molecules
     */
    public static final int DEFAULT_NUMBER_OF_PARTICLES = 100;

    /**
     * Separator string for particles
     */
    public static final String PARTICLE_SEPARATOR = "-";

    /**
     * Regex pattern for PARTICLE_SEPARATOR string
     */
    public static final Pattern PARTICLE_SEPARATOR_PATTERN = Pattern.compile("\\" + PARTICLE_SEPARATOR);
    
    /**
     * Regex string for allowed characters of input structures
     */
    public static final String INPUTSTRUCTURE_ALLOWED_CHARACTERS_REGEX_STRING = "[0-9a-zA-Z\\{\\}\\#\\(\\)\\[\\]\\<\\>\\-\\'\\s]+";
 
    /**
     * Pattern for input structure to match
     */
    public static final Pattern INPUTSTRUCTURE_PATTERN = Pattern.compile("[0-9a-zA-Z\\{\\}\\#\\(\\)\\[\\]\\<\\>\\-\\'\\s]+");
    
}
