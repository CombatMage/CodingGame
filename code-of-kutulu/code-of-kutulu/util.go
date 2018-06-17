package main

import (
	"fmt"
	"math"
	"time"
)

const infinity = 999999

func startClock() {
	start := time.Now()
	go func() {
		for {
			elapsed := time.Since(start)
			debug(fmt.Sprintf("elapsed %d", elapsed))
		}
	}()
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
