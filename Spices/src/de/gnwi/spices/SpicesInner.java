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

/**
 * Inner Spices
 * 
 * @author Mirco Daniel, Achim Zielesny
 */
public class SpicesInner {

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
     * Input structure
     */
    private String inputStructure;

    /**
     * Inner structure tokens
     */
    private String[] innerStructureTokens;

    /**
     * State information whether this structure has START/END attribute
     */
    private boolean hasStartEndAttribute;

    /**
     * State information whether this structure has backbone particles
     */
    private boolean hasBackboneAttribute;

    /**
     * State information whether a Token is a particle or not.
     */
    private boolean[] isInnerParticleList;

    /**
     * Inner particle index (zero-based position of particles in
     * this.structureTokens)
     */
    private int[] innerParticleIndices;

    /**
     * Backbone index (zero-based position of particles with backbone index flag)
     */
    private int[] innerBackboneIndices;

    /**
     * HashMap that maps particles of molecule to their frequencies
     */
    private HashMap<String, ParticleFrequency> particleToFrequencyMap;

    /**
     * Monomers
     */
    private String[] monomers;

    /**
     * Adjacent array (connection between two particles)
     */
    private int[][] adjacentArray;

    /**
     * Numbers of terminal particles
     */
    private int numberOfTerminalParticles;

    /**
     * Index number of start particle in the particle list. Not confuse with
     * index number of an token list. It is 0-based and -1 means no
     * [START]-tag exists.
     */
    private int startParticleIndex;

    /**
     * Index number of end particle in the particle list. Not confuse with
     * index number of an token list. It is 0-based and -1 means no
     * [END]-tag exists.
     */
    private int endParticleIndex;

    /**
     * Valence number of a particle
     */
    private int[] numberOfConnects;

    /**
     * Connected particles (zero-based number of two connected particles)
     */
    private LinkedList<int[]> connectedParticlesList;

    /**
     * Maximum number of connections (bonds) of a single particle
     */
    private int maximumNumberOfConnectionsOfSingleParticle;

    /**
     * Terminal particle flag
     */
    private boolean[] terminalParticleFlag;

    /**
     * Particles
     */
    private String[] innerParticles;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * * Empty constructor
     */
    public SpicesInner() {
    }

    /**
     * * Sets all properties of a InnerSpices object.
     *
     * @param anInputStructure An input structure (may be a monomer)
     * @param anAvailableParticles Hashmap of available particles
     * @param anIsMonomer True: anInputStructure is a monomer, false:
     * Otherwise (default: false)
     * @param aStartIndex First particle number in the Spices matrix
     * (default: 1)
     * @param aFirstParticles Cartesian coordinate of a single first
     * particle
     * @param aLastParticles Cartesian coordinate of a single last particle
     * @param aBondLength User defined bond length for all connections
     * between particles
     */
    public SpicesInner(String anInputStructure, HashMap<String, String> anAvailableParticles, boolean anIsMonomer, int aStartIndex,
            PointInSpace[] aFirstParticles, PointInSpace[] aLastParticles, double aBondLength) {
        this.initialize(anInputStructure, anIsMonomer, anAvailableParticles);
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    /**
     * Returns the input structure
     *
     * @return Input structure
     */
    public String getInputStructure() {
        return this.inputStructure;
    }

    /**
     * Returns the innerstructuretokens
     * 
     * @return inputInnerStructureTokens
     */
    public String[] getInnerStructureTokens() {
        return this.innerStructureTokens;
    }

    /**
     * Returns inner particles
     *
     * @return Inner particles
     */
    public String[] getInnerParticles() {
        return this.innerParticles;
    }

    /**
     * Particle index
     *
     * @return Particle indices
     */
    public int[] getInnerParticleIndices() {
        return this.innerParticleIndices;
    }

    /**
     * Index number of start particle in the particle list. Not confuse with
     * index number of an token list. It is 0-based and -1 means no
     * [START]-tag exists.
     * 
     * @return Index number of start particle 
     */
    public int getStartParticleIndex() {
        return this.startParticleIndex;
    }
    
    /**
     * Determine the particle position with "END"-tag
     *
     * @return 0-based index number of particle with "END"-tag
     */
    public int getEndParticleIndex() {
        return this.endParticleIndex;
    }

    /**
     * Adjacent array (connection between two particles)
     * 
     * @return Adjacent array
     */
    public int[][] getAdjacentArray() {
        return this.adjacentArray;
    }
    
    /**
     * Error message
     * 
     * @return Error message
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }
    
    /**
     * Maximum number of connections (bonds) of a single particle
     * 
     * @return Maximum number of connections
     */
    public int getMaximumNumberOfConnectionsOfSingleParticle() {
        return this.maximumNumberOfConnectionsOfSingleParticle;
    }

    /**
     * Maximum number of connections (bonds) of a single particle
     * 
     * @param aValue Value
     */
    public void setMaximumNumberOfConnectionsOfSingleParticle(int aValue) {
        this.maximumNumberOfConnectionsOfSingleParticle = aValue;
    }
    
    /**
     * Valence number of a particle
     * 
     * @return Valence number of a particle
     */
    public int[] getNumberOfConnects() {
        return this.numberOfConnects;
    }

    /**
     * State information whether this structure has backbone particles
     * 
     * @return State information whether this structure has backbone particles
     */
    public boolean hasBackboneAttribute() {
        return this.hasBackboneAttribute;
    }
    
    /**
     * Inner backbone indices (zero-based position of particles with backbone index flag)
     * 
     * @return Inner backbone indices
     */
    public int[] getInnerBackboneIndices() {
        return this.innerBackboneIndices;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Determine all available neighbors index for structures with parts
     *
     * @param aSegmentLength Length of segment (2: dimer, 3: trimer etc.)
     * @param aPart: A part of Spices
     * @return All available neighbors index with all segment length until
     * aSegmentLength: Index 0: Particles, Index 1: Dimers, Index 2: Trimes
     * etc. up to index (aSegmentLength - 1)
     */
    public LinkedList<int[]> getNextNeighborIndex(int aSegmentLength, SpicesInner aPart) {

        // <editor-fold defaultstate="collapsed" desc="Local variable">
        LinkedList<int[]> tmpResultChainList = new LinkedList<>();
        int[][] tmpConnectedParticleArray = aPart.connectedParticlesList.toArray(new int[0][]);

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simple check">
        if (aPart.getInnerStructureTokens() == null || aPart.getInnerStructureTokens().length == 0 || aPart.connectedParticlesList == null) {
            return null;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Determine single particles">
        for (int i = 0; i < aPart.innerParticleIndices.length; i++) {
            tmpResultChainList.add(new int[]{i});
        }
        if (aSegmentLength == 1) {
            return tmpResultChainList;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Extend neighbor searching">
        int tmpExtendParticleCandidate;
        int tmpNumberOfLink = tmpConnectedParticleArray.length;
        tmpResultChainList.addAll(aPart.connectedParticlesList);

        if (aSegmentLength == 2) {
            return tmpResultChainList;
        } else {
            int tmpCompareParticleIndex = 1;
            int tmpCompareParticle = 0;
            LinkedList<int[]> tmpMultiChainList = new LinkedList<>(aPart.connectedParticlesList);
            ArrayDeque<int[]> tmpHelpingChainList = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
            int tmpMultiChainParticleSize = 3;
            for (int i = 0; i < aSegmentLength - 2; i++) {
                Iterator<int[]> tmpIterator = tmpMultiChainList.iterator();
                // Delete all Chains with terminal particle - they can not be
                // extended
                while (tmpIterator.hasNext()) {
                    if (aPart.terminalParticleFlag[tmpIterator.next()[tmpMultiChainParticleSize - 2]]) {
                        tmpIterator.remove();
                    }
                }
                for (int[] tmpMultiChainListItem : tmpMultiChainList) {
                    tmpCompareParticle = tmpMultiChainListItem[tmpCompareParticleIndex];
                    int[] tmpExtendedLinks = Arrays.copyOf(tmpMultiChainListItem, tmpMultiChainParticleSize);
                    for (int j = 0; j < tmpNumberOfLink; j++) {
                        if (tmpCompareParticle == tmpConnectedParticleArray[j][0]) {
                            tmpExtendParticleCandidate = tmpConnectedParticleArray[j][1];
                            boolean tmpIsCandidatePositive = true;
                            for (int k = tmpMultiChainParticleSize - 3; k >= 0; k--) {
                                if (tmpExtendedLinks[k] == tmpExtendParticleCandidate) {
                                    tmpIsCandidatePositive = false;
                                    break;
                                }
                            }
                            if (tmpIsCandidatePositive) {
                                tmpExtendedLinks[tmpMultiChainParticleSize - 1] = tmpExtendParticleCandidate;
                                tmpHelpingChainList.add(tmpExtendedLinks.clone());
                            }
                        }
                    }
                }
                tmpCompareParticleIndex++;
                tmpMultiChainParticleSize++;
                tmpMultiChainList.clear();
                tmpMultiChainList.addAll(tmpHelpingChainList);
                tmpHelpingChainList.clear();
                tmpResultChainList.addAll(tmpMultiChainList);
            }
            return tmpResultChainList;
        }
        // </editor-fold>
    }

    /**
     * Determines a heuristic diameter. This algorithm choose an arbitrary
     * start particle s. The particle (v) at the farthest from s is then
     * traced. Depth First Search Algorithm (DFS) is used. Again, from v DFS
     * is used to find the particle (w) at the farthest of v. This algorithm
     * get the correct diameter, if the structure is not cyclic. To find the
     * real diameter all possible shortest path between terminal particles
     * have to determined. The longest of them is the diameter. This will
     * take too much computing time by structures with many particles.
     *
     * @return Array of particles from v-particle to w-particle.
     */
    public int[] getHeuristicDiameter() {
        return this.getHeuristicDiameter(this);
    }

    /**
     * Determines the path from START particle to END particle. The path is
     * not necessarely the longest possible.
     *
     * @return Array of particles from START-particle to END-particle.
     */
    public int[] getPathStartToEnd() {
        if(this.hasStartEndAttribute) {
            return this.getPathStartToEnd(this);
        } else {
            return null;
        }
    }
    
    /**
     * Determine the particle position of backbone particles
     *
     * @return 0-based index number of particle with backbone-tag
     */
    public int[] getBackboneIndices() {
        String tmpTestToken = "";
        int tmpBackboneParticleIndex = 0;
        int[] tmpResult = new int[this.innerParticleIndices.length];
        Arrays.fill(tmpResult, 0);
        if (this.hasBackboneAttribute) {
            for (int i = 0; i < this.getInnerStructureTokens().length; i++) {
                tmpTestToken = this.getInnerStructureTokens()[i];
                if (spicesUtility.isBackboneIndex(tmpTestToken)) {
                    tmpBackboneParticleIndex = Integer.parseInt(tmpTestToken.substring(1, tmpTestToken.length() - 1));
                    for (int j = this.innerParticleIndices.length - 1; j >= 0; j--) {
                        if (i > this.innerParticleIndices[j]) {
                            tmpResult[j] = tmpBackboneParticleIndex;
                            break;
                        }
                    }
                }
            }
        }
        return tmpResult;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    // <editor-fold defaultstate="collapsed" desc="- Initialize methods">
    /**
     * Innerinitialize method
     *
     * @param anInputStructure An input structure
     * @param aParticleToDescriptionMap Descriptions for the particles
     * @param anIsMonomer True: anInputStructure is a monomer, false:
     * Otherwise (default: false)
     * @param anAvailableParticles User defined list of available particles
     */
    private void initialize(String aPartStructure, boolean anIsMonomer, HashMap<String, String> anAvailableParticles) {

        this.inputStructure = aPartStructure;
        this.innerStructureTokens = spicesUtility.getStructureTokens(aPartStructure);
        this.errorMessage = this.checkInputStructure(aPartStructure, anIsMonomer, anAvailableParticles);
        if (this.errorMessage != null) {
            return;
        }
        this.innerStructureTokens = this.getSpicesCompilerTokenList();
        this.innerParticleIndices = this.determineInnerParticleIndices();
        this.setInnerParticles();
        if (spicesUtility.hasStartTag(this.inputStructure)) {
            this.hasStartEndAttribute = true;
        }
        if (spicesUtility.hasBackboneTag(this.inputStructure)) {
            this.hasBackboneAttribute = true;
        }
        this.innerBackboneIndices = this.getBackboneIndices();
        this.monomers = this.getMonomerList();
        this.connectedParticlesList = this.getNeighborParticles(true);
        this.adjacentArray = spicesUtility.getAdjacentArray(this.inputStructure, this.connectedParticlesList, this.innerParticleIndices.length);
        this.numberOfConnects = this.getFrequencyOfNeighborParticles();
        this.terminalParticleFlag = this.isTerminalParticle();
        this.startParticleIndex = this.getStartTagParticleIndex();
        this.endParticleIndex = this.getEndTagParticleIndex();
        this.particleToFrequencyMap = new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Structure check related methods">
    /**
     * Checks the input structure
     *
     * @aStructure Structure of a part
     * @Param anIsMonomer Weather aMolecularStructureString is only monomer
     * or not (monomer is partial quantity of structure)
     * @param anAvailableParticles Available particles
     * @return Error message or null
     */
    private String checkInputStructure(String aStructure, Boolean anIsMonomer, HashMap<String, String> anAvailableParticles) {
        // <editor-fold defaultstate="collapsed" desc="Initial settings">
        int tmpNumberOfOpeningCurlyBrackets = spicesUtility.getFrequencyOfCharacterInString(aStructure, "{");
        int tmpNumberOfClosingCurlyBrackets = spicesUtility.getFrequencyOfCharacterInString(aStructure, "}");
        boolean tmpHasCurlyBracket = tmpNumberOfOpeningCurlyBrackets + tmpNumberOfClosingCurlyBrackets > 0;
        boolean tmpIsOddNumbersOfDelimiter;
        char[] tmpCharArrayStructure;
        String tmpNotification;

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Checks monomer">
        if (anIsMonomer) {
            if (!(aStructure.startsWith("{") && aStructure.endsWith("}"))) {
                return MessageSpices.getString("StructureCheck.NoMonomer");
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Simple checks">
        if (aStructure == null || aStructure.length() == 0) {
            return MessageSpices.getString("StructureCheck.NoTokens");
        }

        // Checks whether an invalid character has been used
        if (!SpicesConstants.INPUTSTRUCTURE_PATTERN.matcher(aStructure).matches()) {
            return MessageSpices.getString("StructureCheck.InvalidCharacter");
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Checks number of curly brackets">
        tmpCharArrayStructure = aStructure.toCharArray();
        if (tmpHasCurlyBracket) {
            tmpIsOddNumbersOfDelimiter = false;
            for (int i = 0; i < tmpCharArrayStructure.length; i++) {
                if (tmpCharArrayStructure[i] == '{') {
                    if (tmpIsOddNumbersOfDelimiter) {
                        return MessageSpices.getString("StructureCheck.MissingClosingCurlyBracket");
                    } else {
                        tmpIsOddNumbersOfDelimiter = true;
                    }
                } else if (tmpCharArrayStructure[i] == '}') {
                    if (tmpIsOddNumbersOfDelimiter) {
                        tmpIsOddNumbersOfDelimiter = false;
                    } else {
                        return MessageSpices.getString("StructureCheck.MissingOpeningCurlyBracket");
                    }
                }
            }
            if (tmpIsOddNumbersOfDelimiter) {
                return MessageSpices.getString("StructureCheck.MissingClosingCurlyBracket");
            }
            // Checks whether an empty curly bracket exists
            if (aStructure.contains("{}")) {
                return MessageSpices.getString("StructureCheck.EmptyCurlyBrackets");
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Determine monomers and body">
        String tmpBody = "";
        String tmpMonomer = "";
        tmpNotification = null;
        int tmpIndexOfLeftLimit = 0;
        int tmpIndexOfRightLimit = 0;
        boolean tmpHasReached = false;
        if (tmpHasCurlyBracket) {
            while (!tmpHasReached) {
                tmpIndexOfLeftLimit = aStructure.indexOf('{', tmpIndexOfLeftLimit);
                if (tmpIndexOfLeftLimit == -1) {
                    tmpHasReached = true;
                    tmpBody += aStructure.substring(tmpIndexOfRightLimit - 1, aStructure.length());
                } else if (tmpIndexOfLeftLimit == 0) {
                    tmpBody = "{}";
                } else {
                    tmpBody += aStructure.substring(tmpIndexOfRightLimit, tmpIndexOfLeftLimit + 1) + "}";
                }
                if (!tmpHasReached) {
                    tmpIndexOfRightLimit = aStructure.indexOf('}', tmpIndexOfLeftLimit);
                    tmpMonomer = aStructure.substring(tmpIndexOfLeftLimit + 1, tmpIndexOfRightLimit);
                    tmpNotification = checkSubStructure(tmpMonomer, true, anAvailableParticles);
                    if (tmpNotification != null) {
                        return tmpNotification;
                    }
                    tmpIndexOfLeftLimit = tmpIndexOfRightLimit + 1;
                    tmpIndexOfRightLimit = tmpIndexOfLeftLimit;
                    if (tmpIndexOfRightLimit >= aStructure.length() - 1) {
                        tmpHasReached = true;
                    }
                }
            }
        } else {
            tmpBody = aStructure;
            if (spicesUtility.isParticle(tmpBody) && spicesUtility.isAvailableParticle(tmpBody, anAvailableParticles)) {
                return null;
            }
        }
        tmpNotification = checkSubStructure(tmpBody, false, anAvailableParticles);
        if (tmpNotification != null) {
            return tmpNotification;
        }

        // </editor-fold>
        return null;
    }

    /**
     * Checks the input substructure
     *
     * @param aSubtructure Molecular substructure
     * @param anIsMonomer Whether the substructure is a monomer or not
     * @param anAvailableParticles Available Particles
     * @param anIndexOffset Index offset from aSubStructure to original
     * structure
     * @return Error message or null
     */
    private String checkSubStructure(String aSubtructure, Boolean anIsMonomer, HashMap<String, String> anAvailableParticles) {
        // <editor-fold defaultstate="collapsed" desc="Initial settings">
        int tmpNumberOfOpeningAngularBrackets = spicesUtility.getFrequencyOfCharacterInString(aSubtructure, "[");
        int tmpNumberOfClosingAngularBrackets = spicesUtility.getFrequencyOfCharacterInString(aSubtructure, "]");
        int tmpNumberOfOpeningNormalBrackets = spicesUtility.getFrequencyOfCharacterInString(aSubtructure, "(");
        int tmpNumberOfClosingNormalBrackets = spicesUtility.getFrequencyOfCharacterInString(aSubtructure, ")");
        int tmpNumberOfOpeningBackboneIndices = spicesUtility.getFrequencyOfCharacterInString(aSubtructure, "'");
        int tmpNumberOfBackboneIndexCandidat;
        boolean tmpHasAngularBracket = tmpNumberOfOpeningAngularBrackets + tmpNumberOfClosingAngularBrackets > 0;
        boolean tmpHasNormalBracket = tmpNumberOfOpeningNormalBrackets + tmpNumberOfClosingNormalBrackets > 0;
        ArrayDeque<String> tmpNumberOfRingClosure = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        HashSet<Integer> tmpNumberOfBackboneIndex = new HashSet<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY);
        String[] tmpTokenOfSubstructure = spicesUtility.getStructureTokens(aSubtructure);
        int tmpNumberOfTokens = tmpTokenOfSubstructure.length;
        char[] tmpCharsOfSubStructure = aSubtructure.toCharArray();

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Checks whether the first character is not valid">
        if (aSubtructure.startsWith("0") || aSubtructure.startsWith("[") || aSubtructure.startsWith("]") || aSubtructure.startsWith("-")
                || aSubtructure.startsWith(")") || aSubtructure.startsWith("'")) {
            if (anIsMonomer) {
                return MessageSpices.getString("StructureCheck.InvalidFirstCharacterOfMonomer");
            } else {
                return MessageSpices.getString("StructureCheck.InvalidFirstCharacterOfStructure");
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Checks whether the last character is not valid">
        if (aSubtructure.endsWith("[") || aSubtructure.endsWith("-") || aSubtructure.endsWith("(")) {
            if (anIsMonomer) {
                return MessageSpices.getString("StructureCheck.InvalidLastCharacterOfMonomer");
            } else {
                return MessageSpices.getString("StructureCheck.InvalidLastCharacterOfStructure");
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Checks whether the structure is like (A-B)A and (A-B)-">
        if (aSubtructure.charAt(0) == '(' && aSubtructure.matches("([(].+[)])+[A-Za-z\\-\\[\\]]+")) {
            return MessageSpices.getString("StructureCheck.InvalidParticleAfterNormalClosingBracket");
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Checks whether there is a backbone index in monomer structure">
        if (anIsMonomer && aSubtructure.contains("'")) {
            return MessageSpices.getString("StructureCheck.BackboneIndexInMonomer");
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Checks of legal using of angular brackets">
        if (tmpHasAngularBracket) {
            boolean tmpIsOddNumbersOfDelimiter = false;

            int tmpIndexOfLeftLimit = 0;
            int tmpIndexOfRightLimit = 0;
            int tmpNumberOfHead = 0;
            int tmpNumberOfTail = 0;
            int tmpNumberOfStartTag = 0;
            int tmpNumberOfEndTag = 0;
            String tmpStringBetweenAngularBrackets;
            for (int i = 0; i < tmpCharsOfSubStructure.length; i++) {
                if (tmpCharsOfSubStructure[i] == '[') {
                    if (tmpIsOddNumbersOfDelimiter) {
                        return MessageSpices.getString("StructureCheck.MissingClosingAngularBracket");
                    } else {
                        tmpIsOddNumbersOfDelimiter = true;
                    }
                } else if (tmpCharsOfSubStructure[i] == ']') {
                    if (tmpIsOddNumbersOfDelimiter) {
                        tmpIsOddNumbersOfDelimiter = false;
                    } else {
                        return MessageSpices.getString("StructureCheck.MissingOpeningAngularBracket");
                    }
                }
            }
            if (tmpIsOddNumbersOfDelimiter) {
                return MessageSpices.getString("StructureCheck.MissingClosingAngularBracket");
            }
            for (int i = 0; i < tmpNumberOfOpeningAngularBrackets; i++) {
                tmpIndexOfLeftLimit = aSubtructure.indexOf('[', tmpIndexOfLeftLimit);
                tmpIndexOfRightLimit = aSubtructure.indexOf(']', tmpIndexOfRightLimit);
                tmpStringBetweenAngularBrackets = aSubtructure.substring(tmpIndexOfLeftLimit + 1, tmpIndexOfRightLimit);
                if (anIsMonomer) {
                    if (tmpStringBetweenAngularBrackets.equals("HEAD")) {
                        tmpNumberOfHead++;
                    } else if (tmpStringBetweenAngularBrackets.equals("TAIL")) {
                        tmpNumberOfTail++;
                    } else if (spicesUtility.isIntegerNumber(tmpStringBetweenAngularBrackets)) {
                        tmpNumberOfRingClosure.add(tmpStringBetweenAngularBrackets);
                    } else if (tmpStringBetweenAngularBrackets.equals("START")) {
                        return MessageSpices.getString("StructureCheck.StartAttributeInMonomer");
                    } else if (tmpStringBetweenAngularBrackets.equals("END")) {
                        return MessageSpices.getString("StructureCheck.EndAttributeInMonomer");
                    } else {
                        return MessageSpices.getString("StructureCheck.InvalidCharacterBetweenAngularBrackets");
                    }
                } else {
                    if (tmpStringBetweenAngularBrackets.equals("HEAD") || tmpStringBetweenAngularBrackets.equals("TAIL")) {
                        return MessageSpices.getString("StructureCheck.IllegalUsingOfHeadOrTail");
                    } else if (spicesUtility.isIntegerNumber(tmpStringBetweenAngularBrackets)) {
                        tmpNumberOfRingClosure.add(tmpStringBetweenAngularBrackets);
                    } else if (tmpStringBetweenAngularBrackets.equals("START")) {
                        tmpNumberOfStartTag++;
                    } else if (tmpStringBetweenAngularBrackets.equals("END")) {
                        tmpNumberOfEndTag++;
                    } else {
                        return MessageSpices.getString("StructureCheck.InvalidCharacterBetweenAngularBrackets");
                    }
                }
                tmpIndexOfLeftLimit = tmpIndexOfRightLimit + 1;
                tmpIndexOfRightLimit = tmpIndexOfLeftLimit;
            }
            if (tmpNumberOfStartTag > 1) {
                return MessageSpices.getString("StructureCheck.TooManyStartTag");
            }
            if (tmpNumberOfEndTag > 1) {
                return MessageSpices.getString("StructureCheck.TooManyEndTag");
            }
            if (tmpNumberOfStartTag == 1 && tmpNumberOfEndTag == 0) {
                return MessageSpices.getString("StructureCheck.MissingEndAttribute");
            }
            if (tmpNumberOfStartTag == 0 && tmpNumberOfEndTag == 1) {
                return MessageSpices.getString("StructureCheck.MissingStartAttribute");
            }
            if (anIsMonomer) {
                if (tmpNumberOfHead == 0) {
                    return MessageSpices.getString("StructureCheck.MissingHeadAttribute");
                } else if (tmpNumberOfHead > 1) {
                    return MessageSpices.getString("StructureCheck.TooManyHead");
                }
                if (tmpNumberOfTail == 0) {
                    return MessageSpices.getString("StructureCheck.MissingTailAttribute");
                } else if (tmpNumberOfTail > 1) {
                    return MessageSpices.getString("StructureCheck.TooManyTail");
                }
            }
        } else {
            if (anIsMonomer) {
                return MessageSpices.getString("StructureCheck.MissingHeadOrTailAttribute");
            }
        }

        // Checks whether number of ring closure is not 2
        int tmpCountRingClosure = tmpNumberOfRingClosure.size();
        String[] tmpNumberOfRingClosureArray = tmpNumberOfRingClosure.toArray(new String[0]);
        boolean tmpHasTwo = false;
        int tmpActualIndex = 0;
        if (tmpCountRingClosure > 0) {
            Arrays.sort(tmpNumberOfRingClosureArray);
        }
        if (tmpCountRingClosure == 1) {
            return MessageSpices.getString("StructureCheck.MissingRingClosure");
        } else if (tmpCountRingClosure > 1) {
            while (tmpActualIndex < tmpCountRingClosure) {
                if (tmpActualIndex + 1 < tmpCountRingClosure) {
                    if (tmpNumberOfRingClosureArray[tmpActualIndex].equals(tmpNumberOfRingClosureArray[tmpActualIndex + 1])) {
                        tmpHasTwo = true;
                    } else {
                        return MessageSpices.getString("StructureCheck.MissingRingClosure");
                    }
                } else if (tmpActualIndex + 1 == tmpCountRingClosure) {
                    return MessageSpices.getString("StructureCheck.MissingRingClosure");
                }
                if (tmpHasTwo == true && tmpActualIndex + 2 < tmpCountRingClosure) {
                    if (tmpNumberOfRingClosureArray[tmpActualIndex].equals(tmpNumberOfRingClosureArray[tmpActualIndex + 2])) {
                        return MessageSpices.getString("StructureCheck.TooManyRingClosures");
                    }
                }
                tmpActualIndex += 2;
                tmpHasTwo = false;
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Checks of legal using of normal brackets">
        if (tmpHasNormalBracket) {
            int tmpNumberOfUnclosedBrackets = 0;

            // Checks whether all normal brackets have been closed
            if (tmpNumberOfOpeningNormalBrackets - tmpNumberOfClosingNormalBrackets > 0) {
                return MessageSpices.getString("StructureCheck.MissingClosingNormalBracket");
            } else if (tmpNumberOfClosingNormalBrackets - tmpNumberOfOpeningNormalBrackets > 0) {
                return MessageSpices.getString("StructureCheck.MissingOpeningNormalBracket");
            }

            // Checks whether a normal bracket begins with opening bracket
            for (int i = 0; i < tmpCharsOfSubStructure.length; i++) {
                if (tmpCharsOfSubStructure[i] == '(') {
                    tmpNumberOfUnclosedBrackets++;
                } else if (tmpCharsOfSubStructure[i] == ')') {
                    tmpNumberOfUnclosedBrackets--;
                }
                if (tmpNumberOfUnclosedBrackets < 0) {
                    return MessageSpices.getString("StructureCheck.MissingOpeningNormalBracket");
                }
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Checks of legal using of backbone index">
        boolean tmpIsBackboneIndexOn = false;
        if (this.hasBackboneAttribute) {

        }
        if (aSubtructure.contains("''")) {
            return MessageSpices.getString("StructureCheck.IllegalBackboneIndexFormat");
        }
        if (tmpNumberOfOpeningBackboneIndices % 2 != 0) {
            return MessageSpices.getString("StructureCheck.MissingBackboneIndex");
        }
        if (tmpNumberOfOpeningBackboneIndices == 2) {
            return MessageSpices.getString("StructureCheck.TooLessBackboneindex");
        }
        for (int i = 0; i < tmpCharsOfSubStructure.length; i++) {
            if (tmpCharsOfSubStructure[i] == '\'') {
                tmpIsBackboneIndexOn = !tmpIsBackboneIndexOn;
                continue;
            } else if (tmpIsBackboneIndexOn && !Character.isDigit(tmpCharsOfSubStructure[i])) {
                return MessageSpices.getString("StructureCheck.IllegalBackboneIndexFormat");
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Checks tokens">
        String tmpCurrentToken;
        String tmpNextToken;
        Boolean tmpIsRepeatParticle = false;
        if (tmpTokenOfSubstructure.length == 1) {
            if (spicesUtility.isIntegerNumber(tmpTokenOfSubstructure[0])) {
                return MessageSpices.getString("StructureCheck.InvalidParticlename");
            } else if (spicesUtility.isMonomer(tmpTokenOfSubstructure[0])) {
                return null;
            } else if (spicesUtility.isParticle(tmpTokenOfSubstructure[0])) {
                return null;
            } else if (!(spicesUtility.isAvailableParticle(tmpTokenOfSubstructure[0], anAvailableParticles)
                && spicesUtility.isParticle(tmpTokenOfSubstructure[0]))) {
                    return MessageSpices.getString("StructureCheck.InvalidParticlename");
            } else {
                return MessageSpices.getString("StructureCheck.InvalidParticlename");
            }
        }
        for (int i = 0; i < tmpNumberOfTokens - 1; i++) {
            tmpCurrentToken = tmpTokenOfSubstructure[i];
            tmpNextToken = tmpTokenOfSubstructure[i + 1];

            // <editor-fold defaultstate="collapsed" desc="Particle">
            if (spicesUtility.isParticle(tmpCurrentToken) && spicesUtility.isAvailableParticle(tmpCurrentToken, anAvailableParticles)) {
                if (spicesUtility.isNormalBracketOpen(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isNormalBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isRingClosure(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isCurlyBracketOpen(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAConnectionPriorCurlyOpeningBracket");
                } else if (spicesUtility.isCurlyBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isConnection(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isHead(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isTail(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isStart(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isEnd(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isMonomer(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isIntegerNumber(tmpNextToken)) {
                    continue; // this number always belong to the prior Particle
                } else if (spicesUtility.isAngleBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isBackboneIndex(tmpNextToken)) {
                    continue; // legal expression
                } else {
                    return MessageSpices.getString("StructureCheck.InvalidParticleAfterParticle");
                }
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Number">
            else if (spicesUtility.isIntegerNumber(tmpCurrentToken)) {
                if (Integer.parseInt(tmpCurrentToken) == 0) {
                    return MessageSpices.getString("StructureCheck.IllegalFrequency");
                }
                if (spicesUtility.isParticle(tmpNextToken) && spicesUtility.isAvailableParticle(tmpNextToken, anAvailableParticles)) {
                    continue; // legal expression
                } else if (spicesUtility.isNormalBracketOpen(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.InvalidParticlePriorNormalBracket");
                } else if (spicesUtility.isNormalBracketClose(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAParticlePriorNormalClosingBracket");
                } else if (spicesUtility.isRingClosure(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingParticleAfterNumber");
                } else if (spicesUtility.isCurlyBracketOpen(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isCurlyBracketClose(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingParticleAfterNumber");
                } else if (spicesUtility.isConnection(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingParticlePriorConnection");
                } else if (spicesUtility.isHeadTail(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingParticleAfterNumber");
                } else if (spicesUtility.isMonomer(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isBackboneIndex(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.InvalidPositionOfBackboneIndex");
                } else {
                    return MessageSpices.getString("StructureCheck.InvalidParticlename");
                }
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="NormalBracketOpen">
            else if (spicesUtility.isNormalBracketOpen(tmpCurrentToken)) {
                if (spicesUtility.isIntegerNumber(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isNormalBracketOpen(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.InSeriesOfNormalOpeningBrackets");
                } else if (spicesUtility.isNormalBracketClose(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.EmptyNormalBrackets");
                } else if (spicesUtility.isRingClosure(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAParticlePriorRingClosure");
                } else if (spicesUtility.isCurlyBracketOpen(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isCurlyBracketClose(tmpNextToken)) {
                    continue; // already checked
                } else if (spicesUtility.isConnection(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAParticlePriorConnection");
                } else if (spicesUtility.isHeadTail(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAParticlePriorHeadOrTail");
                } else if (spicesUtility.isMonomer(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isBackboneIndex(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.InvalidPositionOfBackboneIndex");
                } else {
                    return MessageSpices.getString("StructureCheck.InvalidParticleAfterNormalOpeningBracket");
                }
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="NormalBracketClose">
            else if (spicesUtility.isNormalBracketClose(tmpCurrentToken)) {
                if (spicesUtility.isNormalBracketOpen(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isNormalBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isRingClosure(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAParticlePriorRingClosure");
                } else if (spicesUtility.isCurlyBracketOpen(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAConnectionAfterNormalClosingBracket");
                } else if (spicesUtility.isCurlyBracketClose(tmpNextToken)) {
                    continue; // already checked
                } else if (spicesUtility.isConnection(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isHeadTail(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAParticlePriorHeadOrTail");
                } else if (spicesUtility.isMonomer(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAConnectionAfterNormalClosingBracket");
                } else if (spicesUtility.isIntegerNumber(tmpNextToken)) {
                    if (!spicesUtility.isNormalBracketOpen(this.getInnerStructureTokens()[0])
                            && !(spicesUtility.isIntegerNumber(this.getInnerStructureTokens()[0])
                            && spicesUtility.isNormalBracketOpen(this.getInnerStructureTokens()[1]))) {
                        return MessageSpices.getString("StructureCheck.MissingAConnectionAfterNormalClosingBracket");
                    }
                } else if (spicesUtility.isAngleBracketClose(tmpNextToken)) {
                    // legal expression
                } else if (spicesUtility.isBackboneIndex(tmpNextToken)) {
                    continue; // legal expression        
                } else {
                    return MessageSpices.getString("StructureCheck.InvalidParticleAfterNormalClosingBracket");
                }
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="RingClosure">
            else if (spicesUtility.isRingClosure(tmpCurrentToken)) {
                if (spicesUtility.isNormalBracketOpen(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isNormalBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isRingClosure(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isCurlyBracketOpen(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isCurlyBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isConnection(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isHeadTail(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isMonomer(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isIntegerNumber(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isStart(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isEnd(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isAngleBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isBackboneIndex(tmpNextToken)) {
                    continue; // legal expression        
                } else {
                    return MessageSpices.getString("StructureCheck.InvalidParticleAfterRingClosure");
                }
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="CurlyBracketOpen">
            else if (spicesUtility.isCurlyBracketOpen(tmpCurrentToken)) {
                if (spicesUtility.isNormalBracketOpen(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isNormalBracketClose(tmpNextToken)) {
                    continue; // already checked - Structure or monomer begins
                } // with an invalid character exception will be
                // thrown
                else if (spicesUtility.isRingClosure(tmpNextToken)) {
                    continue; // already checked - Structure or monomer begins
                } // with an invalid character exception will be
                // thrown
                else if (spicesUtility.isParticle(tmpNextToken) && spicesUtility.isAvailableParticle(tmpNextToken, anAvailableParticles)) {
                    continue; // legal expression
                } else if (spicesUtility.isCurlyBracketOpen(tmpNextToken)) {
                    continue; // already checked - A closing curly bracket is
                } // missing exception will be thrown
                else if (spicesUtility.isCurlyBracketClose(tmpNextToken)) {
                    continue; // legal expression - it's a monomer.
                } else if (spicesUtility.isConnection(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAParticlePriorConnection");
                } else if (spicesUtility.isHeadTail(tmpNextToken)) {
                    continue; // already checked - Structure or monomer begins
                } // with an invalid character exception will be
                // thrown
                else if (spicesUtility.isMonomer(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MonomerInsideOfCurlyBracket");
                } else if (spicesUtility.isIntegerNumber(tmpNextToken)) {
                    continue; // legal expression
                } else {
                    return MessageSpices.getString("StructureCheck.InvalidParticleAfterCurlyOpeningBracket");
                }
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="CurlyBracketClose">
            else if (spicesUtility.isCurlyBracketClose(tmpCurrentToken)) {
                if (spicesUtility.isNormalBracketOpen(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isNormalBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isRingClosure(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAParticlePriorRingClosure");
                } else if (spicesUtility.isParticle(tmpNextToken) && spicesUtility.isAvailableParticle(tmpNextToken, anAvailableParticles)) {
                    continue; // legal expression
                } else if (spicesUtility.isCurlyBracketOpen(tmpNextToken)) {
                    continue; // already checked - A closing curly bracket is
                } // missing exception will be thrown
                else if (spicesUtility.isCurlyBracketClose(tmpNextToken)) {
                    continue; // legal expression - it's a monomer.
                } else if (spicesUtility.isConnection(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isHeadTail(tmpNextToken)) {
                    continue; // already checked - Structure or monomer begins
                } else if (spicesUtility.isIntegerNumber(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isAngleBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isBackboneIndex(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.InvalidPositionOfBackboneIndex");
                } else {
                    return MessageSpices.getString("StructureCheck.InvalidParticleAfterCurlyClosingBracket");
                }
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Connection">
            else if (spicesUtility.isConnection(tmpCurrentToken)) {
                if (tmpIsRepeatParticle) {
                    tmpIsRepeatParticle = false;
                }
                if (spicesUtility.isNormalBracketOpen(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingParticleBetweenConnectionAndNormalOpeningBracket");
                } else if (spicesUtility.isNormalBracketClose(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingParticleBetweenConnectionAndNormalClosingBracket");
                } else if (spicesUtility.isRingClosure(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingParticleBetweenConnectionAndRingClosure");
                } else if (spicesUtility.isCurlyBracketOpen(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isCurlyBracketClose(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.InvalidLastCharacter");
                } else if (spicesUtility.isConnection(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingParticleBetweenTwoConnections");
                } else if (spicesUtility.isHeadTail(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingParticleBetweenConnectionAndHeadOrTail");
                } else if (spicesUtility.isMonomer(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isIntegerNumber(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isBackboneIndex(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.InvalidPositionOfBackboneIndex");
                } else {
                    return MessageSpices.getString("StructureCheck.InvalidParticleAfterConnection");
                }
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Head or Tail">
            else if (spicesUtility.isHeadTail(tmpCurrentToken)) {
                if (spicesUtility.isNormalBracketOpen(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isNormalBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isRingClosure(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isCurlyBracketOpen(tmpNextToken)) {
                    continue; // already checked
                } else if (spicesUtility.isCurlyBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isConnection(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isHeadTail(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isMonomer(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MonomerAfterHeadOrTail");
                } else if (spicesUtility.isIntegerNumber(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAConnectionAfterHeadOrTail");
                } else if (spicesUtility.isBackboneIndex(tmpNextToken)) {
                    continue; // legal expression
                } else {
                    return MessageSpices.getString("StructureCheck.InvalidParticleAfterHeadTail");
                }
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="START or END">
            else if (spicesUtility.isStartEnd(tmpCurrentToken)) {
                if (spicesUtility.isNormalBracketOpen(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isNormalBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isRingClosure(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isCurlyBracketOpen(tmpNextToken)) {
                    continue; // already checked
                } else if (spicesUtility.isCurlyBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isConnection(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isHeadTail(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isStartEnd(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isAngleBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isBackboneIndex(tmpNextToken)) {
                    continue; // legal expression    
                } else {
                    return MessageSpices.getString("StructureCheck.InvalidParticleAfterStartEnd");
                }
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Monomer">
            else if (spicesUtility.isMonomer(tmpCurrentToken)) {
                if (anIsMonomer) {
                    return MessageSpices.getString("StructureCheck.MonomerInMonomer");
                }
                if (spicesUtility.isNormalBracketOpen(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isNormalBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isRingClosure(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isCurlyBracketOpen(tmpNextToken)) {
                    continue; // already checked
                } else if (spicesUtility.isCurlyBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isConnection(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isHeadTail(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.InvalidParticleAfterMonomer");
                } else if (spicesUtility.isMonomer(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MonomerAfterMonomer");
                } else if (spicesUtility.isIntegerNumber(tmpNextToken)) {
                    continue; // belongs to monomer
                } else if (spicesUtility.isAngleBracketClose(tmpNextToken)) {
                    continue; // legal expression
                } else if (spicesUtility.isBackboneIndex(tmpNextToken)) {
                    continue; // legal expression
                } else {
                    return MessageSpices.getString("StructureCheck.InvalidParticleAfterMonomer");
                }
            } // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="Backbone index">
            else if (spicesUtility.isBackboneIndex(tmpCurrentToken)) {
                tmpNumberOfBackboneIndexCandidat = Integer.parseInt(tmpCurrentToken.substring(1, tmpCurrentToken.length() - 1));
                if (tmpNumberOfBackboneIndexCandidat == 0) {
                    return MessageSpices.getString("StructureCheck.ZeroInBackboneindex");
                }
                if (!tmpNumberOfBackboneIndex.contains(tmpNumberOfBackboneIndexCandidat)) {
                    tmpNumberOfBackboneIndex.add(tmpNumberOfBackboneIndexCandidat);
                } else {
                    return MessageSpices.getString("StructureCheck.RedundancyOfBackboneIndices");
                }
                if (spicesUtility.isParticle(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAConnectionAfterBackboneIndex");
                } else if (spicesUtility.isMonomer(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAConnectionAfterBackboneIndex");
                } else if (spicesUtility.isIntegerNumber(tmpNextToken)) {
                    return MessageSpices.getString("StructureCheck.MissingAConnectionAfterBackboneIndex");
                } else if (spicesUtility.isBackboneIndex(tmpNextToken)) {
                    MessageSpices.getString("StructureCheck.MultipleBackboneIndices");
                }
            } // </editor-fold> 
            else {
                return MessageSpices.getString("StructureCheck.InvalidParticlename");
            }
        }
        // checks last token
        if (spicesUtility.isIntegerNumber(tmpTokenOfSubstructure[tmpNumberOfTokens - 1])) {
            return MessageSpices.getString("StructureCheck.MissingParticleAfterNumber");
        }
        if (spicesUtility.isMonomer(tmpTokenOfSubstructure[tmpNumberOfTokens - 1]) && anIsMonomer) {
            return MessageSpices.getString("StructureCheck.MonomerInMonomer");
        }
        if (spicesUtility.isBackboneIndex(tmpTokenOfSubstructure[tmpNumberOfTokens - 1])) {
            tmpNumberOfBackboneIndexCandidat = Integer.parseInt(tmpTokenOfSubstructure[tmpNumberOfTokens - 1].
                substring(1, tmpTokenOfSubstructure[tmpNumberOfTokens - 1].length() - 1));
            if (!tmpNumberOfBackboneIndex.contains(tmpNumberOfBackboneIndexCandidat)) {
                tmpNumberOfBackboneIndex.add(tmpNumberOfBackboneIndexCandidat);
            } else {
                return MessageSpices.getString("StructureCheck.RedundancyOfBackboneIndices");
            }
        }
        if (tmpNumberOfBackboneIndex.size() > 0 && tmpNumberOfBackboneIndex.size() < Collections.max(tmpNumberOfBackboneIndex)) {
            return MessageSpices.getString("StructureCheck.MissingBackboneIndex");
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Checks whether the structure is out of one part">
        int tmpDepthLevel = 1;
        int tmpNumberOfRegions = 1;
        if (aSubtructure.startsWith("(")) {
            for (int i = 1; i < this.getInnerStructureTokens().length; i++) {
                if (spicesUtility.isNormalBracketOpen(this.getInnerStructureTokens()[i])) {
                    if (tmpDepthLevel == 0) {
                        tmpNumberOfRegions++;
                    }
                    tmpDepthLevel++;
                    continue;
                } else if (spicesUtility.isNormalBracketClose(this.getInnerStructureTokens()[i])) {
                    tmpDepthLevel--;
                }
            }
            String[] tmpStructureRegions = new String[tmpNumberOfRegions];
            int tmpStartPosition = 0;
            int tmpEndPosition = 0;
            tmpDepthLevel = 0;
            int tmpActualPart = 0;
            for (int i = 0; i < tmpNumberOfTokens; i++) {
                if (spicesUtility.isNormalBracketOpen(this.getInnerStructureTokens()[i])) {
                    if (tmpDepthLevel == 0) {
                        tmpStartPosition = i + 1;
                    }
                    tmpDepthLevel++;
                    continue;
                } else if (spicesUtility.isNormalBracketClose(this.getInnerStructureTokens()[i])) {
                    tmpDepthLevel--;
                    if (tmpDepthLevel == 0) {
                        tmpEndPosition = i - 1;
                        StringBuilder tmpStructure = new StringBuilder();
                        for (int j = tmpStartPosition; j <= tmpEndPosition; j++) {
                            tmpStructure.append(this.getInnerStructureTokens()[j]);
                        }
                        tmpStructureRegions[tmpActualPart] = tmpStructure.toString();
                        tmpActualPart++;
                    }
                }
            }
            int tmpNumberOfAngularBrackets = 0;
            int tmpIndexOfLeftLimit;
            int tmpIndexOfRightLimit;
            int tmpStructureRegionsLength = 0;
            String tmpStringBetweenAngularBrackets;
            int[][] tmpPartConnection = new int[tmpStructureRegions.length][];
            for (int i = 0; i < tmpNumberOfRegions; i++) {
                tmpNumberOfAngularBrackets = spicesUtility.getFrequencyOfCharacterInString(tmpStructureRegions[i], "[");
                if (tmpNumberOfAngularBrackets == 0) {
                    return MessageSpices.getString("StructureCheck.MissingConnection");
                }
                tmpPartConnection[i] = new int[tmpNumberOfAngularBrackets];
                tmpStructureRegionsLength = tmpStructureRegions[i].length();
                tmpIndexOfLeftLimit = 0;
                tmpIndexOfRightLimit = 0;
                for (int j = 0; j < tmpStructureRegionsLength; j++) {
                    tmpIndexOfLeftLimit = tmpStructureRegions[i].indexOf('[', tmpIndexOfLeftLimit);
                    if (tmpIndexOfLeftLimit == -1) {
                        break;
                    }
                    tmpIndexOfRightLimit = tmpStructureRegions[i].indexOf(']', tmpIndexOfRightLimit);
                    tmpStringBetweenAngularBrackets = tmpStructureRegions[i].substring(tmpIndexOfLeftLimit + 1, tmpIndexOfRightLimit);
                    if (spicesUtility.isIntegerNumber(tmpStringBetweenAngularBrackets)) {
                        tmpPartConnection[i][j] = Integer.valueOf(tmpStringBetweenAngularBrackets);
                    }
                    tmpIndexOfLeftLimit = tmpIndexOfRightLimit + 1;
                    tmpIndexOfRightLimit = tmpIndexOfLeftLimit;
                }
            }
            if (tmpPartConnection.length > 1) {
                if (!this.isOnePart(tmpPartConnection)) {
                    return MessageSpices.getString("StructureCheck.StructureNotOnePart");
                }
            }
        }

        // </editor-fold>
        return null;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Particle neighbor related methods">
    /**
     * Determines all neighbor particles in a chemical structure represented
     * by aTokens that are connected by a bond
     *
     * @param anIsReverseSequenceIncluded True: Return list of sequences
     * also contains reverse sequences, i.e. if sequence {A, B} is in return
     * list the sequence {B, A} will also be added, false: Otherwise (no
     * redundant information)
     * @return List of sequences. A sequence is an integer array of length 2
     * and consists of indices of particles that are connected by a bond in
     * the chemical structure that aTokens represents. For instance the
     * structure "A-B-C" has two connections, namely "A-B" and "B-C" or
     * expressed as particle index (don't mixed up with token index) "0-1"
     * and "1-2". The result of this example is 0, 1 and 1, 2.
     */
    private LinkedList<int[]> getNeighborParticles(boolean anIsReverseSequenceIncluded) {

        // <editor-fold defaultstate="collapsed" desc="Initialisation">
        LinkedList<int[]> tmpSequenceList = new LinkedList<>();
        int[] tmpSequence = new int[2];
        int[] tmpHeadParticleIndices = null;
        int[] tmpTailParticleIndices = null;
        boolean tmpHasRingClosure = false;

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Particles, normal brackets, curly brackets">
        int tmpRightParticleIndex = 0;
        int tmpLeftParticleIndex = 0;
        int tmpLeftBranchLevel = 0;
        int tmpRightBranchLevel = 0;
        int[] tmpTokenRepeatsNotInSeries = getTotalNumberOfParticleRepetitions(false);
        boolean tmpHasLeftParticle = false;
        boolean[] tmpIsParticleFlags = this.isInnerParticleList;
        String[] tmpInnerStructureTokens =  this.getInnerStructureTokens();

        // Determine first particle of a link
        for (int i = 0; i < tmpInnerStructureTokens.length; i++) {
            // <editor-fold defaultstate="collapsed" desc="Token">

            if (!tmpHasLeftParticle && (tmpIsParticleFlags[i] || spicesUtility.isMonomer(tmpInnerStructureTokens[i]))) {
                while (this.innerParticleIndices[tmpLeftParticleIndex] < i) {
                    tmpLeftParticleIndex++;
                }
                tmpHasLeftParticle = true;
            } else if (spicesUtility.isCurlyBracketOpen(tmpInnerStructureTokens[i])) {
                tmpHeadParticleIndices = getIndexOfParticles("[HEAD]", i);
                tmpTailParticleIndices = getIndexOfParticles("[TAIL]", i);
                if (!tmpHasLeftParticle) {
                    tmpSequence[0] = tmpTailParticleIndices[0];
                    // search for next Particle which is connected with last
                    // TAIL particle
                    int tmpParticleToTailIndex = this.getNextParticleConnectWithTailIndex(i);
                    if (tmpParticleToTailIndex != -1) {
                        tmpSequence[1] = tmpParticleToTailIndex;
                        tmpSequenceList.add(tmpSequence.clone());
                    }
                }
            } else if (!tmpHasLeftParticle && spicesUtility.isNormalBracketClose(tmpInnerStructureTokens[i])) {
                tmpLeftBranchLevel--;
            } else if (!tmpHasLeftParticle && spicesUtility.isNormalBracketOpen(tmpInnerStructureTokens[i])) {
                tmpLeftBranchLevel++;
            } else if (!tmpHasRingClosure && spicesUtility.isRingClosure(tmpInnerStructureTokens[i])) {
                tmpHasRingClosure = true;
            } else if (tmpHasLeftParticle) {
                // Determine the right particle of a link
                tmpRightBranchLevel = tmpLeftBranchLevel;
                for (int j = i; j < tmpInnerStructureTokens.length; j++) {
                    if (spicesUtility.isConnection(tmpInnerStructureTokens[j])) {
                        // legal expression
                    } else if (spicesUtility.isCurlyBracketOpen(tmpInnerStructureTokens[j])) {
                        tmpHeadParticleIndices = getIndexOfParticles("[HEAD]", j);
                        tmpTailParticleIndices = getIndexOfParticles("[TAIL]", j);
                        tmpSequence[0] = tmpLeftParticleIndex;
                        tmpSequence[1] = tmpHeadParticleIndices[0];
                        tmpSequenceList.add(tmpSequence.clone());
                        break;
                    } else if (spicesUtility.isCurlyBracketClose(tmpInnerStructureTokens[j])) {
                        break;
                    } else if (spicesUtility.isNormalBracketOpen(tmpInnerStructureTokens[j])) {
                        tmpRightBranchLevel++;
                    } else if (spicesUtility.isNormalBracketClose(tmpInnerStructureTokens[j])) {
                        tmpRightBranchLevel--;
                        if (tmpRightBranchLevel < tmpLeftBranchLevel) {
                            tmpLeftBranchLevel--;
                            break;
                        }
                    } else if (tmpIsParticleFlags[j] || spicesUtility.isMonomer(tmpInnerStructureTokens[j])) {
                        if (tmpLeftBranchLevel == tmpRightBranchLevel || (tmpLeftBranchLevel == tmpRightBranchLevel - 1 && isTokenAfterNormalBracketOpen(j))) {
                            while (this.innerParticleIndices[tmpRightParticleIndex] < j) {
                                tmpRightParticleIndex++;
                            }
                            int tmpLeftRepeat = tmpTokenRepeatsNotInSeries[this.innerParticleIndices[tmpLeftParticleIndex]];
                            int tmpRightRepeat = tmpTokenRepeatsNotInSeries[this.innerParticleIndices[tmpRightParticleIndex]] / tmpLeftRepeat;
                            for (int ii = 0; ii < tmpLeftRepeat; ii++) {
                                tmpSequence[0] = tmpLeftParticleIndex + ii;
                                for (int jj = 0; jj < tmpRightRepeat; jj++) {
                                    tmpSequence[1] = tmpRightParticleIndex + ii * (tmpRightRepeat) + jj;
                                    tmpSequenceList.add(tmpSequence.clone());
                                }
                            }
                            if (tmpLeftBranchLevel == tmpRightBranchLevel) {
                                break;
                            }
                        }
                    }
                }
                tmpHasLeftParticle = false;
                tmpRightParticleIndex = tmpLeftParticleIndex + 1;
            }

            // </editor-fold>
        }

        // <editor-fold defaultstate="collapsed" desc="- Ring closure">
        int[][] tmpRingClosureParticleIndex = this.getRingClosureParticleIndices();
        if (tmpHasRingClosure) {
            for (int i = 0; i < tmpRingClosureParticleIndex.length; i += 2) {
                tmpSequence[0] = tmpRingClosureParticleIndex[i][1];
                tmpSequence[1] = tmpRingClosureParticleIndex[i + 1][1];
                if (!tmpSequenceList.stream().anyMatch(a -> Arrays.equals(a, tmpSequence))) {
                    tmpSequenceList.add(tmpSequence.clone());
                }
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Sort sequence list">

        if (anIsReverseSequenceIncluded) {
            int[][] tmpSequenceArray = tmpSequenceList.toArray(new int[0][]);
            int tmpLinkSize = tmpSequenceList.size();
            for (int i = 0; i < tmpLinkSize; i++) {
                tmpSequence[0] = tmpSequenceArray[i][1];
                tmpSequence[1] = tmpSequenceArray[i][0];
                tmpSequenceList.add(tmpSequence.clone());
            }
        }
        tmpSequenceList.sort(new ComparatorFirstIndex());
        // </editor-fold>
        // </editor-fold>
        return tmpSequenceList;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Coordinates of Tokens related methods">
    /**
     * Convert the token list to a compiled token list
     */
    private String[] getSpicesCompilerTokenList() {
        if (this.getInnerStructureTokens() == null || this.getInnerStructureTokens().length == 0) {
            return null;
        }
        // <editor-fold defaultstate="collapsed" desc="- Initial settings">
        ArrayDeque<String> tmpTokenList1 = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        ArrayDeque<String> tmpTokenList2 = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        ArrayDeque<String> tmpHelperList = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        int tmpFrequencyNumber = 0;
        int tmpIndex = 0;
        boolean tmpIsAfterNumber = false;
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Numbers prior particle">
        if (this.getInnerStructureTokens().length == 1) {
            return this.getInnerStructureTokens();
        }
        if (spicesUtility.isMonomer(this.getInnerStructureTokens()[0])) {
            tmpTokenList1.add(this.getInnerStructureTokens()[0]);
        }
        for (int i = 1; i < this.getInnerStructureTokens().length; i++) {
            if (spicesUtility.isIntegerNumber(this.getInnerStructureTokens()[i - 1])) {
                if (spicesUtility.isParticle(this.getInnerStructureTokens()[i])) {
                    tmpFrequencyNumber = Integer.parseInt(this.getInnerStructureTokens()[i - 1]);
                    for (int j = 0; j < tmpFrequencyNumber; j++) {
                        tmpTokenList1.add("1");
                        tmpTokenList1.add(this.getInnerStructureTokens()[i]);
                        if (j + 1 < tmpFrequencyNumber) {
                            tmpTokenList1.add("-");
                        }
                    }
                } else {
                    tmpTokenList1.add(this.getInnerStructureTokens()[i - 1]);
                    tmpTokenList1.add(this.getInnerStructureTokens()[i]);
                }
            } else if (spicesUtility.isIntegerNumber(this.getInnerStructureTokens()[i])) {
                if (i == 1) {
                    tmpTokenList1.add(this.getInnerStructureTokens()[0]);
                }
            } else {
                tmpTokenList1.add(this.getInnerStructureTokens()[i]);
            }
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="- Numbers prior curly bracket">
        String[] tmpTokenArray1 = tmpTokenList1.toArray(new String[0]);
        if (spicesUtility.hasCurlyBracket(this.inputStructure)) {
            while (tmpIndex < tmpTokenArray1.length - 1) {
                if (spicesUtility.isIntegerNumber(tmpTokenArray1[tmpIndex])
                        && spicesUtility.isCurlyBracketOpen(tmpTokenArray1[tmpIndex + 1])) {
                    tmpFrequencyNumber = Integer.parseInt(tmpTokenArray1[tmpIndex]);
                    tmpIsAfterNumber = true;
                } else {
                    if (tmpIsAfterNumber) {
                        while (!tmpTokenArray1[tmpIndex].equals("}")) {
                            tmpHelperList.add(tmpTokenArray1[tmpIndex]);
                            tmpIndex++;
                        }
                        for (int i = 0; i < tmpFrequencyNumber; i++) {
                            tmpTokenList2.addAll(tmpHelperList);
                            tmpTokenList2.add("}");
                        }
                        tmpIsAfterNumber = false;
                    } else {
                        tmpTokenList2.add(tmpTokenArray1[tmpIndex]);
                    }
                }
                tmpIndex++;
            }
            if (tmpIndex < tmpTokenArray1.length) {
                tmpTokenList2.add(tmpTokenArray1[tmpTokenArray1.length - 1]);
            }
        } else {
            return tmpTokenList1.toArray(new String[0]);
        }

        // </editor-fold>
        return tmpTokenList2.toArray(new String[0]);
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Other methods">
    /**
     * Determines the index positions (zero-based) of particles in
     * structureTokens
     *
     * @return Array of indices: Length of array = total number of
     * particles, returnArray[i] is the index of the i-th particle of the
     * structure in aTokens
     *
     */
    private int[] determineInnerParticleIndices() {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.getInnerStructureTokens() == null || this.getInnerStructureTokens().length == 0) {
            return null;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Local variables">
        int tmpTotalParticleCount = this.getInnerTotalNumberOfParticles();
        int[] tmpParticleIndices = new int[tmpTotalParticleCount];
        this.isInnerParticleList = new boolean[tmpTotalParticleCount];
        int tmpIndex = 0;

        // </editor-fold>
        int[] tmpTotalNumberOfParticleRepetitions = this.getTotalNumberOfParticleRepetitions(true);
        for (int i = 0; i < tmpTotalNumberOfParticleRepetitions.length; i++) {
            if (tmpTotalNumberOfParticleRepetitions[i] > 0) {
                for (int j = 0; j < tmpTotalNumberOfParticleRepetitions[i]; j++) {
                    tmpParticleIndices[tmpIndex] = i;
                    tmpIndex++;
                }
            }
        }
        return tmpParticleIndices;
    }

    /**
     * Determines the total number of particles in aTokens with all possible
     * repetitions (normal and curly brackets are taken into account).
     *
     * @return Number of particles in aTokens
     */
    private int getInnerTotalNumberOfParticles() {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.getInnerStructureTokens() == null) {
            return 0;
        }

        // </editor-fold>
        int result = 0;
        int[] tmpNumberOfTokenRepeats = this.getTotalNumberOfParticleRepetitions(true);
        for (int n : tmpNumberOfTokenRepeats) {
            result += n;
        }
        return result;
    }

    /**
     * Determines the number of repetitions of each particle in aTokens. If
     * a single token is not a particle or a monomer a "0" is set in the
     * return array.
     *
     * @param aHasRepetitiveParticle True: Structure has repetitive
     * particles, e.g. 5A, false: Otherwise
     * @return Array with the number of total repetitions of each particle.
     * If aTokens[i] is NOT a particle or a monomer returnArray[i] is 0.
     * This method takes normal and curly brackets into account.
     */
    private int[] getTotalNumberOfParticleRepetitions(boolean aHasRepetitiveParticle) {

        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (this.getInnerStructureTokens() == null) {
            return null;
        }

        // </editor-fold>
        int tmpNumberOfTokens = this.getInnerStructureTokens().length;
        int tmpNumberOfCurrentRepetitions = 1;
        int[] tmpNumberOfTokenRepetitions = new int[tmpNumberOfTokens];
        boolean[] tmpIsNumberFlags = new boolean[tmpNumberOfTokens];
        Arrays.fill(tmpIsNumberFlags, false);

        for (int i = 0; i < tmpNumberOfTokens; i++) {
            if (spicesUtility.isParticle(this.getInnerStructureTokens()[i]) || spicesUtility.isMonomer(this.getInnerStructureTokens()[i])) {
                tmpNumberOfTokenRepetitions[i] = 1;
            }
            if (spicesUtility.isIntegerNumber(this.getInnerStructureTokens()[i])) {
                tmpIsNumberFlags[i] = true;
            }
        }
        for (int i = 0; i < tmpNumberOfTokens; i++) {
            if (tmpIsNumberFlags[i]) {
                i++;
                if (i == tmpNumberOfTokens) {
                    break;
                }
                if (tmpNumberOfTokenRepetitions[i] > 0) {
                    // Repeat size of series of particle
                    if (aHasRepetitiveParticle) {
                        tmpNumberOfTokenRepetitions[i] = tmpNumberOfCurrentRepetitions;
                        continue;
                    }
                } else if (spicesUtility.isCurlyBracketOpen(this.getInnerStructureTokens()[i])) {
                    int j = i + 1;
                    while (!spicesUtility.isCurlyBracketClose(this.getInnerStructureTokens()[j])) {
                        if (j >= tmpNumberOfTokens) {
                            break;
                        }
                        tmpNumberOfTokenRepetitions[j] = tmpNumberOfCurrentRepetitions;
                        j++;
                    }
                }
            }
        }
        return tmpNumberOfTokenRepetitions;
    }

    /**
     * Set (zero-based) inner particles
     */
    private void setInnerParticles() {
        if (this.getInnerStructureTokens() != null && this.getInnerStructureTokens().length > 0) {
            this.isInnerParticleList = new boolean[this.getInnerStructureTokens().length];
            Arrays.fill(this.isInnerParticleList, false);
            int totalNumberOfParticles = this.innerParticleIndices.length;
            this.innerParticles = new String[totalNumberOfParticles];
            for (int i = 0; i < totalNumberOfParticles; i++) {
                this.innerParticles[i] = this.getInnerStructureTokens()[this.innerParticleIndices[i]];
                this.isInnerParticleList[this.innerParticleIndices[i]] = true;
            }
        }
    }

    /**
     * Determine the particle position with "START"-tag
     *
     * @return 0-based index number of particle with "START"-tag
     */
    private int getStartTagParticleIndex() {
        return this.getStartEndParticleIndex("[START]");
    }
    
    /**
     * Determine the particle position with "END"-tag
     *
     * @return 0-based index number of particle with "END"-tag
     */
    private int getEndTagParticleIndex() {
        return this.getStartEndParticleIndex("[END]");
    }

    /**
     * Determine the particle position with "START"- or "END"-tag
     * @param aTokenName
     * @return 
     */
    private int getStartEndParticleIndex(String aTokenName) {
        int indexOfResult = 0;
        int indexOfEndToken = 0;
        if (!this.hasStartEndAttribute) {
            return -1;
        } else {
            for (int i = 0; i < this.getInnerStructureTokens().length; i++) {
                if (this.getInnerStructureTokens()[i].equals(aTokenName)) {
                    indexOfEndToken = i;
                    break;
                }
            }
            for (int i = this.innerParticleIndices.length - 1; i >= 0; i--) {
                if (indexOfEndToken > this.innerParticleIndices[i]) {
                    indexOfResult = i;
                    break;
                }
            }
        }
        return indexOfResult;
    }

    /**
     * Returns monomer names of structure
     *
     * @return Monomer names of structure or null if none were found
     */
    private String[] getMonomerList() {
        HashSet<String> tmpMonomersHashSet = new HashSet<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY);
        for (String tmpSingleToken : this.getInnerStructureTokens()) {
            if (!tmpMonomersHashSet.contains(tmpSingleToken) && spicesUtility.isMonomer(tmpSingleToken)) {
                tmpMonomersHashSet.add(tmpSingleToken);
            }
        }
        if (tmpMonomersHashSet.isEmpty()) {
            return null;
        } else {
            return tmpMonomersHashSet.toArray(new String[0]);
        }
    }

    /**
     * Counts the number of neighbor particles of a particle
     *
     * @param aLink All links of a Structure
     * @param aParticleLength Number of all particles of a structure
     * @return Number of neighbor particles
     */
    private int[] getFrequencyOfNeighborParticles() {
        int tmpPartLength = this.innerParticles.length;
        int tmpTargetIndexPosition = 0;
        int[] tmpResults = new int[tmpPartLength];
        int[] tmpInterimResults = new int[tmpPartLength];

        for (int[] tmpConnectedParticlesListItem : this.connectedParticlesList) {
            tmpInterimResults[tmpConnectedParticlesListItem[0]]++;
        }
        System.arraycopy(tmpInterimResults, 0, tmpResults, tmpTargetIndexPosition, tmpInterimResults.length);
        tmpTargetIndexPosition += tmpPartLength;

        return tmpResults;
    }

    /**
     * Determine the index position of a particle with related pattern
     *
     * @param aPattern A Pattern
     * @param aStartIndex Start index of token list
     * @return Index positions of all particles with related pattern
     */
    private int[] getIndexOfParticles(String aPattern, int aStartIndex) {

        // <editor-fold defaultstate="collapsed" desc="Simple checks">
        if (aPattern == null || aPattern.isEmpty()) {
            return null;
        }
        if (aStartIndex < 0 || aStartIndex > this.getInnerStructureTokens().length - 1) {
            return null;
        }

        // </editor-fold>
        ArrayDeque<Integer> tmpParticleIndex = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        int tmpSearchIndex = 0;
        int tmpRepeat = 1;
        for (int i = aStartIndex; i < this.getInnerStructureTokens().length; i++) {
            if (this.isInnerParticleList[i]) {
                if (i > 0 && spicesUtility.isIntegerNumber(this.getInnerStructureTokens()[i - 1])) {
                    tmpRepeat = Integer.parseInt(this.getInnerStructureTokens()[i - 1]);
                } else {
                    tmpRepeat = 1;
                }
                tmpSearchIndex = i;
            }
            if (this.getInnerStructureTokens()[i].equals(aPattern)) {
                for (int j = 0; j < this.innerParticleIndices.length; j++) {
                    if (this.innerParticleIndices[j] == tmpSearchIndex) {
                        tmpParticleIndex.add(j + tmpRepeat - 1);
                        j += tmpRepeat - 1;
                    }
                }
            }
        }
        int[] tmpResults = tmpParticleIndex.stream()
                .mapToInt(Integer::intValue)
                .toArray();
        return tmpResults;
    }

    /**
     * Overloaded method. Determines the path from START particle to END
     * particle. The path is not necessarely the longest possible.
     *
     * @ param aPart: A part of Spices
     * @return Array of particles from START-particle to END-particle.
     */
    private int[] getPathStartToEnd(SpicesInner aPart) {
        ArrayDeque<Integer> tmpPath = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        ArrayDeque<Integer> tmpResultPath = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        int[] tmpNextVertice = new int[aPart.innerParticleIndices.length];
        int[] tmpResult;
        int tmpActualVertice = 0;
        int tmpParticleLength = aPart.innerParticleIndices.length;
        boolean[] tmpVisitedParticleFlag = new boolean[tmpParticleLength];
        boolean tmpPointerAtEnd;

        // Traverse the graph from [START] to [END] and fit the vertices
        // together to the path.
        // Simple breadth-first search algorithm is used.
        Arrays.fill(tmpVisitedParticleFlag, false);
        Queue<Integer> tmpQueue = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        tmpQueue.add(aPart.startParticleIndex);
        tmpNextVertice[0] = 0;
        tmpVisitedParticleFlag[aPart.startParticleIndex] = true;
        while (!tmpQueue.isEmpty()) {
            tmpActualVertice = tmpQueue.remove();
            if (tmpActualVertice == aPart.endParticleIndex) {
                tmpQueue.remove(tmpActualVertice);
                tmpVisitedParticleFlag[tmpActualVertice] = true;
                break;
            }
            for (int tmpNextParticleItem : aPart.adjacentArray[tmpActualVertice]) {
                if (!tmpVisitedParticleFlag[tmpNextParticleItem]) {
                    tmpNextVertice[tmpNextParticleItem] = tmpActualVertice;
                    tmpVisitedParticleFlag[tmpNextParticleItem] = true;
                    tmpQueue.add(tmpNextParticleItem);
                }
            }
        }
        tmpPointerAtEnd = false;
        tmpActualVertice = aPart.endParticleIndex;
        tmpPath.add(tmpActualVertice);
        while (!tmpPointerAtEnd) {
            tmpActualVertice = tmpNextVertice[tmpActualVertice];
            tmpPath.add(tmpActualVertice);
            if (tmpActualVertice == aPart.startParticleIndex) {
                tmpPointerAtEnd = true;
            }
        }
        Iterator tmpIterator = tmpPath.descendingIterator();
        while(tmpIterator.hasNext())
            {
                tmpResultPath.add((Integer)tmpIterator.next());
            }
        tmpResult = tmpResultPath.stream()
                .mapToInt(Integer::intValue)
                .toArray();
        return tmpResult;
    }

    /**
     * Overloaded method. Determines a heuristic diameter. This algorithm
     * choose an arbitrary start particle s. The particle (v) at the
     * farthest from s is then traced. Depth First Search Algorithm (DFS) is
     * used. Again, from v DFS is used to find the particle (w) at the
     * farthest of v. This algorithm get the correct diameter, if the
     * structure is not cyclic. To find the real diameter all possible
     * shortest path between terminal particles have to determined. The
     * longest of them is the diameter. This will take too much computing
     * time by structures with many particles.
     *
     * @param aPart: A part of Spices
     */
    private int[] getHeuristicDiameter(SpicesInner aPart) {
        int tmpStartParticleIndex = 0;

        DepthFirstPath dfs = new DepthFirstPath(aPart.adjacentArray, tmpStartParticleIndex);
        tmpStartParticleIndex = dfs.getEndParticleIndex();
        dfs = new DepthFirstPath(aPart.adjacentArray, tmpStartParticleIndex);
        return dfs.getDiameterPath();
    }

    /**
     * Determine the particle index position which is connected with tail
     * particle
     *
     * @param aStartIndex The token start index position
     * @return The particle index position which is connected with tail
     * particle If nothing is found -1 will be return.
     */
    private int getNextParticleConnectWithTailIndex(int aStartIndex) {

        int tmpCursorIndex = aStartIndex;
        int tmpBranchLevel = 0;
        while (!spicesUtility.isCurlyBracketClose(this.getInnerStructureTokens()[tmpCursorIndex])) {
            tmpCursorIndex++;
        }
        for (int i = tmpCursorIndex; i < this.getInnerStructureTokens().length; i++) {
            if (spicesUtility.isNormalBracketOpen(this.getInnerStructureTokens()[i])) {
                tmpBranchLevel++;
            } else if (spicesUtility.isNormalBracketClose(this.getInnerStructureTokens()[i])) {
                tmpBranchLevel--;
            } else if (tmpBranchLevel == 0 && (this.isInnerParticleList[i] || spicesUtility.isMonomer(this.getInnerStructureTokens()[i]))) {
                for (int j = 0; j < this.innerParticleIndices.length; j++) {
                    if (this.innerParticleIndices[j] == i) {
                        return j;
                    }
                }
            } else if (spicesUtility.isCurlyBracketOpen(this.getInnerStructureTokens()[i])) {
                return getIndexOfParticles("[HEAD]", i)[0];
            }
        }
        return -1;
    }

    /**
     * Determine whether the token is directly after a normal opening
     * bracket (true) or not (false)
     *
     * @param aPosition The position of the token
     * @return Is the token directly after a normal opening bracket or not
     */
    private boolean isTokenAfterNormalBracketOpen(int aPosition) {
        if (aPosition > this.getInnerStructureTokens().length - 1) {
            return false;
        }
        for (int i = aPosition - 1; i >= 0; i--) {
            if (spicesUtility.isIntegerNumber(this.getInnerStructureTokens()[i])) {
                continue;
            }
            return spicesUtility.isNormalBracketOpen(this.getInnerStructureTokens()[i]);
        }
        return false;
    }

    /**
     * Determine whether a particle is a terminal particle (true) or not
     * (false)
     *
     * @return Information about a particle is a terminal particle or not
     */
    private boolean[] isTerminalParticle() {
        boolean[] tmpResult = new boolean[this.innerParticleIndices.length];
        int tmpNumberOfTerminalParticles = 0;
        for (int i = 0; i < this.innerParticleIndices.length; i++) {
            if (this.numberOfConnects[i] == 1) {
                tmpResult[i] = true;
                tmpNumberOfTerminalParticles++;
            } else {
                tmpResult[i] = false;
            }
        }
        this.numberOfTerminalParticles = tmpNumberOfTerminalParticles;
        return tmpResult;
    }

    /**
     * Determine whther the structure is out of only one part (true) or not
     * (false)
     *
     * @param aPartConnectionMarker ConnectionMarker between parts: 1st
     * index stands for Partnumber 2nd index for connectionmarker index
     * @return Information about a structure is out of only one part (true)
     * or not (false)
     */
    private boolean isOnePart(int[][] aPartConnectionMarker) {
        if (aPartConnectionMarker[0].length == 0) {
            return false;
        }
        HashSet<Integer> tmpPartOfStructure = new HashSet<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY);
        tmpPartOfStructure.add(0);
        int tmpActualMarkerNumber = 0;
        for (int i = 0; i < aPartConnectionMarker.length - 1; i++) {
            for (int ii = 0; ii < aPartConnectionMarker[i].length; ii++) {
                tmpActualMarkerNumber = aPartConnectionMarker[i][ii];
                for (int j = i + 1; j < aPartConnectionMarker.length; j++) {
                    for (int jj = 0; jj < aPartConnectionMarker[j].length; jj++) {
                        if (tmpActualMarkerNumber == aPartConnectionMarker[j][jj]) {
                            if (!tmpPartOfStructure.contains(j)) {
                                tmpPartOfStructure.add(j);
                                j = aPartConnectionMarker[i].length;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return (tmpPartOfStructure.size() == aPartConnectionMarker.length);
    }

    /**
     * Determine particle index positions of particles with ringclosure tags
     * @param aInputStructure
     * @return particle indices of particles with ringclosure tags
    */
    private int[][] getRingClosureParticleIndices() {
        int[][] tmpResult;
        int[] tmpRingIndex = new int[2];
        int tmpIndexOfLeftLimit = 0;
        int tmpIndexOfRightLimit = 0;
        int tmpNumberOfOpeningAngularBrackets = spicesUtility.getFrequencyOfCharacterInString(this.inputStructure, "[");
        int tmpNumberOfRingClosures = 0;
        String tmpStringBetweenAngularBrackets;
        String[] tmpTokens = this.getInnerStructureTokens();
        ArrayDeque<Integer> tmpRingClosureNumbersList = new ArrayDeque<>(SpicesConstants.DEFAULT_ARRAYDEQUE_INITIAL_CAPACITY);
        int[] tmpRingClosureNumbersArray;

        // checks existence of ringclosure particle
        if (tmpNumberOfOpeningAngularBrackets == 0) {
            return new int[0][];
        }
        for (int i = 0; i < tmpNumberOfOpeningAngularBrackets; i++) {
            tmpIndexOfLeftLimit = this.inputStructure.indexOf('[', tmpIndexOfLeftLimit);
            tmpIndexOfRightLimit = this.inputStructure.indexOf(']', tmpIndexOfRightLimit);
            tmpStringBetweenAngularBrackets = this.inputStructure.substring(tmpIndexOfLeftLimit + 1, tmpIndexOfRightLimit);
            if (spicesUtility.isIntegerNumber(tmpStringBetweenAngularBrackets)) {
                tmpNumberOfRingClosures++;
                tmpRingClosureNumbersList.add(Integer.parseInt(tmpStringBetweenAngularBrackets));
            }
            tmpIndexOfLeftLimit = tmpIndexOfRightLimit + 1;
            tmpIndexOfRightLimit = tmpIndexOfLeftLimit;
        }
        if (tmpNumberOfRingClosures == 0) {
            return new int[0][];
        }
        tmpRingClosureNumbersArray = tmpRingClosureNumbersList.stream()
               .mapToInt(Integer::intValue)
               .sorted()
               .toArray();
        LinkedList<int[]>tmpHelpingList = new LinkedList<>();
        int tmpMatchNumber = 0;
        String tmpMatchString;
        for (int i = 0; i < tmpRingClosureNumbersArray.length; i += 2) {
            tmpMatchString = "[" + tmpRingClosureNumbersArray[i] + "]";
            for (int j = 0; j < tmpTokens.length; j++) {
                if (tmpMatchString.equals(tmpTokens[j])) {
                    tmpRingIndex[0] = tmpRingClosureNumbersArray[i]; 
                    tmpRingIndex[1] = j;
                    tmpHelpingList.add(tmpRingIndex.clone());
                    tmpMatchNumber++;
                    if (tmpMatchNumber == 2) {
                        tmpMatchNumber = 0;
                        break;
                    }
                }
            }
        }
        tmpHelpingList.sort(new ComparatorFirstIndex());
        tmpResult = tmpHelpingList.toArray(new int[0][]);
        for (int[] tmpResultItem : tmpResult) {
            for (int j = innerParticleIndices.length - 1; j >= 0; j--) {
                if (tmpResultItem[1] > this.innerParticleIndices[j]) {
                    tmpResultItem[1] = j;
                    break;
                }
            }
        }
        return tmpResult;
    }
    // </editor-fold>
    // </editor-fold>

}
