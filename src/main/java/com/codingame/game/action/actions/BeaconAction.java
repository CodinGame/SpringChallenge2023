package com.codingame.game.action.actions;

import com.codingame.game.action.Action;
import com.codingame.game.action.ActionType;

public class BeaconAction extends Action {

    private int cellIndex;
    private int power;

    public BeaconAction(int cellIndex, int power) {
        super(ActionType.BEACON);
        this.cellIndex = cellIndex;
        this.power = power;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public void setCellIndex(int cellIndex) {
        this.cellIndex = cellIndex;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

}
