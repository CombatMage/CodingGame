import sys
import math

# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.
# ---
# Hint: You can use the debug stream to print initialTX and initialTY, if Thor seems not follow your orders.

# light_x: the X position of the light of power
# light_y: the Y position of the light of power
# initial_tx: Thor's starting X position
# initial_ty: Thor's starting Y position
light_x, light_y, initial_tx, initial_ty = [int(i) for i in input().split()]

current_x = initial_tx
current_y = initial_ty
while True:
    remaining_turns = int(input())  # The remaining amount of turns Thor can move. Do not remove this line.

    diff_x = light_x - current_x
    diff_y = light_y - current_y
    print("diff_x: " + str(diff_x), file=sys.stderr)
    print("diff_y: " + str(diff_y), file=sys.stderr)

    direction = ""
    if (diff_y > 0):
        direction += "S"
        current_y += 1
    elif diff_y < 0:
        direction += "N"
        current_y -= 1

    if (diff_x > 0):
        direction += "E"
        current_x += 1
    elif diff_x < 0:
        direction += "W"
        current_x -= 1

    print("direction: " + direction, file=sys.stderr)
    print(direction)
    