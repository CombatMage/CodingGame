package main

import (
	"fmt"
	"os"
)

func debug(msg string) {
	fmt.Fprintln(os.Stderr, msg)
}
