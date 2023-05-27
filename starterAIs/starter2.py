from dataclasses import dataclass
from enum import Enum
from sys import stderr
from typing import Dict
from typing import List


class Channels(Enum):
    LOG = "LOG"
    WARNING = "WARNING"
    ERROR = "ERROR"


class Debug:
    @staticmethod
    def send(channel, msg):
        print(f"[{channel.value}] {msg}", file=stderr, flush=True)

    @staticmethod
    def log(msg):
        Debug.send(Channels.LOG, msg)

    @staticmethod
    def warning(msg):
        Debug.send(Channels.WARNING, msg)

    @staticmethod
    def error(msg):
        Debug.send(Channels.ERROR, msg)


class Actions(Enum):
    PUT_BEACON = "BEACON"
    BUILD_LINE = "LINE"
    WAIT = "WAIT"
    WRITE_MESSAGE = "MESSAGE"


class Action:
    action_type: Actions
    params: List[object] = []

    def __init__(self, action_type, params=None):
        self.action_type = action_type
        if params is not None:
            self.params = params

    def to_str(self):
        return " ".join(str(i) for i in [self.action_type.value, *self.params])

    @staticmethod
    def put_beacon(cell_id, strength):
        return Action(Actions.PUT_BEACON, [str(i) for i in [cell_id, strength]])

    @staticmethod
    def build_line(source_id, target_id, strength):
        return Action(Actions.BUILD_LINE, [str(i) for i in [source_id, target_id, strength]])

    @staticmethod
    def wait():
        return Action(Actions.WAIT)

    @staticmethod
    def write_message(text):
        return Action(Actions.WRITE_MESSAGE, [str(text)])


class Contents(Enum):
    EMPTY = 0
    EGG = 1
    CRYSTAL = 2


@dataclass
class Cell:
    id: int
    contents: Contents
    resources: int
    neighbours: List[int]
    allies: int = 0
    enemies: int = 0

    def update(self, resources, allies, enemies):
        self.resources = resources
        self.allies = allies
        self.enemies = enemies


class Game:
    # If True, outputs a lot of realtime game information to console log
    debug_mode: bool = False

    # List of actions that are going to be sent at the end of each game turn
    actions: List[Action] = []

    # All game field cells stored as dictionary, where their ID (index) is a key
    cells: Dict[int, Cell] = {}

    # IDs of cells where allied and enemy bases are placed
    my_base_ids: List[int] = []
    enemy_base_ids: List[int] = []

    # IDs for all crystals and eggs presented on game field
    crystal_ids: List[int] = []
    egg_ids: List[int] = []

    def __init__(self, debug_mode=False):
        self.debug_mode = debug_mode

    def initial_input(self):
        for i in range(int(input())):
            inputs = [int(j) for j in input().split()]
            self.cells[i] = Cell(i, Contents(inputs[0]), inputs[1], [k for k in inputs[2:] if k > -1])
        self.crystal_ids = list([k for k, v in self.cells.items() if v.contents == Contents.CRYSTAL])
        self.egg_ids = list([k for k, v in self.cells.items() if v.contents == Contents.EGG])
        _ = int(input())
        self.my_base_ids = [int(i) for i in input().split()]
        self.enemy_base_ids = [int(i) for i in input().split()]

    def regular_input(self):
        for i in range(len(self.cells)):
            self.cells[i].update(*[int(j) for j in input().split()])

    def report_status(self):
        Debug.log(f"My bases: {self.my_base_ids}")
        Debug.log(f"Enemy bases: {self.enemy_base_ids}")
        for cell in self.cells.values():
            Debug.log(cell)

    def regular_actions(self):
        # Write your actions here
        # ...
        self.actions.append(Action.wait())

    def regular_output(self):
        print(";".join(action.to_str() for action in self.actions))
        self.actions.clear()

    def run(self):
        self.initial_input()
        while True:
            self.regular_input()
            if self.debug_mode:
                self.report_status()
            self.regular_actions()
            self.regular_output()


Game(debug_mode=True).run()
