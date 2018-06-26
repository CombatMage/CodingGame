package main

import (
	"math"
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

