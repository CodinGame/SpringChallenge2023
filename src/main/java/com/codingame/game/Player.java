package com.codingame.game;

import java.util.ArrayList;
import java.util.List;

import com.codingame.game.action.Action;
import com.codingame.game.action.actions.BeaconAction;
import com.codingame.game.action.actions.LineAction;
import com.codingame.game.action.actions.MessageAction;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

public class Player extends AbstractMultiplayerPlayer {

    String message;
    List<Integer> anthills;
    List<BeaconAction> beacons;
    List<LineAction> lines;
    int points;

    public Player() {
        points = 0;
        anthills = new ArrayList<>();
        beacons = new ArrayList<>();
        lines = new ArrayList<>();
    }

    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (message != null) {
            String trimmed = message.trim();
            if (trimmed.length() > 48) {
                trimmed = trimmed.substring(0, 46) + "...";
            }
            if (trimmed.length() > 0) {
                this.message = trimmed;
            }
        }
    }

    public void reset() {
        message = null;
        beacons.clear();
        lines.clear();
    }

    public void addAction(Action action) {
        switch (action.getType()) {
        case BEACON:
            beacons.add((BeaconAction) action);
            break;
        case LINE:
            lines.add((LineAction) action);
            break;
        case MESSAGE:
            setMessage(((MessageAction) action).getMessage());
            break;
        default:
            break;
        }
    }

    public void addAnthill(int index) {
        anthills.add(index);
    }

    public List<Integer> getAnthills() {
        return anthills;
    }

    public void addPoints(int n) {
        points += n;
    }

    public int getPoints() {
        return points;
    }

}
