package main

import "fmt"

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
