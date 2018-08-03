package main

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
