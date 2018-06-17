package main

import (
	"encoding/json"
	"fmt"
	"os"
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
