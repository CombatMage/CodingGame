package main

import (
	"fmt"
	"os"
)

type creature struct {
	instanceID int
	cost       int
	attack     int
	defense    int
}

func filter(cards []creature, test func(creature) bool) (ret []creature) {
	for _, s := range cards {
		if test(s) {
			ret = append(ret, s)
		}
	}
	return
}

func weakest(cards []creature) (int, creature) {
	weakestCard := creature{
		attack:  9999,
		defense: 9999,
	}
	index := -1

	for i, card := range cards {
		if (card.attack + card.defense) <= (weakestCard.attack + weakestCard.defense) {
			weakestCard = card
			index = i
		}
	}

	return index, weakestCard
}

func strongest(cards []creature) (int, creature) {
	strongestCard := creature{
		attack:  0,
		defense: 0,
	}
	index := -1

	for i, card := range cards {
		if (card.attack + card.defense) >= (strongestCard.attack + strongestCard.defense) {
			strongestCard = card
			index = i
		}
	}

	return index, strongestCard
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

func selectCardsToSummon(mana int, hand []creature) []creature {
	var toSummon []creature

	enoughMana := filter(hand, func(card creature) bool {
		return card.cost <= mana
	})
	for len(enoughMana) > 0 {
		toSummon = append(toSummon, enoughMana[0])
		mana -= enoughMana[0].cost
		hand = append(hand[:0], hand[1:]...)

		enoughMana = filter(hand, func(card creature) bool {
			return card.cost <= mana
		})
	}

	return toSummon
}

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
				instanceID: instanceID,
				cost:       cost,
				attack:     attack,
				defense:    defense,
			})
		}
		selectedCard := selectCardForDeck(cards, &deck)
		pick(selectedCard)
	}

	for round := 0; ; round++ {
		debug("battle phase round %d", round)

		myself := player{}
		enemy := player{}

		var playerDeck, playerRune int
		fmt.Scan(&myself.health, &myself.mana, &playerDeck, &playerRune)
		fmt.Scan(&enemy.health, &enemy.mana, &playerDeck, &playerRune)
		debug("SELF : health:%d, mana:%d", myself.health, myself.mana)
		debug("ENEMY: health:%d, mana:%d", enemy.health, enemy.mana)

		var opponentHand int
		fmt.Scan(&opponentHand)

		var cardCount int
		fmt.Scan(&cardCount)

		var cardsInMyHand []creature
		var cardsOnMySide []creature
		var cardsOnEnemySide []creature
		for i := 0; i < cardCount; i++ {
			var cardNumber, instanceID, location, cardType, cost, attack, defense int
			var abilities string
			var myHealthChange, opponentHealthChange, cardDraw int
			fmt.Scan(&cardNumber, &instanceID, &location, &cardType, &cost, &attack, &defense, &abilities, &myHealthChange, &opponentHealthChange, &cardDraw)
			card := creature{
				instanceID: instanceID,
				cost:       cost,
				attack:     attack,
				defense:    defense,
			}
			switch location {
			case 0:
				cardsInMyHand = append(cardsInMyHand, card)
			case 1:
				cardsOnMySide = append(cardsOnMySide, card)
			case -1:
				cardsOnEnemySide = append(cardsOnEnemySide, card)
			}
		}

		toSummon := selectCardsToSummon(myself.mana, cardsInMyHand)
		debug("have %d cards to summon", len(toSummon))

		/*
			for len(cardsOnMySide)+len(toSummon) > 6 && len(cardsOnEnemySide) > 0 {
				debug("clear my board")
				a, myCard := weakest(cardsOnMySide)
				b, enemyCard := strongest(cardsOnEnemySide)

				attack(myCard, enemyCard.instanceID)
				debug("attack %d with %d", enemyCard, myCard)

				// calculate result
				if enemyCard.attack >= myCard.defense {
					// remove my card
					cardsOnMySide = append(cardsOnMySide[:a], cardsOnMySide[a+1:]...)
				} else if myCard.attack >= enemyCard.defense {
					// remove enemy card
					cardsOnEnemySide = append(cardsOnEnemySide[:b], cardsOnMySide[b+1:]...)
				} else {
					myCard.defense -= enemyCard.attack
					enemyCard.defense -= myCard.attack
				}
			}*/

		for _, card := range cardsOnMySide {
			attack(card, -1)
		}
		for _, card := range toSummon {
			if len(cardsOnMySide) <= 6 {
				summon(card)
				cardsOnMySide = append(cardsOnMySide, card)
			} else {
				break
			}
		}

		fmt.Println("")
	}
}

type player struct {
	health int
	mana   int
}

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
