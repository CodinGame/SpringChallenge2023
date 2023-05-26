package com.codingame.game.move;
/*
 * Click `Run` to execute the snippet below!
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.codingame.game.Board;
import com.codingame.game.Cell;

class CellData {
    Cell cell;
    long ants, beacons, wiggleRoom;

    public CellData(Cell cell, int playerIdx) {
        this.cell = cell;
        this.ants = cell.getAnts(playerIdx);
        this.beacons = cell.getBeaconPower(playerIdx);
    }
}

class AntBeaconPair {
    private CellData ant, beacon;

    public AntBeaconPair(CellData ant, CellData beacon) {
        this.ant = ant;
        this.beacon = beacon;
    }

    public CellData getAnt() {
        return ant;
    }

    public CellData getBeacon() {
        return beacon;
    }
}

public class AntAllocater {
    private static List<CellData> convert(List<Cell> cells, int playerIdx) {
        return cells.stream().map(cell -> new CellData(cell, playerIdx)).collect(Collectors.toList());
    }

    public static List<AntAllocation> allocateAnts(List<Cell> antCells, List<Cell> beaconCells, int playerIdx, Board board) {
        return innerAllocateAnts(
            convert(antCells, playerIdx),
            convert(beaconCells, playerIdx),
            playerIdx,
            board
        );
    }

    private static int getDistance(AntBeaconPair p, int playerIdx, Board board) {
        return board.getDistance(p.getAnt().cell.getIndex(), p.getBeacon().cell.getIndex());
    }

    private static List<AntAllocation> innerAllocateAnts(List<CellData> antCells, List<CellData> beaconCells, int playerIdx, Board board) {
        List<AntAllocation> allocations = new ArrayList<>();
        long antSum = 0;
        for (CellData cell : antCells) {
            antSum += cell.ants;
        }

        long beaconSum = 0;
        for (CellData cell : beaconCells) {
            beaconSum += cell.beacons;
        }

        double scalingFactor = (double) antSum / beaconSum;
        for (CellData cell : beaconCells) {
            long highBeaconValue = (long) Math.ceil(cell.beacons * scalingFactor);
            long lowBeaconValue = (long) (cell.beacons * scalingFactor);
            cell.beacons = Math.max(1, lowBeaconValue);
            cell.wiggleRoom = highBeaconValue - cell.beacons;
            //XXX: wiggleRoom will equals 1 if the beaconValue got rounded down
        }

        List<AntBeaconPair> allPairs = new ArrayList<>();

        for (CellData antCell : antCells) {
            for (CellData beaconCell : beaconCells) {
                AntBeaconPair pair = new AntBeaconPair(antCell, beaconCell);
                if (getDistance(pair, playerIdx, board) != -1) {
                    allPairs.add(pair);
                }
            }
        }

        Collections.sort(
            allPairs,
            Comparator.comparing((AntBeaconPair pair) -> getDistance(pair, playerIdx, board))
                // Tie-breakers    
                .thenComparing(pair -> pair.getAnt().cell.getIndex())
                .thenComparing(pair -> pair.getBeacon().cell.getIndex())
        );

        boolean stragglers = false;
        while (!allPairs.isEmpty()) {
            for (AntBeaconPair pair : allPairs) {
                CellData antCell = pair.getAnt();
                long antCount = antCell.ants;
                CellData beaconCell = pair.getBeacon();
                long beaconCount = beaconCell.beacons;
                long wiggleRoom = beaconCell.wiggleRoom;

                int maxAlloc = (int) (stragglers ? Math.min(antCount, beaconCount + wiggleRoom) : Math.min(antCount, beaconCount));
                if (maxAlloc > 0) {
                    allocations.add(
                        new AntAllocation(
                            antCell.cell.getIndex(), beaconCell.cell.getIndex(), maxAlloc
                        )
                    );

                    antCell.ants -= maxAlloc;
                    if (!stragglers) {
                        beaconCell.beacons -= maxAlloc;
                    } else {
                        beaconCell.beacons -= (maxAlloc - wiggleRoom);
                        beaconCell.wiggleRoom = 0;
                    }
                }
            }
            allPairs.removeIf(pair -> pair.getAnt().ants <= 0);
            stragglers = true;
        }

        return allocations;
    }
}