package main

import "math"

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
