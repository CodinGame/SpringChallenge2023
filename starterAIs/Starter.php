<?php

class Cell
{
	public $index;
	public $cell_type;
	public $resources;
	public $neighbors = [];
	public $my_ants;
	public $opp_ants;

	public function __construct( int $index, int $cell_type, int $resources, array $neighbors, int $my_ants, int $opp_ants )
	{
			$this->index = $index;
			$this->cell_type = $cell_type;
			$this->resources = $resources;
			$this->neighbors = $neighbors;
			$this->my_ants = $my_ants;
			$this->opp_ants = $opp_ants;
	}
}

$cells = array();

fscanf(STDIN, "%d", $numberOfCells);
for ($i = 0; $i < $numberOfCells; $i++)
{
    // $type: 0 for empty, 1 for eggs, 2 for crystal
    // $Resources: initial amount of eggs/crystals on this cell
    // $neigh0: index of the neighbouring cell for each direction
    fscanf(STDIN, "%d %d %d %d %d %d %d %d", $cell_type, $resources, $neigh0, $neigh1, $neigh2, $neigh3, $neigh4, $neigh5);
		$neighbors = [$neigh0, $neigh1, $neigh2, $neigh3, $neigh4, $neigh5];
    $cells[$i] = new Cell( $i, $cell_type, $resources, $neighbors, 0, 0 );
}

fscanf(STDIN, "%d", $numberOfBases);
$myBases = array();
$oppBases = array();
$inputs = explode(" ", fgets(STDIN));
for ($i = 0; $i < $numberOfBases; $i++)
{
    $myBaseIndex = intval($inputs[$i]);
    $myBases[$i] = $myBaseIndex;
}
$inputs = explode(" ", fgets(STDIN));
for ($i = 0; $i < $numberOfBases; $i++)
{
    $oppBaseIndex = intval($inputs[$i]);
    $oppBases[$i] = $oppBaseIndex;
}

// game loop
while (TRUE)
{
    $actions = [];

    for ($i = 0; $i < $numberOfCells; $i++)
    {
        // $resources: amount of eggs/crystals on this cell
        // $my_ants: amount of your ants on this cell
        // $opp_ants: amount of opponent ants on this cell
        fscanf(STDIN, "%d %d %d", $resources, $my_ants, $opp_ants);
        $cells[$i]->resources = $resources;
        $cells[$i]->my_ants = $my_ants;
        $cells[$i]->opp_ants = $opp_ants;
    }

    // Write an action using echo(). DON'T FORGET THE TRAILING \n
    // To debug: error_log(var_export($var, true)); (equivalent to var_dump)

    //TODO: choose actions to perform and push them into actions. E.g:
    foreach ($cells as $cell) {
        if ($cell->resources > 0) {
            $actions[] = "LINE " . $myBases[0] . " " . $cell->index . " 1";
            break;
        }
    }

    // WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text>
    if ( count( $actions ) == 0 ) echo("WAIT\n");
    else echo( implode( ';', $actions ) . "\n" );
}


