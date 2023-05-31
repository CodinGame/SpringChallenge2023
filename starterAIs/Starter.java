import java.util.*;
import java.io.*;
import java.math.*;


class Cell {
    int index;
    int cellType;
    int resources;
    List<Integer> neighbors;
    int myAnts;
    int oppAnts;

    public Cell(int index, int cellType, int resources, List<Integer> neighbors, int myAnts, int oppAnts) {
        this.index = index;
        this.cellType = cellType;
        this.resources = resources;
        this.neighbors = neighbors;
        this.myAnts = myAnts;
        this.oppAnts = oppAnts;
    }
}

class Player {

    public static void main(String args[]) {

        List<Cell> cells = new ArrayList<>();

        Scanner in = new Scanner(System.in);
        int numberOfCells = in.nextInt(); // amount of hexagonal cells in this map
        for (int i = 0; i < numberOfCells; i++) {
            int cellType = in.nextInt(); // 0 for empty, 1 for eggs, 2 for crystal
            int initialResources = in.nextInt(); // the initial amount of eggs/crystals on this cell
            List<Integer> neighbors = new ArrayList<>(); // the index of the neighbouring cell for each direction
            neighbors.add(in.nextInt());
            neighbors.add(in.nextInt());
            neighbors.add(in.nextInt());
            neighbors.add(in.nextInt());
            neighbors.add(in.nextInt());
            neighbors.add(in.nextInt());
            Cell cell = new Cell(i, cellType, initialResources, neighbors, 0, 0);
            cells.add(cell);
        }
        int numberOfBases = in.nextInt();
        List<Integer> myBases = new ArrayList<>();
        List<Integer> oppBases = new ArrayList<>();

        for (int i = 0; i < numberOfBases; i++) {
            int myBaseIndex = in.nextInt();
            myBases.add(myBaseIndex);
        }
        for (int i = 0; i < numberOfBases; i++) {
            int oppBaseIndex = in.nextInt();
            oppBases.add(oppBaseIndex);
        }

        // game loop
        while (true) {
            for (int i = 0; i < numberOfCells; i++) {
                int resources = in.nextInt(); // the current amount of eggs/crystals on this cell
                int myAnts = in.nextInt(); // the amount of your ants on this cell
                int oppAnts = in.nextInt(); // the amount of opponent ants on this cell

                cells.get(i).resources = resources;
                cells.get(i).myAnts = myAnts;
                cells.get(i).oppAnts = oppAnts;
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            List<String> actions = new ArrayList<>();
            
            //TODO: choose actions to perform and push them into actions. E.g:
            for (Cell cell : cells) {
                if (cell.resources > 0) {
                    actions.add("LINE " + myBases.get(0) + " " + cell.index + " 1");
                    break;
                }
            }

            if (actions.size() == 0) {
                System.out.println("WAIT");
            } else {
                // WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text>
                System.out.println(String.join(";", actions));
            }
        }
    }
}