package main

import (
	"math"
)

const infinity = 999999

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

func max(a map[entity]float64) entity {
	max := -1.0
	var maxA entity
	for k, v := range a {
		if v > max {
			maxA = k
			max = v
		}
	}
	return maxA
}

func min(a map[entity]float64) entity {
	min := math.Inf(1)
	var minA entity
	for k, v := range a {
		if v < min {
			minA = k
			min = v
		}
	}
	return minA
}
