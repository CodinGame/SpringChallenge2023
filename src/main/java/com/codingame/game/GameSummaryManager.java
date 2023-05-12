package com.codingame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Singleton;

@Singleton
public class GameSummaryManager {
    private List<String> lines;
    private Map<String, List<String>> playersErrors;
    private Map<Integer, List<AntConsumption>> mealMap;
    private Map<Integer, List<AntConsumption>> eggMap;

    public GameSummaryManager() {
        this.lines = new ArrayList<>();
        this.playersErrors = new HashMap<>();
        this.mealMap = new HashMap<>();
        this.mealMap.put(0, new ArrayList<>());
        this.mealMap.put(1, new ArrayList<>());

        this.eggMap = new HashMap<>();
        this.eggMap.put(0, new ArrayList<>());
        this.eggMap.put(1, new ArrayList<>());
    }

    public String getSummary() {
        return toString();
    }

    public void clear() {
        this.lines.clear();
        this.playersErrors.clear();
        this.mealMap.forEach((pIdx, list) -> list.clear());
        this.eggMap.forEach((pIdx, list) -> list.clear());
    }

    @Override
    public String toString() {
        return formatErrors() + formatLines();
    }

    public void addError(Player player, String error) {
        String key = player.getNicknameToken();
        if (!playersErrors.containsKey(key)) {
            playersErrors.put(key, new ArrayList<String>());
        }
        playersErrors.get(key).add(error);
    }

    private String formatLines() {
        ArrayList<String> harvestLines = new ArrayList<String>();

        for (int pIdx = 0; pIdx < 2; ++pIdx) {
            List<AntConsumption> meals = this.eggMap.get(pIdx);
            Collections.sort(meals, Comparator.comparing(meal -> meal.cell.getIndex()));

            if (meals.size() == 1) {
                AntConsumption meal = meals.get(0);
                harvestLines.add(
                    String.format(
                        "%s has harvested %d egg from cell %d, spawning that many ants on %s %s.\n",
                        meal.player.getNicknameToken(), meal.amount, meal.cell.getIndex(),
                        meal.player.anthills.size() == 1 ? "cell" : "cells",
                        formatCellList(meal.player.anthills)
                    )
                );
            } else if (meals.size() > 1) {
                Player player = meals.get(0).player;
                harvestLines.add(
                    String.format("%s has harvested:", player.getNicknameToken())
                );
                int total = 0;
                for (AntConsumption meal : meals) {
                    harvestLines.add(
                        String.format(
                            "- %d egg from cell %d",
                            meal.amount, meal.cell.getIndex()
                        )
                    );
                    total += meal.amount;
                }
                harvestLines.add(
                    String.format(
                        "Spawning a total of %d ants on %s %s.\n", total,
                        player.anthills.size() == 1 ? "cell" : "cells",
                        formatCellList(player.anthills)
                    )
                );
            }

            meals = this.mealMap.get(pIdx);
            Collections.sort(meals, Comparator.comparing(meal -> meal.cell.getIndex()));
            if (meals.size() == 1) {
                AntConsumption meal = meals.get(0);
                harvestLines.add(
                    String.format(
                        "%s has harvested %d crystal from cell %d, scoring that many points.\n",
                        meal.player.getNicknameToken(), meal.amount, meal.cell.getIndex()
                    )
                );
            } else if (meals.size() > 1) {
                Player player = meals.get(0).player;
                harvestLines.add(
                    String.format("%s has harvested:", player.getNicknameToken())
                );
                int total = 0;
                for (AntConsumption meal : meals) {
                    harvestLines.add(
                        String.format(
                            "- %d crystal from cell %d",
                            meal.amount, meal.cell.getIndex()
                        )
                    );
                    total += meal.amount;
                }
                harvestLines.add(
                    String.format("Scoring a total of %d points.\n", total)
                );
            }

        }

        return Stream.concat(
            harvestLines.stream(),
            lines.stream()
        ).collect(Collectors.joining("\n"));
    }

    private String formatErrors() {
        if (playersErrors.isEmpty()) {
            return "";
        }

        return playersErrors.entrySet().stream().map(errorMap -> {
            List<String> errors = errorMap.getValue();
            String additionnalErrorsMessage = errors.size() > 1 ? " + " + (errors.size() - 1) + " other error" + (errors.size() > 2 ? "s" : "") : "";
            return errorMap.getKey() + ": " + errors.get(0) + additionnalErrorsMessage;
        }).collect(Collectors.joining("\n"))
            + "\n\n";
    }

    public void addNotEnoughFoodLeft(int remainingFood, Player player) {
        lines.add(
            player.getNicknameToken() + " has harvested more than half the crystals. Game over!"
        );
    }

    public void addNoMoreFood() {
        lines.add(
            "All the crystals have been harvested. Game over!"
        );
    }

    public void addBuild(AntConsumption meal) {
        eggMap.get(meal.player.getIndex()).add(meal);

    }

    public void addMeal(AntConsumption meal) {
        mealMap.get(meal.player.getIndex()).add(meal);

    }

    private String formatCellList(List<Integer> list) {
        if (list.size() == 1) {
            return list.get(0).toString();
        }
        return list.stream().map(String::valueOf).collect(Collectors.joining(" & "));
    }

}