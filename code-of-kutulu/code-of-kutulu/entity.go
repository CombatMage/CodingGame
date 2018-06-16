package main

import "fmt"

const explorer = "EXPLORER"
const wanderer = "WANDERER"

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
