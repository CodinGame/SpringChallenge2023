using System;
using System.Linq;
using System.IO;
using System.Text;
using System.Collections;
using System.Collections.Generic;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player
{
    static void Main(string[] args)
    {
        string[] inputs;
        int numberOfCells = int.Parse(Console.ReadLine()); // amount of hexagonal cells in this map

        List<Cell> cells = new List<Cell>();

        for (int i = 0; i < numberOfCells; i++)
        {
            inputs = Console.ReadLine().Split(' ');
            int type = int.Parse(inputs[0]); // 0 for empty, 1 for eggs, 2 for crystal
            int initialResources = int.Parse(inputs[1]); // the initial amount of eggs/crystals on this cell
            int neigh0 = int.Parse(inputs[2]); // the index of the neighbouring cell for each direction
            int neigh1 = int.Parse(inputs[3]);
            int neigh2 = int.Parse(inputs[4]);
            int neigh3 = int.Parse(inputs[5]);
            int neigh4 = int.Parse(inputs[6]);
            int neigh5 = int.Parse(inputs[7]);

            Cell cell = new Cell();
            cell.index = i;
            cell.cell_type = type;
            cell.resources = initialResources;

            cell.neighbors = new int[6] { neigh0, neigh1, neigh2, neigh3, neigh4, neigh5 };
            
            cell.my_ants = 0;
            cell.opp_ants = 0;

            cells.Add(cell);
        }
        int numberOfBases = int.Parse(Console.ReadLine());

        List<int> my_bases = new List<int>();
        List<int> opp_bases = new List<int>();

        inputs = Console.ReadLine().Split(' ');
        for (int i = 0; i < numberOfBases; i++)
        {
            int myBaseIndex = int.Parse(inputs[i]);
            my_bases.Add(myBaseIndex);
        }
        inputs = Console.ReadLine().Split(' ');
        for (int i = 0; i < numberOfBases; i++)
        {
            int oppBaseIndex = int.Parse(inputs[i]);
            opp_bases.Add(oppBaseIndex);
        }

        // game loop
        while (true)
        {
            for (int i = 0; i < numberOfCells; i++)
            {
                inputs = Console.ReadLine().Split(' ');
                int resources = int.Parse(inputs[0]); // the current amount of eggs/crystals on this cell
                int myAnts = int.Parse(inputs[1]); // the amount of your ants on this cell
                int oppAnts = int.Parse(inputs[2]); // the amount of opponent ants on this cell

                cells[i].resources = resources;
                cells[i].my_ants = myAnts;
                cells[i].opp_ants = oppAnts;
            }

            // Write an action using Console.WriteLine()
            // To debug: Console.Error.WriteLine("Debug messages...");
            List<string> actions = new List<string>();


            // ------- ! -----   Now you can generate a list of actions to do --------
            // WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text>

            foreach (var cell in cells)
            {
                if (cell.resources > 0)
                {
                    actions.Add($"LINE {my_bases[0]} {cell.index} 1");
                    break;
                }
            }


            if (actions.Count == 0)
            {
                Console.WriteLine("WAIT");
            }
            else
            {
                string final_action = String.Join(";", actions);
                Console.WriteLine(final_action);
            }

        }
    }

    public class Cell
    {
        public int index { get; set; }
        public int cell_type { get; set; }
        public int resources { get; set; }
        public int[]? neighbors { get; set; }
        public int my_ants { get; set; }
        public int opp_ants { get; set; }
    }
}
