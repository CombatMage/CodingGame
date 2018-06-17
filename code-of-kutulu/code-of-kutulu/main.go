package main

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

func main() {
	lightRemaining := 3
	// healRemaining := 2

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
				if nextSection != wall {
					b := node{x: x + 1, y: y}
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
				if nextSection != wall {
					b := node{x: x, y: y + 1}
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
				wanderers = append(wanderers, newEntity)
			} else if newEntity.entityType == slasher { // ad slasher to wanderers for the time being
				wanderers = append(wanderers, newEntity)
			}
		}

		myPos := node{x: myself.x, y: myself.y}
		// no monsters around, move to nearest friend

		if len(wanderers) == 0 {
			pathToNearestExplorer := g.pathToNearestEntity(myPos, theOthers)
			if len(pathToNearestExplorer) > 0 {
				target := pathToNearestExplorer[len(pathToNearestExplorer)-1]
				moveToNode(target)
				continue
			}
		}

		actionTaken := false
		pathWanderers := make(map[entity]map[node]node)
		adjacent := g.links[myPos]
		for _, w := range wanderers {
			pathWanderers[w] = g.shortestPathDijkstra(node{x: w.x, y: w.y})
		}

		// heal to yell
		distanceNearestMonster := len(g.pathToNearestEntityDijkstra(myPos, pathWanderers))
		distanceNearestExplorer := len(g.pathToNearestEntity(myPos, theOthers))
		debug(fmt.Sprintf("nearest monster %d", distanceNearestMonster))
		debug(fmt.Sprintf("nearest explorer %d", distanceNearestExplorer))
		if distanceNearestMonster >= 2 && distanceNearestExplorer <= 2 {
			yell()
			actionTaken = true
		} /*else if distanceNearestMonster >= 2 && healRemaining > 0 && myself.sanity() < 200 {
			healRemaining--
			heal()
			actionTaken = true
		}*/

		// run away
		for _, neighbor := range adjacent {
			d := len(g.pathToNearestEntityDijkstra(neighbor, pathWanderers))
			if d > distanceNearestMonster {
				actionTaken = true
				moveToNode(neighbor)
				break
			}
		}

		// debug("wait")
		if !actionTaken {
			if lightRemaining > 0 && distanceNearestMonster < 6 {
				lightRemaining--
				light()
			} else {
				wait()
			}
		}
	}
}
