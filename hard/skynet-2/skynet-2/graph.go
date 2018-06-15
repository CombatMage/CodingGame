package main

import (
	"fmt"
	"math"
)

type node int

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

func (g *graph) addLink(a, b node) {
	log(fmt.Sprintf("adding link: %d->%d", a, b))

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
	log(fmt.Sprintf("removing link: %d->%d", a, b))
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

func (g *graph) shortestPathToNodesDijkstra(start node) map[node]node {
	dist := make(map[node]float64)
	previous := make(map[node]node)

	for _, node := range g.nodes {
		dist[node] = math.Inf(1)
	}

	dist[start] = 0
	var toVisit []node
	toVisit = append(toVisit, g.nodes...)

	for len(toVisit) > 0 {
		nearest := min(toVisit, dist)
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

func (g *graph) shortestPathToNode(start, end node) []node {
	shortestPathToAllNodes := g.shortestPathToNodesDijkstra(start)
	var path []node
	path = append(path, end)
	current := end
	n, ok := shortestPathToAllNodes[current]
	for ok {
		path = append(path, n)
		current = n
		n, ok = shortestPathToAllNodes[n]
	}
	return path
}

func min(nodes []node, dist map[node]float64) node {
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
