import java.util.*
import kotlin.collections.ArrayList

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
			val enemySide = cardsInPlay.filter { it.location == ENEMY_SIDE }.toMutableList()

			var command = ""

			val toSummon = mySelf.getCardsToSummon(myHand).toMutableList()

			while (enemySide.guards().isNotEmpty() && mySide.attacker().isNotEmpty()) {
				val guard = enemySide.guards().first()
				val attacker = mySide.attacker().sortedBy { it.attack }.first()

				command += attack(attacker, guard.instanceID) + ";"
				attacker.hasAttacked = true

				guard.defense -= attacker.attack
				attacker.defense -= guard.attack

				if (guard.defense <= 0) {
					enemySide.remove(guard)
				}
				if (attacker.defense <= 0) {
					mySide.remove(attacker)
				}
			}

			if (mySide.size + toSummon.size > MAX_SIDE_LIMIT) {
				// mySide + toSummon > MAX_SIDE_LIMIT => attack strongest enemy with weakest monster
			}

			if (enemySide.guards().isEmpty()) {
				mySide.attacker().forEach { card ->
					command += attack(card, ENEMY_SIDE) + ";"
					card.hasAttacked = true
				}
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

fun List<Card>.attacker(): List<Card> {
	return this.filter { !it.hasAttacked }
}

fun List<Card>.guards(): List<Card> {
	return this.filter { it.hasGuard }
}
