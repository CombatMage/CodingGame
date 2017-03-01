import sys
import math

def graph():
    """graph of nodes"""
    nodes = []
    neighbors = {}
    exit_nodes = []

    def add_link(node_a, node_b):
        """add link to graph"""
        print("adding link {} {}".format(node_a, node_b), file=sys.stderr)
        if not node_a in nodes:
            nodes.append(node_a)
        if not node_b in nodes:
            nodes.append(node_b)
        if not node_a in neighbors:
            neighbors[node_a] = []
        if not node_b in neighbors:
            neighbors[node_b] = []
        neighbors[node_a].append(node_b)
        neighbors[node_b].append(node_a)

    def remove_link(node_a, node_b):
        """remove link from graph"""
        print("remove link {} {}".format(node_a, node_b), file=sys.stderr)
        assert node_b in neighbors[node_a]
        assert node_a in neighbors[node_b]
        neighbors[node_a].remove(node_b)
        neighbors[node_b].remove(node_a)

    def dijkstra(start):
        """calculate distance to all nodes using dijkstra's algorithm"""
        assert start in nodes
        dist = {}
        previous = {}
        for node in nodes:
            dist[node] = math.inf
            previous[node] = None
        dist[start] = 0
        to_visit = []
        to_visit += nodes
        while to_visit:
            nearest = min(to_visit, key=lambda x: dist[x])
            if math.isinf(dist[nearest]):
                break
            to_visit.remove(nearest)
            for neighbor in neighbors[nearest]:
                alt = dist[nearest] + 1
                if alt < dist[neighbor]:
                    dist[neighbor] = alt
                    previous[neighbor] = nearest
        return previous

    return add_link, remove_link, dijkstra, exit_nodes

ADD_LINK, REMOVE_LINK, DISJKSTRA, EXIT_NODES = graph()

def get_path_to_target(predecessor, target):
    """return the shortest path from given dictionary of predecessor"""
    path = []
    node = target
    while not predecessor[node] is None:
        path.insert(0, node)
        node = predecessor[node]
    return path

def shortest_list(lists):
    """helper to return the shortest list from given list of lists
    >>> shortest_list([[1, 2, 3], [1]])
    [1]
    >>> shortest_list([[1, 2, 3], []])
    []
    """
    assert not lists is None
    shortest = None
    for entry in lists:
        if not shortest or len(shortest) > len(entry):
            shortest = entry
    return shortest


# n: the total number of nodes in the level, including the gateways
# l: the number of links
# e: the number of exit gateways
NODE_COUNT, LINK_COUNT, EXIT_COUNT = [int(i) for i in input().split()]
for i in range(LINK_COUNT):
    # n1: N1 and N2 defines a link between these nodes
    n1, n2 = [int(j) for j in input().split()]
    ADD_LINK(n1, n2)

for i in range(EXIT_COUNT):
    ei = int(input())  # the index of a gateway node
    EXIT_NODES.append(ei)


# game loop
print("prevent agent from {}".format(EXIT_NODES), file=sys.stderr)
while True:
    INPUT = input()
    if not INPUT:
        continue
    AGENT_POSITION = int(INPUT)
    PREDECESSOR_DICT = DISJKSTRA(AGENT_POSITION)


    EXIT_ROUTES = []
    for exit_node in EXIT_NODES:
        PATH = get_path_to_target(PREDECESSOR_DICT, exit_node)
        if PATH:
            EXIT_ROUTES.append(PATH)

    print("exit routes {}".format(EXIT_ROUTES), file=sys.stderr)
    SHORTEST_EXIT = shortest_list(EXIT_ROUTES)
    SHORTEST_EXIT.insert(0, AGENT_POSITION)

    NODE_A, NODE_B = SHORTEST_EXIT.pop(), SHORTEST_EXIT.pop()
    REMOVE_LINK(NODE_B, NODE_A)
    print("{} {}".format(NODE_A, NODE_B))
