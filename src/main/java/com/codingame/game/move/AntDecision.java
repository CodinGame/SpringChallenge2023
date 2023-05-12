package com.codingame.game.move;

import com.codingame.game.CubeCoord;

public class AntDecision {

    CubeCoord coord;
    int dispatch;
    double weight, dispatchTarget;

    public AntDecision(CubeCoord coord) {
        this.coord = coord;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setDispatchTarget(double dispatchTarget) {
        this.dispatchTarget = dispatchTarget;
    }

    public void setDispatch(int dispatch) {
        this.dispatch = dispatch;
    }

    public double getRemainder() {
        return dispatchTarget - dispatch;
    }

    public CubeCoord getCoord() {
        return coord;
    }

    public double getWeight() {
        return weight;
    }

    public double getDispatchTarget() {
        return dispatchTarget;
    }
    public int getDispatch() {
        return dispatch;
    }

}
