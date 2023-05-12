package com.codingame.game.action.actions;

import com.codingame.game.action.Action;
import com.codingame.game.action.ActionType;

public class LineAction extends Action {

    private int from;
    private int to;
    private int ants;

    public LineAction(int from, int to, int ants) {
        super(ActionType.LINE);
        this.from = from;
        this.to = to;
        this.ants = ants;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getAnts() {
        return ants;
    }

    public void setAnts(int ants) {
        this.ants = ants;
    }

}
