#                                          MMMMMMMMMMM
#                                       MMMMMMMMMMMMMMMMM
#                                   NMMMMMMMMMMMMMMMMMMMMMMMM
#                                 MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#                                MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN
#                                MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#                               MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#                               MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMD
#                              DMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#                              MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#                              MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#                             MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#                             MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN
#                            MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN
#                           MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN
#                           MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN
#      NM                  MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#      MMMMM              MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#       MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#        MMMMMMMMMMMMMMMMMMMMMMMMMM8MMMMMMMMMIMMMMM8,. ...........OMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#           MMMMMMMMMMMMMMMMMMMMMMM ..N. .....~MMMM...............:MMMMNMMMMMMMMMMMMMMMMMMMMMMM
#             NMMMMMMMMMMMMMMMMMMMMM.....:..DMMMMMNZ Z.... .......M$MMMMMMMMMMMMMMMMMMMMMMMMMMM
#                 MMMMMMMMMMNMMMMMMM....... 7=MMMMMMO....Z .......MM7MMMMMMMMMMMMMMMMMMMMMMMMM
#                    MMMMMMMMMMMMMMMMM  Z...MMMZ .. .,M..,........MMMMMMMMMMMMMMMMMMMMMMMMMMMM
#                        MMMMMM.......DOM ....N7..................MMMMMMMMMMMMMMMMMMMMMMMMMMM
#                           MMM....... M. ... .  ... ..............M...$MMMMMMMMMMMMMMMMMMMM
#                             ........... ........~. ..............M..=....+MMMMMMMMMMMMMM
#                             ......+.NMI~........ . ..............M.,.I...MMMMMMMMMMMMMMN
#                             ......$... ...... O..................,.....$MMMMMMMMMMMMN
#                             .....M.......... M M.. .............. 8  .OMMMMMMMMMMMN
#                              ..=7I,,.,,IMI...M.................. ..MMMMMMMMMMM
#                              ....DMMMMMMMMMMMMMMMO..............D...MMMMMMMMM
#                               .MMMMMMMMMMMMMMDDMM:,N..............DMMMMMMMMMMM
#                               NMMMMMNMM8 . .... ...,~............  MMMMMMMMM
#                               MMMMM,:......::~..M8M8MM...............MMMMMM
#                               MMMM ... . .........,MM..............MMMMMO$
#                               MMMMM... =.=. .. . . MM ....... . ...MMMMMMM
#                                NMMMMMMMMMM?  ..O.?NM7 ....... ......MMMMMM
#                                 NMMMMMMMMMMMMMMMMM........  $ . ...MMMNMMM
#                                  MMMMMMMMMMMMMMM.........,, ......MMMMMMMM
#                                   OMMMMMMMM8 , .. .. .,N.... ...:MMMMMMMMMMN
#                                    MMMMMMMM?N. ...~MD.:MNI8MMMMMMMMMMMMMMMMMN
#                               MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN
#                            NMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN
#                           MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMN
#                        MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#                     MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#                    NMMMMMMMMMMMMNMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
#                   MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM

def lower_bits(char)
  bits = char.ord.to_s(2)
  bits.length < 7 ? '0' + bits : bits[bits.length - 7..bits.length]
end

def string_to_bits(string)
  string.chars.map { |char| lower_bits(char) }.join
end

# Extension for String class to provide Chuck Norris encoding
class String

  ENCODED_1 = '0'.freeze
  ENCODED_0 = '00'.freeze

  def encoded_like_a_pro
    chuck_norris = ''
    last_processed = -1
    string_to_bits(self).each_char do |bit|
      if bit == last_processed
        chuck_norris += '0'
      else
        chuck_norris += " #{bit == '1' ? ENCODED_1 : ENCODED_0} 0"
      end
      last_processed = bit
    end
    chuck_norris.strip
  end
end

message = gets.chomp

puts message.encoded_like_a_pro
