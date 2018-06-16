package main

const explorer = "EXPLORER"
const wanderer = "WANDERER"

const minionSpwaning = 0
const minionWandering = 1

type entity struct {
	entityType             string
	id, x, y               int
	param0, param1, param2 int
}

/*
Integer param0

Explorer: sanity
Spawning minion: time before spawn
Wanderer: time before being recalled

Integer param1 :

Explorer: ignore for this league
Minion: Current state amongst those -> SPAWNING = 0 , WANDERING = 1

Integer param2 :

Explorer: ignore for this league
Minion: id of the explorer targeted by this minion. -1 if no target (occurs only on spawn)
*/
