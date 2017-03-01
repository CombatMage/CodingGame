import sys
import math
import doctest

FILE = open("input.txt")
def get_input():
    """reading test data"""
    #return input()
    return FILE.readline()


class Card:
    """card in war game: (value)(color)
    where value is (sorted from weak to strong): 2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K, A
    and color: D, H, C, S """
    def __init__(self, card):
        assert isinstance(card, str)
        self.card = card.strip()

    def raw_value(self):
        """return the raw card value, used for comparision"""
        raw_value = self.card[: -1]
        if raw_value == "J":
            raw_value = "11"
        elif raw_value == "Q":
            raw_value = "12"
        elif raw_value == "K":
            raw_value = "13"
        elif raw_value == "A":
            raw_value = "14"
        return raw_value

    def __eq__(self, other):
        assert isinstance(other, Card)
        return self.raw_value() == other.raw_value()

    def __lt__(self, other):
        assert isinstance(other, Card)
        return self.raw_value() < other.raw_value()

    def __str__(self):
        return "Card({})".format(self.card)

    def __repr__(self):
        return str(self)


def create_stack():
    """Stack of cards"""
    stack = []

    def pop():
        """remove the first card of the stack and returns is"""
        assert not is_empty()
        return stack.pop()

    def push_bottom(card):
        """put card under the stack"""
        return stack.insert(0, card)

    def is_empty():
        """check if the card stack is empty"""
        return len(stack) == 0

    def get_str():
        """print current stack to stdout"""
        return str(stack)

    return pop, push_bottom, is_empty, get_str


STACK_P1_POP, STACK_P1_PUSH, STACK_P1_EMPTY, PRINT_P1 = create_stack()
STACK_P2_POP, STACK_P2_PUSH, STACK_P2_EMPTY, PRINT_P2 = create_stack()


def build_stacks():
    """read from stdin to build card stacks for both players"""
    nb_cards_p1 = int(get_input())  # the number of cards for player 1
    for _ in range(nb_cards_p1):
        STACK_P1_PUSH(Card(get_input()))  # the n cards of player 1
    nb_cards_p2 = int(get_input())  # the number of cards for player 2
    for _ in range(nb_cards_p2):
        STACK_P2_PUSH(Card(get_input()))  # the m cards of player 2


def add_card_to_player_stack(cards_1, cards_2, add):
    """adds the given card stacks the stack of 1 player by calling add for every card"""
    reversed(cards_1)
    reversed(cards_2)
    map(add, cards_1)
    map(add, cards_2)


def resolve_war(cards_1, cards_2):
    """if tie was draw a war is started, resolve it's outcome"""
    assert cards_1
    assert cards_2
    for _ in range(3):
        if STACK_P1_EMPTY() or STACK_P2_EMPTY():
            return
        cards_1.append(STACK_P1_POP())
        cards_2.append(STACK_P2_POP())
    if STACK_P1_EMPTY() or STACK_P2_EMPTY():
        return
    card_faceup_1 = STACK_P1_POP()
    card_faceup_2 = STACK_P2_POP()
    cards_1.append(card_faceup_1)
    cards_2.append(card_faceup_2)
    if card_faceup_1 > card_faceup_2:
        add_card_to_player_stack(cards_1, cards_2, STACK_P1_PUSH)
    elif card_faceup_2 > card_faceup_1:
        add_card_to_player_stack(cards_1, cards_2, STACK_P2_PUSH)
    else:
        resolve_war(cards_1, cards_2)

def check_winner():
    """check who will win this war by evaluating both stacks"""
    game_round = 0
    while not STACK_P1_EMPTY() and not STACK_P2_EMPTY():
        card_faceup_1 = STACK_P1_POP()
        card_faceup_2 = STACK_P2_POP()
        game_round += 1

        print("P1: " + PRINT_P1(), file=sys.stderr)
        print("P2: " + PRINT_P2(), file=sys.stderr)

        if card_faceup_1 > card_faceup_2:
            STACK_P1_PUSH(card_faceup_1)
            STACK_P1_PUSH(card_faceup_2)
        elif card_faceup_2 > card_faceup_1:
            STACK_P2_PUSH(card_faceup_1)
            STACK_P2_PUSH(card_faceup_2)
        else:
            game_round += 1
            resolve_war([card_faceup_1], [card_faceup_2])

    if not STACK_P1_EMPTY() and STACK_P2_EMPTY():
        return "1", game_round
    elif not STACK_P2_EMPTY() and STACK_P1_EMPTY():
        return "2", game_round
    return "PAT", game_round

build_stacks()
WINNER, ROUNDS_PLAYED = check_winner()
print("{} {}".format(WINNER, ROUNDS_PLAYED))
