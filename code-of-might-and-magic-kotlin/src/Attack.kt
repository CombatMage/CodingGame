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

		if (!guard.hasWard) {
			guard.defense -= attackingCard.attack
		}
		if (!attackingCard.hasWard) {
			attackingCard.defense -= guard.attack
		}
		if (!guard.hasWard && (guard.defense <= 0 || attackingCard.hasLethal)) {
			enemySideResult.remove(guard)
		}
		if (!attackingCard.hasWard && (attackingCard.defense <= 0 || guard.hasLethal)) {
			mySideResult.remove(attackingCard)
		}
		if (guard.hasWard) {
			guard.removeWard()
		}
		if (attackingCard.hasWard) {
			attackingCard.removeWard()
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