"""ghost in the shell"""
import sys
import math

# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.

ENTITY_FACTORY = "FACTORY"
ENTITY_TROOP = "TROOP"

SELF = 1
NEUTRAL = 0
ENEMY = -1

class Factory:
    """cyborg producing factory"""
    entity_id = -1
    ownership = NEUTRAL
    number_of_cyborgs = 0
    production = 0

    def __str__(self):
        return "Factory(entity_id: {}, ownership: {}, number_of_cyborgs: {}, production:{})".format(
            self.entity_id, self.ownership, self.number_of_cyborgs, self.ownership)

    def __repr__(self):
        return str(self)


class FactoryNetwork:
    """network of factories in graph-like structure"""
    factories = []
    neighbors = {}

    def __init__(self):
        """init network by reading input"""
        factory_count = int(input())
        link_count = int(input())  # the number of links between factories
        print("__init__({}, {})".format(factory_count, link_count), file=sys.stderr)

        self.factories = [None] * factory_count
        for i in range(factory_count):
            self.factories[i] = Factory()
            self.factories[i].entity_id = i
        for _ in range(link_count):
            factory_1, factory_2, distance = [int(j) for j in input().split()]
            self.add_link(factory_1, factory_2, distance)

    def add_link(self, factory_a, factory_b, travel_time):
        """adding link between to factories"""
        print("add_link({}, {}, {})".format(factory_a, factory_b, travel_time), file=sys.stderr)
        if not factory_a in self.neighbors:
            self.neighbors[factory_a] = []
        if not factory_b in self.neighbors:
            self.neighbors[factory_b] = []

        self.neighbors[factory_a].append([factory_b, travel_time])
        self.neighbors[factory_b].append([factory_a, travel_time])

    def __str__(self):
        return "FactoryNetwork(factories: {}, neighbors: {})".format(self.factories, self.neighbors)

def read_game_status(factory_network):
    """reading current game status from stdin"""
    entity_count = int(input())  # the number of entities (e.g. factories and troops)
    for _ in range(entity_count):
        entity_id, entity_type, arg_1, arg_2, arg_3, arg_4, arg_5 = input().split()
        entity_id = int(entity_id)
        arg_1 = int(arg_1)
        arg_2 = int(arg_2)
        arg_3 = int(arg_3)
        arg_4 = int(arg_4)
        arg_5 = int(arg_5)

        if entity_type == ENTITY_FACTORY:
            factory = factory_network.factories[entity_id]
            factory.ownership = arg_1
            factory.number_of_cyborgs = arg_2
            factory.production = arg_3


def get_factory_for_attack(factory_network):
    """returns the factory from which to send troops
    Rules:
    Factory.ownership == self
    Factory.number_of_cyborgs > 20
    """
    possible_attacker = []
    for factory in factory_network.factories:
        print("factory to evaluate: " + str(factory), file=sys.stderr)
        if factory.ownership == SELF and factory.number_of_cyborgs > 20:
            possible_attacker.append(factory)

    print("possible attackers: " + str(possible_attacker), file=sys.stderr)
    if possible_attacker:
        return max(possible_attacker, key=lambda x: x.number_of_cyborgs)
    return None


def get_factory_to_attack(factory_network, attacking_factory):
    """returns the factory to attacking_factory
    Rules:
    Factory.ownership != self
    Factory.number_of_cyborgs + (Factory.production * travel_time)
        < attacking_factory.number_of_cyborgs / 2
    """
    possible_targets = []
    neighbors = factory_network.neighbors[attacking_factory.entity_id]
    for factory_id, travel_time in neighbors:
        factory = factory_network.factories[factory_id]
        if factory.ownership == SELF:
            continue
        defenders = factory.number_of_cyborgs + factory.production * travel_time
        if defenders < attacking_factory.number_of_cyborgs / 2:
            possible_targets.append((factory, defenders))

    if possible_targets:
        return min(possible_targets, key=lambda factory_defenders: factory_defenders[1])[0]
    return None


FACTORY_NETWORK = FactoryNetwork()
print(str(FACTORY_NETWORK), file=sys.stderr)

def game_turn():
    """single gameturn"""
    read_game_status(FACTORY_NETWORK)
    attacker = get_factory_for_attack(FACTORY_NETWORK)
    print("possible attacker: " + str(attacker), file=sys.stderr)
    if attacker:
        target = get_factory_to_attack(FACTORY_NETWORK, attacker)
        print("possible target: " + str(target), file=sys.stderr)
        if target:
            print("MOVE {} {} {}".format(
                attacker.entity_id, target.entity_id, int(attacker.number_of_cyborgs / 2)))
            return
    print("WAIT")

while True:
    game_turn()
