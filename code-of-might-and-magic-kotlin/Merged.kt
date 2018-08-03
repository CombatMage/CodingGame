import kotlin.collections.ArrayList
import java.util.*



data class Card(
		val instanceID: Int,
		val location: Int,
		val cost: Int,
		val attack: Int,
		val defense: Int,
		val abilities: String,

		var hasAttacked: Boolean
) {

	val hasGuard: Boolean get() = this.abilities.contains("G", true)
	val hasCharge: Boolean get() = this.abilities.contains("C", true)
	val hasBreakthrough: Boolean get() = this.abilities.contains("B", true)

	companion object {
		fun fromScanner(input: Scanner): Card {
			val cardNumber = input.nextInt()
			val instanceId = input.nextInt()
			val location = input.nextInt()
			val cardType = input.nextInt()
			val cost = input.nextInt()
			val attack = input.nextInt()
			val defense = input.nextInt()
			val abilities = input.next()
			val myHealthChange = input.nextInt()
			val opponentHealthChange = input.nextInt()
			val cardDraw = input.nextInt()
			return Card(instanceId, location, cost, attack, defense, abilities, false)
		}
	}

}
val TARGET_MANA_CURVE: Map<Int, Int> = hashMapOf(
		0 to 1,
		1 to 10,
		2 to 6,
		3 to 5,
		4 to 4,
		5 to 2,
		6 to 1,
		7 to 1
)
const val MY_HAND = 0
const val MY_SIDE = 1
const val ENEMY_SIDE = -1

const val MAX_SIDE_LIMIT = 6

class Deck{
	val cards = ArrayList<Card>()
	val manaCurve = HashMap<Int, Int>()

	private fun addCard(card: Card) {
		cards.add(card)
		manaCurve[card.cost] = manaCurve.getOrDefault(card.cost, 0) + 1
	}

	fun selectCardToAdd(cards: List<Card>): Int{
		if (cards.isEmpty()) throw IllegalArgumentException("given card list is empty")
		cards.forEachIndexed { index, card ->
			if (this.manaCurve.getOrDefault(card.cost, 0) < TARGET_MANA_CURVE.getOrDefault(card.cost, 0)) {
				this.addCard(card)
				return index
			}
		}
		this.addCard(cards[0])
		return 0
	}
}

fun main(args : Array<String>) {
	val input = Scanner(System.`in`)

	val myDeck = Deck()
	// game loop
	for (round in 0 until Int.MAX_VALUE) {
		val mySelf = Player.fromScanner(input)
		val enemy = Player.fromScanner(input)
		debug("Myself: $mySelf")
		debug("Enemy: $enemy")

		val opponentHand = input.nextInt()
		val cardCount = input.nextInt()

		if (round < 30) {
			debug("draw phase round $round")
			val toDraw = ArrayList<Card>()
			for (i in 0 until cardCount) { toDraw.add(Card.fromScanner(input)) }
			val selectedCard = myDeck.selectCardToAdd(toDraw)
			pick(selectedCard)
		} else {
			debug("battle phase round $round")
			val cardsInPlay = ArrayList<Card>()
			for (i in 0 until cardCount) { cardsInPlay.add(Card.fromScanner(input)) }
			val myHand = cardsInPlay.filter { it.location ==  MY_HAND }.toMutableList()
			val mySide = cardsInPlay.filter { it.location == MY_SIDE }.toMutableList()
			val enemySide = cardsInPlay.filter { it.location == ENEMY_SIDE }

			var command = ""

			val toSummon = mySelf.getCardsToSummon(myHand).toMutableList()

			val guards = enemySide.filter { it.hasGuard }
			if (guards.isNotEmpty()) {
				// if enemy has guard => attack until dead
			}
			if (mySide.size + toSummon.size > MAX_SIDE_LIMIT) {
				// mySide + toSummon > MAX_SIDE_LIMIT => attack strongest enemy with weakest monster
			}

			val remainingAttacker = myHand.filter { !it.hasAttacked }
			remainingAttacker.forEach { card ->
				command += attack(card, ENEMY_SIDE) + ";"
				card.hasAttacked = true
			}

			while (mySide.size < MAX_SIDE_LIMIT && toSummon.isNotEmpty()) {
				val card = toSummon.removeAt(0)
				command += summon(card) + ";"
				mySide.add(card)
				myHand.remove(card)
			}

			if (command.isBlank()) {
				println("PASS")
			} else {
				println(command)
			}
		}
	}
}

data class Player (
		var health: Int,
		var mana: Int,
		val playerDeck: Int,
		val playerRune: Int
) {
	fun getCardsToSummon(cardsInHand: List<Card>): List<Card> {
		val toSummon = ArrayList<Card>()
		val cards = cardsInHand.toMutableList()

		var enoughMana = cards.filter { it.cost <= this.mana }
		while (enoughMana.isNotEmpty()) {
			toSummon.add(enoughMana[0])
			this.mana -= enoughMana[0].cost
			cards.remove(enoughMana[0])
			enoughMana = cards.filter { it.cost <= this.mana }
		}
		return toSummon
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
fun debug(message: String) {
	System.err.println(message)
}

fun pick(index: Int) {
	println("PICK $index")
}

fun attack(card: Card, target: Int): String {
	return "ATTACK ${card.instanceID} $target"
}

fun summon(card: Card): String {
	return "SUMMON ${card.instanceID}"
}
