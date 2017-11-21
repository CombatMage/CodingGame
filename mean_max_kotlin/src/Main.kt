import java.util.*

fun getReaperAction(input: Input): String {
	val reaper = input.myReapers.first()
	val wrecks = reaper.getObjectByDistance(input.wrecks)

	if (wrecks.count() == 0) {
		return "WAIT"
	}

	val (distance, target) = wrecks.first()
	val thrust = reaper.getThrustForTarget(distance)
	val vector = reaper.getVectorForTarget(target)

	return "${vector.x} ${vector.y} $thrust"
}

fun getDestroyerAction(input: Input): String {
	val destroyer = input.myDestroyers.first()

	// select tanker close to our reaper as target
	val reaper = input.myReapers.first()
	val tanker = reaper.getObjectByDistance(input.tanker)

	if (tanker.count() == 0) {
		return "WAIT"
	}

	val (_, target) = tanker.first()
	val distance = destroyer.getDistanceToTarget(target)
	val thrust = destroyer.getThrustForTarget(distance)
	val vector = destroyer.getVectorForTarget(target)

	return "${vector.x} ${vector.y} $thrust"
}

fun main(args : Array<String>) {
	val scanner = Scanner(System.`in`)

	// game loop
	while (true) {
		val input = Input.fromScanner(scanner)

		val reaperAction = getReaperAction(input)
		val destroyerAction = getDestroyerAction(input)



		println(reaperAction)
		println(destroyerAction)

		// unused
		println("WAIT")
	}
}