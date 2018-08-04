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

		input.nextInt() // opponentHand
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
			var mySide = cardsInPlay.filter { it.location == MY_SIDE }.toMutableList()
			var enemySide = cardsInPlay.filter { it.location == ENEMY_SIDE }

			var command = ""

			val cardsToPlay = mySelf.getCardsToPlay(myHand)
			val toSummon = cardsToPlay.creatures().toMutableList()
			val toUse = cardsToPlay.items().toMutableList()

			// check items to use
			toUse.forEach { card ->
				if (card.isPlayerBuff || card.isPlayerDebuff) {
					command += use(card, -1) + ";"
				} else if (card.isCreatureBuff && mySide.isNotEmpty()) {
					val toBuff = mySide[0]
					command += use(card, toBuff.instanceID) + ";"
					toBuff.attack += card.attack
					toBuff.defense += card.defense
				} else if (card.isCreatureDebuff && enemySide.isNotEmpty()) {
					val toDebuff = enemySide.sortedBy { it.hasGuard }.first()
					command += use(card, toDebuff.instanceID) + ";"
					toDebuff.attack += card.attack
					toDebuff.defense += card.defense
				}
			}

			// first attack phase, try to kill guards
			val attackResult = performAttack(mySide.attacker(), mySide, enemySide)
			mySide = attackResult.mySide.toMutableList()
			enemySide = attackResult.enemySide
			command += attackResult.command

			// make room for new cards to summon
			if (mySide.size + toSummon.size > MAX_SIDE_LIMIT) {
				// mySide + toSummon > MAX_SIDE_LIMIT => attack strongest enemy with weakest monster
			}

			// attack enemy player
			command += performAttackEnemyPlayer(mySide.attacker(), enemySide)

			// summon new creatures
			while (mySide.size < MAX_SIDE_LIMIT && toSummon.isNotEmpty()) {
				val card = toSummon.removeAt(0)
				command += summon(card) + ";"
				mySide.add(card)
				myHand.remove(card)
			}

			// attack again with chargers
			val chargeAttackResult = performAttack(mySide.chargingAttacker(), mySide, enemySide)
			mySide = chargeAttackResult.mySide.toMutableList()
			enemySide = chargeAttackResult.enemySide
			command += chargeAttackResult.command

			// attack enemy player
			command += performAttackEnemyPlayer(mySide.chargingAttacker(), enemySide)

			if (command.isBlank()) {
				println("PASS")
			} else {
				println(command)
			}
		}
	}
}

fun List<Card>.attacker(): List<Card> {
	return this.filter { it.isCreature && !it.hasAttacked && it.attack > 0 }
}

fun List<Card>.chargingAttacker(): List<Card> {
	return this.filter { it.isCreature && !it.hasAttacked && it.hasCharge && it.attack > 0 }
}

fun List<Card>.guards(): List<Card> {
	return this.filter { it.isCreature && it.hasGuard }
}

fun List<Card>.creatures(): List<Card> {
	return this.filter { it.isCreature }
}

fun List<Card>.items(): List<Card> {
	return this.filter { !it.isCreature }
}