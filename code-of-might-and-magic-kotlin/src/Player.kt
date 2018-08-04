import java.util.*

data class Player (
		var health: Int,
		var mana: Int,
		val playerDeck: Int,
		val playerRune: Int
) {
	fun getCardsToPlay(cardsInHand: List<Card>): List<Card> {
		val toPlay = ArrayList<Card>()
		val cards = cardsInHand.toMutableList()

		var enoughMana = cards.filter { it.cost <= this.mana }
		while (enoughMana.isNotEmpty()) {
			toPlay.add(enoughMana[0])
			this.mana -= enoughMana[0].cost
			cards.remove(enoughMana[0])
			enoughMana = cards.filter { it.cost <= this.mana }
		}
		return toPlay
	}

	companion object {
		fun fromScanner(input: Scanner): Player {
			val playerHealth = input.nextInt()
			val playerMana = input.nextInt()
			val playerDeck = input.nextInt()
			val playerRune = input.nextInt()
			return Player(playerHealth, playerMana, playerDeck, playerRune)
		}
	}
}