STDOUT.sync = true # DO NOT REMOVE
# Don't let the machines win. You are humanity's last hope...

EMPTY_NODE = '-1 -1'.freeze
EMPTY_CELL = '.'.freeze

class Node
  attr_reader :position
  attr_accessor :right_neighbor
  attr_accessor :bottom_neighbor

  def initialize(x, y)
    @position = [x, y]
  end

  def to_s
    "#{@position[0]} #{@position[1]}"
  end

  def print
    right = @right_neighbor.nil? ? EMPTY_NODE : @right_neighbor.to_s
    bottom = @bottom_neighbor.nil? ? EMPTY_NODE : @bottom_neighbor.to_s
    "#{self} #{right} #{bottom}"
  end
end


# Testdata
input = File.open('test_input.txt')
# End Testdata

# reading input
width = input.gets.to_i # the number of cells on the X axis
height = input.gets.to_i # the number of cells on the Y axis
STDERR.puts width
STDERR.puts height

all_nodes = []
nodes_in_row = {}
height.times do |y|
  nodes_in_line = []
  line = input.gets.chomp # width characters, each either 0 or .
  STDERR.puts line
  width.times do |x|
    nodes_in_row[x] = [] if nodes_in_row[x].nil?
    next if line[x] == EMPTY_CELL

    new_node = Node.new(x, y)
    previous_node = nodes_in_line.last
    previous_node.right_neighbor = new_node unless previous_node.nil?
    nodes_in_line << new_node
    nodes_in_row[x] << new_node
    all_nodes << new_node
  end
end

nodes_in_row.each do |_, value|
  value.each_cons(2) { |a, b| a.bottom_neighbor = b }
end

all_nodes.each do |node|
  puts node.print
end

# Write an action using puts
# To debug: STDERR.puts "Debug messages..."


# Three coordinates: a node, its right neighbor, its bottom neighbor
# One line per node. Six integers on each line:   x1  y1  x2  y2  x3  y3
# 
# Where:
# (x1,y1) the coordinates of a node
# (x2,y2) the coordinates of the closest neighbor on the right of the node
# (x3,y3) the coordinates of the closest bottom neighbor
# If there is no neighbor, the coordinates should be -1 -1

