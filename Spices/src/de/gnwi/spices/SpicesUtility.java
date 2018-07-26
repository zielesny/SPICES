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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Pattern;

/**
 * Spices utility
 * 
 * @author Mirco Daniel
 */
public class SpicesUtility {
    // <editor-fold defaultstate="collapsed" desc="Private variables">
    /**
     * Pattern for input structure to match
     */
    private final Pattern inputStructurePattern = Pattern.compile(SpicesConstants.INPUTSTRUCTURE_ALLOWED_CHARACTERS_REGEX_STRING);

    /**
    * Structure tokens as HashMap
    */
    private final HashMap<String, String> outerStructureTokensHashMap = new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY);
    
    /**
     * Innerstructure tokens as HashMap
     * key: input structure, value: tokens
     */
    private final HashMap<String, String[]> structureTokensHashMap = new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY);
        
    /**
     * Adjacentarray as HashMap
     */
    private final HashMap<String, int[][]> adjacentArrayHashMap = new HashMap<>(SpicesConstants.DEFAULT_HASHMAP_INITIAL_CAPACITY);
    
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    // <editor-fold defaultstate="collapsed" desc="- Is-methods">
    /**
     * Check if part exists in the structure
     *
     * @param aStructure Structure
     * @return True: Part exists, false: not
     */
    public boolean hasPart(String aStructure) {
        return aStructure.contains("<");
    }

    public boolean hasPart(char[] aStructure) {
        for (char aChar : aStructure) {
            if (aChar == '<') {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if structure has a START tag
     *
     * @param aStructure A input structure
     * @return True: Structure has START tag, false: otherwise
     */
    public boolean hasStartTag(String aStructure) {
        return aStructure.contains("[START]");
    }

    /**
     * Check whether the structure has backbone particles
     *
     * @param aStructure A input structure
     * @return True: structure has backbone particles, False: otherwise
     */
    public boolean hasBackboneTag(String aStructure) {
        return aStructure.contains("'");
    }

    /**
     * Checks whether the structure has a curly bracket
     *
     * @param aStructure A input structure
     * @return True: structure has curly bracket, False: otherwise
     */
    public boolean hasCurlyBracket(String aStructure) {
        return aStructure.contains("{");
    }

    /**
     * Checks whether aToken is defined in availableParticles-hashmap. If
     * availableParticles-hashmap is empty, true will be return.
     *
     * @param aToken Token
     * @param availableParticles Hashmap of available particles
     * @return True: aToken is defined in availableParticles-hashmap or the
     * hashmap is empty False: aToken is not defined in
     * availableParticles-hashmap
     */
    public boolean isAvailableParticle(String aToken, HashMap<String, String> availableParticles) {
        if (availableParticles.isEmpty()) {
            return true;
        } else {
            return availableParticles.containsKey(aToken);
        }
    }

    /**
     *
     * @param aToken Token
     * @return True: Token is backbone index, false: Otherwise
     */
    public boolean isBackboneIndex(String aToken) {
        char[] tmpToken = aToken.toCharArray();
        if (tmpToken[0] != '\'' || tmpToken[tmpToken.length - 1] != '\'') {
            return false;
        }
        for (int i = 1; i < tmpToken.length - 1; i++) {
            if (!Character.isDigit(tmpToken[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Is-method for molecular structure/monomer
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isConnection(String aToken) {
        return aToken.equals("-");
    }

    /**
     * Is-method for particle
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isParticle(String aToken) {
        char[] tmpToken = aToken.toCharArray();
        if (tmpToken.length > 10) {
            return false;
        }
        if (!Character.isUpperCase(tmpToken[0])) {
            return false;
        }
        for (int i = 1; i < tmpToken.length; i++) {
            if (!(tmpToken[i] >= 'a' && tmpToken[i] <= 'z')
                    && !(tmpToken[i] >= 'A' && tmpToken[i] <= 'Z')
                    && !Character.isDigit(tmpToken[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Is-method for molecular structure/monomer
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isHead(String aToken) {
        return aToken.equals("[HEAD]");
    }

    /**
     * Is-method for molecular structure/monomer
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isTail(String aToken) {
        return aToken.equals("[TAIL]");
    }

    /**
     * Is-method for molecular structure/monomer
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isHeadTail(String aToken) {
        return isHead(aToken) || isTail(aToken);
    }

    /**
     * Is-method for molecular structure/monomer
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isStart(String aToken) {
        return aToken.equals("[START]");
    }

    /**
     * Is-method for molecular structure/monomer
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isStartEnd(String aToken) {
        return this.isStart(aToken) || this.isEnd(aToken);
    }

    /**
     * Is-method for molecular structure/monomer
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isEnd(String aToken) {
        return aToken.equals("[END]");
    }

    /**
     * Returns if anIntegerNumberRepresentation represents an integer value by
     * check of each digit
     *
     * @param anIntegerNumberRepresentation Integer representation
     * @return true: anIntegerRepresentation represents an integer value, false:
     * Otherwise
     */
    public boolean isIntegerNumber(String anIntegerNumberRepresentation) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (anIntegerNumberRepresentation == null || anIntegerNumberRepresentation.isEmpty()) {
            return false;
        }
        // </editor-fold>
        char[] tmpCharSequence = anIntegerNumberRepresentation.toCharArray();
        for (char tmpSingleChar : tmpCharSequence) {
            if (!Character.isDigit(tmpSingleChar)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Is-method for molecular structure/monomer
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isCurlyBracketOpen(String aToken) {
        return aToken.equals("{");
    }

    /**
     * Is-method for molecular structure/monomer
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isCurlyBracketClose(String aToken) {
        return aToken.equals("}");
    }
    
    /**
     * Is-method for molecular structure/monomer
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isNormalBracketClose(String aToken) {
        return aToken.equals(")");
    }

    /**
     * Is-method for molecular structure/monomer
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isNormalBracketOpen(String aToken) {
        return aToken.equals("(");
    }

    /**
     * Is-method for subParticle
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isAngleBracketOpen(String aToken) {
        return aToken.equals("<");
    }

    /**
     * Is-method for subParticle
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isAngleBracketClose(String aToken) {
        return aToken.equals(">");
    }

    /**
     * Is-method for molecular structure/monomer
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isMonomer(String aToken) {
        if (aToken.contains("#") && aToken.length() > 1) {
            if (Character.isUpperCase(aToken.charAt(1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is-method for molecular structure/monomer
     *
     * @param aToken Token
     * @return True: Condition is met, false: Otherwise
     */
    public boolean isRingClosure(String aToken) {
        if (aToken == null || aToken.isEmpty()) {
            return false;
        }
        char[] tmpToken = aToken.toCharArray();
        if (tmpToken[0] != '[' || tmpToken[tmpToken.length - 1] != ']') {
            return false;
        }
        for (int i = 1; i < tmpToken.length - 1; i++) {
            if (!Character.isDigit(tmpToken[i])) {
                return false;
            }
        }
        return true;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Token related methods">
    /**
     * Determine the adjacent array (all particles connected with particle i
     * in a row)
     *
     * @param aPartStructureString Part structure string
     * @param aConnectedParticleList Connected particle list
     * @param aInnerParticleIndicesLength Inner particle indices length
     * @return Adjacent array
     */
    public int[][] getAdjacentArray(String aPartStructureString, LinkedList<int[]> aConnectedParticleList, int aInnerParticleIndicesLength) {
        // <editor-fold defaultstate="collapsed" desc="Local variables">
        int tmpEdgeLength = aConnectedParticleList.size();
        int tmpConnectionLength;
        int tmpFirstParticleIndex = 0;
        int tmpLastParticleIndex;
        Queue<Integer> tmpQueue = new ArrayDeque<>();
        
        // </editor-fold>
        if (aInnerParticleIndicesLength == 1 || aPartStructureString == null || aPartStructureString.isEmpty() || aConnectedParticleList.isEmpty()) {
            return null;
        }
        int[][] tmpConnectedParticleArray = aConnectedParticleList.toArray(new int[0][]);
        int[][] tmpResult = new int[aInnerParticleIndicesLength][];
        if (this.adjacentArrayHashMap.containsKey(aPartStructureString)) {
            return this.adjacentArrayHashMap.get(aPartStructureString);
        }
        for (int i = 0; i < tmpEdgeLength; i++) {
            if (tmpFirstParticleIndex == tmpConnectedParticleArray[i][0]) {
                tmpQueue.add(tmpConnectedParticleArray[i][1]);
            } else {
                tmpConnectionLength = tmpQueue.size();
                tmpResult[tmpFirstParticleIndex] = new int[tmpConnectionLength];
                tmpLastParticleIndex = 0;
                while (!tmpQueue.isEmpty()) {
                    tmpResult[tmpFirstParticleIndex][tmpLastParticleIndex] = tmpQueue.remove();
                    tmpLastParticleIndex++;
                }
                tmpFirstParticleIndex++;
                tmpQueue.add(tmpConnectedParticleArray[i][1]);
            }
        }
        if (!tmpQueue.isEmpty()) {
            tmpConnectionLength = tmpQueue.size();
            tmpResult[tmpFirstParticleIndex] = new int[tmpConnectionLength];
            tmpLastParticleIndex = 0;
            while (!tmpQueue.isEmpty()) {
                tmpResult[tmpFirstParticleIndex][tmpLastParticleIndex] = tmpQueue.remove();
                tmpLastParticleIndex++;
            }
        }
        this.adjacentArrayHashMap.put(aPartStructureString, tmpResult);
        return tmpResult;
    }
    /**
     * Tokenizes molecular structure. NOTE: Molecular structure string may only
     * consist of characters 0-9, a-z, A-Z, {}, (), [], -, #
     *
     * @param aMolecularStructureString Molecular structure string
     * @return Tokens or null
     */
    public String[] getStructureTokens(String aMolecularStructureString) {
        // <editor-fold defaultstate="collapsed" desc="Local variables">
        int tmpFrequencyOfSplitToken = 0;
        int tmpCurrentTokenNumber = 0;
        int tmpBeginIndexOfSubString = 0;
        int tmpEndIndexOfSubString = -1;
        String tmpCurrentToken = "";
        String tmpSeparatedTokens = "";
        String[] tmpResult = null;
        
        // </editor-fold>
        if (aMolecularStructureString == null && aMolecularStructureString.isEmpty()) {
            return new String[]{};
        }
        if (this.structureTokensHashMap.containsKey(aMolecularStructureString)) {
            return this.structureTokensHashMap.get(aMolecularStructureString);
        } else {
            tmpSeparatedTokens = this.prepareTokenizedStructure(aMolecularStructureString) + "_";
            tmpFrequencyOfSplitToken = this.getFrequencyOfCharacterInString(tmpSeparatedTokens, "_");
        tmpResult = new String[tmpFrequencyOfSplitToken];
        do {
            tmpEndIndexOfSubString = tmpSeparatedTokens.indexOf("_", tmpEndIndexOfSubString + 1);
            tmpCurrentToken = tmpSeparatedTokens.substring(tmpBeginIndexOfSubString, tmpEndIndexOfSubString);
            tmpBeginIndexOfSubString = tmpEndIndexOfSubString + 1;
            this.outerStructureTokensHashMap.putIfAbsent(tmpCurrentToken, tmpCurrentToken);
            tmpResult[tmpCurrentTokenNumber] = this.outerStructureTokensHashMap.get(tmpCurrentToken);
            tmpCurrentTokenNumber++;
        } while(tmpCurrentTokenNumber < tmpFrequencyOfSplitToken); 
        this.structureTokensHashMap.put(aMolecularStructureString, tmpResult);
        return tmpResult;
        }
    }

    /**
     * Counts the frequency of a character in a string
     *
     * @param aString A string
     * @param aCharacter A character to be counted
     * @return Frequency of a character in string
     */
    public int getFrequencyOfCharacterInString(String aString, String aCharacter) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aString == null || aString.length() == 0) {
            return 0;
        }

        // </editor-fold>
        int tmpIndex = -1;
        int tmpNumberOfCharacters = 0;
        while ((tmpIndex = aString.indexOf(aCharacter, ++tmpIndex)) > -1) {
            tmpNumberOfCharacters++;
        }
        return tmpNumberOfCharacters;
    }
           
    /**
     * Eliminate all Whitespaces in String
     * 
     * @param aMolecularStructureString Molecular structure string
     * @return String without whitespaces
     */
    public String eliminateWhiteSpaces(String aMolecularStructureString) {
        if (aMolecularStructureString == null) {
            return null;
        }
        String tmpResult= aMolecularStructureString;
        char[] tmpStructureCharArray = aMolecularStructureString.toCharArray();
        int tmpPosition = 0;
        for (int i = 0; i < tmpStructureCharArray.length; i++) {
            if (!Character.isWhitespace(tmpStructureCharArray[i])) {
                tmpStructureCharArray[tmpPosition++] = tmpStructureCharArray[i];
            }
        }
        tmpResult = new String(tmpStructureCharArray, 0, tmpPosition);    
        return tmpResult;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="- Miscellaneous methods">
    /**
     * Reverses an integer array
     *
     * @param anArray An integer array
     * @return Reversed integer array
     */
    public int[] reverseIntegerArray(int[] anArray) {
        int i = 0;
        int j = anArray.length - 1;
        int tmpHelp;
        while (i < j) {
            tmpHelp = anArray[i];
            anArray[i] = anArray[j];
            anArray[j] = tmpHelp;
            i++;
            j--;
        }
        return anArray;
    }

    // </editor-fold>
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private methods">
    /**
     * Prepares tokenized molecular structure. NOTE: Molecular structure may
     * only consist of characters 0-9, a-z, A-Z, {}, (), [], -, #
     *
     * @param aMolecularStructureString Molecular structure string
     * @return Prepared tokenized structure or null
     */
    private String prepareTokenizedStructure(String aMolecularStructureString) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aMolecularStructureString == null || aMolecularStructureString.length() == 0) {
            return null;
        }
        if (!this.inputStructurePattern.matcher(aMolecularStructureString).matches()) {
            return null;
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Local variables">
        String tmpSeparatedTokens = "";
        int tmpPosition = 0;
        int tmpEstimatedLength = 3 * aMolecularStructureString.length();
        boolean tmpIsParticleCharacter = false;
        boolean tmpIsBetweenSquareBrackets = false;
        boolean tmpIsAfterFrequency = false;
        boolean tmpIsMonomerCharacter = false;
        boolean tmpIsBackboneCharacter = false;
        char[] tmpTokensCharArray = aMolecularStructureString.toCharArray();
        char[] tmpResultArray = new char[tmpEstimatedLength];
        // </editor-fold>
        // Remove all spaces
        // Set frequency 1 to all particles without a frequency attribute
        for (int i = 0; i < tmpTokensCharArray.length; i++) {
            if (tmpTokensCharArray[i] == '[') {
                tmpIsParticleCharacter = false;
                tmpIsBetweenSquareBrackets = true;
                tmpResultArray[tmpPosition++] = tmpTokensCharArray[i];
            } else if (tmpTokensCharArray[i] == ']') {
                tmpIsBetweenSquareBrackets = false;
                tmpResultArray[tmpPosition++] = tmpTokensCharArray[i];
            } else if (tmpTokensCharArray[i] == '#') {
                tmpIsMonomerCharacter = true;
                tmpResultArray[tmpPosition++] = tmpTokensCharArray[i];
            } else if (tmpTokensCharArray[i] == '\'') {
                if (tmpIsBackboneCharacter) {
                    tmpIsBackboneCharacter = false;
                    tmpResultArray[tmpPosition++] = tmpTokensCharArray[i];
                    tmpResultArray[tmpPosition++] = '_';
                } else {
                    tmpIsBackboneCharacter = true;
                    tmpResultArray[tmpPosition++] = '_';
                    tmpResultArray[tmpPosition++] = tmpTokensCharArray[i];
                }
            } else if (tmpIsBetweenSquareBrackets || tmpIsBackboneCharacter) {
                tmpResultArray[tmpPosition++] = tmpTokensCharArray[i];
            } else if (!tmpIsParticleCharacter && Character.isDigit(tmpTokensCharArray[i])) {
                tmpIsAfterFrequency = true;
                tmpResultArray[tmpPosition++] = tmpTokensCharArray[i];
            } else if (!tmpIsMonomerCharacter && !tmpIsParticleCharacter && Character.isUpperCase(tmpTokensCharArray[i])) {
                if (!tmpIsAfterFrequency) {
                    tmpResultArray[tmpPosition++] = '1';
                }
                tmpResultArray[tmpPosition++] = '_';
                tmpResultArray[tmpPosition++] = tmpTokensCharArray[i];
                tmpIsParticleCharacter = true;
                tmpIsAfterFrequency = false;
                tmpIsMonomerCharacter = false;
            } else if ((tmpIsParticleCharacter || tmpIsMonomerCharacter) && Character.isLetterOrDigit(tmpTokensCharArray[i])) {
                tmpIsAfterFrequency = false;
                tmpResultArray[tmpPosition++] = tmpTokensCharArray[i];
            } else {
                tmpIsParticleCharacter = false;
                tmpIsAfterFrequency = false;
                tmpIsMonomerCharacter = false;
                tmpResultArray[tmpPosition++] = tmpTokensCharArray[i];
            }
        }
        tmpSeparatedTokens = new String(tmpResultArray, 0, tmpPosition);
        tmpTokensCharArray = tmpSeparatedTokens.toCharArray();
        tmpPosition = 0;
        int tmpNumberOfNewCharacters = tmpTokensCharArray.length;
        tmpNumberOfNewCharacters += 2 * this.getFrequencyOfCharacterInString(tmpSeparatedTokens, "{");
        tmpNumberOfNewCharacters += 2 * this.getFrequencyOfCharacterInString(tmpSeparatedTokens, "}");
        tmpNumberOfNewCharacters += 2 * this.getFrequencyOfCharacterInString(tmpSeparatedTokens, "(");
        tmpNumberOfNewCharacters += 2 * this.getFrequencyOfCharacterInString(tmpSeparatedTokens, ")");
        tmpNumberOfNewCharacters += this.getFrequencyOfCharacterInString(tmpSeparatedTokens, "[");
        tmpNumberOfNewCharacters += this.getFrequencyOfCharacterInString(tmpSeparatedTokens, "]");
        tmpNumberOfNewCharacters += 2 * this.getFrequencyOfCharacterInString(tmpSeparatedTokens, "<");
        tmpNumberOfNewCharacters += 2 * this.getFrequencyOfCharacterInString(tmpSeparatedTokens, ">");
        tmpNumberOfNewCharacters += 2 * this.getFrequencyOfCharacterInString(tmpSeparatedTokens, "-");
        tmpNumberOfNewCharacters += this.getFrequencyOfCharacterInString(tmpSeparatedTokens, "#");
        char[] tmpTokensResult = new char[tmpNumberOfNewCharacters];
        for (int i = 0; i < tmpTokensCharArray.length; i++) {
            switch (tmpTokensCharArray[i]) {
                case '{':
                    tmpTokensResult[tmpPosition++] = '_';
                    tmpTokensResult[tmpPosition++] = '{';
                    tmpTokensResult[tmpPosition++] = '_';
                    break;
                case '}':
                    tmpTokensResult[tmpPosition++] = '_';
                    tmpTokensResult[tmpPosition++] = '}';
                    tmpTokensResult[tmpPosition++] = '_';
                    break;
                case '(':
                    tmpTokensResult[tmpPosition++] = '_';
                    tmpTokensResult[tmpPosition++] = '(';
                    tmpTokensResult[tmpPosition++] = '_';
                    break;
                case ')':
                    tmpTokensResult[tmpPosition++] = '_';
                    tmpTokensResult[tmpPosition++] = ')';
                    tmpTokensResult[tmpPosition++] = '_';
                    break;
                case '[':
                    tmpTokensResult[tmpPosition++] = '_';
                    tmpTokensResult[tmpPosition++] = '[';
                    break;
                case ']':
                    tmpTokensResult[tmpPosition++] = ']';
                    tmpTokensResult[tmpPosition++] = '_';
                    break;
                case '<':
                    tmpTokensResult[tmpPosition++] = '_';
                    tmpTokensResult[tmpPosition++] = '<';
                    tmpTokensResult[tmpPosition++] = '_';
                    break;
                case '>':
                    tmpTokensResult[tmpPosition++] = '_';
                    tmpTokensResult[tmpPosition++] = '>';
                    tmpTokensResult[tmpPosition++] = '_';
                    break;
                case '-':
                    tmpTokensResult[tmpPosition++] = '_';
                    tmpTokensResult[tmpPosition++] = '-';
                    tmpTokensResult[tmpPosition++] = '_';
                    break;
                case '#':
                    tmpTokensResult[tmpPosition++] = '_';
                    tmpTokensResult[tmpPosition++] = '#';
                    break;
                default:
                    tmpTokensResult[tmpPosition++] = tmpTokensCharArray[i];
                    break;
            }
        }
        // Remove multiple separators
        tmpPosition = 0;
        boolean tmpHasUnderScore = false;
        int tmpFirstIndexOfCharArray = 0;
        int tmpLengthOfCharArray = 0;
        for (int i = 0; i < tmpTokensResult.length; i++) {
            if (tmpTokensResult[i] == '_') {
                if (!tmpHasUnderScore) {
                    tmpHasUnderScore = true;
                    tmpTokensResult[tmpPosition++] = tmpTokensResult[i];
                }
            } else {
                if (tmpHasUnderScore) {
                    tmpHasUnderScore = false;
                }
                tmpTokensResult[tmpPosition++] = tmpTokensResult[i];
            }
        }
        tmpLengthOfCharArray = tmpPosition;
        if (tmpTokensResult[0] == '_') {
            tmpFirstIndexOfCharArray = 1;
            tmpLengthOfCharArray--;
        }
        if (tmpTokensResult[tmpFirstIndexOfCharArray + tmpLengthOfCharArray - 1] == '_') {
            tmpLengthOfCharArray--;
        }
        tmpSeparatedTokens = new String(tmpTokensResult, tmpFirstIndexOfCharArray, tmpLengthOfCharArray);
        // Return tokens
        return tmpSeparatedTokens;
    }
    // </editor-fold>

}
