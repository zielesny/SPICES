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
 * Interface for point in space. A point in space is defined by its x, y and z coordinate.
 *
 * @author Achim Zielesny
 */
public interface IPointInSpace {

    // <editor-fold defaultstate="collapsed" desc="Public methods">
    /**
     * Returns if aPoint is equal to this point
     *
     * @param aPoint Point
     * @return True: aPoint is equal to this point, false: Otherwise
     */
    public boolean isEqual(IPointInSpace aPoint);

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public properties (get/set)">
    /**
     * Gets x coordinate of point in space
     *
     * @return X coordinate of point in space
     */
    public double getX();

    /**
     * Sets x coordinate of point in space
     *
     * @param aXCoordinate X coordinate of the point in space
     */
    public void setX(double aXCoordinate);

    /**
     * Gets y coordinate of point in space
     *
     * @return Y coordinate of point in space
     */
    public double getY();

    /**
     * Sets y coordinate of point in space
     *
     * @param aYCoordinate Y coordinate of the point in space
     */
    public void setY(double aYCoordinate);

    /**
     * Gets z coordinate of point in space
     *
     * @return Z coordinate of point in space
     */
    public double getZ();

    /**
     * Sets z coordinate of point in space
     *
     * @param aZCoordinate Z coordinate of the point in space
     */
    public void setZ(double aZCoordinate);
    // </editor-fold>

}
