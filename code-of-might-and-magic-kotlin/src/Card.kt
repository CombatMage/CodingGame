import java.util.*

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
			return Card(instanceId, location, cardType, cost, attack, defense, abilities, false)
		}
	}

}