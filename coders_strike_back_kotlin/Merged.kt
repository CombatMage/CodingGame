import javafx.geometry.Pos
import java.util.*



private const val CHECKPOINT_R = 600

fun calculateOutput(input: Input, target: Position): Output {
	// val angle = input.angleCheckpoint
	val distance = input.distanceCheckpoint
	val thrust = when {
		distance > 1000 -> 100
		distance > 600 -> 50
		distance > 400 -> 10
		else -> 10
	}

	return Output(target, thrust)
}

fun calculateTarget(current: Position, next: Position): Position {
	val m = (next.y - current.y) / (next.x - current.x)
	val x = Math.sqrt((square(CHECKPOINT_R) - square(m * (current.x - next.x))).toDouble()) + next.x
	val y = m * (x - next.x) + next.y
	return Position(x.toInt(), y.toInt())
}

private fun square(x: Int): Int {
	return Math.pow(x.toDouble(), 2.0).toInt()
}
class Input(
		val positionPod: Position,
		val positionCheckpoint: Position,
		val distanceCheckpoint: Int,
		val angleCheckpoint: Int,
		val positionEnemy: Position
) {
	companion object {
		fun fromInput(scanner: Scanner): Input {
			return Input(
					Position(scanner.nextInt(), scanner.nextInt()),
					Position(scanner.nextInt(), scanner.nextInt()),
					scanner.nextInt(),
					scanner.nextInt(),
					Position(scanner.nextInt(), scanner.nextInt())
			)
		}
	}
}fun main(args : Array<String>) {
	val scanner = Scanner(System.`in`)
	val checkPoints = ArrayList<Position>()

	var count = 0

	// game loop
	while (true) {
		val input = Input.fromInput(scanner)

		val target = if (count == 3) {
			System.err.println("Round 2")
			System.err.println("checkpoints are: $checkPoints")

			/*
			val currentIndex = checkPoints.indexOf(input.positionCheckpoint) - 1
			var nextIndex = currentIndex + 1
			if (nextIndex > checkPoints.size) {
				nextIndex = 0
			}*/
			val currentIndex = 2
			val nextIndex = 0

			System.err.println("current: $currentIndex next: $nextIndex")
			calculateTarget(checkPoints[currentIndex], checkPoints[nextIndex])
		} else if (!checkPoints.contains(input.positionCheckpoint)) {
			System.err.println("Round 1, reached $count")
			count++

			checkPoints.add(input.positionCheckpoint)
			input.positionCheckpoint
		} else {
			System.err.println("Round 1, heading for checkpoint $count")

			input.positionCheckpoint
		}

		val output = calculateOutput(input, target)
		output.write()
	}
}class Output(
		private val target: Position,
		private val thrust: Int
) {
	fun write() {
		println("${this.target.x} ${this.target.y} ${this.thrust}")
	}
}data class Position(val x: Int, val y: Int)