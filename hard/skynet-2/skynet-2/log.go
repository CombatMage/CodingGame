package main

import (
	"fmt"
	"os"
)

func log(message string) {
	fmt.Fprintln(os.Stderr, message)
}
