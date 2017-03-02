"""ghost in the shell"""
import sys

# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.

ENTITY_FACTORY = "FACTORY"
ENTITY_TROOP = "TROOP"

SELF = 1
NEUTRAL = 0
ENEMY = -1

class Trooper:
    """Cyborgs ready for battle"""
    entity_id = -1
    ownership = NEUTRAL
    src_id = -1
    dst_id = -1
    count = 0
    time_till_arival = -1

    def __init__(self, entity_id, ownership, src_id, dst_id, count, time_till_arival):
        self.entity_id = entity_id
        self.ownership = ownership
        self.src_id = src_id
        self.dst_id = dst_id
        self.count = count
        self.time_till_arival = time_till_arival


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

    def enemy_has_production(self):
        """check if the enemy has factories remaining with a production > 0
        """
        enemy_has_production = filter(
            lambda factory: factory.ownership != SELF and factory.production > 0,
            self.factories)
        return enemy_has_production

    def __str__(self):
        return "FactoryNetwork(factories: {}, neighbors: {})".format(self.factories, self.neighbors)

def read_game_status(factory_network, troops):
    """reading current game status from stdin"""
    entity_count = int(input())  # the number of entities (e.g. factories and troops)
    troops.clear()
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
        elif entity_type == ENTITY_TROOP:
            trooper = Trooper(entity_id, arg_1, arg_2, arg_3, arg_4, arg_5)
            troops[entity_id] = trooper


def calc_attack_move(factory_network):
    """Calculate if we can attack"""
    attacker = get_factory_for_attack(factory_network)
    print("possible attacker: " + str(attacker), file=sys.stderr)
    if attacker:
        target = get_factory_to_attack(factory_network, attacker)
        print("possible target: " + str(target), file=sys.stderr)
        if target:
            return "MOVE {} {} {}".format(
                attacker.entity_id, target.entity_id, int(attacker.number_of_cyborgs / 2))
    return None


def get_factory_for_attack(factory_network):
    """returns the factory from which to send troops
    Rules:
    Factory.ownership == self
    """
    possible_attacker = []
    for factory in factory_network.factories:
        if factory.ownership == SELF:
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
    enemy_has_production = factory_network.enemy_has_production()
    for factory_id, travel_time in neighbors:
        factory = factory_network.factories[factory_id]
        if factory.ownership == SELF:
            continue
        # factory is useless and more important targets are available
        if factory.production == 0 and enemy_has_production:
            continue
        defenders = factory.number_of_cyborgs + factory.production * travel_time
        if defenders < attacking_factory.number_of_cyborgs / 2:
            possible_targets.append((factory, defenders, travel_time))

    if possible_targets:
        primary_targets = []
        secondary_targets = []
        remaining_targets = []
        for factory, defenders, travel_time in possible_targets:
            if factory.production == 3:
                primary_targets.append((factory, defenders, travel_time))
            elif factory.production == 2:
                secondary_targets.append((factory, defenders, travel_time))
            else:
                remaining_targets.append((factory, defenders, travel_time))
        to_attack = []
        if primary_targets:
            to_attack = primary_targets
        elif secondary_targets:
            to_attack = secondary_targets
        elif remaining_targets:
            to_attack = remaining_targets
        else:
            return None

        weak_defenders = sorted(to_attack, key=lambda x: x[1])
        return weak_defenders[0][0]
    return None


def calc_preparation_move(factory_network):
    """if we do not attack, we are concentrating our troops"""
    my_factories = filter(
        lambda factory: factory.ownership == SELF,
        factory_network.factories)
    sorted_factories = sorted(
        my_factories,
        key=lambda factory: factory.number_of_cyborgs,
        reverse=True)

    if len(sorted_factories) >= 2:
        most_troops = sorted_factories[0]

        return "MOVE {} {} {}".format(
            sorted_factories[1].entity_id,
            most_troops.entity_id,
            int(sorted_factories[1].number_of_cyborgs))
    return None


TROOPS = {}
FACTORY_NETWORK = FactoryNetwork()
print(str(FACTORY_NETWORK), file=sys.stderr)

def game_turn():
    """single gameturn"""
    read_game_status(FACTORY_NETWORK, TROOPS)
    attack_move = calc_attack_move(FACTORY_NETWORK)
    if attack_move:
        print(attack_move, file=sys.stderr)
        print(attack_move)
        return
    prepare_move = calc_preparation_move(FACTORY_NETWORK)
    if prepare_move:
        print(prepare_move, file=sys.stderr)
        print(prepare_move)
        return
    print("WAIT")

while True:
    game_turn()
