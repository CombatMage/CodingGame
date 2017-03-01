# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.

n = gets.to_i # the number of temperatures to analyse

if n.zero?
  puts 0
else
  temperatures = gets.chomp.split(' ').map(&:to_i) # the n temperatures expressed as integers ranging from -273 to 5526
  closest_to_zero = 5527

  temperatures.each do |temp|
    abs_value = temp.abs
    if abs_value < closest_to_zero.abs
      closest_to_zero = temp
    elsif abs_value == closest_to_zero.abs && temp > 0 # ties are broken by favouring the positive input
      closest_to_zero = temp
    end
  end

  puts closest_to_zero
end
