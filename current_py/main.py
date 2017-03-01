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
    ownership = NEUTRAL
    number_of_cyborgs = 0
    production = 0

    def __str__(self):
        return "Factory(ownership: {}, number_of_cyborgs: {}, production:{})".format(
            self.ownership, self.number_of_cyborgs, self.ownership)

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
        self.factories = [Factory] * factory_count
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

        if entity_type is ENTITY_FACTORY:
            factory = factory_network.factories[entity_id]
            factory.ownership = arg_1
            factory.number_of_cyborgs = arg_2
            factory.production = arg_3


FACTORY_NETWORK = FactoryNetwork()
print(str(FACTORY_NETWORK), file=sys.stderr)

while True:
    read_game_status(FACTORY_NETWORK)

    print("WAIT")
