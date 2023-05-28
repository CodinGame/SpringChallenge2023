package com.codingame.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import com.codingame.event.Animation;
import com.codingame.event.EventData;
import com.codingame.game.action.actions.BeaconAction;
import com.codingame.game.action.actions.LineAction;
import com.codingame.game.move.AntAllocater;
import com.codingame.game.move.AntAllocation;
import com.codingame.game.move.AntMove;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.codingame.view.Serializer;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Game {
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private EndScreenModule endScreenModule;
    @Inject private Animation animation;
    @Inject private GameSummaryManager gameSummaryManager;

    int STARTING_HILL_DISTANCE = 2;

    private Random random;

    List<Player> players;

    private List<EventData> viewerEvents;

    private int gameTurn;
    private Board board;
    private boolean moveAnimatedThisTurn;

    public void init() {
        players = gameManager.getPlayers();
        random = gameManager.getRandom();
        viewerEvents = new ArrayList<>();
        gameTurn = 0;
        board = BoardGenerator.generate(random, players);
    }

    public boolean isKeyFrame() {
        return gameTurn % Config.FRAMES_PER_TURN == 0;
    }

    public void resetGameTurnData() {
        if (isKeyFrame()) {
            board.cells.forEach(cell -> cell.removeBeacons());
        }
        viewerEvents.clear();
        animation.reset();
        players.stream().forEach(Player::reset);
        moveAnimatedThisTurn = false;
        board.resetAttackCache();
    }

    public List<String> getGlobalInfoFor(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add(String.valueOf(board.coords.size()));
        board.coords.forEach(coord -> {
            Cell cell = board.map.get(coord);
            lines.add(
                String.format(
                    "%d %d %s",
                    cell.getType() == CellType.EGG ? 1 : cell.getType() == CellType.FOOD ? 2 : 0,
                    cell.getRichness(),
                    board.getNeighbourIds(coord)
                )
            );
        });

        Player other = getOpponent(player);
        lines.add(String.valueOf(player.anthills.size()));
        lines.add(Serializer.serialize(player.anthills));
        lines.add(Serializer.serialize(other.anthills));

        return lines;
    }

    private Player getOpponent(Player player) {
        return players.get(1 - player.getIndex());
    }

    public List<String> getCurrentFrameInfoFor(Player player) {
        List<String> lines = new ArrayList<>();
        Player other = getOpponent(player);
        if (Config.SCORES_IN_IO) {
            lines.add(Serializer.join(player.points, other.points));
        }
        for (CubeCoord coord : board.coords) {
            Cell cell = board.get(coord);
            lines.add(Serializer.join(cell.getRichness(), cell.getAnts(player), cell.getAnts(other)));
        }

        return lines.stream().map(String::valueOf).collect(Collectors.toList());
    }

    private void doBuild() {
        List<Cell> eggCells = board.getEggCells();
        List<AntConsumption> builds = new ArrayList<>();
        for (Player player : gameManager.getPlayers()) {
            builds.addAll(computeCellConsumption(player, eggCells));
        }
        for (AntConsumption build : builds) {
            for (int idx : build.player.anthills) {
                board.get(idx).placeAnts(build.player, build.amount);
            }
            launchBuildEvent(build.amount, build.player.getIndex(), build.path);
            build.cell.deplete(build.amount);
            gameSummaryManager.addBuild(build);
        }
    }

    private void doFights() {
        if (Config.FIGHTING_ANTS_KILL) {
            for (CubeCoord coord : board.coords) {
                Cell cell = board.get(coord);
                int ants0 = cell.getAnts(0);
                int ants1 = cell.getAnts(1);
                cell.removeAnts(0, Math.min(Config.MAX_ANT_LOSS, ants1));
                cell.removeAnts(1, Math.min(Config.MAX_ANT_LOSS, ants0));
            }
        }
    }

    public void performGameUpdate(int frameIdx) {
        doLines();
        doBeacons();
        doMove();
        animation.catchUp();
        if (moveAnimatedThisTurn) {
            animation.wait(Animation.THIRD);
        }
        doFights();
        doBuild();
        animation.catchUp();
        board.resetAttackCache();
        doScore();
        animation.catchUp();
        gameTurn++;
        if (checkGameOver()) {
            gameManager.endGame();
        }

        gameManager.addToGameSummary(gameSummaryManager.toString());
        gameSummaryManager.clear();

        int frameTime = animation.computeEvents();
        gameManager.setFrameDuration(frameTime);

    }

    private List<AntConsumption> computeCellConsumption(Player player, List<Cell> targetCells) {
        List<Integer> anthills = player.getAnthills();
        List<AntConsumption> meals = new ArrayList<>();

        for (Cell foodCell : targetCells) {
            List<LinkedList<Cell>> allPaths = new ArrayList<>();
            for (Integer anthill : anthills) {
                Cell anthillCell = board.get(anthill);

                // Dijkstra from food to anthill
                LinkedList<Cell> bestPathToHill = board.getBestPath(foodCell, anthillCell, player.getIndex(), Config.LOSING_ANTS_CANT_CARRY);

                if (bestPathToHill != null) {
                    allPaths.add(bestPathToHill);
                }
            }

            Comparator<LinkedList<Cell>> byPathValue = Comparator.comparing(list -> pathValue(player, list));

            // For this particular foodSource, this is the best path back to an anthill
            Optional<LinkedList<Cell>> bestPath = allPaths.stream()
                .sorted(
                    byPathValue.reversed().thenComparing(list -> list.size())
                )
                .findFirst();

            int maxMin = bestPath
                .map(list -> pathValue(player, list))
                .orElse(0);

            // What if there's only 1 food on a cell and both players eat it at the same time?
            // => it gets duplicated and they both eat 1
            int foodEaten = Math.min(maxMin, foodCell.getRichness());
            if (foodEaten > 0) {
                meals.add(new AntConsumption(player, foodEaten, foodCell, bestPath.get()));
            }
        }
        return meals;

    }

    private int pathValue(Player player, LinkedList<Cell> list) {
        return list.stream()
            .mapToInt(cell -> cell.getAnts(player))
            .min()
            .orElse(0);
    }

    private void doScore() {
        List<Cell> foodCells = board.getFoodCells();
        List<AntConsumption> meals = new ArrayList<>();
        for (Player player : gameManager.getPlayers()) {
            // For each food output, find best path that leads to one of the player's anthills.
            // The best path is the one with the largest minimum amount of ants on a node of the path.
            // e.g.   food--- 10 --- 2 --- 10 --- hill         
            //         \            /      /
            //          \---5------7----6-/                 should retrieve the path food-5-7-6-10-hill
            // the player should then be given as many points as the lowest node: 5 points.
            // This repeats for each food source.

            meals.addAll(computeCellConsumption(player, foodCells));
        }
        for (AntConsumption meal : meals) {
            launchFoodEvent(meal);
            gameSummaryManager.addMeal(meal);

            meal.player.addPoints(meal.amount);
            meal.cell.deplete(meal.amount);
        }
    }

    private void launchFoodEvent(AntConsumption meal) {
        EventData e = new EventData();
        e.type = EventData.FOOD;
        e.playerIdx = meal.player.getIndex();
        e.path = meal.path.stream().map(Cell::getIndex).collect(Collectors.toList());

        e.amount = meal.amount;
        animation.startAnim(e.animData, Animation.HALF);
        viewerEvents.add(e);
    }

    private void doMove() {
        for (Player player : gameManager.getPlayers()) {

            List<Cell> playerAntCells = getPlayerAntCells(player);
            List<Cell> playerBeaconCells = getPlayerBeaconCells(player);
            List<AntAllocation> allocations = AntAllocater.allocateAnts(playerAntCells, playerBeaconCells, player.getIndex(), board);

            Map<AntMove, Integer> moves = new HashMap<>();

            for (AntAllocation alloc : allocations) {
                // Get next step in path
                List<Integer> path = board.findShortestPath(alloc.getAntIndex(), alloc.getBeaconIndex(), player.getIndex());

                if (path.size() > 1) {
                    int neighbor = path.get(1);
                    int from = alloc.getAntIndex();
                    int to = neighbor;
                    int amount = alloc.getAmount();

                    AntMove antMove = new AntMove(from, to);
                    moves.compute(antMove, (k, v) -> v == null ? amount : v + amount);
                }
            }
            moves.forEach(
                (move, amount) -> applyMove(move.getFrom(), move.getTo(), amount, player.getIndex())
            );
        }
    }

    private void applyMove(int fromIdx, Integer toIdx, int amount, int playerIdx) {
        Cell source = board.get(fromIdx);
        Cell target = board.get(toIdx);

        source.removeAnts(playerIdx, amount);
        target.placeAnts(playerIdx, amount);

        // viewer animation
        launchMoveEvent(source.getIndex(), target.getIndex(), amount, playerIdx);
        moveAnimatedThisTurn = true;
    }

    private List<Cell> getPlayerAntCells(Player player) {
        return board.cells.stream()
            .filter(cell -> cell.getAnts(player.getIndex()) > 0)
            .collect(Collectors.toList());
    }

    private List<Cell> getPlayerBeaconCells(Player player) {
        return board.cells.stream()
            .filter(cell -> cell.getBeaconPower(player.getIndex()) > 0)
            .collect(Collectors.toList());
    }

    private void launchMoveEvent(int fromIdx, int toIdx, int amount, int playerIdx) {
        EventData e = new EventData();
        e.type = EventData.MOVE;
        e.playerIdx = playerIdx;
        e.cellIdx = fromIdx;
        e.targetIdx = toIdx;
        e.amount = amount;
        animation.startAnim(e.animData, Animation.HALF);
        viewerEvents.add(e);
    }

    private void launchBuildEvent(int amount, int playerIdx, List<Cell> path) {
        EventData e = new EventData();
        e.type = EventData.BUILD;
        e.playerIdx = playerIdx;
        e.amount = amount;
        e.path = path.stream().map(Cell::getIndex).collect(Collectors.toList());
        animation.startAnim(e.animData, Animation.HALF);
        viewerEvents.add(e);
    }

    private void doBeacons() {
        for (Player player : gameManager.getPlayers()) {
            for (BeaconAction beacon : player.beacons) {
                int cellIdx = beacon.getCellIndex();
                int power = beacon.getPower();
                setBeaconPower(cellIdx, player, power);
            }
        }
    }

    private void setBeaconPower(int cellIndex, Player player, int power) {
        Cell cell = board.get(cellIndex);
        if (!cell.isValid()) {
            gameSummaryManager.addError(
                player,
                String.format("cannot find cell %d", cellIndex)
            );
            return;
        }
        cell.setBeaconPower(player.getIndex(), Math.max(1, power));
    }

    private void launchBeaconEvent(int playerIdx, int power, int cellIdx) {
        EventData e = new EventData();
        e.type = EventData.BEACON;
        e.playerIdx = playerIdx;
        e.cellIdx = cellIdx;
        e.amount = power;
        animation.startAnim(e.animData, Animation.HALF);
        viewerEvents.add(e);
    }

    private void doLines() {
        for (Player player : gameManager.getPlayers()) {
            for (LineAction line : player.lines) {
                int from = line.getFrom();
                int to = line.getTo();
                int beaconPower = line.getAnts();
                if (!board.get(from).isValid()) {
                    gameSummaryManager.addError(
                        player,
                        String.format("cannot find cell %d", from)
                    );
                    continue;
                }
                if (!board.get(to).isValid()) {
                    gameSummaryManager.addError(
                        player,
                        String.format("cannot find cell %d", to)
                    );
                    continue;
                }
                Deque<Integer> path = board.findShortestPath(from, to);
                for (Integer cellIndex : path) {
                    setBeaconPower(cellIndex, player, beaconPower);
                }
            }
        }
    }

    private boolean checkGameOver() {
        int remainingFood = board.getRemainingFood();
        int pointsDiff = Math.abs(players.get(1).getPoints() - players.get(0).getPoints());
        if (remainingFood == 0) {
            gameSummaryManager.addNoMoreFood();
        } else if (remainingFood < pointsDiff) {
            if (players.get(0).getPoints() > players.get(1).getPoints()) {
                gameSummaryManager.addNotEnoughFoodLeft(remainingFood, players.get(0));
            } else {
                gameSummaryManager.addNotEnoughFoodLeft(remainingFood, players.get(1));
            }
        }
        return remainingFood == 0 || remainingFood < pointsDiff;
    }

    public void onEnd() {
        players.stream().forEach(p -> {
            if (p.isActive()) {
                p.setScore(p.points);
            } else {
                p.setScore(-1);
            }
        });

        if (players.get(0).getScore() == players.get(1).getScore() && players.get(0).getScore() != -1) {
            players.stream().forEach(p -> {
                p.setScore(getAntTotal(p));
            });
            endScreenModule.setScores(
                players.stream()
                    .mapToInt(p -> p.getScore())
                    .toArray(),
                players.stream()
                    .map(p -> String.format("%d points and %d ants", p.points, p.getScore()))
                    .toArray(length -> new String[length])
            );
        } else {
            endScreenModule.setScores(
                players.stream()
                    .mapToInt(p -> p.getScore())
                    .toArray()
            );
        }

    }

    private int getAntTotal(Player p) {
        return board.cells.stream().mapToInt(cell -> cell.getAnts(p)).sum();
    }

    public List<EventData> getViewerEvents() {
        return viewerEvents;
    }

    public static String getExpected(String playerOutput) {
        String attempt = playerOutput.toUpperCase();
        if (attempt.startsWith("BEACON")) {
            return "BEACON <cell_index> <beacon_strength>";
        }
        if (attempt.startsWith("LINE")) {
            return "LINE <from_index> <to_index> <beacon_strength>";
        }
        if (attempt.startsWith("MESSAGE")) {
            return "MESSAGE <text>";
        }
        if (attempt.startsWith("WAIT")) {
            return "WAIT";
        }
        return "BEACON |"
            + " LINE |"
            + " MESSAGE |"
            + " WAIT";
    }

    public Board getBoard() {
        return board;
    }

    public List<CubeCoord> getBoardCoords() {
        return board.coords;
    }

}
