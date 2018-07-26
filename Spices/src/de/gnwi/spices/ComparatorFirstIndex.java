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

import java.util.Comparator;

/**
 * Compare Class for sorting of list of integer array
 * 
 * @author Mirco Daniel
 */
public class ComparatorFirstIndex implements Comparator<int[]> {

    /**
     * @param anArray1
     *            first input array
     * @param anArray2
     *            second input array
     * @return result of compare, -1: anArray1 smaller anArray2 0: anArray1 equals anArray2 1: anArray1 greater anArray2
     */
    @Override
    public int compare(int[] anArray1, int[] anArray2) {
        int tmpInt1 = anArray1[0];
        int tmpInt2 = anArray2[0];

        if (tmpInt1 < tmpInt2)
            return -1;
        else if (tmpInt1 == tmpInt2)
            return 0;
        else
            return 1;
    }

}
