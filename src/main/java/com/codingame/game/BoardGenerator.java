package com.codingame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardGenerator {

    static Random random;

    public static Board generate(Random random, List<Player> players) {
        BoardGenerator.random = random;

        Board board = createEmptyBoard(players);

        addResourceCells(board, players);

        return board;
    }

    private static Board createEmptyBoard(List<Player> players) {
        Board board;
        int iterations = 1000;
        do {
            board = generatePotentiallyUnconnectedGraph(players);
            iterations--;
        } while (!board.isConnected() && iterations > 0);
        return board;
    }

    private static double VERTICAL_CUTOFF = 0.6;

    public static Board generatePotentiallyUnconnectedGraph(List<Player> players) {
        Map<CubeCoord, Cell> cells = new HashMap<>();
        int nextCellIndex = 0;

        int ringCount = random.nextInt(Config.MAP_RING_COUNT_MAX - Config.MAP_RING_COUNT_MIN + 1) + Config.MAP_RING_COUNT_MIN;

        // Generate all coords as a hexagon
        List<CubeCoord> coordList = new ArrayList<>();

        CubeCoord center = CubeCoord.CENTER;
        coordList.add(center);
        CubeCoord cur = center.neighbor(0);
        int verticalLimit = (int) Math.ceil(ringCount * VERTICAL_CUTOFF);
        for (int distance = 1; distance <= ringCount; distance++) {
            for (int orientation = 0; orientation < 6; orientation++) {
                for (int count = 0; count < distance; count++) {
                    if (cur.z > -verticalLimit && cur.z < verticalLimit) {
                        if (!coordList.contains(cur)) {
                            coordList.add(cur);
                            coordList.add(cur.getOpposite());
                        }
                    }
                    cur = cur.neighbor((orientation + 2) % 6);
                }
            }
            cur = cur.neighbor(0);
        }

        // Create holes
        int coordListSize = coordList.size();
        int wantedEmptyCells = randomPercentage(Config.MIN_EMPTY_CELLS_PERCENT, Config.MAX_EMPTY_CELLS_PERCENT, coordList.size());

        Set<CubeCoord> toRemove = new HashSet<>();
        while (toRemove.size() < wantedEmptyCells) {
            int randIndex = random.nextInt(coordListSize);
            CubeCoord randCoord = coordList.get(randIndex);
            toRemove.add(randCoord);
            toRemove.add(randCoord.getOpposite());

            // TODO: check it's still connected and remove it directly
        }
        coordList.removeAll(toRemove);

        boolean CORRIDOR_MODE = random.nextDouble() < 0.05; // 5% chance
        if (CORRIDOR_MODE) {
            toRemove.clear();
            for (CubeCoord coord : coordList) {
                if (hasSixNeighbours(coord, coordList)) {
                    toRemove.add(coord);
                }
            }
            coordList.removeAll(toRemove);
        }

        boolean NO_BLOB_MODE = random.nextDouble() < 0.70; // 70% chance
        if (NO_BLOB_MODE) {
            boolean changed = true;
            while (changed) {
                changed = false;
                Optional<CubeCoord> blobCenter = coordList.stream()
                    .filter(c -> hasSixNeighbours(c, coordList))
                    .findAny();
                if (blobCenter.isPresent()) {
                    List<CubeCoord> neighbours = blobCenter.get().neighbours();
                    Collections.shuffle(neighbours, random);

                    coordList.remove(neighbours.get(0));
                    coordList.remove(neighbours.get(0).getOpposite());
                    changed = true;
                } else {
                    changed = false;
                }
            }
        }

        // Create empty Cells
        for (CubeCoord coord : coordList) {
            Cell cell = new Cell(nextCellIndex++, coord);
            cells.put(coord, cell);
        }

        return new Board(cells, ringCount, players);
    }

    private static int randomPercentage(int min, int max, int total) {
        int percentage = min + random.nextInt((max + 1) - min);
        return (int) (percentage * total / 100.0);
    }

    private static boolean hasSixNeighbours(CubeCoord coord, List<CubeCoord> coordList) {
        return coord.neighbours()
            .stream()
            .filter(c -> coordList.contains(c))
            .count() == 6;
    }

    private static void addResourceCells(Board board, List<Player> players) {
        // Fill center cell
        if (random.nextBoolean()) {
            Cell centerCell = board.get(CubeCoord.CENTER);
            if (centerCell != Cell.NO_CELL) {
                centerCell.setFoodAmount(getLargeFoodAmount());
            }
        }

        // Place anthills
        int hillsPerPlayer = Config.FORCE_SINGLE_HILL ? 1 : (random.nextDouble() < .33 ? 2 : 1);

        List<CubeCoord> validCoords = selectAnthillCoords(board, hillsPerPlayer);

        Player player1 = players.get(0);
        Player player2 = players.get(1);
        for (int i = 0; i < Math.min(hillsPerPlayer, validCoords.size()); i++) {
            CubeCoord coord = validCoords.get(i);

            Cell cell1 = board.map.get(coord);
            cell1.setAnthill(player1);
            player1.addAnthill(cell1.getIndex());

            Cell cell2 = board.map.get(coord.getOpposite());
            cell2.setAnthill(player2);
            player2.addAnthill(cell2.getIndex());
        }

        // Place food
        boolean SURPLUS_MODE = random.nextDouble() < 0.1; // 10% chance
        boolean HUNGRY_MODE = !SURPLUS_MODE && random.nextDouble() < 0.08; // 8% chance
        boolean FAMINE_MODE = !SURPLUS_MODE && !HUNGRY_MODE && random.nextDouble() < 0.04; // 4% chance

        if (!FAMINE_MODE) {
            List<CubeCoord> validFoodCoords = board.coords.stream()
                .filter(coord -> board.get(coord).getAnthill() == null)
                .collect(Collectors.toList());
            int wantedFoodCells = randomPercentage(Config.MIN_FOOD_CELLS_PERCENT, Config.MAX_FOOD_CELLS_PERCENT, validFoodCoords.size());
            wantedFoodCells = Math.max(2, wantedFoodCells);

            Collections.shuffle(validFoodCoords, random);
            for (int i = 0; i < wantedFoodCells; i += 2) {
                CubeCoord coord = validFoodCoords.get(i);
                Cell cell = board.get(coord);
                double roll = random.nextDouble();
                if (roll < 0.65) {
                    int amount = HUNGRY_MODE ? getSmallFoodAmount() : getLargeFoodAmount();
                    if (SURPLUS_MODE) {
                        amount *= 10;
                    }
                    cell.setFoodAmount(amount);
                    board.get(coord.getOpposite()).setFoodAmount(amount);
                } else {
                    int amount = HUNGRY_MODE ? getSmallFoodAmount() : getSmallFoodAmount() / 2;
                    if (SURPLUS_MODE) {
                        amount *= 10;
                    }
                    cell.setFoodAmount(amount);
                    board.get(coord.getOpposite()).setFoodAmount(amount);
                }
            }

        }
        // Make sure there is food on the board
        boolean boardHasFood = board.cells.stream().anyMatch(cell -> cell.getType() == CellType.FOOD);
        if (!boardHasFood) {
            List<Cell> emptyCells = board.cells.stream()
                .filter(cell -> cell.getType() == CellType.EMPTY && cell.isValid() && cell.getAnthill() == null)
                .collect(Collectors.toList());
            int randomIndex = random.nextInt(emptyCells.size());
            Cell emptyCell = emptyCells.get(randomIndex);
            int amount = getLargeFoodAmount();
            if (SURPLUS_MODE) {
                amount *= 10;
            }
            emptyCell.setFoodAmount(amount);
            Cell oppositeCell = board.get(emptyCell.getCoord().getOpposite());
            if (!oppositeCell.getCoord().equals(emptyCell.getCoord())) {
                oppositeCell.setFoodAmount(amount);
            }
        }

        int antPotential = 0;
        if (Config.ENABLE_EGGS) {
            // Place egg
            List<CubeCoord> validEggCoords = board.coords.stream()
                .filter(
                    coord -> board.get(coord).getAnthill() == null && board.get(coord).getRichness() == 0
                )
                .collect(Collectors.toList());
            int wantedEggCells = randomPercentage(Config.MIN_EGG_CELLS_PERCENT, Config.MAX_EGG_CELLS_PERCENT, validEggCoords.size());

            Collections.shuffle(validEggCoords, random);
            for (int i = 0; i < wantedEggCells; i += 2) {
                CubeCoord coord = validEggCoords.get(i);
                Cell cell = board.get(coord);
                double roll = random.nextDouble();
                if (roll < 0.4) {
                    int amount = getLargeEggsAmount();
                    cell.setSpawnPower(amount);
                    board.get(coord.getOpposite()).setSpawnPower(amount);
                    antPotential += amount * 2;
                } else {
                    int amount = getSmallEggsAmount();
                    cell.setSpawnPower(amount);
                    board.get(coord.getOpposite()).setSpawnPower(amount);
                    antPotential += amount * 2;
                }
            }
        }

        // Place ants;
        int antsPerHill = Math.max(10, 60 - antPotential);
        players.forEach(player -> {
            player.anthills.forEach(idx -> {
                board.get(idx).placeAnts(player, antsPerHill);
            });
        });

    }

    private static int getSmallEggsAmount() {
        return (10 + random.nextInt(10));
    }

    private static int getLargeEggsAmount() {
        return (20 + random.nextInt(20));
    }

    private static int getSmallFoodAmount() {
        return (10 + random.nextInt(30));
    }

    private static int getLargeFoodAmount() {
        return (40 + random.nextInt(20));
    }

    private static List<CubeCoord> selectAnthillCoords(Board board, int hillsPerPlayer) {
        List<CubeCoord> validCoords = new ArrayList<>();

        int iter = 1000;
        while (validCoords.size() < hillsPerPlayer && iter > 0) {
            iter--;
            validCoords = trySelectAnthillCoords(board, hillsPerPlayer);
        }
        // Failsafes
        if (validCoords.size() < hillsPerPlayer) {
            validCoords = board.getEdges();
        }
        if (validCoords.size() < hillsPerPlayer) {
            validCoords = board.coords;
        }
        return validCoords;
    }

    /**
     * Select hill positions for player 1 on board edges without food
     */
    private static List<CubeCoord> trySelectAnthillCoords(Board board, int startingHillCount) {
        List<CubeCoord> coordinates = new ArrayList<>();

        List<CubeCoord> availableCoords = new ArrayList<>(board.coords);
        availableCoords.removeIf(coord -> board.get(coord).getIndex() == 0);
        availableCoords.removeIf(coord -> board.get(coord).getRichness() > 0);
        for (int i = 0; i < startingHillCount && !availableCoords.isEmpty(); i++) {
            int r = random.nextInt(availableCoords.size());
            CubeCoord normalCoord = availableCoords.get(r);
            CubeCoord oppositeCoord = normalCoord.getOpposite();
            availableCoords.removeIf(coord -> {
                return coord.distanceTo(normalCoord) <= Config.STARTING_HILL_DISTANCE ||
                    coord.distanceTo(oppositeCoord) <= Config.STARTING_HILL_DISTANCE;
            });
            coordinates.add(normalCoord);
        }
        return coordinates;
    }

}