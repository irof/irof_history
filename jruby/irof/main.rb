require 'Irof'
require 'Responder'

def prompt(irof)
  return irof.name + ':' + irof.responder_name + '> '
end

puts 'irof system :irof'
irof = Irof.new 'irof'
while true
  print '> '
  input = gets
  input.chomp!
  break if input == ''

  response = irof.dialoque input
  puts prompt(irof) + response
end

