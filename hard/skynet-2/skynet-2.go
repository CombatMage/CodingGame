package main

import (
	"math"
	"fmt"
	"os"
)


func getNodeToRemove(g *graph, agent node) (a, b node) {
	lengthOfShortestPath := math.Inf(1)
	var shortestPath []node
	for _, exit := range g.exists {
		exitPath := g.shortestPathToNode(exit, agent)
		if math.IsInf(lengthOfShortestPath, 1) || len(shortestPath) > len(exitPath) {
			lengthOfShortestPath = float64(len(shortestPath))
			shortestPath = exitPath
		}

	}
	a = shortestPath[len(shortestPath)-1]
	b = shortestPath[len(shortestPath)-2]
	return a, b
}



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

func (g *graph) shortestPathToNodeWithDijkstraGiven(start, end node, shortestPathToAllNodes map[node]node) []node {
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

		a, b := getNodeToRemove(&graph, agent)

		graph.removeLink(a, b)
		if len(graph.links[a]) == 0 {
			log(fmt.Sprintf("Node %d is severed, removing from graph", a))
			graph.exists = remove(graph.exists, a)
			graph.nodes = remove(graph.nodes, a)
			delete(graph.links, a)
		}

		fmt.Println(fmt.Sprintf("%d %d", a, b))
	}
}


// getScoreOfSituation evaluates the given game situation and returns a score.
// Larger values are better.
func getScoreOfSituation(g *graph, exits []node, agentPosition node) float64 {
	var reachableExits []node
	for _, node := range exits {
		if _, ok := g.links[node]; ok {
			reachableExits = append(reachableExits, node)
		}
	}

	distanceToExit := calculateDistanceToNodes(g, agentPosition, reachableExits)
	longestPath := len(g.nodes) - 1

	squareSum := 0.0
	for _, d := range distanceToExit {
		squareSum += math.Pow(float64(longestPath-d), 2.0)
	}
	squareSum /= float64(len(exits))
	squareSum = math.Sqrt(squareSum)

	return squareSum
}

func calculateDistanceToNodes(g *graph, start node, targetList []node) map[node]int {
	shortestPathToAllNodes := g.shortestPathToNodesDijkstra(start)
	result := make(map[node]int)
	for _, target := range targetList {
		path := g.shortestPathToNodeWithDijkstraGiven(start, target, shortestPathToAllNodes)
		result[target] = len(path)
	}
	return result
}
