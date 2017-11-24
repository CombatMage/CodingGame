import java.util.*

fun isReaperBlocked(input: Input): Boolean {
	val reaper = input.myReaper
	val enemiesNearby = reaper.getObjectByDistance(input.enemies, { it.distance }).filter { it.distance < 1000 }

	return enemiesNearby.count() >= 2
}

fun getReaperAction(input: Input): String {
	val reaper = input.myReaper
	val wrecks = reaper.getObjectByDistance(input.wrecks, { it.target.waterQuantity})
	val tanker = reaper.getObjectByDistance(input.tanker, { it.target.waterQuantity})

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
	val reaper = input.myReaper
	val rage = input.myRage

	// check if reaper is blocked and throw grenade if possible
	val isReaperBlocked = isReaperBlocked(input)
	val isReaperInRange = destroyer.getDistanceToTarget(reaper) <= 2000
	if (isReaperBlocked && isReaperInRange && rage >= COST_GRENADE_RAGE) {
		return destroyer.getOutputForSkill(reaper)
	}

	// select tanker close to our reaper as target
	val tanker = reaper.getObjectByDistance(input.tanker, { it.target.waterQuantity })

	if (tanker.count() == 0) {
		return "WAIT"
	}

	val (_, target) = tanker.first()
	val distance = destroyer.getDistanceToTarget(target)

	return destroyer.getOutputForTarget(distance, target)
}

fun getDoofAction(input: Input, gameTurn: Int): String {
	val doof = input.myDoof
	val reaper = input.myReaper
	val rage = input.myRage
	val enemies = doof.getObjectByDistance(input.enemyReapers, { input.getScoreForGameUnit(it.target)})

	val wrecks = doof.getObjectByDistance(input.wrecks, { it.target.waterQuantity })
	val wrecksInRange = wrecks.filter { it.distance <= 2000 }

	if (wrecksInRange.count() > 0 && rage >= (COST_OIL_RAGE + COST_GRENADE_RAGE)) {
		// enough rage and wrecks in range, spill oil
		// target is the wreck with the largest distance to our reaper
		val (distance, wreck) = reaper.getObjectByDistance(wrecksInRange.map { it.target }.toList(), { it.target.waterQuantity }).last()

		if (distance > 1000) {
			return doof.getOutputForSkill(wreck)
		}
	}

	// crash nearest enemy
	val (distance, target) = enemies.first()
	val vector = doof.getVectorForTarget(target)
	val thrust = doof.getThrustForTarget(distance)

	// battle cry
	val speak = if (gameTurn % 10 < 5) {
		"I live, I die."
	} else {
		"I LIVE AGAIN!!"
	}
	return "${vector.x} ${vector.y} $thrust $speak" // always go full speed ahead
}

fun main(args : Array<String>) {
	val scanner = Scanner(System.`in`)

	var gameTurn = 0
	// game loop
	while (true) {
		val start = System.nanoTime()

		val input = Input.fromScanner(scanner)

		val reaperAction = getReaperAction(input)
		val destroyerAction = getDestroyerAction(input)
		val doofAction = getDoofAction(input, gameTurn)

		val end = System.nanoTime()

		val responseTime = (end - start) / 1000000
		System.err.println("Response time: $responseTime ms")

		println(reaperAction)
		println(destroyerAction)
		println(doofAction)
		gameTurn++
	}
}