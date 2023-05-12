package com.codingame.event;

import java.util.ArrayList;
import java.util.List;

public class EventData {
    public static final int BUILD = 0;
    public static final int MOVE = 1;
    public static final int FOOD = 2;
    public static final int BEACON = 3;

    public int type;
    public List<AnimationData> animData;
    public Integer playerIdx;
    public Integer cellIdx, targetIdx;
    public Integer amount;
    public List<Integer> path;

    public EventData() {
        animData = new ArrayList<>();
    }

}
