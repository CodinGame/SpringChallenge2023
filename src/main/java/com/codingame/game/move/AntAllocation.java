package com.codingame.game.move;

public class AntAllocation {

    private int antIndex, beaconIndex, amount;

    public AntAllocation(int antIndex, int beaconIndex, int amount) {
        this.antIndex = antIndex;
        this.beaconIndex = beaconIndex;
        this.amount = amount;
    }

    public int getAntIndex() {
        return antIndex;
    }

    public int getBeaconIndex() {
        return beaconIndex;
    }

    public int getAmount() {
        return amount;
    }

}
