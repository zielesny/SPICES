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
package de.gnwi.spices.test;

import junit.framework.TestCase;
import de.gnwi.spices.DepthFirstPath;

/**
 * Test class for class DepthFirstPath
 *
 * @author Mirco Daniel, Achim Zielesny
 */
public class TestDepthFirstPath extends TestCase {
    
    /**
     * Test of property getEndParticleIndex
     */
    public void testGetEndParticleIndex() {
    	int[][] tmpAdjacentArray = new int[14][];
    	tmpAdjacentArray[0] = new int[1];
    	tmpAdjacentArray[0][0] = 1;
    	tmpAdjacentArray[1] = new int[2];
    	tmpAdjacentArray[1][0] = 0;
    	tmpAdjacentArray[1][1] = 2;
    	tmpAdjacentArray[2] = new int[2];
    	tmpAdjacentArray[2][0] = 1;
    	tmpAdjacentArray[2][1] = 3;
    	tmpAdjacentArray[3] = new int[3];
    	tmpAdjacentArray[3][0] = 2;
    	tmpAdjacentArray[3][1] = 4;
    	tmpAdjacentArray[3][2] = 5;
    	tmpAdjacentArray[4] = new int[1];
    	tmpAdjacentArray[4][0] = 3;
    	tmpAdjacentArray[5] = new int[3];
    	tmpAdjacentArray[5][0] = 3;
    	tmpAdjacentArray[5][1] = 6;
    	tmpAdjacentArray[5][2] = 10;
    	tmpAdjacentArray[6] = new int[3];
    	tmpAdjacentArray[6][0] = 5;
    	tmpAdjacentArray[6][1] = 7;
    	tmpAdjacentArray[6][2] = 8;
    	tmpAdjacentArray[7] = new int[1];
    	tmpAdjacentArray[7][0] = 6;
    	tmpAdjacentArray[8] = new int[2];
    	tmpAdjacentArray[8][0] = 6;
    	tmpAdjacentArray[8][1] = 9;
    	tmpAdjacentArray[9] = new int[1];
    	tmpAdjacentArray[9][0] = 8;
    	tmpAdjacentArray[10] = new int[2];
    	tmpAdjacentArray[10][0] = 5;
    	tmpAdjacentArray[10][1] = 11;
    	tmpAdjacentArray[11] = new int[2];
    	tmpAdjacentArray[11][0] = 10;
    	tmpAdjacentArray[11][1] = 12;
    	tmpAdjacentArray[12] = new int[2];
    	tmpAdjacentArray[12][0] = 11;
    	tmpAdjacentArray[12][1] = 13;
    	tmpAdjacentArray[13] = new int[1];
    	tmpAdjacentArray[13][0] = 12;
    	int tmpResult = 0;
    	int tmpStartIndex = 3;

    	DepthFirstPath tmpDFP = new DepthFirstPath(tmpAdjacentArray, tmpStartIndex);
    	tmpResult = tmpDFP.getEndParticleIndex();
    	assertEquals(13, tmpResult);
    	
    	int[] tmpResultPath = tmpDFP.getDiameterPath();
    	assertEquals(3, tmpResultPath[0]);
    	assertEquals(5, tmpResultPath[1]);
    	assertEquals(10, tmpResultPath[2]);
    	assertEquals(11, tmpResultPath[3]);
    	assertEquals(12, tmpResultPath[4]);
    	assertEquals(13, tmpResultPath[5]);
    	assertEquals(6, tmpResultPath.length);
    }
    
}
