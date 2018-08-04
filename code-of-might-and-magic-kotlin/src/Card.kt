import java.util.*

data class Card(
		val instanceID: Int,
		val location: Int,
		val cardType: Int,
		val cost: Int,
		var attack: Int,
		var defense: Int,
		var abilities: String,
		val myHealthChange: Int,
		val opponentHealthChange: Int,

		var hasAttacked: Boolean
) {

	val isCreature: Boolean get() = this.cardType == 0

	val hasGuard: Boolean get() = this.abilities.contains("G")
	val hasCharge: Boolean get() = this.abilities.contains("C")
	val hasBreakthrough: Boolean get() = this.abilities.contains("B")
	val hasLethal: Boolean get() = this.abilities.contains("L")
	val hasDrain: Boolean get() = this.abilities.contains("D")
	val hasWard: Boolean get() = this.abilities.contains("W")

	val isItem: Boolean get() = this.cardType != 0
	val isCreatureBuff get() = this.cardType == 1
	val isCreatureDebuff get() = this.cardType == 2
	val isPlayerBuff get() = this.cardType == 3 && this.myHealthChange > 0
	val isPlayerDebuff get() = this.cardType == 3 && this.opponentHealthChange < 0

	fun removeWard() {
		this.abilities = this.abilities.replace("W", "")
	}

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
			return Card(instanceId, location, cardType, cost, attack, defense, abilities, myHealthChange, opponentHealthChange, false)
		}
	}

}