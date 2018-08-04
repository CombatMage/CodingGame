data class AttackResult(
		val enemySide: List<Card>,
		val mySide: List<Card>,
		val command: String
)

fun performAttack(attacker: List<Card>, mySide: List<Card>, enemySide: List<Card>): AttackResult {
	if (attacker.find { it.hasAttacked } != null) throw IllegalArgumentException("invalid attacker given")

	var command = ""
	val mySideResult = mySide.toMutableList()
	val enemySideResult = enemySide.toMutableList()

	while (enemySide.guards().isNotEmpty() && attacker.isNotEmpty()) {
		val guard = enemySide.guards().first()
		val attackingCard = attacker.sortedByDescending { it.attack }.first()

		command += attack(attackingCard, guard.instanceID) + ";"
		attackingCard.hasAttacked = true

		guard.defense -= attackingCard.attack
		attackingCard.defense -= guard.attack

		if (guard.defense <= 0) {
			enemySideResult.remove(guard)
		}
		if (attackingCard.defense <= 0) {
			mySideResult.remove(attackingCard)
		}
	}

	return AttackResult(enemySideResult, mySideResult, command)
}