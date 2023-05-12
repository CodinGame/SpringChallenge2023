package com.codingame.event;

public class AnimationData {
    public int start, end;

    public AnimationData(int start, int duration) {
        this.start = start;
        this.end = start + duration;
    }
}
