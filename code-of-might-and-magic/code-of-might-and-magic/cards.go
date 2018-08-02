package main

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
