# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.

type_map = {}

n = gets.to_i # Number of elements which make up the association table.
q = gets.to_i # Number Q of file names to be analyzed.
n.times do
  # ext: file extension
  # mt: MIME type.
  ext, mt = gets.split(' ')
  type_map[ext.downcase] = mt
end

files = []
q.times do
  files << gets.chomp # One file name per line.
end

files.each do |file|
  ext = if file[-1] == '.' || !file.include?('.')
          ''
        else
          file.split('.').last.downcase
        end
  mime = type_map[ext]
  puts mime.nil? ? 'UNKNOWN' : mime
end
