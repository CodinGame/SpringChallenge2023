package com.codingame.game.action.actions;

import com.codingame.game.action.Action;
import com.codingame.game.action.ActionType;

public class MessageAction extends Action {

    private String message;

    public MessageAction(String message) {
        super(ActionType.MESSAGE);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
