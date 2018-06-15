package main

import (
	"fmt"
	"math"
	"os"
	"encoding/json"
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


func log(message string) {
	fmt.Fprintln(os.Stderr, message)
}


func main() {
	var nodeCount, linkCount, exitsCount int
	fmt.Scan(&nodeCount, &linkCount, &exitsCount)

	graph := graph{}
	for i := 0; i < linkCount; i++ {
		var a, b node
		fmt.Scan(&a, &b)
		graph.addLink(a, b)

	}
	for i := 0; i < exitsCount; i++ {
		var exit node
		fmt.Scan(&exit)
		graph.exists = append(graph.exists, exit)
	}

	for {
		var agent node
		fmt.Scan(&agent)
		log(fmt.Sprintf("Agent is at %d", agent))

		lengthOfShortestPath := math.Inf(1)
		var shortestPath []node
		for _, exit := range graph.exists {
			exitPath := graph.shortestPathToNode(exit, agent)
			if math.IsInf(lengthOfShortestPath, 1) || len(shortestPath) > len(exitPath) {
				lengthOfShortestPath = float64(len(shortestPath))
				shortestPath = exitPath
			}

		}
		str, _ := json.MarshalIndent(shortestPath, "", "  ")
		log("Shortest path to exit is" + string(str) + " with length " + fmt.Sprintf("%f", lengthOfShortestPath))

		exit := shortestPath[len(shortestPath)-1]
		b := shortestPath[len(shortestPath)-2]

		graph.removeLink(exit, b)
		if len(graph.links[exit]) == 0 {
			log(fmt.Sprintf("Node %d is severed, removing from graph", exit))
			graph.exists = remove(graph.exists, exit)
			graph.nodes = remove(graph.nodes, exit)
			delete(graph.links, exit)
		}

		fmt.Println(fmt.Sprintf("%d %d", exit, b))
	}
}
