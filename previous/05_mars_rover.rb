STDOUT.sync = true # DO NOT REMOVE
# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.

# example_input = 2500 2500 0 0 500 0 0 	(X Y hSpeed vSpeed fuel rotate power)

@height_map = {}
@surface_n = gets.to_i # the number of points used to draw the surface of Mars.
@surface_n.times do
  # land_x: X coordinate of a surface point. (0 to 6999)
  # land_y: Y coordinate of a surface point. By linking all the points together in a sequential fashion, you form the surface of Mars.
  land_x, land_y = gets.split(' ').collect(&:to_i)
  height_map[land_x] = land_y
end

REQUIRED_LANDING_SPEED = 40
MAX_THRUST = 4

def is_thrust_sufficient(remaining_height, current_speed, current_thrust)
  while remaining_height >= 0
    if current_speed == 0 && remaining_height > 0
      return false
    elsif current_speed <= REQUIRED_LANDING_SPEED
      return true
    end
    remaining_height -= current_speed
    current_speed -= current_thrust
  end
  return current_speed <= REQUIRED_LANDING_SPEED
end

def get_thrust(remaining_height, current_speed, currentThrust)
  if is_thrust_sufficient(remaining_height, current_speed, current_thrust - 1)
    return currentThrust - 1
  elsif is_thrust_sufficient(remaining_height, current_speed, current_thrust)
    return current_thrust
  else 
    return current_thrust + 1
end

thrust = 0
rotation = 0

# game loop
loop do
  # h_speed: the horizontal speed (in m/s), can be negative.
  # v_speed: the vertical speed (in m/s), can be negative.
  # fuel: the quantity of remaining fuel in liters.
  # rotate: the rotation angle in degrees (-90 to 90).
  # power: the thrust power (0 to 4).
  x, y, h_speed, v_speed, fuel, rotate, power = gets.split(' ').collect { |x| x.to_i }

  remaining_height = y - @height_map[x]
  STDERR.puts "remaining_height: " + remaining_height.to_s
  STDERR.puts "current_speed: " + v_speed.to_s
  STDERR.puts "current_thrust: " + thrust.to_s

  thrust = get_thrust(remaining_height, v_speed, thrust)

  # 2 integers: rotate power. rotate is the desired rotation angle (should be 0 for level 1), power is the desired thrust power (0 to 4).
  puts rotation.to_s + " " + thrust.to_s
end
