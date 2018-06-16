package main

import (
	"math"
)

func getDistance(a, b entity) float64 {
	return math.Sqrt(math.Pow(float64(a.x-b.x), 2) + math.Pow(float64(a.y-b.y), 2))
}

func getDistances(a entity, bs []entity) <-chan map[entity]float64 {
	out := make(chan map[entity]float64)
	go func() {
		result := make(map[entity]float64)
		for _, b := range bs {
			go func() {
				d := getDistance(a, b)
				result[b] = d
			}()
		}
		out <- result
	}()

	return out
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
