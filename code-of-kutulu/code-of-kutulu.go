package main

import (
	"strings"
	"time"
	"encoding/json"
	"fmt"
	"os"
	"math"
	"bufio"
)


func debug(msg string) {
	fmt.Fprintln(os.Stderr, msg)
}

func debugPrintGraph(g graph) {
	str, _ := json.MarshalIndent(g, "", "  ")
	debug(string(str))
}

func debugPrintPath(p []node) {
	fmt.Fprintln(os.Stderr, p)
}

func debugPrintDist(d map[node]float64) {
	for n, d := range d {
		debug(fmt.Sprintf("%d,%d : %f", n.x, n.y, d))
	}
}


const explorer = "EXPLORER"
const wanderer = "WANDERER"
const slasher = "SLASHER"

const minionSpawning = 0
const minionWandering = 1

type entity struct {
	entityType             string
	id, x, y               int
	param0, param1, param2 int
}

func (e entity) sanity() int {
	return e.param0
}

func (e entity) spawningTime() int {
	return e.param0
}

func (e entity) recallTime() int {
	return e.param0
}

func (e entity) minionState() int {
	return e.param1
}

func (e entity) targetedExplorer() int {
	return e.param2
}

func move(x, y int) {
	fmt.Println(fmt.Sprintf("MOVE %d %d", x, y))
}

func moveToNode(n node) {
	move(n.x, n.y)
}

func wait() {
	fmt.Println("WAIT")
}

func light() {
	fmt.Println("LIGHT")
}

func heal() {
	fmt.Println("PLAN")
}

func yell() {
	fmt.Println("YELL")
}


const wall = "#"
const spawnPoint = "w"
const empty = "."

type node struct {
	x, y int
}

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

func (g *graph) addNode(a node) {
	//debug(fmt.Sprintf("Adding node %d,%d", a.x, a.y))
	if !g.containsNode(a) {
		g.nodes = append(g.nodes, a)
	}
}

func (g *graph) addLink(a, b node) {
	//debug(fmt.Sprintf("Adding link %d,%d->%d,%d", a.x, a.y, b.x, b.y))
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

func (g *graph) shortestPathDijkstra(start node) map[node]node {
	dist := make(map[node]float64)
	previous := make(map[node]node)

	for _, node := range g.nodes {
		dist[node] = math.Inf(1)
	}

	dist[start] = 0
	var toVisit []node
	toVisit = append(toVisit, g.nodes...)
	for len(toVisit) > 0 {
		nearest := minDistanceOfNode(toVisit, dist)
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

func (g *graph) shortestPathBetweenNode(start, end node) []node {
	// debug(fmt.Sprintf("Shortest Path for link %d,%d->%d,%d", start.x, start.y, end.x, end.y))
	shortestPathToAllNodes := g.shortestPathDijkstra(start)
	var path []node
	current := end
	path = append(path, end)
	n, ok := shortestPathToAllNodes[current]
	for ok {
		path = append(path, n)
		current = n
		n, ok = shortestPathToAllNodes[n]
	}
	return path
}

func (g *graph) shortestPathBetweenNodesDijkstra(start, end node, pathFromStart map[node]node) []node {
	// debug(fmt.Sprintf("Shortest Path for link %d,%d->%d,%d", start.x, start.y, end.x, end.y))
	var path []node
	current := end
	path = append(path, end)
	n, ok := pathFromStart[current]
	for ok {
		path = append(path, n)
		current = n
		n, ok = pathFromStart[n]
	}
	return path
}

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

func (g *graph) pathToNearestEntityDijkstra(pos node, entitiesWithPath map[entity]map[node]node) []node {
	shortestDistance := infinity
	var shortestPath []node
	for e, paths := range entitiesWithPath {
		p := g.shortestPathBetweenNodesDijkstra(node{x: e.x, y: e.y}, pos, paths)
		if len(p) < shortestDistance {
			shortestDistance = len(p)
			shortestPath = p
		}
	}
	return shortestPath
}

func minDistanceOfNode(nodes []node, dist map[node]float64) node {
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
