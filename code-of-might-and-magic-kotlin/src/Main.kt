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