package com.codingame.game.action;

public abstract class Action {

    private final ActionType type;

    protected Action(ActionType type) {
        this.type = type;
    }

    public ActionType getType() {
        return type;
    }

}