enum CellType {
  EMPTY = 0,
  EGG = 1,
  CRYSTAL = 2
}

interface Cell {
  type: CellType
  resources: number
  neighbors: number[]
  myAnts: number
  oppAnts: number
}

const cells: Cell[] = []

const numberOfCells: number = parseInt(readline()); // amount of hexagonal cells in this map
for (let i = 0; i < numberOfCells; i++) {
  const inputs: string[] = readline().split(' ');
  const type: number = parseInt(inputs[0]); // 0 for empty, 1 for eggs, 2 for food
  const initialResources: number = parseInt(inputs[1]); // the initial amount of eggs/crystals on this cell
  const neigh0: number = parseInt(inputs[2]); // the index of the neighbouring cell for each direction
  const neigh1: number = parseInt(inputs[3]);
  const neigh2: number = parseInt(inputs[4]);
  const neigh3: number = parseInt(inputs[5]);
  const neigh4: number = parseInt(inputs[6]);
  const neigh5: number = parseInt(inputs[7]);

  const cell: Cell = {
    type,
    resources: initialResources,
    neighbors: [neigh0, neigh1, neigh2, neigh3, neigh4, neigh5].filter(id => id > -1),
    myAnts: 0,
    oppAnts: 0
  }
  cells.push(cell)
}

const numberOfBases: number = parseInt(readline());
const myBases: number[] = readline().split(' ').map(n => parseInt(n))
const oppBases: number[] = readline().split(' ').map(n => parseInt(n))

// game loop
while (true) {
  for (let i = 0; i < numberOfCells; i++) {
    const inputs = readline().split(' ')
    const resources: number = parseInt(inputs[0]); // the current amount of eggs/crystals on this cell
    const myAnts: number = parseInt(inputs[1]); // the amount of your ants on this cell
    const oppAnts: number = parseInt(inputs[2]); // the amount of opponent ants on this cell

    cells[i].resources = resources
    cells[i].myAnts = myAnts
    cells[i].oppAnts = oppAnts
  }


  // WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text>
  const actions = []

  // TODO: choose actions to perform and push them into actions
  // To debug: console.error('Debug messages...');
  if (actions.length === 0) {
    console.log('WAIT');
  } else {
    console.log(actions.join(';'))
  }
}
