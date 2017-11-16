import kotlin.coroutines.experimental.buildSequence
import java.util.*
import java.lang.Math.abs


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

fun calculateOutputAsync(input: Input): Int {
	val sortedHorses = input.horsePower.sorted()
	val diffs = buildSequence {
		for (i in 0 until sortedHorses.size - 1) {
			yield(abs(sortedHorses[i] - sortedHorses[i + 1]))
		}
	}
	return diffs.min() ?: 0
}

fun main(args : Array<String>) {
	val input = readInput()
	val result = calculateOutputAsync(input)
	print(result)
}
