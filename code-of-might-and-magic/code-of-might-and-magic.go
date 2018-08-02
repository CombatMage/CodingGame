package main

import (
	"fmt"
	"os"
)

type creature struct {
	cost    int
	attack  int
	defense int
}

var targetManaCurve = map[int]int{
	0: 1,
	1: 10,
	2: 6,
	3: 5,
	4: 4,
	5: 2,
	6: 1,
	7: 1,
}

type deck struct {
	cards     []creature
	manaCurve map[int]int
}

func (deck *deck) add(card creature) {
	deck.cards = append(deck.cards, card)
	if _, ok := deck.manaCurve[card.cost]; ok {
		deck.manaCurve[card.cost]++
	} else {
		deck.manaCurve[card.cost] = 0
	}
}

// selectCardForDeck selects the best matching card for target mana curve and returns index.
func selectCardForDeck(cards []creature, deck *deck) int {
	for i, card := range cards {
		if deck.manaCurve[card.cost] < targetManaCurve[card.cost] {
			deck.add(card)
			return i
		}
	}
	deck.add(cards[0])
	return 0
}


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


func debug(message string, a ...interface{}) {
	fmt.Fprintln(os.Stderr, fmt.Sprintf(message, a))
}

func pick(index int) {
	fmt.Println(fmt.Sprintf("PICK %d", index))
}
