"""ghost in the shell"""
import sys

# Auto-generated code below aims at helping you parse
# the standard input according to the problem statement.

ENTITY_FACTORY = "FACTORY"
ENTITY_TROOP = "TROOP"
ENTITY_BOMB = "BOMB"

SELF = 1
NEUTRAL = 0
ENEMY = -1

class Bomb:
    """deployed bombs"""
    entity_id = -1
    ownership = NEUTRAL
    src_id = -1
    dst_id = -1
    time_till_arival = -1

    def __init__(self, entity_id, ownership, src_id, dst_id, time_till_arival):
        self.entity_id = entity_id
        self.ownership = ownership
        self.src_id = src_id
        self.dst_id = dst_id
        self.time_till_arival = time_till_arival

    def __str__(self):
        return "Bomb(entity_id: {}, ownership: {}, src: {}, dst: {}, tta: {})".format(
            self.entity_id, self.ownership, self.src_id, self.dst_id, self.time_till_arival)

    def __repr__(self):
        return str(self)


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

    def get_dispatched_troops(self, troops, ownership):
        """check wether troops are dispatched to this factory
        return number of dispatched troops
        """
        pass

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
        """check if the enemy has factories remaining with a production > 0"""
        enemy_production_facilities = list(filter(
            lambda factory: factory.ownership != SELF and factory.production > 0,
            self.factories))
        return len(enemy_production_facilities) > 0

    def my_factories(self):
        """return factories with ownership self"""
        return list(filter(lambda factory: factory.ownership == SELF, self.factories))

    def enemy_factories(self):
        """return factories with ownership enemy"""
        return list(filter(lambda factory: factory.ownership == ENEMY, self.factories))

    def neutral_factories(self):
        """return factory with ownership enemy and most troops"""
        return list(filter(lambda factory: factory.ownership == NEUTRAL, self.factories))

    def enemy_factories_most_troops(self):
        """return factories with ownership enemy"""
        return sorted(
            self.enemy_factories(),
            key=lambda x: x.number_of_cyborgs,
            reverse=True)

    def get_nearest_factory(self, target, ownership):
        """get nearest factory to target with ownership"""
        neighbors = list(filter(
            lambda x: self.factories[x[0]].ownership == ownership,
            self.neighbors[target.entity_id]))
        print("get_nearest_factory: " + str(neighbors), file=sys.stderr)
        if not neighbors:
            return
        return self.factories[min(neighbors, key=lambda x: x[1])[0]]

    def __str__(self):
        return "FactoryNetwork(factories: {}, neighbors: {})".format(self.factories, self.neighbors)


def format_move(src, dst, units):
    """format troop move"""
    src.number_of_cyborgs -= units
    return "MOVE {} {} {}".format(src.entity_id, dst.entity_id, units)


def format_bomb(src, dst):
    """format bomb deployment"""
    return "BOMB {} {}".format(src.entity_id, dst.entity_id)


def read_game_status(factory_network, troops, bombs):
    """reading current game status from stdin"""
    entity_count = int(input())  # the number of entities (e.g. factories and troops)
    troops.clear()
    bombs.clear()
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
            troops.append(trooper)
        elif entity_type == ENTITY_BOMB:
            bomb = Bomb(entity_id, arg_1, arg_2, arg_3, arg_4)
            bombs.append(bomb)


def calc_attack_move(factory_network):
    """Calculate if we can attack"""
    attacker = get_factory_for_attack(factory_network)
    print("possible attacker: " + str(attacker), file=sys.stderr)
    if attacker:
        target = get_factory_to_attack(factory_network, attacker)
        print("possible target: " + str(target), file=sys.stderr)
        if target:
            return format_move(attacker, target, int(attacker.number_of_cyborgs / 2))
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
    if not enemy_has_production:
        print("enemy has no production, domination is archived", file=sys.stderr)

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
    my_factories = factory_network.my_factories()
    sorted_factories = sorted(
        my_factories,
        key=lambda factory: factory.number_of_cyborgs,
        reverse=True)

    if len(sorted_factories) >= 2:
        most_troops = sorted_factories[0]

        return format_move(
            sorted_factories[1], most_troops, int(sorted_factories[1].number_of_cyborgs))
    return None


def calc_defend_move(factory_network):
    """calculte defensive move"""
    defensive_moves = []
    my_factories = sorted(
        factory_network.my_factories(),
        key=lambda factory: factory.production,
        reverse=True)
    count_defensive_moves = 3 if len(my_factories) > 3 else 1
    move_to = my_factories[0:count_defensive_moves]
    take_from = my_factories[count_defensive_moves:]

    troops_available = 0
    for factory in take_from:
        troops_available += factory.number_of_cyborgs

    troops_per_factory = int(troops_available / len(move_to))
    troops_per_factory_dispatch = [troops_per_factory] * len(move_to)
    index = 0
    for factory in take_from:
        available_troops = factory.number_of_cyborgs
        dst = move_to[index]
        defensive_moves.append(format_move(factory, dst, available_troops))
        troops_per_factory_dispatch[index] -= available_troops
        if troops_per_factory_dispatch[index] < 0:
            index += 1
    return defensive_moves


def check_rush_move(factory_network):
    """Check if we should perform a rush move
    Rush is done by sending every trooper and a bomb
    to enemy's most productive factory
    """
    neutral_factories_by_production = sorted(
        factory_network.neutral_factories(),
        key=lambda x: x.production,
        reverse=True)
    print("rush: " + str(neutral_factories_by_production), file=sys.stderr)
    if neutral_factories_by_production and neutral_factories_by_production[0].production == 0:
        print("there are no neutral factories worth fighting for, perform rush", file=sys.stderr)
        return True
    return False


BOMBS = []
TROOPS = []
FACTORY_NETWORK = FactoryNetwork()
print(str(FACTORY_NETWORK), file=sys.stderr)


def game_turn(turn):
    """single gameturn"""
    print("reading status for turn " + str(turn), file=sys.stderr)
    read_game_status(FACTORY_NETWORK, TROOPS, BOMBS)

    my_bombs_in_play = list(filter(lambda x: x.ownership == SELF, BOMBS))
    print("my bombs in play: " + str(my_bombs_in_play), file=sys.stderr)
    perform_rush = check_rush_move(FACTORY_NETWORK)
    print("perform rush: " + str(perform_rush), file=sys.stderr)
    if perform_rush and len(my_bombs_in_play) == 0:
        targets = FACTORY_NETWORK.enemy_factories_most_troops()
        if targets:
            target = targets[0]
            src = FACTORY_NETWORK.get_nearest_factory(target, SELF)
            if src:
                print("perform rush: deploy bomb on {} from {}".format(
                    target, src), file=sys.stderr)
                BOMBS.append(Bomb(-1, SELF, src.entity_id, target.entity_id, -1))
                print(format_bomb(src, target))
                return
    elif perform_rush and len(my_bombs_in_play) == 1:
        targets = FACTORY_NETWORK.enemy_factories_most_troops()
        if targets:
            target = targets[0]
            src = FACTORY_NETWORK.get_nearest_factory(target, SELF)
            print("perform rush: deploy troops on {} from {}".format(target, src), file=sys.stderr)
            if src:
                print(format_move(src, target, src.number_of_cyborgs))
                return

    print("reached", file=sys.stderr)

    all_moves = []
    attack_move = calc_attack_move(FACTORY_NETWORK)
    if attack_move:
        all_moves.append(attack_move)
    #     print(attack_move, file=sys.stderr)
    #     print(attack_move)
    #     return

    defensive_moves = calc_defend_move(FACTORY_NETWORK)
    if defensive_moves:
        all_moves.append(defensive_moves)
    #     print(str(defensive_moves), file=sys.stderr)
    #     print(";".join(defensive_moves))
    #     return
    print("all available moves " + str(all_moves), file=sys.stderr)
    if all_moves:
        print(";".join(defensive_moves))
    else:
        print("WAIT")


def game_loop():
    """play game till victory or death"""
    turn_count = 0
    while True:
        game_turn(turn_count)
        turn_count += 1

game_loop()
