package com.codingame.game;

public class Cell {
    public static final Cell NO_CELL = new Cell(-1, null) {
        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public int getIndex() {
            return -1;
        }
    };

    private CellType type;
    private int richness;
    private int index;
    private Player anthill;
    private int[] ants;
    private int[] beacons;
    private CubeCoord coord;

    public Cell(int index, CubeCoord coord) {
        this.index = index;
        this.richness = 0;
        this.ants = new int[2];
        this.beacons = new int[2];
        this.coord = coord;
        this.type = CellType.EMPTY;
    }

    public int getIndex() {
        return index;
    }

    public boolean isValid() {
        return true;
    }

    public void setFoodAmount(int richness) {
        this.richness = richness;
        this.type = CellType.FOOD;
    }

    public int getRichness() {
        return richness;
    }

    public int getSpawnPower() {
        return richness;
    }

    public void setSpawnPower(int richness) {
        this.richness = richness;
        this.type = CellType.EGG;
    }

    public Player getAnthill() {
        return anthill;
    }

    public CubeCoord getCoord() {
        return coord;
    }

    public void setAnthill(Player anthill) {
        this.anthill = anthill;
    }

    public void placeAnts(Player player, int amount) {
        placeAnts(player.getIndex(), amount);
    }
    public void placeAnts(int playerIdx, int amount) {
        ants[playerIdx] += amount;
    }

    public void removeAnts(Player player, int amount) {
        removeAnts(player.getIndex(), amount);
    }

    public int getAnts(Player player) {
        return getAnts(player.getIndex());
    }

    public int getAnts(int playerIdx) {
        return ants[playerIdx];
    }

    public void setBeaconPower(int playerIdx, int power) {
        beacons[playerIdx] = power;
    }

    public int getBeaconPower(int playerIdx) {
        return beacons[playerIdx];
    }

    public int getBeaconPower(Player player) {
        return beacons[player.getIndex()];
    }

    public void removeAnts(int playerIdx, int amount) {
        ants[playerIdx] -= Math.min(amount, ants[playerIdx]);
    }

    public void deplete(int amount) {
        this.richness -= Math.min(amount, richness);
    }

    public CellType getType() {
        return type;
    }

    public void removeBeacons() {
        beacons[0] = 0;
        beacons[1] = 0;
    }

    public int[] getAnts() {
        return ants;
    }
}
