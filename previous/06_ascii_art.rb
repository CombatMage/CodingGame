# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.

def in_range?(char)
  char.ord >= 'A'.ord && char.ord <= 'Z'.ord
end

def index_of_char(char)
  if in_range?(char)
    char.ord - 'A'.ord
  else
    26 # index of question mark
  end
end

char_width = gets.to_i # width of characters
char_heigh = gets.to_i # heigh of characters
text_to_print = gets.chomp # text to print
text = []
char_heigh.times do
  input = gets.chomp
  text_line = ''
  text_to_print.each_char do |char|
    index = index_of_char(char.upcase) * char_width
    text_line << input[index...(index + char_width)]
  end
  text << text_line
end

text.each { |line| puts line }
