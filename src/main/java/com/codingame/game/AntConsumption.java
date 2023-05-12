package com.codingame.game;

import java.util.LinkedList;

public class AntConsumption {
    Player player;
    int amount;
    Cell cell;
    LinkedList<Cell> path;

    public AntConsumption(Player player, int amount, Cell cell, LinkedList<Cell> path) {
        this.player = player;
        this.amount = amount;
        this.cell = cell;
        this.path = path;
    }

}
