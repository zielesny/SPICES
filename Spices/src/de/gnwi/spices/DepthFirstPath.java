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

import java.util.Stack;

/**
 * Class, which generates a DepthFirstPath object
 * 	Reference: Robert Sedgewick, Kevin Wayne; Algorithms; Addison-Wesley Professional; 4th edition (2011)
 * 
 * @author Mirco Daniel, Achim Zielesny
 */
public class DepthFirstPath {
	
	private boolean[] visited;
	private int[] edgeTo;
	private int[] distanceToStart;
	private int[] diameterPath;
	private int[][] adjacentArray;
	private int startParticleIndex;
	private int endParticleIndex;
	private int numberOfParticles;

	/**
	 * End particle index 
         * 
         * @return index of the last particle
         * 
	 */
	public int getEndParticleIndex() {
		return this.endParticleIndex;
	}

	/**
	 * Path between start and farthest particle 
         * 
         * @return diameter path 
	 */
	public int[] getDiameterPath() {
		return this.diameterPath;
	}
	
	/**
	 * Constructor 
	 * 
	 * @param aAdjacentArray an adjacent array. First index gives the index number of the previous particle.
	 * 			Second index gives the position of each neighbor particle indices.
	 * @param aStartParticleIndex start particle index.
	 */
	public DepthFirstPath(int[][] aAdjacentArray, int aStartParticleIndex) {
		this.initialize(aAdjacentArray, aStartParticleIndex);
	}
	
	/**
	 * Initialize method
	 * 
	 * @param aAdjacentArray an adjacent array. First index gives the index number of the previous particle.
	 * 			Second index gives the position of each neighbor particle indices.
	 * @param aStartParticleIndex aStartParticleIndex start particle index.
	 */
	private void initialize(int[][] aAdjacentArray, int aStartParticleIndex) {
		if (aAdjacentArray.length == 0) {
			this.adjacentArray = aAdjacentArray.clone();
			this.numberOfParticles = 0;
			this.visited = new boolean[this.numberOfParticles];
			this.edgeTo = new int[this.numberOfParticles];
			this.distanceToStart = new int[this.numberOfParticles];
			this.startParticleIndex = 0;
			this.endParticleIndex = 0;
			this.diameterPath = null;
			return;
		}
		this.adjacentArray = aAdjacentArray.clone();
		this.numberOfParticles = aAdjacentArray.length;
		this.visited = new boolean[this.numberOfParticles];
		this.edgeTo = new int[this.numberOfParticles];
		this.distanceToStart = new int[this.numberOfParticles];
		this.startParticleIndex = aStartParticleIndex;
		this.depthFirstSearch(aStartParticleIndex);
		this.endParticleIndex = this.getFarthestParticleIndex();
		this.diameterPath = this.getPathFarthestParticleToStart();
	}

	/**
	 * Depth first search algorithm (recursive)
	 * 
	 * @param aPreviousParticleIndex Index of the previous particle.
	 */
	private void depthFirstSearch(int aPreviousParticleIndex) {
		this.visited[aPreviousParticleIndex] = true;
		for (int tmpNextParticleIndex : this.adjacentArray[aPreviousParticleIndex]) {
			if (!this.visited[tmpNextParticleIndex]) {
				this.edgeTo[tmpNextParticleIndex] = aPreviousParticleIndex;
				this.distanceToStart[tmpNextParticleIndex] = this.distanceToStart[aPreviousParticleIndex] + 1;
				this.depthFirstSearch(tmpNextParticleIndex);
			}
		}
	}
	
	/**
	 * Determine the index of farthest particle from start particle.
	 * 
	 * @return Index of farthest particle
	 */
	private int getFarthestParticleIndex() {
		int tmpResult = 0;
		int tmpMaxIndex = 0;
		
		for (int i = 0; i < this.numberOfParticles; i++) {
			if (this.distanceToStart[i] > tmpResult) {
				tmpResult = this.distanceToStart[i];
				tmpMaxIndex = i;
			}
		}
		return tmpMaxIndex;
	}

	/**
	 * Determine all particleindices from start to farthest particle
	 * 
	 * @return Particleindices list from start to farthes particle
	 */
	private int[] getPathFarthestParticleToStart() {
		Stack<Integer> tmpPath = new Stack<>();
		int tmpParticleIndex;
		int tmpIndex = 0;
		boolean tmpFoundStart = false;
		int[] tmpResult;
		
		tmpParticleIndex = this.endParticleIndex;
		while (!tmpFoundStart) {
			tmpPath.push(tmpParticleIndex);
			tmpParticleIndex = this.edgeTo[tmpParticleIndex];
			if (tmpParticleIndex == this.startParticleIndex) {
				tmpPath.push(tmpParticleIndex);
				tmpFoundStart = true;
			}
		}
		tmpResult = new int[tmpPath.size()];
		while (!tmpPath.empty()) {
			tmpResult[tmpIndex] = tmpPath.pop();
			tmpIndex++;
		}
		return tmpResult;
	}
        
}
