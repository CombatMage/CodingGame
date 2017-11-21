import java.util.*

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
}