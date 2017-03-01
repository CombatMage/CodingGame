# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.

# Defibrillator
class Defibrillator
  attr_reader :name
  def initialize(input_line)
    fields = input_line.split(';')
    @id = fields[0]
    @name = fields[1]
    @address = fields[2]
    @phone = fields[3]
    @lon = fields[4].tr(',', '.').to_f
    @lat = fields[5].tr(',', '.').to_f
  end

  def distance_to(lat, lon)
    return nil if @lat.nil? || @lon.nil?
    x = (lon - @lon) * Math.cos((@lat - lat) / 2)
    y = lat - @lat
    Math.sqrt(x**2 + y**2) * 6371
  end
end

defibrillators = []

user_lon = gets.chomp.tr(',', '.').to_f
user_lat = gets.chomp.tr(',', '.').to_f
n        = gets.to_i
n.times do
  input = gets.chomp
  defibrillators << Defibrillator.new(input)
end

nearest_distance = nil
nearest = nil

defibrillators.each do |defibrillator|
  distance = defibrillator.distance_to(user_lat, user_lon)
  if nearest_distance.nil? || distance < nearest_distance
    nearest = defibrillator
    nearest_distance = distance
  end
end
# Write an action using puts
# To debug: STDERR.puts "Debug messages..."

puts nearest.name
