package com.codingame.game;

import java.util.ArrayList;
import java.util.List;

import com.codingame.view.Serializer;

public class CubeCoord {

    static int[][] directions = new int[][] { { 1, -1, 0 }, { +1, 0, -1 }, { 0, +1, -1 }, { -1, +1, 0 }, { -1, 0, +1 }, { 0, -1, +1 } };
    static CubeCoord CENTER = new CubeCoord(0, 0, 0);

    int x, y, z;

    public CubeCoord(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CubeCoord other = (CubeCoord) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        if (z != other.z)
            return false;
        return true;
    }

    CubeCoord neighbor(int orientation) {
        return neighbor(orientation, 1);
    }

    CubeCoord neighbor(int orientation, int distance) {
        int nx = this.x + directions[orientation][0] * distance;
        int ny = this.y + directions[orientation][1] * distance;
        int nz = this.z + directions[orientation][2] * distance;

        return new CubeCoord(nx, ny, nz);
    }

    /**
     * Warning: this return ALL neighbor coords, even those that aren't within the board limits.
     */
    List<CubeCoord> neighbours() {
        List<CubeCoord> neighborCoords = new ArrayList<>(CubeCoord.directions.length);
        for (int i = 0; i < CubeCoord.directions.length; ++i) {
            CubeCoord next = this.neighbor(i);
            neighborCoords.add(next);
        }
        return neighborCoords;
    }

    int distanceTo(CubeCoord dst) {
        return (Math.abs(x - dst.x) + Math.abs(y - dst.y) + Math.abs(z - dst.z)) / 2;
    }

    @Override
    public String toString() {
        return Serializer.join(x, y, z);
    }

    public CubeCoord getOpposite() {
        CubeCoord oppositeCoord = new CubeCoord(-this.x, -this.y, -this.z);
        return oppositeCoord;
    }
}
