package main

import (
	"fmt"
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
