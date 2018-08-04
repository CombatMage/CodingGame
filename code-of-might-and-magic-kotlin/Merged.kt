import kotlin.collections.ArrayList
import java.util.*


data class AttackResult(
		val enemySide: List<Card>,
		val mySide: List<Card>,
		val command: String
)

fun performAttack(attacker: List<Card>, mySide: List<Card>, enemySide: List<Card>): AttackResult {
	if (attacker.find { it.hasAttacked } != null) throw IllegalArgumentException("invalid attacker given")

	var command = ""
	val attackerTmp = attacker.toMutableList()
	val mySideResult = mySide.toMutableList()
	val enemySideResult = enemySide.toMutableList()

	while (enemySide.guards().isNotEmpty() && attackerTmp.isNotEmpty()) {
		val guard = enemySide.guards().first()
		val attackingCard = attackerTmp.sortedWith(
				compareByDescending<Card> { it.hasLethal }
						.thenByDescending { it.attack }).first()

		command += attack(attackingCard, guard.instanceID) + ";"
		attackingCard.hasAttacked = true
		attackerTmp.remove(attackingCard)

		guard.defense -= attackingCard.attack
		attackingCard.defense -= guard.attack

		if (guard.defense <= 0 || attackingCard.hasLethal) {
			enemySideResult.remove(guard)
		}
		if (attackingCard.defense <= 0 || guard.hasLethal) {
			mySideResult.remove(attackingCard)
		}
	}

	return AttackResult(enemySideResult, mySideResult, command)
}

fun performAttackEnemyPlayer(attacker: List<Card>, enemySide: List<Card>): String {
	var command = ""
	if (enemySide.guards().isEmpty()) {
		attacker.forEach { card ->
			command += attack(card, ENEMY_SIDE) + ";"
			card.hasAttacked = true
		}
	}
	return command
}

data class Card(
		val instanceID: Int,
		val location: Int,
		val cardType: Int,
		val cost: Int,
		val attack: Int,
		var defense: Int,
		val abilities: String,

		var hasAttacked: Boolean
) {

	val isCreature: Boolean get() = this.cardType == 0

	val hasGuard: Boolean get() = this.abilities.contains("G")
	val hasCharge: Boolean get() = this.abilities.contains("C")
	val hasBreakthrough: Boolean get() = this.abilities.contains("B")
	val hasLethal: Boolean get() = this.abilities.contains("L")
	val hasDrain: Boolean get() = this.abilities.contains("D")
	val hasWard: Boolean get() = this.abilities.contains("W")

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
			return Card(instanceId, location, cardType, cost, attack, defense, abilities, false)
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
		debug("addCard $card")
		cards.add(card)
		manaCurve[card.cost] = manaCurve.getOrDefault(card.cost, 0) + 1
	}

	fun selectCardToAdd(cards: List<Card>): Int{
		if (cards.isEmpty()) throw IllegalArgumentException("given card list is empty")
		cards.sortedBy { it.isCreature }.forEachIndexed { index, card ->
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
			var mySide = cardsInPlay.filter { it.location == MY_SIDE }.toMutableList()
			var enemySide = cardsInPlay.filter { it.location == ENEMY_SIDE }

			var command = ""

			val toSummon = mySelf.getCardsToSummon(myHand).creatures().toMutableList()
			val toUse = mySelf.getCardsToSummon(myHand).items().toMutableList()

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
	return this.filter { it.isCreature && !it.hasAttacked }
}

fun List<Card>.chargingAttacker(): List<Card> {
	return this.filter { it.isCreature && !it.hasAttacked && it.hasCharge }
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
