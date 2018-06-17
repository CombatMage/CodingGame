package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

func main() {
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Buffer(make([]byte, 1000000), 1000000)

	var width int
	scanner.Scan()
	fmt.Sscan(scanner.Text(), &width)

	var height int
	scanner.Scan()
	fmt.Sscan(scanner.Text(), &height)

	var rows []string
	for y := 0; y < height; y++ {
		scanner.Scan()
		rows = append(rows, scanner.Text())
	}

	g := graph{}
	for y := 0; y < height-1; y++ {
		row := strings.Split(rows[y], "")
		for x := 0; x < width-1; x++ {
			section := row[x]
			nextSection := row[x+1]
			if section != wall {
				a := node{x: x, y: y}
				g.addNode(a)
				if nextSection != wall {
					b := node{x: x + 1, y: y}
					g.addNode(b)
					g.addLink(a, b)
				}
			}
		}
		nextRow := strings.Split(rows[y+1], "")
		for x := 0; x < width; x++ {
			section := row[x]
			nextSection := nextRow[x]
			if section != wall {
				a := node{x: x, y: y}
				g.addNode(a)
				if nextSection != wall {
					b := node{x: x, y: y + 1}
					g.addNode(b)
					g.addLink(a, b)
				}
			}
		}
	}

	// sanityLossLonely: how much sanity you lose every turn when alone, always 3 until wood 1
	// sanityLossGroup: how much sanity you lose every turn when near another player, always 1 until wood 1
	// wandererSpawnTime: how many turns the wanderer take to spawn, always 3 until wood 1
	// wandererLifeTime: how many turns the wanderer is on map after spawning, always 40 until wood 1
	var sanityLossLonely, sanityLossGroup, wandererSpawnTime, wandererLifeTime int
	scanner.Scan()
	fmt.Sscan(scanner.Text(), &sanityLossLonely, &sanityLossGroup, &wandererSpawnTime, &wandererLifeTime)

	for {
		// entityCount: the first given entity corresponds to your explorer
		var entityCount int
		scanner.Scan()
		fmt.Sscan(scanner.Text(), &entityCount)

		var myself entity
		var theOthers, wanderers []entity

		for i := 0; i < entityCount; i++ {
			newEntity := entity{}
			scanner.Scan()
			fmt.Sscan(
				scanner.Text(),
				&newEntity.entityType,
				&newEntity.id,
				&newEntity.x,
				&newEntity.y,
				&newEntity.param0,
				&newEntity.param1,
				&newEntity.param2)
			if i == 0 {
				myself = newEntity
			} else if newEntity.entityType == explorer {
				theOthers = append(theOthers, newEntity)
			} else if newEntity.entityType == wanderer {
				wanderers = append(theOthers, newEntity)
			}
		}

		myPos := node{x: myself.x, y: myself.y}
		// no monsters around, move to nearest friend
		if len(wanderers) == 0 {
			debug("no monsters around")
			pathToNearestExplorer := g.pathToNearestEntity(myPos, theOthers)
			if len(pathToNearestExplorer) > 0 {
				target := pathToNearestExplorer[len(pathToNearestExplorer)-1]
				debug(fmt.Sprintf("move to %d,%d", target.x, target.y))
				moveToNode(target)
				continue
			}
		}

		// run away
		debug("run away")
		pathWanderers := make(map[entity]map[node]node)
		adjacent := g.links[myPos]
		for _, w := range wanderers {
			pathWanderers[w] = g.shortestPathDijkstra(node{x: w.x, y: w.y})
		}

		distanceNearestMonster := len(g.pathToNearestEntityDijkstra(myPos, pathWanderers))
		debug(fmt.Sprintf("distance to nearest monster %d", distanceNearestMonster))

		for _, neighbor := range adjacent {
			d := len(g.pathToNearestEntityDijkstra(neighbor, pathWanderers))
			if d > distanceNearestMonster {
				debug(fmt.Sprintf("move to %d,%d", neighbor.x, neighbor.y))
				moveToNode(neighbor)
				break
			}
		}

		debug("wait")
		wait()
	}
}
