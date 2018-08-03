import java.util.*

data class Card(
		val instanceID: Int,
		val location: Int,
		val cost: Int,
		val attack: Int,
		val defense: Int,
		val abilities: String
) {

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
			return Card(instanceId, location, cost, attack, defense, abilities)
		}
	}

}