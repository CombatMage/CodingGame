def diff(a, b)
  (a - b).abs
end

horses = []

n_horse = gets.to_i
n_horse.times do
  horses << gets.to_i
end

horses.sort!
smallest_diff = horses[-1] # last element is the largest and diff can't be larger

horses.each_index do |index|
  break if (index + 1) >= n_horse
  d = diff(horses[index], horses[index + 1])
  smallest_diff = d unless d > smallest_diff
end

puts smallest_diff.to_s
