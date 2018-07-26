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

/**
 * Particle frequency
 *
 * @author Achim Zielesny
 */
public class ParticleFrequency implements Comparable<ParticleFrequency> {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * Particle
     */
    private String particle;
    
    /**
     * Frequency
     */
    private int frequency;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor
     *
     * @param aParticle Particle (not allowed to be null/empty)
     * @param aFrequency Frequency of aParticle (must be greater 0)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public ParticleFrequency(String aParticle, int aFrequency) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticle == null || aParticle.isEmpty() || aFrequency < 1) {
            throw new IllegalArgumentException("An argument is illegal.");
        }
        // </editor-fold>
        this.particle = aParticle;
        this.frequency = aFrequency;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Adds aFrequency to current frequency of particle
     *
     * @param aFrequency Frequency to be added
     */
    public void addFrequency(int aFrequency) {
        this.frequency += aFrequency;
    }

    /**
     * Standard compareTo
     *
     * @param aParticleFrequency ParticleFrequency instance to compare
     * @return Standard compareTo result
     */
    @Override
    public int compareTo(ParticleFrequency aParticleFrequency) {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aParticleFrequency == null) {
            throw new IllegalArgumentException("aParticleFrequency is null.");
        }
        // </editor-fold>
        return this.particle.compareTo(aParticleFrequency.getParticle());
    }

    /**
     * Increments frequency of particle
     */
    public void incrementFrequency() {
        this.frequency++;
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties">
    // <editor-fold defaultstate="collapsed" desc="- Particle (get)">
    /**
     * Particle
     *
     * @return Particle
     */
    public String getParticle() {
        return this.particle;
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="- Frequency (get/set)">
    /**
     * Frequency of particle
     *
     * @return Frequency of particle
     */
    public int getFrequency() {
        return this.frequency;
    }

    /**
     * Frequency of particle
     *
     * @param aFrequency Frequency of particle (must be greater 0)
     * @throws IllegalArgumentException Thrown if an argument is illegal
     */
    public void setFrequency(int aFrequency) throws IllegalArgumentException {
        // <editor-fold defaultstate="collapsed" desc="Checks">
        if (aFrequency < 1) {
            throw new IllegalArgumentException("aFrequency < 1.");
        }
        // </editor-fold>
        this.frequency = aFrequency;
    }
    // </editor-fold>
    // </editor-fold>

}
