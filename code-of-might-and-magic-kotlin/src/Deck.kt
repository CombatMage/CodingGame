class Deck{
	val cards = ArrayList<Card>()
	val manaCurve = HashMap<Int, Int>()

	private fun addCard(card: Card) {
		debug("addCard $card")
		cards.add(card)
		manaCurve[card.cost] = manaCurve.getOrDefault(card.cost, 0) + 1
	}

	fun selectCardToAdd(cards: List<Card>): Int{
		if (cards.isEmpty()) throw IllegalArgumentException("given card list is empty")

		val cardsTmp = cards.toMutableList().sortedBy { it.cost }
		cardsTmp.forEach { card ->
			if (this.manaCurve.getOrDefault(card.cost, 0) < TARGET_MANA_CURVE.getOrDefault(card.cost, 0)) {
				this.addCard(card)
				return cards.indexOf(card)
			}
		}
		this.addCard(cardsTmp[0])
		return cards.indexOf(cardsTmp[0])
	}
}