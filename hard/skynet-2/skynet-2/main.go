package main

import (
	"encoding/json"
	"fmt"
	"math"
)

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
