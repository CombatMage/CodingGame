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