package main

import (
	"math"
)

const wall = "#"
const spawnPoint = "w"
const empty = "."

type node struct {
	x, y int
}

type graph struct {
	nodes  []node
	exists []node
	links  map[node][]node
}

func (g *graph) containsNode(n node) bool {
	for _, ng := range g.nodes {
		if ng == n {
			return true
		}
	}
	return false
}

func (g *graph) addNode(a node) {
	//debug(fmt.Sprintf("Adding node %d,%d", a.x, a.y))
	if !g.containsNode(a) {
		g.nodes = append(g.nodes, a)
	}
}

func (g *graph) addLink(a, b node) {
	//debug(fmt.Sprintf("Adding link %d,%d->%d,%d", a.x, a.y, b.x, b.y))
	if !g.containsNode(a) {
		g.nodes = append(g.nodes, a)
	}
	if !g.containsNode(b) {
		g.nodes = append(g.nodes, b)
	}

	if g.links == nil {
		g.links = make(map[node][]node)
	}

	g.links[a] = append(g.links[a], b)
	g.links[b] = append(g.links[b], a)
}

func (g *graph) removeLink(a, b node) {
	g.links[a] = remove(g.links[a], b)
	g.links[b] = remove(g.links[b], a)
}

func remove(nodes []node, node node) []node {
	contains := false
	var index int
	for i, n := range nodes {
		if n == node {
			index = i
			contains = true
		}
	}
	if !contains {
		return nodes
	}
	nodes[index] = nodes[len(nodes)-1]
	return nodes[:len(nodes)-1]
}

func (g *graph) shortestPathDijkstra(start node) map[node]node {
	dist := make(map[node]float64)
	previous := make(map[node]node)

	for _, node := range g.nodes {
		dist[node] = math.Inf(1)
	}

	dist[start] = 0
	var toVisit []node
	toVisit = append(toVisit, g.nodes...)
	for len(toVisit) > 0 {
		nearest := minDistanceOfNode(toVisit, dist)
		if math.IsInf(dist[nearest], 1) {
			break
		}
		toVisit = remove(toVisit, nearest)

		for _, neighbor := range g.links[nearest] {
			alt := dist[nearest] + 1
			if alt < dist[neighbor] {
				dist[neighbor] = alt
				previous[neighbor] = nearest
			}
		}
	}
	return previous
}

func (g *graph) shortestPathBetweenNode(start, end node) []node {
	// debug(fmt.Sprintf("Shortest Path for link %d,%d->%d,%d", start.x, start.y, end.x, end.y))
	shortestPathToAllNodes := g.shortestPathDijkstra(start)
	var path []node
	current := end
	path = append(path, end)
	n, ok := shortestPathToAllNodes[current]
	for ok {
		path = append(path, n)
		current = n
		n, ok = shortestPathToAllNodes[n]
	}
	return path
}

func (g *graph) shortestPathBetweenNodesDijkstra(start, end node, pathFromStart map[node]node) []node {
	// debug(fmt.Sprintf("Shortest Path for link %d,%d->%d,%d", start.x, start.y, end.x, end.y))
	var path []node
	current := end
	path = append(path, end)
	n, ok := pathFromStart[current]
	for ok {
		path = append(path, n)
		current = n
		n, ok = pathFromStart[n]
	}
	return path
}

func (g *graph) pathToNearestEntity(pos node, entities []entity) []node {
	shortestDistance := infinity
	var shortestPath []node
	for _, e := range entities {
		p := g.shortestPathBetweenNode(pos, node{x: e.x, y: e.y})
		if len(p) < shortestDistance {
			shortestDistance = len(p)
			shortestPath = p
		}
	}
	return shortestPath
}

func (g *graph) pathToNearestEntityDijkstra(pos node, entitiesWithPath map[entity]map[node]node) []node {
	shortestDistance := infinity
	var shortestPath []node
	for e, paths := range entitiesWithPath {
		p := g.shortestPathBetweenNodesDijkstra(node{x: e.x, y: e.y}, pos, paths)
		if len(p) < shortestDistance {
			shortestDistance = len(p)
			shortestPath = p
		}
	}
	return shortestPath
}

func minDistanceOfNode(nodes []node, dist map[node]float64) node {
	var nearestNode node
	var min float64
	min = math.Inf(1)
	for _, n := range nodes {
		distance := dist[n]
		if distance < min {
			min = distance
			nearestNode = n
		}
	}
	return nearestNode
}
