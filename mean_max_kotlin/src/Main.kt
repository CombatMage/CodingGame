import java.util.*

fun getReaperAction(input: Input): String {
	val reaper = input.myReaper
	val wrecks = reaper.getObjectByDistance(input.wrecks)
	val tanker = reaper.getObjectByDistance(input.tanker)

	if (wrecks.count() > 0) {
		// target nearest wreck
		val (distance, target) = wrecks.first()
		return reaper.getOutputForTarget(distance, target)
	}
	if (tanker.count() > 0) {
		// if nothing to harvest, close in to nearest tanker
		val (distance, target) = tanker.first()
		return reaper.getOutputForTarget(distance, target)
	}

	// else idle
	return "WAIT"
}

fun getDestroyerAction(input: Input): String {
	val destroyer = input.myDestroyer

	// select tanker close to our reaper as target
	val reaper = input.myReaper
	val tanker = reaper.getObjectByDistance(input.tanker)

	if (tanker.count() == 0) {
		return "WAIT"
	}

	val (_, target) = tanker.first()
	val distance = destroyer.getDistanceToTarget(target)

	return destroyer.getOutputForTarget(distance, target)
}

fun getDoofAction(input: Input): String {
	val doof = input.myDoof
	val reaper = input.myReaper
	val rage = input.myRage
	val enemies = doof.getObjectByDistance(input.enemyReapers)

	val wrecks = doof.getObjectByDistance(input.wrecks)
	val wrecksInRange = wrecks.filter { it.distance < 2000 }

	System.err.println("wrecks: " + wrecks.toList())
	System.err.println("wrecks in range: " + wrecksInRange.toList())
	System.err.println("current rage: " + rage)

	if (wrecksInRange.count() > 0 && rage >= COST_OIL_RAGE) {
		// enough rage and wrecks in range, spill oil
		// target is the wreck with the largest distance to our reaper
		val wreck = reaper.getObjectByDistance(wrecksInRange.map { it.target }.toList()).last().target
		return doof.getOutputForSkill(wreck)
	}

	// crash nearest enemy
	val (_, target) = enemies.first()
	val vector = doof.getVectorForTarget(target)
	return "${vector.x} ${vector.y} 300" // always go full speed ahead
}

fun main(args : Array<String>) {
	val scanner = Scanner(System.`in`)

	// game loop
	while (true) {
		val start = System.nanoTime()

		val input = Input.fromScanner(scanner)

		val reaperAction = getReaperAction(input)
		val destroyerAction = getDestroyerAction(input)
		val doofAction = getDoofAction(input)

		val end = System.nanoTime()

		val responseTime = (end - start) / 1000000
		System.err.println("Response time: $responseTime ms")

		println(reaperAction)
		println(destroyerAction)
		println(doofAction)
	}
}