package main

import "fmt"

//import "os"

func main() {
	deck := deck{
		manaCurve: make(map[int]int),
	}
	for round := 0; round < 30; round++ {
		debug("draw phase round %d", round)
		for i := 0; i < 2; i++ {
			var playerHealth, playerMana, playerDeck, playerRune int
			fmt.Scan(&playerHealth, &playerMana, &playerDeck, &playerRune)
		}
		var opponentHand int
		fmt.Scan(&opponentHand)

		var cardCount int
		fmt.Scan(&cardCount)

		var cards []creature
		for i := 0; i < cardCount; i++ {
			var cardNumber, instanceID, location, cardType, cost, attack, defense int
			var abilities string
			var myHealthChange, opponentHealthChange, cardDraw int
			fmt.Scan(&cardNumber, &instanceID, &location, &cardType, &cost, &attack, &defense, &abilities, &myHealthChange, &opponentHealthChange, &cardDraw)
			cards = append(cards, creature{
				cost:    cost,
				attack:  attack,
				defense: defense,
			})
		}
		selectedCard := selectCardForDeck(cards, &deck)
		pick(selectedCard)
	}

	for round := 0; ; round++ {
		debug("battle phase round %d", round)

		for i := 0; i < 2; i++ {
			var playerHealth, playerMana, playerDeck, playerRune int
			fmt.Scan(&playerHealth, &playerMana, &playerDeck, &playerRune)
		}
		var opponentHand int
		fmt.Scan(&opponentHand)

		var cardCount int
		fmt.Scan(&cardCount)

		for i := 0; i < cardCount; i++ {
			var cardNumber, instanceId, location, cardType, cost, attack, defense int
			var abilities string
			var myHealthChange, opponentHealthChange, cardDraw int
			fmt.Scan(&cardNumber, &instanceId, &location, &cardType, &cost, &attack, &defense, &abilities, &myHealthChange, &opponentHealthChange, &cardDraw)
		}

		// fmt.Fprintln(os.Stderr, "Debug messages...")
		fmt.Println("PASS") // Write action to stdout
	}
}
