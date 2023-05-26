package com.codingame.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Board {

    public final Map<CubeCoord, Cell> map;
    // Sorted by cell index
    public final List<Cell> cells;
    // Sorted by cell index
    public final List<CubeCoord> coords;
    int ringCount;
    List<Player> players;

    public Board(Map<CubeCoord, Cell> map, int ringCount, List<Player> players) {
        this.players = players;
        this.map = map;
        this.ringCount = ringCount;
        this.cells = map.values()
            .stream()
            .sorted(Comparator.comparing(cell -> cell.getIndex()))
            .collect(Collectors.toList());
        this.coords = cells
            .stream()
            .map(Cell::getCoord)
            .collect(Collectors.toList());
        this.distanceCache = new int[map.size()][map.size()];
        this.attackCache = new ArrayList<>();
        for (Player player : players) {
            this.attackCache.add(new HashMap<>());
        }
    }

    public List<Integer> getNeighbours(int i) {
        return getNeighbours(coords.get(i))
            .stream()
            .map(coord -> map.get(coord).getIndex())
            .collect(Collectors.toList());
    }

    public List<CubeCoord> getNeighbours(CubeCoord coord) {
        return coord.neighbours().stream()
            .filter(neighbor -> map.containsKey(neighbor))
            .collect(Collectors.toList());
    }

    public List<Cell> getNeighbours(Cell cell) {
        return getNeighbours(cell.getCoord())
            .stream()
            .map(coord -> map.getOrDefault(coord, Cell.NO_CELL))
            .collect(Collectors.toList());
    }

    public String getNeighbourIds(CubeCoord coord) {
        List<Integer> orderedNeighborIds = new ArrayList<>(CubeCoord.directions.length);
        for (int i = 0; i < CubeCoord.directions.length; ++i) {
            orderedNeighborIds.add(
                map.getOrDefault(coord.neighbor(i), Cell.NO_CELL).getIndex()
            );
        }
        return orderedNeighborIds.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(" "));
    }

    public List<CubeCoord> getEdges() {
        CubeCoord center = new CubeCoord(0, 0, 0);
        return coords.stream()
            .filter(coord -> coord.distanceTo(center) == ringCount)
            .collect(Collectors.toList());
    }

    public Cell get(CubeCoord coord) {
        return map.getOrDefault(coord, Cell.NO_CELL);
    }

    public Cell get(int index) {
        if (index < 0 || index >= coords.size()) {
            return Cell.NO_CELL;
        }
        return map.get(coords.get(index));
    }

    // Distance cache
    private int[][] distanceCache;

    /**
     * @return -1 if no path exist between A and B, otherwise the length of the shortest path
     */
    public int getDistance(int ai, int bi) {
        if (ai == bi) {
            return 0;
        }

        int cached = distanceCache[ai][bi];
        if (cached > 0) {
            return cached;
        }

        CubeCoord a = coords.get(ai);
        CubeCoord b = coords.get(bi);
        int distance = internalGetDistance(a, b, null);
        distanceCache[ai][bi] = distance;
        distanceCache[bi][ai] = distance;
        return distance;
    }

    private int internalGetDistance(CubeCoord a, CubeCoord b, Integer playerIdx) {
        Deque<Integer> path = findShortestPath(map.get(a).getIndex(), map.get(b).getIndex(), playerIdx);

        if (path == null) {
            // impossibru
            return -1;
        }
        return path.size() - 1;

    }

    /**
     * Finds the shortest path between 2 points, using a BFS.
     */
    public Deque<Integer> findShortestPath(int from, int to) {
        return findShortestPath(from, to, null);
    }

    public LinkedList<Integer> findShortestPath(int a, int b, Integer playerIdx) {
        // BFS
        LinkedList<Integer> queue = new LinkedList<>();
        Map<Integer, Integer> prev = new HashMap<>();

        prev.put(a, null);
        queue.add(a);

        while (!queue.isEmpty()) {
            if (prev.containsKey(b)) {
                break;
            }
            Integer head = queue.pop();

            List<Integer> neighbours = getNeighbours(head);
            if (playerIdx != null) {
                // Order by amount of friendly ants, then beacon strength, then id of cell
                neighbours = neighbours.stream()
                    .sorted(
                        Comparator
                            .comparing((Integer idx) -> -get(idx).getAnts(playerIdx))
                            .thenComparing((Integer idx) -> -get(idx).getBeaconPower(playerIdx))
                            .thenComparing(Function.identity())
                    )
                    .collect(Collectors.toList());
            }
            for (Integer neighbour : neighbours) {
                Cell cell = cells.get(neighbour);
                boolean visited = prev.containsKey(neighbour);
                if (cell.isValid() && !visited) {
                    prev.put(neighbour, head);
                    queue.add(neighbour);
                }
            }
        }

        if (!prev.containsKey(b)) {
            return null; // impossibru
        }

        // Reconstruct path
        LinkedList<Integer> path = new LinkedList<>();
        Integer current = b;
        while (current != null) {
            path.addFirst(current);
            current = prev.get(current);
        }

        return path;
    }

    /**
     * @return The path that maximizes the given player score between start and end, while minimizing the distance from start to end.
     */
    public LinkedList<Cell> getBestPath(Cell start, Cell end, int playerIdx, boolean interruptedByFight) {
        return getBestPath(start.getIndex(), end.getIndex(), playerIdx, interruptedByFight);
    }

    //TODO: cache
    List<Map<Integer, Integer>> attackCache;

    private int getAttackPower(int cellIdx, int playerIdx) {
        Integer cachedAttackPower = attackCache.get(playerIdx).get(cellIdx);
        if (cachedAttackPower != null) {
            return cachedAttackPower;
        }

        List<Integer> anthills = players.get(playerIdx).anthills;

        List<LinkedList<Cell>> allPaths = new ArrayList<>();
        for (Integer anthill : anthills) {
            LinkedList<Cell> bestPath = getBestPath(cellIdx, anthill, playerIdx, false);

            if (bestPath != null) {
                allPaths.add(bestPath);
            }
        }

        int maxMin = allPaths.stream()
            .mapToInt(list -> {
                return list.stream()
                    .mapToInt(c -> c.getAnts(playerIdx))
                    .min()
                    .orElse(0);
            })
            .max()
            .orElse(0);

        attackCache.get(playerIdx).put(cellIdx, maxMin);
        return maxMin;

    }

    public void resetAttackCache() {
        for (Map<Integer, Integer> attackCache : this.attackCache) {
            attackCache.clear();
        }
    }

    /**
     * @return The path that maximizes the given player score between start and end, while minimizing the distance from start to end.
     */
    public LinkedList<Cell> getBestPath(int start, int end, int playerIdx, boolean interruptedByFight) {
        // Dijkstra's algorithm based on the tuple (maxValue, minDist)

        // TODO: optim: pre-compute all distances from each cell to the end
        int[] maxPathValues = new int[cells.size()];
        int[] prev = new int[cells.size()];
        int[] distanceFromStart = new int[cells.size()];
        boolean[] visited = new boolean[cells.size()];
        Arrays.fill(maxPathValues, Integer.MIN_VALUE);
        Arrays.fill(prev, -1);
        Arrays.fill(visited, false);

        Comparator<Integer> valueComparator = Comparator.comparing(cellIndex -> maxPathValues[cellIndex]);
        Comparator<Integer> distanceComparator = Comparator.comparing(cellIndex -> distanceFromStart[cellIndex] + getDistance(cellIndex, end));
        PriorityQueue<Integer> queue = new PriorityQueue<>(valueComparator.reversed().thenComparing(distanceComparator));
        Cell startCell = cells.get(start);
        maxPathValues[start] = startCell.getAnts(playerIdx);
        distanceFromStart[start] = 0;
        int startAnts = startCell.getAnts(playerIdx);
        if (interruptedByFight) {
            int myForce = getAttackPower(start, playerIdx);
            int otherForce = getAttackPower(start, 1 - playerIdx);
            if (otherForce > myForce) {
                startAnts = 0;
            }
        }
        if (startAnts > 0) {
            queue.add(start);
        }

        while (!queue.isEmpty() && !visited[end]) {
            Integer currentIndex = queue.poll();
            visited[currentIndex] = true;

            // Update the max values of the neighbors
            for (Cell neighbor : getNeighbours(get(currentIndex))) {
                int neighborIndex = neighbor.getIndex();
                int neighborAnts = neighbor.getAnts(playerIdx);
                if (neighborAnts > 0) {
                    if (interruptedByFight) {
                        int myForce = getAttackPower(neighborIndex, playerIdx);
                        int otherForce = getAttackPower(neighborIndex, 1 - playerIdx);
                        if (otherForce > myForce) {
                            neighborAnts = 0;
                        }
                    }
                }

                if (!visited[neighborIndex] && neighborAnts > 0) {
                    int potentialMaxPathValue = Math.min(maxPathValues[currentIndex], neighborAnts);
                    if (potentialMaxPathValue > maxPathValues[neighborIndex]) {
                        maxPathValues[neighborIndex] = potentialMaxPathValue;
                        distanceFromStart[neighborIndex] = distanceFromStart[currentIndex] + 1;
                        prev[neighborIndex] = currentIndex;
                        queue.add(neighborIndex);
                    }
                }
            }
        }

        if (!visited[end]) {
            // No path from start to end
            return null;
        }

        // Compute the path from start to end
        LinkedList<Cell> path = new LinkedList<>();
        int currentIndex = end;
        while (currentIndex != -1) {
            path.addFirst(get(currentIndex));
            currentIndex = prev[currentIndex];
        }
        return path;
    }

    public static boolean isConnected(List<CubeCoord> coords) {
        Set<CubeCoord> coordsSet = new HashSet<>(coords);
        Set<CubeCoord> visited = new HashSet<>();
        Stack<CubeCoord> stack = new Stack<>();

        CubeCoord start = coords.get(0);

        stack.push(start);
        visited.add(start);

        while (!stack.isEmpty()) {
            CubeCoord coord = stack.pop();
            for (CubeCoord neighbor : coord.neighbours()) {
                if (coordsSet.contains(neighbor) && !visited.contains(neighbor)) {
                    stack.push(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        return visited.size() == coords.size();
    }

    public boolean isConnected() {
        return Board.isConnected(coords);
    }

    public List<Cell> getFoodCells() {
        return map.values().stream()
            .filter(cell -> cell.getType() == CellType.FOOD && cell.getRichness() > 0)
            .collect(Collectors.toList());
    }

    public int getRemainingFood() {
        return map.values().stream()
            .filter(cell -> cell.getType() == CellType.FOOD)
            .mapToInt(cell -> cell.getRichness())
            .sum();
    }

    public List<Cell> getEggCells() {
        return map.values().stream()
            .filter(cell -> cell.getType() == CellType.EGG && cell.getRichness() > 0)
            .collect(Collectors.toList());
    }

}
