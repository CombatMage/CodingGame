import java.util.*
fun main(args : Array<String>) {
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
}