package com.codingame.event;

import java.util.List;

import com.google.inject.Singleton;

@Singleton
public class Animation {
    public static final int HUNDREDTH = 10;
    public static final int TWENTIETH = 50;
    public static final int TENTH = 100;
    public static final int THIRD = 300;
    public static final int HALF = 500;
    public static final int WHOLE = 1000;

    private int frameTime;
    private int endTime;

    public void reset() {
        frameTime = 0;
        endTime = 0;
    }

    public int wait(int time) {
        return frameTime += time;
    }

    public int getFrameTime() {
        return frameTime;
    }

    public void startAnim(List<AnimationData> animData, int duration) {
        animData.add(new AnimationData(frameTime, duration));
        endTime = Math.max(endTime, frameTime + duration);
    }

    public void waitForAnim(List<AnimationData> animData, int duration) {
        animData.add(new AnimationData(frameTime, duration));
        frameTime += duration;
        endTime = Math.max(endTime, frameTime);
    }

    public void chainAnims(int count, List<AnimationData> animData, int duration, int separation) {
        chainAnims(count, animData, duration, separation, true);
    }

    public void chainAnims(int count, List<AnimationData> animData, int duration, int separation, boolean waitForEnd) {
        for (int i = 0; i < count; ++i) {
            animData.add(new AnimationData(frameTime, duration));

            if (i < count - 1) {
                frameTime += separation;
            }
        }
        endTime = Math.max(endTime, frameTime + duration);
        if (waitForEnd && count > 0) {
            frameTime += duration;
        }
    }

    public void setFrameTime(int startTime) {
        this.frameTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void catchUp() {
        frameTime = endTime;
    }
    
    public int computeEvents() {
        int minTime = 1000;

        catchUp();

        int frameTime = Math.max(
            getFrameTime(),
            minTime
        );
        return frameTime;
    }
}
