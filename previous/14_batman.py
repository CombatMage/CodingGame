import sys
import math

# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.

UP = "U"
DOWN = "D"
LEFT = "L"
RIGHT = "R"

# w: width of the building.
# h: height of the building.
WIDTH, HEIGHT = [int(i) for i in input().split()]
NR_TURNS = int(input())  # maximum number of turns before game over.
START_X, START_Y = [int(i) for i in input().split()]

def search_bomb(pos_x, pos_y, direction, possible_bomb_area):
    """calculate the next position to jump to and
    reduce the area where the bomb might be located"""
    new_x, new_y = pos_x, pos_y
    bomb_x_range = possible_bomb_area[0]
    bomb_y_range = possible_bomb_area[1]

    if LEFT in direction:
        start_x = possible_bomb_area[0][0]
        bomb_x_range = range(start_x, pos_x)
         # split range in half
         # go at least on step left (pos_x - 1)
        new_x = min(int((pos_x - start_x) / 2 + start_x), pos_x - 1)
    elif RIGHT in direction:
        end_x = possible_bomb_area[0][-1]
        bomb_x_range = range(pos_x + 1, end_x + 1)
        # split range in half
        # go at least on step right (pos_x + 1)
        new_x = max(int((end_x - pos_x) / 2 + pos_x + 1), pos_x + 1)

    if UP in direction:
        start_y = possible_bomb_area[1][0]
        bomb_y_range = range(start_y, pos_y)
        # split range in half
        # go at least on step up (pos_y - 1)
        new_y = min(int((pos_y - start_y) / 2 + start_y), pos_y - 1)
    elif DOWN in direction:
        end_y = possible_bomb_area[1][-1]
        bomb_y_range = range(pos_y + 1, end_y + 1)
        # split range in half
        # go at least on step down (pos_y + 1)
        new_y = max(int((end_y - pos_y) / 2 + pos_y + 1), pos_y + 1)

    return new_x, new_y, [bomb_x_range, bomb_y_range]

def run_game_loop():
    """running the game loop"""
    pos_x, pos_y = START_X, START_Y
    possible_bomb_area = [range(0, WIDTH), range(0, HEIGHT)]
    for _ in range(NR_TURNS):
        direction = input()  # the direction of the bombs (U, UR, R, DR, D, DL, L or UL)
        pos_x, pos_y, possible_bomb_area = search_bomb(
            pos_x, pos_y, direction, possible_bomb_area)

        print("{} {}".format(pos_x, pos_y))

run_game_loop()
