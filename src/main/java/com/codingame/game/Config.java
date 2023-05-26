package com.codingame.game;

import java.util.Properties;

public class Config {

    /**
     * Map generation
     */
    public static final int MAP_MIN_WIDTH = 12;
    public static final double MAP_ASPECT_RATIO = 1 / 2d;
    public static final int MAP_RING_COUNT_MIN = 4;
    public static int MAP_RING_COUNT_MAX = 7;
    public static final int MIN_EMPTY_CELLS_PERCENT = 10;
    public static final int MAX_EMPTY_CELLS_PERCENT = 20;
    public static final int MIN_EGG_CELLS_PERCENT = 10;
    public static final int MAX_EGG_CELLS_PERCENT = 28;
    public static final int MIN_FOOD_CELLS_PERCENT = 10;
    public static final int MAX_FOOD_CELLS_PERCENT = 30;
    public static final int STARTING_HILL_DISTANCE = 2;

    /**
     * Gameplay
     */
    public static final int MAX_ANT_LOSS = 3;
    public static int FRAMES_PER_TURN = 1;
    public static final int MAX_TURNS = 100 * FRAMES_PER_TURN;

    /***
     * Rules
     */
    public static boolean FIGHTING_ANTS_KILL = false;
    public static boolean LOSING_ANTS_CANT_MOVE = false;
    public static boolean LOSING_ANTS_CANT_CARRY = true;
    public static boolean FORCE_SINGLE_HILL = false;
    public static boolean ENABLE_EGGS = true;
    public static boolean SCORES_IN_IO = false;

    public static void takeFrom(Properties params) {
        FIGHTING_ANTS_KILL = getFromParams(params, "FIGHTING_ANTS_KILL", FIGHTING_ANTS_KILL);
        LOSING_ANTS_CANT_MOVE = getFromParams(params, "LOSING_ANTS_CANT_MOVE", LOSING_ANTS_CANT_MOVE);
        FRAMES_PER_TURN = getFromParams(params, "FRAMES_PER_TURN", FRAMES_PER_TURN);
        LOSING_ANTS_CANT_CARRY = getFromParams(params, "LOSING_ANTS_CANT_CARRY", LOSING_ANTS_CANT_CARRY);
    }

    public static void giveTo(Properties params) {
        params.put("LOSING_ANTS_CANT_MOVE", LOSING_ANTS_CANT_MOVE);
        params.put("FIGHTING_ANTS_KILL", FIGHTING_ANTS_KILL);
        params.put("FRAMES_PER_TURN", FRAMES_PER_TURN);
        params.put("LOSING_ANTS_CANT_CARRY", LOSING_ANTS_CANT_CARRY);
    }

    private static double getFromParams(Properties params, String name, double defaultValue) {
        String inputValue = params.getProperty(name);
        if (inputValue != null) {
            try {
                return Double.parseDouble(inputValue);
            } catch (NumberFormatException e) {
                // Do naught
            }
        }
        return defaultValue;
    }

    private static int getFromParams(Properties params, String name, int defaultValue) {
        String inputValue = params.getProperty(name);
        if (inputValue != null) {
            try {
                return Integer.parseInt(inputValue);
            } catch (NumberFormatException e) {
                // Do naught
            }
        }
        return defaultValue;
    }

    private static boolean getFromParams(Properties params, String name, boolean defaultValue) {
        String inputValue = params.getProperty(name);
        if (inputValue != null) {
            try {
                return new Boolean(inputValue);
            } catch (NumberFormatException e) {
                // Do naught
            }
        }
        return defaultValue;
    }

}
