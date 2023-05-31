class Cell {
  constructor(index, type, resources, neighbors, myAnts, oppAnts) {
      this.index = index
      this.type = type
      this.resources = resources
      this.neighbors = neighbors
      this.myAnts = myAnts
      this.oppAnts = oppAnts
  }
}

const cells = []

const numberOfCells = parseInt(readline()); // amount of hexagonal cells in this map
for (let i = 0; i < numberOfCells; i++) {
  const inputs = readline().split(' ');
  const type = parseInt(inputs[0]); // 0 for empty, 1 for eggs, 2 for food
  const initialResources = parseInt(inputs[1]); // the initial amount of eggs/crystals on this cell
  const neigh0 = parseInt(inputs[2]); // the index of the neighbouring cell for each direction
  const neigh1 = parseInt(inputs[3]);
  const neigh2 = parseInt(inputs[4]);
  const neigh3 = parseInt(inputs[5]);
  const neigh4 = parseInt(inputs[6]);
  const neigh5 = parseInt(inputs[7]);

  const cell = new Cell(
      i,
      type,
      initialResources,
      [neigh0, neigh1, neigh2, neigh3, neigh4, neigh5].filter(id => id > -1),
      0,
      0
  )
  cells.push(cell)
}

const numberOfBases = parseInt(readline());
const myBases = readline().split(' ').map(n => parseInt(n))
const oppBases = readline().split(' ').map(n => parseInt(n))

// game loop
while (true) {
  for (let i = 0; i < numberOfCells; i++) {
      const inputs = readline().split(' ')
      const resources = parseInt(inputs[0]); // the current amount of eggs/crystals on this cell
      const myAnts = parseInt(inputs[1]); // the amount of your ants on this cell
      const oppAnts = parseInt(inputs[2]); // the amount of opponent ants on this cell

      cells[i].resources = resources
      cells[i].myAnts = myAnts
      cells[i].oppAnts = oppAnts
  }


  // WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text>
  const actions = []

  // TODO: choose actions to perform and push them into actions. E.g:
  for (const cell of cells) {
      if (cell.resources > 0) {
          actions.push(`LINE ${myBases[0]} ${cell.index} 1`)
          break
      }
  }

  // To debug: console.error('Debug messages...');
  if (actions.length === 0) {
      console.log('WAIT');
  } else {
      console.log(actions.join(';'))
  }
}
