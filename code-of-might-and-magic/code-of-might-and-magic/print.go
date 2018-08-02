package main

import (
	"fmt"
	"os"
)

func debug(message string, a ...interface{}) {
	fmt.Fprintln(os.Stderr, fmt.Sprintf(message, a))
}

func pick(index int) {
	fmt.Println(fmt.Sprintf("PICK %d", index))
}
