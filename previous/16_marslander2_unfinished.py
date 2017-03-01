"""marslander landing program"""
import sys
import math
import doctest

POS_X = "POS_X"
POS_Y = "POS_Y"
H_SPEED = "H_SPEED"
V_SPEED = "V_SPEED"
ROTATE = "ROTATE"
POWER = "POWER"

LEFT = "LEFT"
RIGHT = "RIGHT"
ASCEND = "ASCEND"
DESCEND = "DESCEND"

MAX_V_SPEED = 40
MAX_H_SPEED = 20


def read_input():
    """read input either from test data or stdin"""
    return input()

def scan_mars_surface():
    """scanning mars surface"""
    surface_points = []
    height_map = {}
    surface_n = int(read_input())  # the number of points used to draw the surface of Mars.
    for _ in range(surface_n):
        land_x, land_y = [int(j) for j in read_input().split()]
        surface_points.append(land_x)
        height_map[land_x] = land_y
    return surface_points, height_map


def read_marslander_status(s_input):
    """reading the current status of the mars lander
    >>> p = read_marslander_status("5000 2500 -50 0 1000 90 0")
    >>> p == {"POS_X": 5000, "POS_Y": 2500, "H_SPEED": -50, "V_SPEED": 0, "ROTATE": 90, "POWER": 0}
    True
    """
    pos_x, pos_y, h_speed, v_speed, _, rotate, power = [int(i) for i in s_input.split()]
    return {
        POS_X : pos_x,
        POS_Y : pos_y,
        H_SPEED : h_speed,
        V_SPEED : v_speed,
        ROTATE : rotate,
        POWER : power
    }


def get_bearing_for_target(landing_area, height, current_x, current_y):
    """get the current bearing for designated landing area
    >>> get_bearing_for_target(range(0, 50), 50, 100, 20)
    "ASCEND"
    >>> get_bearing_for_target(range(0, 50), 50, 100, 50)
    "LEFT"
    >>> get_bearing_for_target(range(0, 50), 50, 100, 51)
    "LEFT"
    """
    print("get_bearing_for_target({}, {}, {}, {})".format(
        landing_area, height, current_x, current_y), file=sys.stderr)
    # assert we are not under the surface
    assert current_x not in landing_area or current_y > height
    if current_y < height: # gaining height is most important
        return ASCEND
    elif current_x in landing_area:
        return DESCEND
    elif current_x < landing_area[0]:
        return RIGHT
    elif current_x > landing_area[-1]:
        return LEFT
    else:
        raise "Invalid state, mars rover has already crashed"

def get_nearest_landing_spot(surface_points, height_map, current_x):
    """returns the nearest landingspot with a flat surface as []
    >>> get_nearest_landing_spot([0, 10, 20], {0: 50, 10: 100, 20: 100}, 5)
    range(10, 20)
    """
    assert isinstance(surface_points, list)
    assert len(surface_points) >= 2
    assert isinstance(height_map, dict)
    assert len(height_map.keys()) == len(surface_points)
    assert isinstance(current_x, int)

    flat_areas = []
    for i in range(len(surface_points) - 1):
        x_point = surface_points[i]
        x_point_next = surface_points[i + 1]
        height = height_map[x_point]
        height_next = height_map[x_point_next]
        if height == height_next:
            flat_areas.append(range(x_point, x_point_next))

    min_distance = math.inf
    target = None
    for area in flat_areas:
        if current_x in area:
            return area
        distance = min(abs(area[0] - current_x), abs(area[-1] - current_x))
        if distance < min_distance:
            target = area
    return target


def get_angle_bearing(bearing):
    """returns the required navigation params for bearing
    >>> get_navigation_params(RIGHT)
    -45
    >>> get_navigation_params(LEFT)
    45
    >>> get_navigation_params(ASCEND)
    0
    """
    if bearing is RIGHT:
        return -45
    elif bearing is LEFT:
        return 45
    elif bearing is ASCEND or bearing is DESCEND:
        return 0


def get_bearing_for_angle(angle):
    """returns the bearing for given angle, if the angle is 0 it returns DESCEND
    >>> get_bearing_for_angle(-45)
    RIGHT
    >>> get_bearing_for_angle(45)
    LEFT
    >>> get_bearing_for_angle(45)
    DESCEND
    """
    assert angle >= -45
    assert angle <= 45
    if angle < 0:
        return RIGHT
    elif angle > 0:
        return LEFT
    else:
        return DESCEND


def stabilize_horizontal():
    """stabilize mars lander horizontal (h speed is 0)"""
    status = read_marslander_status(read_input())
    while abs(status[H_SPEED]) > 0:
        print("stabilize_horizontal: current h speed is " + str(status[H_SPEED]), file=sys.stderr)
        h_speed_to_fast = abs(status[H_SPEED]) > MAX_H_SPEED
        v_speed_to_fast = abs(status[V_SPEED]) > MAX_V_SPEED
        drift_left = status[H_SPEED] < 0
        drift_right = status[H_SPEED] > 0

        if v_speed_to_fast:
            print("0 4")
            stabilize_v_speed()
        elif drift_left:
            if h_speed_to_fast:
                print("-45 4")
            else:
                print("-30 4")
        elif drift_right:
            if h_speed_to_fast:
                print("45 4")
            else:
                print("30 4")
        else:
            print("0 4")
        status = read_marslander_status(read_input())
    print("0 4")
    print("stabilize_horizontal: done", file=sys.stderr)


def stabilize_v_speed():
    """stabilize vertical speed by giving thrust until v_speed is smaller than MAX_V_SPEED"""
    status = read_marslander_status(read_input())
    print("stabilize_v_speed: current: " + str(status), file=sys.stderr)
    while status[V_SPEED] > MAX_V_SPEED:
        print("0 4")
        status = read_marslander_status(read_input())
    print("0 4")
    print("stabilize_v_speed: done", file=sys.stderr)


def decrease_altitude(altitude):
    """reduce altitude but make sure that current v_speed does not exceed max"""
    assert altitude >= 0
    status = read_marslander_status(read_input())
    print("decrease_altitude: target: {}, status: {}".format(altitude, status), file=sys.stderr)
    while status[POS_Y] > altitude:
        if abs(status[V_SPEED]) > MAX_V_SPEED:
            print("0 4")
        else:
            print("0 0")
        status = read_marslander_status(read_input())
    print("0 0")
    print("decrease_altitude: done", file=sys.stderr)


def fly_left(target_x):
    """fly to the left"""
    assert target_x >= 0
    status = read_marslander_status(read_input())
    print("fly_left: target: {}, status: {}".format(target_x, status), file=sys.stderr)
    while status[POS_X] > target_x:
        h_speed_to_fast = abs(status[H_SPEED]) > MAX_H_SPEED
        v_speed_to_fast = abs(status[V_SPEED]) > MAX_V_SPEED
        if not h_speed_to_fast:
            print("45 4")
        elif v_speed_to_fast:
            print("0 4")
            stabilize_v_speed()
        else:
            print("0 0")
        status = read_marslander_status(read_input())
    print("0 4")
    stabilize_v_speed()
    print("fly_left: done", file=sys.stderr)


def fly_right(target_x):
    """fly to the left"""
    assert target_x >= 0
    status = read_marslander_status(read_input())
    print("fly_right: target: {}, status: {}".format(target_x, status), file=sys.stderr)
    while status[POS_X] < target_x:
        h_speed_to_fast = abs(status[H_SPEED]) > MAX_H_SPEED
        v_speed_to_fast = abs(status[V_SPEED]) > MAX_V_SPEED
        if not h_speed_to_fast:
            print("-45 4")
        elif v_speed_to_fast:
            print("0 4")
            stabilize_v_speed()
        else:
            print("0 0")
        status = read_marslander_status(read_input())
    print("0 4")
    stabilize_v_speed()
    print("fly_right: done", file=sys.stderr)


def landing_phase(surface_points, height_map):
    """initiate mars lander landing manouver"""
    landing_area = None
    while True:
        status = read_marslander_status(read_input())
        print("current status is " + str(status), file=sys.stderr)

        if not landing_area:
            landing_area = get_nearest_landing_spot(surface_points, height_map, status[POS_X])
            print("landing area is: " + str(landing_area), file=sys.stderr)

        height = height_map[landing_area[0]]
        bearing = get_bearing_for_target(landing_area, height, status[POS_X], status[POS_Y])

        print("bearing to target {} is {}".format(landing_area, bearing), file=sys.stderr)
        print("0 4")
        stabilize_horizontal()
        if bearing is LEFT:
            fly_left(landing_area[-1])
        elif bearing is RIGHT:
            fly_right(landing_area[0])
        elif bearing is DESCEND:
            decrease_altitude(0)

#doctest.testmod()

MARS_SURFACE, HEIGH_MAP = scan_mars_surface()
landing_phase(MARS_SURFACE, HEIGH_MAP)
#decrease_altitude(0)
