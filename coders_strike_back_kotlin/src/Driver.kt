import javafx.geometry.Pos

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