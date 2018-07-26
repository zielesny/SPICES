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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Spices
 *
 * @author Mirco Daniel, Achim Zielesny
 */
public class Spices {

    // <editor-fold defaultstate="collapsed" desc="Private final class variables">
    /**
     * Instance of SpicesUtility class
     */
    private final SpicesUtility spicesUtility = new SpicesUtility();
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Error message
     */
    private String errorMessage;

    /**
     * Monomer names
     */
    private String[] monomers;

    /**
     * Sub input structures.
     */
    private String[] parts;

    /**
     * This matrix is structured as follows: 1. column: Particle number
     * (1-based), 2. column: Particle, 3. column: X-coordinate of the particle
     * (if no coordinate information is specified: Beginning with 3rd column
     * connected particle numbers are listed), 4. column: Y-coordinate of the
     * particle, 5. column: Z-coordinate of the particle, 6ff. column: Particle
     * numbers of the particles which are connected to this particle
     */
    private String[][] particlePositionsAndConnections;

    /**
     * Maximum number of connections (bonds) of a single particle
     */
    private int maximumNumberOfConnectionsOfSingleParticle;

    /**
     * Number of disconnected structures.
     */
    private int numberOfParts;
    
    /**
     * Outer particle index (zero-based position of particles in this.structureTokens)
     */
    private int[] outerParticleIndices;
    
    /**
     * Outer backbone index (zero-based position of particles: value corresponds to index number in input string)
     */
    private int[] outerBackboneIndices;
    
    /**
     * Maximum outer backbone index
     */
    private int maxOuterBackboneIndex;
    
    /**
     * Valence numbers of a particle
     */
    private int[] numberOfConnects;

    /**
     * True: The input structure is valid, false: Otherwise
     */
    private boolean isValid;

    /**
     * True: The input structure is a monomer, false: Otherwise. False is
     * default value.
     */
    private boolean isMonomer;

    /**
     * Where there is backbone particles
     */
    private boolean hasBackboneAttribute;
    
    /**
     * Whether there is disconnected structure in input structure
     */
    private boolean hasParts;

    /**
     * HashMap that maps particles of molecule to their frequencies
     */
    private HashMap<String, ParticleFrequency> particleToFrequencyMap;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Protected class variables">
    /**
     * Input structure
     */
    protected String inputStructure;

    /**
     * Part of spices
     */
    protected SpicesInner[] partOfSpices;

    /**
     * Number of total particles
     */
    protected int numberOfTotalParticles;

    /**
     * Structure tokens
     */
    protected String[] outerStructureTokens;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * * Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (NOT allowed to be a monomer
     * or to contain monomer shortcuts)
     */
    public Spices(String anInputStructure) {
        this.initialize(anInputStructure, false, 1, new PointInSpace[]{null}, new PointInSpace[]{null}, 0.0, new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY));
    }

    /**
     * * Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (NOT allowed to be a monomer
     * or to contain monomer shortcuts)
     * @param anAvailableParticles Available particles
     */
    public Spices(String anInputStructure, HashMap<String, String> anAvailableParticles) {
        this.initialize(anInputStructure, false, 1, new PointInSpace[]{null}, new PointInSpace[]{null}, 0.0, anAvailableParticles);
    }

    /**
     * * Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     */
    public Spices(String anInputStructure, boolean anIsMonomer) {
        this.initialize(anInputStructure, anIsMonomer, 1, new PointInSpace[]{null}, new PointInSpace[]{null}, 0.0, new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY));
    }

    /**
     * * Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anAvailableParticles Hashmap of available particles
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     */
    public Spices(String anInputStructure, HashMap<String, String> anAvailableParticles, boolean anIsMonomer) {
        this.initialize(anInputStructure, anIsMonomer, 1, new PointInSpace[]{null}, new PointInSpace[]{null}, 0.0, anAvailableParticles);
    }

    /**
     * * Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     * @param aStartIndex First particle number in the Spices matrix (default:
     * 1)
     */
    public Spices(String anInputStructure, boolean anIsMonomer, int aStartIndex) {
        this.initialize(anInputStructure, anIsMonomer, aStartIndex, new PointInSpace[]{null}, new PointInSpace[]{null}, 0.0, new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY));
    }

    /**
     * * Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anAvailableParticles Hashmap of available particles
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     * @param aStartIndex First particle number in the Spices matrix (default:
     * 1)
     */
    public Spices(String anInputStructure, HashMap<String, String> anAvailableParticles, boolean anIsMonomer, int aStartIndex) {
        this.initialize(anInputStructure, anIsMonomer, aStartIndex, new PointInSpace[]{null}, new PointInSpace[]{null}, 0.0, anAvailableParticles);
    }

    /**
     * * Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     * (default: false)
     * @param aStartIndex First particle number in the Spices matrix (default:
     * 1)
     * @param aFirstParticle Cartesian coordinate of a single first particle
     * @param aLastParticle Cartesian coordinate of a single last particle
     * @param aBondLength User defined bond length for all connections between
     * particles
     */
    public Spices(String anInputStructure, boolean anIsMonomer, int aStartIndex, PointInSpace aFirstParticle, PointInSpace aLastParticle, double aBondLength) {
        this.initialize(anInputStructure, anIsMonomer, aStartIndex, new PointInSpace[]{aFirstParticle}, new PointInSpace[]{aLastParticle}, aBondLength,
                new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY));
    }

    /**
     * * Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anAvailableParticles Hashmap of available particles
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     * (default: false)
     * @param aStartIndex First particle number in the Spices matrix (default:
     * 1)
     * @param aFirstParticle Cartesian coordinate of a single first particle
     * @param aLastParticle Cartesian coordinate of a single last particle
     * @param aBondLength User defined bond length for all connections between
     * particles
     */
    public Spices(String anInputStructure, HashMap<String, String> anAvailableParticles, boolean anIsMonomer, int aStartIndex, PointInSpace aFirstParticle,
            PointInSpace aLastParticle, double aBondLength) {
        this.initialize(anInputStructure, anIsMonomer, aStartIndex, new PointInSpace[]{aFirstParticle}, new PointInSpace[]{aLastParticle}, aBondLength,
                anAvailableParticles);
    }

    /**
     * * Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (NOT allowed to be a monomer
     * or to contain monomer shortcuts)
     * @param aStartIndex First particle number in the Spices matrix (default:
     * 1)
     * @param aFirstParticle Cartesian coordinates of first particles
     * @param aLastParticle Cartesian coordinates of last particles
     * @param aBondLength User defined bond length for all connections between
     * particles
     */
    public Spices(String anInputStructure, int aStartIndex, PointInSpace[] aFirstParticle, PointInSpace[] aLastParticle, double aBondLength) {
        this.initialize(anInputStructure, false, aStartIndex, aFirstParticle, aLastParticle, aBondLength, new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY));
    }

    /**
     * * Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     * (default: false)
     * @param aStartIndex First particle number in the Spices matrix (default:
     * 1)
     * @param aFirstParticle Cartesian coordinates of first particles
     * @param aLastParticle Cartesian coordinates of last particles
     * @param aBondLength User defined bond length for all connections between
     * particles
     */
    public Spices(String anInputStructure, boolean anIsMonomer, int aStartIndex, PointInSpace[] aFirstParticle, PointInSpace[] aLastParticle,
            double aBondLength) {
        this.initialize(anInputStructure, anIsMonomer, aStartIndex, aFirstParticle, aLastParticle, aBondLength, new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY));
    }

    /**
     * * Sets all properties of a Spices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anAvailableParticles Hashmap of available particles
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     * (default: false)
     * @param aStartIndex First particle number in the Spices matrix (default:
     * 1)
     * @param aFirstParticle Cartesian coordinates of first particles
     * @param aLastParticle Cartesian coordinates of last particles
     * @param aBondLength User defined bond length for all connections between
     * particles
     */
    public Spices(String anInputStructure, HashMap<String, String> anAvailableParticles, boolean anIsMonomer, int aStartIndex, PointInSpace[] aFirstParticle,
            PointInSpace[] aLastParticle, double aBondLength) {
        this.initialize(anInputStructure, anIsMonomer, aStartIndex, aFirstParticle, aLastParticle, aBondLength, anAvailableParticles);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Destructors">
    /**
     * Destructor method for spicesMatrix
     */
    public void destroySpicesMatrix() {
        this.particlePositionsAndConnections = null;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Sets the input structure and reinitializes all properties
     *
     * @param anInputStructure An input structure (NOT allowed to be a monomer
     * or to contain monomer shortcuts)
     */
    public void setInputStructure(String anInputStructure) {
        this.resetProperty();
        this.initialize(anInputStructure, false, 1, new PointInSpace[]{null}, new PointInSpace[]{null}, 0.0, new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY));
    }

    /**
     * Sets the input structure and reinitializes all properties
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     */
    public void setInputStructure(String anInputStructure, boolean anIsMonomer) {
        this.resetProperty();
        this.initialize(anInputStructure, anIsMonomer, 1, new PointInSpace[]{null}, new PointInSpace[]{null}, 0.0, new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY));
    }

    /**
     * Sets the input structure and reinitializes all properties
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anAvailableParticles Hashmap of available particles
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     */
    public void setInputStructure(String anInputStructure, HashMap<String, String> anAvailableParticles, boolean anIsMonomer) {
        this.resetProperty();
        this.initialize(anInputStructure, anIsMonomer, 1, new PointInSpace[]{null}, new PointInSpace[]{null}, 0.0, anAvailableParticles);
    }

    /**
     * Sets the coordinates of a PSmile object
     * 
     * @param aStartIndex Start index
     * @param aFirstParticlePositions First particle positions
     * @param aLastParticlePositions Last particle positions
     * @param aBondLength Bond length
     */
    public void setCoordinates(int aStartIndex, PointInSpace[] aFirstParticlePositions, PointInSpace[] aLastParticlePositions, double aBondLength) {
        if (this.inputStructure == null || this.inputStructure.isEmpty()) {
            return;
        }
        this.initialize(this.inputStructure, false, aStartIndex, aFirstParticlePositions, aLastParticlePositions, aBondLength, new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY));
    }
    
    /**
     * Returns the input structure
     *
     * @return Input structure
     */
    public String getInputStructure() {
        return this.inputStructure;
    }

    /**
     * Returns structure tokens
     *
     * @return Structure tokens
     */
    public String[] getStructureTokens() {
        return this.outerStructureTokens;
    }

    /**
     * Returns error message
     *
     * @return Error message
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Particle index
     *
     * @return Particle indices
     */
    public int[] getParticleIndices() {
        return this.outerParticleIndices;
    }

    /**
     * Backbone indices
     * 
     * @return Backbone indices (may be null)
     */
    public int[] getBackboneIndices() {
        return this.outerBackboneIndices;
    }

    /**
     * Maximum backbone index
     * 
     * @return Maximum backbone index or -1 if no backbone attribute is defined
     */
    public int getMaxBackboneIndex() {
        if (this.maxOuterBackboneIndex == -1) {
            if (this.outerBackboneIndices != null && this.outerBackboneIndices.length > 0) {
                this.maxOuterBackboneIndex = this.outerBackboneIndices[0];
                for (int i = 1; i < this.outerBackboneIndices.length; i++) {
                    if (this.outerBackboneIndices[i] > this.maxOuterBackboneIndex) {
                        this.maxOuterBackboneIndex = this.outerBackboneIndices[i];
                    }
                }
            }
        }
        return this.maxOuterBackboneIndex;
    }
    
    /**
     * Returns Monomers
     *
     * @return Monomers
     */
    public String[] getMonomers() {
        return this.monomers;
    }

    /**
     * Returns the Spices particle positions and connections. 
     * This matrix is structured as follows:
     * 1. column: Particle number (1-based or startIndex-based),
     * 2. column: Particle,
     * 3. column: Backbone index
     * 4. column: X-coordinate of the particle (if no coordinate information is
     * specified: Beginning with 3rd column connected particle numbers are
     * listed),
     * 5. column: Y-coordinate of the particle,
     * 6. column: Z-coordinate of the particle,
     * 6ff. column: Relative particle positions to the the particle of other
     * particles which are connected to the particle (e.g. "1" means a
     * connection to the next particle, "-1" means a connection to the previous
     * particle
     *
     * @return Spices particle positions and connections
     */
    public String[][] getParticlePositionsAndConnections() {
        return this.particlePositionsAndConnections;
    }

    /**
     * Maximum number of connections (bonds) of a single particle
     *
     * @return Maximum number of connections (bonds) of a single particle
     */
    public int getMaximumNumberOfConnectionsOfSingleParticle() {
        return this.maximumNumberOfConnectionsOfSingleParticle;
    }

    /**
     * Parts of Spices
     *
     * @return If the structure consists of multiple parts a FSmile of each part
     * is returned, otherwise null is returned.
     */
    public SpicesInner[] getPartsOfSpices() {
        return this.partOfSpices;
    }

    /**
     * HashMap that maps particles of molecule to their frequencies. NOTE:
     * Molecular structure MUST be valid (NO checks are performed) and is NOT
     * allowed to contain monomer shortcuts.
     *
     * @return HashMap that maps particles of molecule to their frequencies
     */
    public HashMap<String, ParticleFrequency> getParticleToFrequencyMap() {
        if (this.particleToFrequencyMap == null || this.particleToFrequencyMap.isEmpty()) {
            this.determineParticleFequenciesOfMolecularStructure();
        }
        return this.particleToFrequencyMap;
    }

    /**
     * Returns number of different particles
     *
     * @return Number of different particles
     */
    public int getNumberOfDifferentParticles() {
        return this.getParticleToFrequencyMap().size();
    }

    /**
     * Returns frequency of specified particle in molecule
     *
     * @param aParticle Particle
     * @return Frequency of specified particle in molecule
     */
    public int getFrequencyOfSpecifiedParticle(String aParticle) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle == null || !this.getParticleToFrequencyMap().containsKey(aParticle)) {
            return 0;
        }
        // </editor-fold>
        return this.getParticleToFrequencyMap().get(aParticle).getFrequency();
    }

    /**
     * Returns array of particle frequencies of molecule
     *
     * @return Array of particle frequencies of molecule
     */
    public ParticleFrequency[] getParticleFrequencies() {
        return this.getParticleToFrequencyMap().values().toArray(new ParticleFrequency[0]);
    }

    /**
     * Returns array of sorted particle frequencies of molecule
     *
     * @return Array of sorted particle frequencies of molecule
     */
    public ParticleFrequency[] getSortedParticleFrequencies() {
        ParticleFrequency[] tmpParticleFrequencies = this.getParticleToFrequencyMap().values().toArray(new ParticleFrequency[0]);
        Arrays.sort(tmpParticleFrequencies);
        return tmpParticleFrequencies;
    }

    /**
     * Checks whether there is backbone particles
     * 
     * @return True: yes, false: no
     */
    public boolean hasBackboneParticle() {
        return this.hasBackboneAttribute;
    }
    
    /**
     * Checks whether molecule contains particle
     *
     * @param aParticle Particle
     * @return True: Molecule contains particle, false: Otherwise
     */
    public boolean hasParticle(String aParticle) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle == null) {
            return false;
        }
        // </editor-fold>
        return this.getParticleToFrequencyMap().containsKey(aParticle);
    }

    /**
     * Returns whether structure is out of substructures.
     *
     * @return Informations flag about the input structure has more parts then
     * 1.
     */
    public boolean hasMultipleParts() {
        return (this.parts.length > 1);
    }
    
    /**
     * Returns array with particles of molecule
     *
     * @return Array with particles of molecule
     */
    public String[] getParticles() {
        return this.getParticleToFrequencyMap().keySet().toArray(new String[0]);
    }

    /**
     * Returns total number of particles of molecule
     *
     * @return Total number of particles of molecule
     */
    public int getTotalNumberOfParticles() {
        return this.numberOfTotalParticles;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Chain and next neighbor related methods">
    /**
     * Determines all available neighbors
     *
     * @param aSegmentLength Length of segment (2: dimer, 3: trimer etc.)
     * @param aHasDoublet Whether doublet should be considered or not (e.g. if
     * true "A-F" and "F-A" will be returned instead one of both)
     * @return All available neighbors with all segment lengths until
     * aSegmentLength: Index 0: Particles, Index 1: Dimers, Index 2: Trimes etc.
     * up to index (aSegmentLength - 1)
     */
    public String[][] getNextNeighbors(int aSegmentLength, boolean aHasDoublet) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.outerStructureTokens == null || aSegmentLength < 1) {
            return null;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initialisation">
        String[][] resultString = new String[aSegmentLength][];
        ArrayDeque<String> tmpInterimResult = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        final ArrayDeque<int[]> tmpArray = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        int tmpPartsLength;
        int tmpSegmentLength = aSegmentLength;
        boolean tmpHasNeighbors = false;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Determine neighbor">
        tmpPartsLength = this.numberOfParts;
        final LinkedList<int[]>[] tmpLinkList = new LinkedList[tmpPartsLength];
        for (int i = 0; i < tmpPartsLength; i++) {
            tmpLinkList[i] = this.partOfSpices[i].getNextNeighborIndex(tmpSegmentLength, this.partOfSpices[i]);
        }
        for (int i = 0; i < tmpSegmentLength; i++) {
            for (int j = 0; j < tmpPartsLength; j++) {
                if (!tmpHasNeighbors && tmpLinkList[j].size() > 0) {
                    tmpHasNeighbors = true;
                }
                final int tmpAllowedLinkLength = i + 1;
                tmpArray.addAll(tmpLinkList[j].stream()
                        .filter(currentLink -> currentLink.length == tmpAllowedLinkLength)
                        .collect(Collectors.toList()));
                if (!tmpArray.isEmpty()) {
                    tmpInterimResult.addAll((Arrays.asList(this.getParticleList(tmpArray, j, aHasDoublet))));
                    tmpArray.clear();
                }
            }
            if (tmpInterimResult.isEmpty()) {
                resultString[i] = null;
            } else {
                resultString[i] = tmpInterimResult.toArray(new String[0]);
            }
            tmpInterimResult.clear();
        }
        if (!tmpHasNeighbors) {
            return null;
        }
        return resultString;
        // </editor-fold>
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Particle coordinates related methods">
    /**
     * Returns coordinates of particles
     *
     * @param aFirstParticleCoordinate Coordinate of the first particle
     * @param aLastParticleCoordinate Coordinate of the last particle
     * @param aBondLength BondLength
     * @return Coordinates of the particles (index of particle can be examined
     * by getParticlesIndex() method). First index is index of molecular
     * structure, second index is index of particle. If PointInSpaceInterface is
     * unknown or different in aFirstParticleCoordinate and
     * aLastParticleCoordinate null is returned.
     */
    public PointInSpace[][] getParticleCoordinates(PointInSpace aFirstParticleCoordinate, PointInSpace aLastParticleCoordinate, double aBondLength) {
        return this.getParticleCoordinates(new PointInSpace[]{aFirstParticleCoordinate},
                                            new PointInSpace[]{aLastParticleCoordinate}, aBondLength);
    }
    
    /**
     * Returns coordinates of particles
     *
     * @param aFirstParticleCoordinates Coordinates of the first particle
     * @param aLastParticleCoordinates Coordinates of the last particle
     * @param aBondLength BondLength
     * @return Coordinates of the particles (index of particle can be examined
     * by getParticlesIndex() method) First index is index of molecular
     * structure, second index is index of particle.
     */
    public PointInSpace[][] getParticleCoordinates(PointInSpace[] aFirstParticleCoordinates,
            PointInSpace[] aLastParticleCoordinates, double aBondLength) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.getInputStructure() == null || this.getInputStructure().isEmpty()) {
            return null;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initialize variables">
        int tmpPartsCount;
        int tmpPartLength;
        int tmpTargetIndexPosition = 0;
        PointInSpace[][] resultCoordinates = new PointInSpace[aFirstParticleCoordinates.length][];
        tmpPartsCount = this.partOfSpices.length;
        for (int i = 0; i < aFirstParticleCoordinates.length; i++) {
            resultCoordinates[i] = new PointInSpace[this.numberOfTotalParticles];
        }

        // </editor-fold>
        if (this.numberOfTotalParticles == 1) {
            // <editor-fold defaultstate="collapsed" desc="1 particle only in molecular structure">
            for (int i = 0; i < aFirstParticleCoordinates.length; i++) {
                resultCoordinates[i] = new PointInSpace[]{aFirstParticleCoordinates[i]};
            }

            // </editor-fold>
        } else {
            // <editor-fold defaultstate="collapsed" desc="Several particles in molecular structure">
            for (int i = 0; i < tmpPartsCount; i++) {
                tmpPartLength = this.partOfSpices[i].getInnerParticles().length;
                for (int j = 0; j < aFirstParticleCoordinates.length; j++) {
                    PointInSpace[] tmpInterimResult = this.getCoordinatesOfTokens(this, i, aFirstParticleCoordinates[j], aLastParticleCoordinates[j], aBondLength);
                    System.arraycopy(tmpInterimResult, 0, resultCoordinates[j], tmpTargetIndexPosition, tmpInterimResult.length);
                }
                tmpTargetIndexPosition += tmpPartLength;
            }
            // </editor-fold>
        }
        return resultCoordinates;
    }

    /**
     * Returns monomer names of structure
     *
     * @return Monomer names of structure or null if none were found
     */
    public String[] getMonomerList() {
        HashMap<String, String> tmpMonomersHashMap = new HashMap<>();
        for (String tmpSingleToken : this.outerStructureTokens) {
            if (spicesUtility.isMonomer(tmpSingleToken) && !tmpMonomersHashMap.containsKey(tmpSingleToken)) {
                tmpMonomersHashMap.put(tmpSingleToken, tmpSingleToken);
            }
        }
        if (tmpMonomersHashMap.isEmpty()) {
            return null;
        } else {
            return tmpMonomersHashMap.keySet().toArray(new String[0]);
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Is methods">
    /**
     * Returns true if the input structure is valid, false: Otherwise
     *
     * @return True: Input structure is valid, false: Otherwise
     */
    public boolean isValid() {
        return this.isValid;
    }

    /**
     * Returns true if the input structure is a monomer, false: Otherwise
     *
     * @return True: Input structure is a monomer, false: Otherwise
     */
    public boolean isMonomer() {
        return this.isMonomer;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Returns the number of particles for display
     *
     * @return Number of particles for display
     */
    public int getNumberOfDisplayParticles() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.outerStructureTokens == null) {
            return 0;
        }

        // </editor-fold>
        int tmpCounter = 0;
        for (String tmpSingleStructureToken : this.outerStructureTokens) {
            if (spicesUtility.isParticle(tmpSingleStructureToken)) {
                tmpCounter++;
            }
        }
        return tmpCounter;
    }
    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Protected methods">
    /**
     * Get coordinate of tokens. NOTE: No checks are performed due to
     * performance reasons.
     *
     * @param aSpicesBase: A part of Spices
     * @param aPartIndex: Index of the part
     * @param aFirstParticleCoordinate: First particle coordinate
     * @param aLastParticleCoordinate: Last particle coordinate
     * @param aBondLength: Bond length
     * @return Coordinate: of tokens
     */
    protected PointInSpace[] getCoordinatesOfTokens(
            Spices aSpicesBase, 
            int aPartIndex, 
            IPointInSpace aFirstParticleCoordinate, 
            IPointInSpace aLastParticleCoordinate,
            double aBondLength) {
        // <editor-fold defaultstate="collapsed" desc="Local variables">
        double tmpBondLength = aBondLength;
        double tmpDistanceOfMainChain;
        double tmpDeltaXOfChain;
        double tmpDeltaYOfChain;
        double tmpDeltaZOfChain;
        double tmpDeltaXOfBond;
        double tmpDeltaYOfBond;
        double tmpDeltaZOfBond;
        int[] tmpParticleIndices = aSpicesBase.partOfSpices[aPartIndex].getInnerParticleIndices();
        int[] tmpMainChainIndices = null;
        int tmpNumberOfParticles = tmpParticleIndices.length;
        boolean[] tmpHasCalculatedArray = new boolean[tmpNumberOfParticles];
        PointInSpace[] tmpParticleCoordinates = new PointInSpace[tmpNumberOfParticles];
        ArrayDeque<Integer> tmpSeedParticleList = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        
        if (aSpicesBase.partOfSpices[aPartIndex].getStartParticleIndex() == -1) {
            tmpMainChainIndices = aSpicesBase.partOfSpices[aPartIndex].getHeuristicDiameter();
        } else {
            tmpMainChainIndices = aSpicesBase.partOfSpices[aPartIndex].getPathStartToEnd();
        }
        int tmpMainChainSize = tmpMainChainIndices.length;

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Calculate distance of first particle to last particle">
        Arrays.fill(tmpHasCalculatedArray, false);
        tmpDeltaXOfChain = aLastParticleCoordinate.getX() - aFirstParticleCoordinate.getX();
        tmpDeltaYOfChain = aLastParticleCoordinate.getY() - aFirstParticleCoordinate.getY();
        tmpDeltaZOfChain = aLastParticleCoordinate.getZ() - aFirstParticleCoordinate.getZ();
        tmpDistanceOfMainChain = Math.sqrt(tmpDeltaXOfChain * tmpDeltaXOfChain + tmpDeltaYOfChain * tmpDeltaYOfChain + tmpDeltaZOfChain * tmpDeltaZOfChain);
        int tmpMainChainBondSize = tmpMainChainSize - 1;
        if (tmpDistanceOfMainChain < tmpMainChainBondSize * tmpBondLength) {
            tmpDeltaXOfBond = tmpDeltaXOfChain / tmpMainChainBondSize;
            tmpDeltaYOfBond = tmpDeltaYOfChain / tmpMainChainBondSize;
            tmpDeltaZOfBond = tmpDeltaZOfChain / tmpMainChainBondSize;
        } else {
            tmpDeltaXOfBond = tmpDeltaXOfChain * aBondLength / tmpDistanceOfMainChain;
            tmpDeltaYOfBond = tmpDeltaYOfChain * aBondLength / tmpDistanceOfMainChain;
            tmpDeltaZOfBond = tmpDeltaZOfChain * aBondLength / tmpDistanceOfMainChain;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Determine coordinates of main chain particles">
        if (aSpicesBase.partOfSpices[aPartIndex].getStartParticleIndex() == -1 && tmpMainChainIndices[0] > tmpMainChainIndices[tmpMainChainSize - 1]) {
                spicesUtility.reverseIntegerArray(tmpMainChainIndices);
        }
        // <editor-fold defaultstate="collapsed" desc="- aFirstParticleCoordinate is an instance of PointInSpace">
        for (int i = 0; i < tmpMainChainSize; i++) {
            tmpParticleCoordinates[tmpMainChainIndices[i]] = new PointInSpace(
                    aFirstParticleCoordinate.getX() + (i) * tmpDeltaXOfBond,
                    aFirstParticleCoordinate.getY() + (i) * tmpDeltaYOfBond, 
                    aFirstParticleCoordinate.getZ() + (i) * tmpDeltaZOfBond);
            tmpHasCalculatedArray[tmpMainChainIndices[i]] = true;
        }
        // </editor-fold>
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Determine beginning of side chain">
        for (int i = 0; i < tmpMainChainSize; i++) {
            for (int tmpItem : aSpicesBase.partOfSpices[aPartIndex].getAdjacentArray()[tmpMainChainIndices[i]]) {
                if (tmpMainChainIndices[i] == tmpMainChainIndices[i] && !tmpHasCalculatedArray[tmpItem]) {
                    tmpSeedParticleList.add(tmpItem);
                    tmpParticleCoordinates[tmpItem] = tmpParticleCoordinates[tmpMainChainIndices[i]];
                    tmpHasCalculatedArray[tmpItem] = true;
                }
            }
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Determine coordinates of side chain">
        ArrayDeque<Integer> tmpIndicesOfSideChainParticles = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        for (int tmpSideParticleItem : tmpSeedParticleList) {
            this.setIndicesOfSideChainParticles(tmpSideParticleItem, tmpIndicesOfSideChainParticles, tmpHasCalculatedArray, aPartIndex);
            for (int tmpIndexOfNeighborParticlesItem : tmpIndicesOfSideChainParticles) {
                tmpParticleCoordinates[tmpIndexOfNeighborParticlesItem] = tmpParticleCoordinates[tmpSideParticleItem];
                tmpHasCalculatedArray[tmpIndexOfNeighborParticlesItem] = true;
            }
            tmpIndicesOfSideChainParticles.clear();
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Determine coordinates of leftover particles">
        Queue<Integer> tmpHasNotCalculatedList = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        int tmpActualParticleIndex ;
        boolean tmpHasFound;
        for (int i = 0; i < tmpHasCalculatedArray.length; i++) {
            if (!tmpHasCalculatedArray[i]) {
                tmpHasNotCalculatedList.add(i);
            }
        }
        while (!tmpHasNotCalculatedList.isEmpty()) {
            tmpActualParticleIndex = tmpHasNotCalculatedList.remove();
            tmpHasFound = false;
            for (int tmpItem : aSpicesBase.partOfSpices[aPartIndex].getAdjacentArray()[tmpActualParticleIndex]) {
                if (tmpHasCalculatedArray[tmpItem]) {
                    tmpParticleCoordinates[tmpActualParticleIndex] = tmpParticleCoordinates[tmpItem];
                    tmpHasCalculatedArray[tmpActualParticleIndex] = true;
                    tmpHasFound = true;
                    break;
                }
            }
            if (!tmpHasFound) {
                tmpHasNotCalculatedList.add(tmpActualParticleIndex);
            }
        }
        // </editor-fold>
        return tmpParticleCoordinates;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Initialize and reset methods">
    /**
     * Initialize method
     *
     * @param anInputStructure An input structure
     * @param aParticleToDescriptionMap Descriptions for the particles
     * @param anIsMonomer True: anInputStructure is a monomer, false: Otherwise
     * (default: false)
     * @param aStartIndex First particle number in the Spices matrix (default:
 1)
     * @param aFirstParticlePositions Cartesian coordinates of first particles
     * @param aLastParticlePositions Cartesian coordinates of last particles
     * @param aBondLength User defined bond length for all connections between
     * particles
     * @param anAvailableParticles User defined list of available particles
     */
    private void initialize(String anInputStructure, boolean anIsMonomer, int aStartIndex, PointInSpace[] aFirstParticlePositions, PointInSpace[] aLastParticlePositions,
            double aBondLength, HashMap<String, String> anAvailableParticles) {
        // <editor-fold defaultstate="collapsed" desc="Initialisation">
        String tmpCheckMessage;
        HashMap<String, SpicesInner> tmpPartsHashMap = new HashMap<>(SpicesConstants.DEFAULT_NUMBER_OF_PARTICLES);
        ArrayDeque<String> tmpStructureTokens = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        int tmpTotalParticlesCount = 0;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Check outer parts">
        tmpCheckMessage = this.preCheckInputStructure(anInputStructure);
        if (!tmpCheckMessage.isEmpty()) {
            this.isValid = false;
            this.errorMessage = tmpCheckMessage;
            return;
        }
        this.inputStructure = this.spicesUtility.eliminateWhiteSpaces(anInputStructure);
        this.isMonomer = anIsMonomer;
        tmpCheckMessage = this.checkOuterParts(this.inputStructure);
        if (!tmpCheckMessage.isEmpty()) {
            this.isValid = false;
            this.errorMessage = tmpCheckMessage;
            return;
        }
        this.outerStructureTokens = spicesUtility.getStructureTokens(anInputStructure);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Check inner parts">
        this.parts = this.partStructures(this.inputStructure);
        this.partOfSpices = new SpicesInner[this.numberOfParts];
        for (int i = 0; i < this.numberOfParts; i++) {
            if (!tmpPartsHashMap.containsKey(this.parts[i])) {
                this.partOfSpices[i] = new SpicesInner(this.parts[i], anAvailableParticles, anIsMonomer, aStartIndex,
                        aFirstParticlePositions, aLastParticlePositions, aBondLength);
            } else {
                this.partOfSpices[i] = tmpPartsHashMap.get(this.parts[i]);
            }
            if (this.partOfSpices[i].getErrorMessage() != null) {
                this.isValid = false;
                this.errorMessage = this.partOfSpices[i].getErrorMessage();
                return;
            }
            tmpPartsHashMap.put(this.parts[i], this.partOfSpices[i]);
            tmpTotalParticlesCount += this.partOfSpices[i].getInnerParticles().length;
        }
        if (this.parts.length == 1) {
            this.outerStructureTokens = this.partOfSpices[0].getInnerStructureTokens();
        } else {
            for (int i = 0; i < parts.length; i++) {
                tmpStructureTokens.addAll(Arrays.asList(this.partOfSpices[i].getInnerStructureTokens()));
            }
            this.outerStructureTokens = tmpStructureTokens.toArray(new String[0]);
        }
        this.isValid = true;
        this.errorMessage = null;
        // </editor-fold>
        this.numberOfTotalParticles = tmpTotalParticlesCount;
        this.outerParticleIndices = new int[tmpTotalParticlesCount];
        this.outerBackboneIndices = new int[tmpTotalParticlesCount];
        this.maxOuterBackboneIndex = -1;
        this.monomers = this.getMonomerList();
        this.hasBackboneAttribute = false;
        this.initializePartsOfSpicesBase(aStartIndex, aFirstParticlePositions, aLastParticlePositions, aBondLength);
        int tmpMaxConnectionNumber = this.partOfSpices[0].getMaximumNumberOfConnectionsOfSingleParticle();
        for (int i = 1; i < this.partOfSpices.length; i++) {
            if (tmpMaxConnectionNumber < this.partOfSpices[i].getMaximumNumberOfConnectionsOfSingleParticle()) {
                tmpMaxConnectionNumber = this.partOfSpices[i].getMaximumNumberOfConnectionsOfSingleParticle();
            }
        }
        this.maximumNumberOfConnectionsOfSingleParticle = tmpMaxConnectionNumber;
    }

    /**
     * Initialize method for structures with sub structures
     */
    private void initializePartsOfSpicesBase(int aStartIndex, 
            PointInSpace[] aFirstParticlePositions, PointInSpace[] aLastParticlePositions, double aBondLength) {
        // <editor-fold defaultstate="collapsed" desc="Initialisation">
        int tmpPartsCount = this.parts.length;
        int tmpSourceIndex = 0;
        int tmpTargetIndex = 0;
        int tmpMatrixDimension = 0;
        int tmpIndex = 0;
        int tmpFirstParticlesPosition;
        String[][][] tmpParticlePositionsAndConnections = new String[tmpPartsCount][][];

        // </editor-fold>
        if (aFirstParticlePositions == null) {
            for (int i = 0; i < tmpPartsCount; i++) {
                tmpParticlePositionsAndConnections[i] = this.getParticlePositionsAndConnections(i, aStartIndex);
            }
        } else{
            for (int i = 0; i < tmpPartsCount; i++) {
                tmpParticlePositionsAndConnections[i] = this.getParticlePositionsAndConnections(i, aStartIndex, aFirstParticlePositions, aLastParticlePositions, aBondLength);
            }
        }
        tmpFirstParticlesPosition =  tmpParticlePositionsAndConnections[0].length / this.partOfSpices[0].getInnerParticles().length;
        this.numberOfConnects = new int[this.numberOfTotalParticles];
        for (int i = 0; i < tmpPartsCount; i++) {
            System.arraycopy(this.partOfSpices[i].getNumberOfConnects(), 0, this.numberOfConnects, tmpTargetIndex, this.partOfSpices[i].getNumberOfConnects().length);
            tmpTargetIndex += this.partOfSpices[i].getInnerParticles().length;
        }
        for (int i = 0; i < tmpPartsCount; i++) {
            if (this.partOfSpices[i].hasBackboneAttribute()) {
                this.hasBackboneAttribute = true;
                break;
            }
        }
        tmpTargetIndex = 0;
        int tmpTotalParticlePositions = this.numberOfTotalParticles * tmpFirstParticlesPosition;
        this.particlePositionsAndConnections = new String[tmpTotalParticlePositions][];
        for (int i = 0; i < tmpFirstParticlesPosition; i++) {
            for (int j = 0; j < this.numberOfTotalParticles; j++) {
                tmpMatrixDimension = this.numberOfConnects[j] + 6;
                this.particlePositionsAndConnections[tmpIndex] = new String[tmpMatrixDimension];
                tmpIndex++;
            }
        }
        for (int i = 0; i < tmpPartsCount; i++) {
            System.arraycopy(this.partOfSpices[i].getInnerParticleIndices(), 0, this.outerParticleIndices, tmpTargetIndex, this.partOfSpices[i].getInnerParticleIndices().length);
            System.arraycopy(this.partOfSpices[i].getInnerBackboneIndices(), 0, this.outerBackboneIndices, tmpTargetIndex, this.partOfSpices[i].getInnerBackboneIndices().length);
            tmpTargetIndex += this.partOfSpices[i].getInnerParticles().length;
        }
        this.maxOuterBackboneIndex = -1;
        tmpTargetIndex = 0;
        for (int i = 0; i < tmpFirstParticlesPosition; i++) {
            for (int j = 0; j < tmpPartsCount; j++) {
                for (int k = 0; k < this.partOfSpices[j].getInnerParticles().length; k++) {
                    tmpSourceIndex = i * this.partOfSpices[j].getInnerParticles().length + k;
                    System.arraycopy(tmpParticlePositionsAndConnections[j][tmpSourceIndex], 0, this.particlePositionsAndConnections[tmpTargetIndex], 0,
                            tmpParticlePositionsAndConnections[j][tmpSourceIndex].length);
                    tmpTargetIndex++;
                }
            }
        }

        // Reset number of particles in spicesMatrix
        int tmpParticleIndex = 0;
        for (int i = 0; i < tmpFirstParticlesPosition; i++) {
            for (int j = 0; j < this.numberOfTotalParticles; j++) {
                this.particlePositionsAndConnections[tmpParticleIndex][0] = Integer.toString(tmpParticleIndex + aStartIndex);
                tmpParticleIndex++;
            }
        }
    }

    /**
     * Reset property method
     */
    private void resetProperty() {
        this.errorMessage = null;
        this.inputStructure = null;
        this.particleToFrequencyMap = null;
        this.outerStructureTokens = null;
        this.outerParticleIndices = null;
        this.monomers = null;
        this.particlePositionsAndConnections = null;
        this.isValid = false;
        this.isMonomer = false;
        this.maximumNumberOfConnectionsOfSingleParticle = 0;
        this.numberOfParts = 0;
        this.numberOfTotalParticles = 0;
        this.hasParts = false;
        this.partOfSpices = null;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Structure check related methods">
    
    /**
     * precheck methode for inputStructure
     * @param aStructure Molecular structure
     * @return Error message or ""
     */
    private String preCheckInputStructure (String aStructure) {
        if (aStructure == null || aStructure.isEmpty()) {
            return "";
        }
        if (aStructure.length() < 3) {
            return "";
        }
        char[] tmpStructureCharArray = aStructure.toCharArray();
        for (int i = 1; i < tmpStructureCharArray.length - 1; i++) {
            if (Character.isWhitespace(tmpStructureCharArray[i])) {
                if (Character.isLetterOrDigit(tmpStructureCharArray[i - 1]) && Character.isLetterOrDigit(tmpStructureCharArray[i + 1])) {
                    return MessageSpices.getString("StructureCheck.InvalidWhiteSpace");
                }
            }
        }
        return "";
    }
    
    /**
     * Checks the outer parts of the input structure
     *
     * @return Error message or null
     */
    private String checkOuterParts(String aInputStructure) {
        // <editor-fold defaultstate="collapsed" desc="Simple check">
        if (aInputStructure == null || aInputStructure.isEmpty()) {
            return MessageSpices.getString("StructureCheck.NoTokens");
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initial settings">
        int tmpNumberOfOpeningAngleBrackets = spicesUtility.getFrequencyOfCharacterInString(this.inputStructure, "<");
        int tmpNumberOfClosingAngleBrackets = spicesUtility.getFrequencyOfCharacterInString(this.inputStructure, ">");
        int tmpIndexOfOpeningAngleBracket = 0;
        int tmpIndexOfClosingAngleBracket = -1;
        int tmpIndexOfLastClosingAnbleBracket;
        boolean tmpIsOddNumbersOfDelimiter;
        boolean tmpHasAngleBracket = tmpNumberOfOpeningAngleBrackets + tmpNumberOfClosingAngleBrackets > 0;
        char[] tmpInputStructure = aInputStructure.toCharArray();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Checks number of angle brackets">
        if (tmpHasAngleBracket) {
            tmpIsOddNumbersOfDelimiter = false;
            for (int i = 0; i < tmpInputStructure.length; i++) {
                if (tmpInputStructure[i] == '<') {
                    if (tmpIsOddNumbersOfDelimiter) {
                        return MessageSpices.getString("StructureCheck.MissingClosingAngleBracket");
                    } else {
                        tmpIsOddNumbersOfDelimiter = true;
                    }
                } else if (tmpInputStructure[i] == '>') {
                    if (tmpIsOddNumbersOfDelimiter) {
                        tmpIsOddNumbersOfDelimiter = false;
                    } else {
                        return MessageSpices.getString("StructureCheck.MissingOpeningAngleBracket");
                    }
                }
            }
            if (tmpIsOddNumbersOfDelimiter) {
                return MessageSpices.getString("StructureCheck.MissingClosingAngleBracket");
            }
            // Checks whether an empty angle bracket exists
            if (this.inputStructure.contains("<>")) {
                return MessageSpices.getString("StructureCheck.EmptyAngleBrackets");
            }
            // Checks whether an character, except numbers and whitespaces, outside bracket exists
            for (int i = 0; i < tmpInputStructure.length; i++) {
                tmpIndexOfOpeningAngleBracket = this.inputStructure.indexOf("<", tmpIndexOfClosingAngleBracket + 1);
                if (tmpIndexOfOpeningAngleBracket == -1) {
                    break;
                }
                if (tmpIndexOfOpeningAngleBracket > tmpIndexOfClosingAngleBracket + 1) {
                    for (int j = tmpIndexOfClosingAngleBracket + 1; j < tmpIndexOfOpeningAngleBracket; j++) {
                        if (!Character.isDigit(tmpInputStructure[j])) {
                            return MessageSpices.getString("StructureCheck.InvalidCharacterPriorAngularBracket");
                        }
                    }
                }
                tmpIndexOfClosingAngleBracket = this.inputStructure.indexOf(">", tmpIndexOfOpeningAngleBracket + 1);
            }
            tmpIndexOfLastClosingAnbleBracket = this.inputStructure.lastIndexOf(">");
            if (tmpIndexOfLastClosingAnbleBracket + 1 < tmpInputStructure.length) {
                return MessageSpices.getString("StructureCheck.InvalidParticleAfterAngleClosingBracket");
            }
        }
        // </editor-fold>

        return "";
    }

    /**
     * Checks whether there is disconnected structure in the input structure,
     * set hasDisconnectedStructure flag and find the disconnectedstrucutres
     *
     * @param aSubStrucutre Molecular substrucuture
     * @return Disconnected Substructure
     */
    private String[] partStructures(String aStructure) {
        if (aStructure == null || aStructure.isEmpty()) {
            return null;
        }

        int tmpStructureLength = aStructure.length();
        int tmpStartPosition = 0;
        int tmpEndPosition = -1;
        int tmpPartFrequency = 0;
        ArrayDeque<String> tmpPartStructure = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        String tmpSubstring;
        //String[] tmpResultString;

        if (spicesUtility.hasPart(aStructure)) {
            this.hasParts = true;
            for (int i = 0; i < tmpStructureLength; i++) {
                tmpStartPosition = aStructure.indexOf("<", tmpStartPosition);
                if (tmpStartPosition - tmpEndPosition > 1) {
                    tmpPartFrequency = Integer.parseInt(aStructure.substring(tmpEndPosition + 1, tmpStartPosition));
                } else {
                    tmpPartFrequency = 1;
                }
                tmpEndPosition = aStructure.indexOf(">", tmpStartPosition);
                tmpSubstring = aStructure.substring(tmpStartPosition + 1, tmpEndPosition);
                for (int j = 0; j < tmpPartFrequency; j++) {
                    tmpPartStructure.add(tmpSubstring);
                }
                tmpStartPosition = tmpEndPosition + 1;
                i = tmpStartPosition;
            }
        } else {
            this.hasParts = false;
            this.numberOfParts = 1;
            return new String[] {aStructure};
        }
        this.numberOfParts = tmpPartStructure.size();
        return tmpPartStructure.toArray(new String[0]);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Token related methods">
    /**
     * Determine the particle list and delete all duplicate
     *
     * @param aChain List of chains (a chain is built out of particles and
     * links)
     * @param aPartIndex Partindexnumber
     * @param aHasDoublet Whether doublet should be considered or not (e.g. if
     * true "A-F" and "F-A" will be returned instead one of both)
     * @return list of particle list without duplicates
     */
    private String[] getParticleList(ArrayDeque<int[]> aChain, int aPartIndex, boolean aHasDoublet) {
        if (aChain == null || aChain.isEmpty()) {
            return null;
        }
        int tmpChainSize = aChain.size();
        int tmpNumberOfParticles = aChain.getFirst().length;
        int[] tmpHelpingArray = new int[tmpChainSize];
        int[][] tmpParticleArray = aChain.toArray(new int[0][]);
        HashSet<String> tmpOutputParticleHashSet = new HashSet<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY);
        String tmpCandidate;

        for (int i = 0; i < tmpChainSize; i++) {
            tmpCandidate = "";
            tmpHelpingArray = tmpParticleArray[i].clone();
            // reverse the array if first Token is lexically bigger then the
            // last Token and delete all duplicates
            if (!aHasDoublet) {
                if (this.hasParts) {
                    if (this.partOfSpices[aPartIndex].getInnerStructureTokens()[this.partOfSpices[aPartIndex].getInnerParticleIndices()[tmpHelpingArray[0]]]
                            .compareTo(this.outerStructureTokens[this.outerParticleIndices[tmpHelpingArray[tmpNumberOfParticles - 1]]]) > 0) {
                        spicesUtility.reverseIntegerArray(tmpHelpingArray);
                    }
                } else {
                    if (this.outerStructureTokens[this.outerParticleIndices[tmpHelpingArray[0]]]
                            .compareTo(this.outerStructureTokens[this.outerParticleIndices[tmpHelpingArray[tmpNumberOfParticles - 1]]]) > 0) {
                        spicesUtility.reverseIntegerArray(tmpHelpingArray);
                    }
                }
            }
            for (int j = 0; j < tmpNumberOfParticles; j++) {
                if (this.hasParts) {
                    if (j < tmpNumberOfParticles - 1) {
                        tmpCandidate += this.partOfSpices[aPartIndex].getInnerStructureTokens()[this.partOfSpices[aPartIndex].getInnerParticleIndices()[tmpHelpingArray[j]]] + SpicesConstants.PARTICLE_SEPARATOR;
                    } else {
                        tmpCandidate += this.partOfSpices[aPartIndex].getInnerStructureTokens()[this.partOfSpices[aPartIndex].getInnerParticleIndices()[tmpHelpingArray[j]]];
                    }
                } else {
                    if (j < tmpNumberOfParticles - 1) {
                        tmpCandidate += this.outerStructureTokens[this.outerParticleIndices[tmpHelpingArray[j]]] + SpicesConstants.PARTICLE_SEPARATOR;
                    } else {
                        tmpCandidate += this.outerStructureTokens[this.outerParticleIndices[tmpHelpingArray[j]]];
                    }
                }
            }
            if (!(tmpOutputParticleHashSet.contains(tmpCandidate))) {
                tmpOutputParticleHashSet.add(tmpCandidate);
            }
        }
        return tmpOutputParticleHashSet.toArray(new String[0]);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- getParticleCoordinates">
    /**
     * Determine all indices of side chain particles
     * 
     * @param anActualParticleIndex The particle index of observed particle
     * @param aHasCalculatedArray Flag information of whether a particle has
     * been calculated or not
     * @param aPartIndex  Part index (-1 if the particle has only one part)
     * @return Indices of side chain particles as hashset
     */    
    private void setIndicesOfSideChainParticles(int anActualParticleIndex, ArrayDeque<Integer> anIndicesOfSideChainParticles, boolean[] aHasCalculatedArray, int aPartIndex) {
        ArrayDeque<Integer> tmpIndicesOfSideChainParticles = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        Queue<Integer> tmpIndexOfParticlesInQueue = new ArrayDeque<>();
        boolean tmpNoParticles = false;
        do {
            for (int tmpItem : this.partOfSpices[aPartIndex].getAdjacentArray()[anActualParticleIndex]) {
                if (!aHasCalculatedArray[tmpItem]) {
                    tmpIndicesOfSideChainParticles.add(tmpItem);
                    aHasCalculatedArray[tmpItem] = true;
                }
            }
            if (!tmpIndicesOfSideChainParticles.isEmpty()) {
                tmpIndexOfParticlesInQueue.addAll(tmpIndicesOfSideChainParticles);
                tmpIndicesOfSideChainParticles.clear();
            }
            if (!tmpIndexOfParticlesInQueue.isEmpty()) {
                anActualParticleIndex = tmpIndexOfParticlesInQueue.remove();
                anIndicesOfSideChainParticles.add(anActualParticleIndex);
            } else {
                tmpNoParticles = true;
            }
                
        } while (!tmpIndexOfParticlesInQueue.isEmpty() || !tmpNoParticles);
    }
   
    /**
     * Generate a matrix with coordinates and connection information of the
     * particles
     *
     * @param aPart: A part of FSmile
     * @param aStartIndex The index number of the first particle
     * @return Matrix with connection information of the particles
     */
    private String[][] getParticlePositionsAndConnections(int aPartIndex, int aStartIndex) {
        return this.getParticlePositionsAndConnections(aPartIndex, aStartIndex, new PointInSpace[]{null}, new PointInSpace[]{null}, 1);
    }

    /**
     * Generate a matrix with coordinates and connection information of the
     * particles
     *
     * @param aPart: A part of FSmile
     * @param aStartIndex The index number of the first particle
     * @param aFirstParticleCoordinates Coordinates of the first particle
     * @param aLastParticleCoordinates Coordinates of the last particle
     * @param aBondLength BondLength
     * @return Matrix with connection and coordination information of the
     * particles
     */
    private String[][] getParticlePositionsAndConnections(int aPartIndex, int aStartIndex, PointInSpace[] aFirstParticleCoordinates, PointInSpace[] aLastParticleCoordinates, double aBondLength) {
        // <editor-fold defaultstate="collapsed" desc="Local variables">
        int tmpNumberOfParticles = this.partOfSpices[aPartIndex].getInnerParticleIndices().length;
        int tmpParticleCoordinateSize = aFirstParticleCoordinates.length;
        int tmpCurrentStructureTokenIndex = 0;
        IPointInSpace[] tmpParticleCoordinates;
        LinkedList<Integer> tmpLinkTargetList = new LinkedList<>();
        String[][] tmpResultStrings = new String[tmpNumberOfParticles * tmpParticleCoordinateSize][];

        // </editor-fold>
        int[] tmpNeighborParticleSizes = this.partOfSpices[aPartIndex].getNumberOfConnects();
        // Initialize aPart.maximumNumberOfConnectionsOfSingleParticle
        this.partOfSpices[aPartIndex].setMaximumNumberOfConnectionsOfSingleParticle(0);
        int tmpRow = 0;
        int k;
        // New format with relative connections:
        // int tmpRows = 0;
        for (int i = 0; i < tmpParticleCoordinateSize; i++) {
            if (aFirstParticleCoordinates[0] != null) {
                if (tmpNumberOfParticles == 1) {

                    // <editor-fold defaultstate="collapsed" desc="1 particle only in molecular structure">
                    tmpParticleCoordinates = new PointInSpace[]{aFirstParticleCoordinates[i]};

                    // </editor-fold>
                } else {

                    // <editor-fold defaultstate="collapsed" desc="Several particles in molecular structure">
                    tmpParticleCoordinates = this.getCoordinatesOfTokens(this, aPartIndex, aFirstParticleCoordinates[i], aLastParticleCoordinates[i], aBondLength);

                    // </editor-fold>
                }
            } else {
                tmpParticleCoordinates = null;
            }
            for (int j = 0; j < tmpNumberOfParticles; j++) {
                tmpResultStrings[tmpRow] = new String[6 + tmpNeighborParticleSizes[j]];
                // 1. column: Number of the particle (1-based)
                tmpResultStrings[tmpRow][0] = Integer.toString(aStartIndex + tmpRow);
                // 2. column: Name of the particle
                tmpCurrentStructureTokenIndex = this.partOfSpices[aPartIndex].getInnerParticleIndices()[j];
                tmpResultStrings[tmpRow][1] = this.partOfSpices[aPartIndex].getInnerStructureTokens()[tmpCurrentStructureTokenIndex];
                // 3. column: Backbone index
                tmpResultStrings[tmpRow][2] = Integer.toString(this.partOfSpices[aPartIndex].getBackboneIndices()[j]);
                // 4-6. columns: xyz-Coordinate of the particle (if not
                // specified it will empty)
                tmpResultStrings[tmpRow][3] = "";
                tmpResultStrings[tmpRow][4] = "";
                tmpResultStrings[tmpRow][5] = "";
                if (aFirstParticleCoordinates[0] != null) {
                    tmpResultStrings[tmpRow][3] += tmpParticleCoordinates[j].getX();
                    tmpResultStrings[tmpRow][4] += tmpParticleCoordinates[j].getY();
                    tmpResultStrings[tmpRow][5] += tmpParticleCoordinates[j].getZ();
                }
                // >5.column: Particle number(s) which connected with aPart
                // particle
                if (this.partOfSpices[aPartIndex].getAdjacentArray() != null) {
                    for (int tmpItem : this.partOfSpices[aPartIndex].getAdjacentArray()[j]) {
                        tmpLinkTargetList.add(tmpItem);
                    }
                }
                // Set aPart.maximumNumberOfConnectionsOfSingleParticle
                if (tmpLinkTargetList.size() > this.partOfSpices[aPartIndex].getMaximumNumberOfConnectionsOfSingleParticle()) {
                    this.partOfSpices[aPartIndex].setMaximumNumberOfConnectionsOfSingleParticle(tmpLinkTargetList.size());
                }
                Collections.sort(tmpLinkTargetList);
                Iterator<Integer> tmpIterator = tmpLinkTargetList.iterator();
                k = 6;
                while (tmpIterator.hasNext()) {
                    // New format with relative connections:
                    tmpResultStrings[tmpRow][k] = Integer.toString(tmpIterator.next() - j);
                    tmpIterator.remove();
                    k++;
                }
                tmpRow++;
            }
            // New format with relative connections:
            // tmpRows = tmpRow;
        }
        return tmpResultStrings;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Particle frequency related methods">
    /**
     * Determines particle frequencies of molecular structure. NOTE: Molecular
     * structure MUST be valid (NO checks are performed) and is NOT allowed to
     * contain monomer shortcuts.
     */
    private void determineParticleFequenciesOfMolecularStructure() {
        HashMap<String, ParticleFrequency> tmpParticleToFrequencyMap = new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY);
        for (SpicesInner partOfFSmile : this.partOfSpices) {
            for (String innerParticle : partOfFSmile.getInnerParticles()) {
                if (tmpParticleToFrequencyMap.containsKey(innerParticle)) {
                    tmpParticleToFrequencyMap.get(innerParticle).addFrequency(1);
                } else {
                    tmpParticleToFrequencyMap.put(innerParticle, new ParticleFrequency(innerParticle, 1));
                }
            }
        }
        this.particleToFrequencyMap = tmpParticleToFrequencyMap;
    }

    // </editor-fold>
    // </editor-fold>

}
