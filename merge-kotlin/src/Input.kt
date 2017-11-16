class Input(val horsePower: IntArray)

fun readInput(): Input {
	val input = Scanner(System.`in`)
	val horseCount = input.nextInt()
	val horses = IntArray(horseCount)
	for (i in 0 until horseCount) {
		horses[i] = input.nextInt()
	}
	return Input(horses)
}
