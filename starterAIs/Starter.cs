using System;
using System.Collections.Generic;
using System.Linq;

namespace StarterForEvent
{
    enum CellType
    {
        Empty = 0,
        Egg = 1,
        Crystal = 2
    }

    class Cell
    {
        public CellType Type { get; set; }
        public int Resource { get; set; }
        public IList<int> Neighbors { get; set; } = new List<int>();
        public int MyAnts { get; set; }
        public int OppAnts { get; set; }

        public void AddNeighbor(params int[] neighborIndexs)
        {
            foreach (var neighborIndex in neighborIndexs.Where(n => n > -1))
            {
                Neighbors.Add(neighborIndex);
            }
        }
    }

    class Player
    {
        static void Main(string[] args)
        {
            string[] inputs;
            int numberOfCells = int.Parse(Console.ReadLine()); // amount of hexagonal cells in this map
            var cells = new List<Cell>();
            for (int i = 0; i < numberOfCells; i++)
            {
                inputs = Console.ReadLine().Split(' ');
                var cell = new Cell
                {
                    Type = (CellType)int.Parse(inputs[0]), // 0 for empty, 1 for eggs, 2 for food
                    Resource = int.Parse(inputs[1]) // the initial amount of eggs/crystals on this cell
                };
                cell.AddNeighbor(int.Parse(inputs[2]), int.Parse(inputs[3]), int.Parse(inputs[4]), int.Parse(inputs[5]),
                    int.Parse(inputs[6]), int.Parse(inputs[7])); // the index of the neighbouring cell for each direction
                cells.Add(cell);
            }

            int numberOfBases = int.Parse(Console.ReadLine());
            var myBaseIndexs = Console.ReadLine().Split(' ').Take(numberOfBases).Select(int.Parse);
            var oppBaseIndexs = Console.ReadLine().Split(' ').Take(numberOfBases).Select(int.Parse);

            // game loop
            while (true)
            {
                for (int i = 0; i < numberOfCells; i++)
                {
                    inputs = Console.ReadLine().Split(' ');
                    cells[i].Resource = int.Parse(inputs[0]); // the current amount of eggs/crystals on this cell
                    cells[i].MyAnts = int.Parse(inputs[1]); // the amount of your ants on this cell
                    cells[i].OppAnts = int.Parse(inputs[2]); // the amount of opponent ants on this cell
                }

                // Write an action using Console.WriteLine()
                // To debug: Console.Error.WriteLine("Debug messages...");


                // WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text>
                Console.WriteLine("WAIT");
            }
        }
    }
}
