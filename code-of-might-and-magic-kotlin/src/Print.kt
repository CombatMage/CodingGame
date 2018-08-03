fun debug(message: String) {
	System.err.println(message)
}

fun pick(index: Int) {
	println("PICK $index")
}

fun summon(card: Card): String {
	return "SUMMON ${card.instanceID}"
}