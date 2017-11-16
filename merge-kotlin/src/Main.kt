import java.lang.Math.abs
import java.util.*
import kotlin.coroutines.experimental.buildSequence

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
