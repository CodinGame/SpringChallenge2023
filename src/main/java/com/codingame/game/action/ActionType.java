package com.codingame.game.action;

import java.util.regex.Pattern;

public enum ActionType {

    BEACON(
        "^BEACON (?<index>\\d+) (?<power>\\d+)"
    ),
    LINE(
        "^LINE (?<from>\\d+) (?<to>\\d+) (?<ants>\\d+)"
    ),
    MESSAGE(
        "^MESSAGE (?<message>.*)"
    ),
    WAIT(
        "^WAIT"
    );

    private Pattern pattern;

    ActionType(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public Pattern getPattern() {
        return pattern;
    }

}
