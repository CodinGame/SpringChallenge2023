open System

type Ressource = 
    | Empty 
    | Eggs of int
    | Crystal of int
module Ressource =
    let parse (token: string array) =
        let _type = token.[0] (* 0 for empty, 1 for eggs, 2 for crystal *)
        let initialResources = int token[1] (* the initial amount of eggs/crystals on this cell *)
        match _type[0] with
        | '0' -> Empty
        | '1' -> Eggs(initialResources)
        | '2' -> Crystal(int token[1])
        | _ -> failwithf "Invalid cell type %A" token

    let update (ressource: Ressource) (newCount:int) =
        match ressource with
        | Empty -> Empty
        | Eggs(_) -> Eggs(newCount)
        | Crystal(_) -> Crystal(newCount)

type Cell =
    {
        mutable Ressource: Ressource
        Neighbours: int array
        mutable MyAnts: int
        mutable HisAnts: int
    }
module Cell =
    let parse (token: string array) =
        (* neigh0: the index of the neighbouring cell for each direction *)
        {
            Ressource = Ressource.parse token
            Neighbours = [| int token[2]; int token[3]; int token[4]; int token[5]; int token[6]; int token[7] |]
            MyAnts = 0
            HisAnts = 0
        }

let numberOfCells = int(Console.In.ReadLine()) (* amount of hexagonal cells in this map *)
let cells = 
    [| for i in 0 .. numberOfCells - 1 do
        let token = (Console.In.ReadLine()).Split [|' '|]
        Cell.parse token
    |]

let numberOfBases = int(Console.In.ReadLine())
let myBaseWords = (Console.In.ReadLine()).Split [|' '|]
let myBases = 
    [| for baseWord in myBaseWords do
        int baseWord
    |]

let hisBaseWords = (Console.In.ReadLine()).Split [|' '|]
let hisBases = 
    [| for baseWord in hisBaseWords do
        int baseWord
    |]


(* game loop *)
while true do
    for i in 0 .. numberOfCells - 1 do
        (* resources: the current amount of eggs/crystals on this cell *)
        (* myAnts: the amount of your ants on this cell *)
        (* oppAnts: the amount of opponent ants on this cell *)
        let token1 = (Console.In.ReadLine()).Split [|' '|]
        let resources = int token1[0]
        let myAnts = int token1[1]
        let oppAnts = int token1[2]
        cells[i].Ressource <- Ressource.update cells[i].Ressource resources
        cells[i].MyAnts <- myAnts
        cells[i].HisAnts <- oppAnts


    (* WAIT | LINE <sourceIdx> <targetIdx> <strength> | BEACON <cellIdx> <strength> | MESSAGE <text> *)
    let actions = []
    
    (* Write an action using printfn *)
    (* To debug: eprintfn "Debug message" *)
    if actions.Length = 0 then
        printfn "WAIT"
    else
        printfn "%s" (String.concat ";" actions)
