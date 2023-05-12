STDOUT.sync = true # DO NOT REMOVE

class Cell
  attr_accessor :index
  attr_accessor :type
  attr_accessor :resources
  attr_accessor :neighbors
  attr_accessor :my_ants
  attr_accessor :opp_ants

  def initialize(index, type, resources, neighbors, my_ants, opp_ants)
      @index = index
      @type = type
      @resources = resources
      @neighbors = neighbors
      @my_ants = my_ants
      @opp_ants = opp_ants
  end
end

cells = []

number_of_cells = gets.to_i # amount of hexagonal cells in this map
number_of_cells.times do |i|
  inputs = gets.split.map &:to_i
  _type = inputs[0] # 0 for empty, 1 for eggs, 2 for crystal
  initial_resources = inputs[1] # the initial amount of eggs/crystals on this cell
  neigh_0 = inputs[2] # the index of the neighbouring cell for each direction
  neigh_1 = inputs[3]
  neigh_2 = inputs[4]
  neigh_3 = inputs[5]
  neigh_4 = inputs[6]
  neigh_5 = inputs[7]
  cell = Cell.new(
    i,
    _type,
    initial_resources,
    [neigh_0, neigh_1, neigh_2, neigh_3, neigh_4, neigh_5].select { |id| id > -1 },
    0,
    0
  )
  cells.append cell
end
number_of_bases = gets.to_i
inputs = gets.split
my_bases = []
for i in 0..(number_of_bases-1)
  my_base_index = inputs[i].to_i
  my_bases.append my_base_index
end
inputs = gets.split
opp_bases = []
for i in 0..(number_of_bases-1)
  opp_base_index = inputs[i].to_i
  opp_bases.append opp_base_index
end

# game loop
loop do
  number_of_cells.times do |i|
    inputs = gets.split.map &:to_i
    resources = inputs[0] # the current amount of eggs/crystals on this cell
    my_ants = inputs[1] # the amount of your ants on this cell
    opp_ants = inputs[2] # the amount of opponent ants on this cell

    cells[i].resources = resources
    cells[i].my_ants = my_ants
    cells[i].opp_ants = opp_ants
  end

  # WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text>
  actions = []

  # TODO: choose actions to perform and push them into actions
  # To debug: STDERR.puts "Debug messages..."
  if actions.length == 0
    puts 'WAIT'
  else
    puts actions.join ";"
  end
end
