import sys
import math

# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.

EMPTY_GAME_TURN = ["-1", "-1", "NONE"]
LEFT = "LEFT"
RIGHT = "RIGHT"
NONE = "NONE"
WAIT = "WAIT"
BLOCK = "BLOCK"

# nb_floors: number of floors
# width: width of the area
# nb_rounds: maximum number of rounds
# exit_floor: floor on which the exit is found
# exit_pos: position of the exit on its floor
# nb_total_clones: number of generated clones
# nb_additional_elevators: ignore (always zero)
# nb_elevators: number of elevators
NB_FLOORS, WIDTH, NB_ROUNDS, EXIT_FLOOR, EXIT_POSITION, NB_TOTAL_CLONES, _, NB_ELEVATOR = (
    [int(i) for i in input().split()])

ELEVATOR = {}
for i in range(NB_ELEVATOR):
    # elevator_floor: floor on which this elevator is found
    # elevator_pos: position of the elevator on its floor
    elevator_floor, elevator_pos = [int(j) for j in input().split()]
    ELEVATOR[elevator_floor] = elevator_pos

def is_right_way(position, direction, target):
    """determinate if clone is moving in the right direction to reach target"""
    assert not direction is NONE
    assert position >= 0 and position < WIDTH
    assert target >= 0 and target < WIDTH
    if position <= target and direction == RIGHT:
        return True
    if position >= target and direction == LEFT:
        return True
    return False

def get_target_on_floor(current_floor):
    """get the target position on this floor, either next elevator or exit"""
    assert current_floor >= 0 and current_floor < NB_FLOORS
    if current_floor == EXIT_FLOOR:
        return EXIT_POSITION
    return ELEVATOR[current_floor]


BLOCKED_FLOORS = []
# game loop
while True:
    INPUT = input().split()
    if INPUT == EMPTY_GAME_TURN:
        print(WAIT)
        continue

    # clone_floor: floor of the leading clone
    # clone_pos: position of the leading clone on its floor
    # direction: direction of the leading clone: LEFT or RIGHT
    CLONE_FLOOR = int(INPUT[0])
    CLONE_POS = int(INPUT[1])
    DIRECTION = INPUT[2]
    print("clone(floor: {} pos: {} dir: {})".format(
        CLONE_FLOOR, CLONE_POS, DIRECTION), file=sys.stderr)

    TARGET_ON_CURRENT_FLOOR = get_target_on_floor(CLONE_FLOOR)
    IS_RIGHT_WAY = is_right_way(CLONE_POS, DIRECTION, TARGET_ON_CURRENT_FLOOR)
    print("target: {} right way: {}".format(
        TARGET_ON_CURRENT_FLOOR, IS_RIGHT_WAY), file=sys.stderr)

    if not IS_RIGHT_WAY and CLONE_FLOOR not in BLOCKED_FLOORS:
        print(BLOCK)
        BLOCKED_FLOORS.append(CLONE_FLOOR)
    else:
        print(WAIT)
