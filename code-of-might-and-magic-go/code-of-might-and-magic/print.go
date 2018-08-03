package main

import (
	"fmt"
	"os"
)

func debug(message string, a ...interface{}) {
	fmt.Fprintln(os.Stderr, fmt.Sprintf(message, a...))
}

func pick(index int) {
	fmt.Println(fmt.Sprintf("PICK %d", index))
}

func summon(card creature) {
	fmt.Print(fmt.Sprintf("SUMMON %d;", card.instanceID))
}

func attack(card creature, target int) {
	fmt.Print(fmt.Sprintf("ATTACK %d %d;", card.instanceID, target))
}
