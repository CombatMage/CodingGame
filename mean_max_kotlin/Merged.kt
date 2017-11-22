import java.util.*
import kotlin.coroutines.experimental.buildSequence



const val T_REAPER = 0
const val T_DESTROYER = 1
const val T_DOOF = 2
const val T_TANKER = 3
const val T_WRECK = 4

data class DistanceByTarget(val distance: Double, val target: GameUnit)
data class Vector(val x: Int, val y: Int)

data class GameUnit(
		val unitId: Int,
		val unitType: Int,
		val player: Int,
		val mass: Float,
		val radius: Int,
		val x: Int,
		val y: Int,
		val speedX: Int,
		val speedY: Int,
		val waterQuantity: Int,
		val waterCapacity: Int
) {
	val isReaper = this.unitType == T_REAPER
	val isDestroyer = this.unitType == T_DESTROYER
	val isDoof = this.unitType == T_DOOF
	val isTanker = this.unitType == T_TANKER
	val isWreck = this.unitType == T_WRECK

	val isOwned = this.player == 0


	fun getObjectByDistance(objects: List<GameUnit>): Sequence<DistanceByTarget> {
		val distances = buildSequence {
			objects.forEach { obj ->
				val d = getDistanceToTarget(obj)
				yield(DistanceByTarget(d, obj))
			}
		}
		return distances.sortedBy { it.distance }
	}

	fun getDistanceToTarget(target: GameUnit): Double {
		return Math.sqrt(Math.pow((x - target.x).toDouble(), 2.0) + Math.pow((y - target.y).toDouble(), 2.0))
	}

	fun getOutputForTarget(distance: Double, target: GameUnit): String {
		val vector = this.getVectorForTarget(target)
		val thrust = this.getThrustForTarget(distance)
		return "${vector.x} ${vector.y} $thrust"
	}

	fun getVectorForTarget(target: GameUnit): Vector {
		return Vector(
				target.x - this.speedX + target.speedX,
				target.y - this.speedY + target.speedY
		)
	}

	fun getThrustForTarget(distance: Double): Int {
		if (this.isDestroyer) {
			return 300
		}

		if (this.isDoof) {
			return 300
		}

		if (this.isReaper) {
			return when {
				distance > 500 -> 300
				distance > 250 -> 100
				else -> 0
			}
		}

		return 0
	}

	fun getOutputForSkill(target: GameUnit): String {
		return "SKILL ${target.x} ${target.y}"
	}

	companion object {
		fun fromScanner(input: Scanner): GameUnit {
			return GameUnit(
					unitId = input.nextInt(),
					unitType = input.nextInt(),
					player = input.nextInt(),
					mass = input.nextFloat(),
					radius = input.nextInt(),
					x = input.nextInt(),
					y = input.nextInt(),
					speedX = input.nextInt(),
					speedY = input.nextInt(),
					waterQuantity = input.nextInt(),
					waterCapacity = input.nextInt()
			)
		}
	}
}
const val COST_OIL_RAGE = 30

class Input(
	val myRage: Int,
	val myScore: Int,
	val enemyScore1: Int,
	val enemyScore2: Int,
	private val allUnits: Array<GameUnit>
) {
	val myReaper: GameUnit = this.allUnits.first { it.isReaper && it.isOwned }
	val myDestroyer: GameUnit = this.allUnits.first { it.isDestroyer && it.isOwned }
	val myDoof: GameUnit = this.allUnits.first { it.isDoof && it.isOwned }

	val enemyReapers: List<GameUnit> = this.allUnits.filter { it.isReaper && !it.isOwned }

	val tanker: List<GameUnit> = this.allUnits.filter { it.isTanker }
	val wrecks: List<GameUnit> = this.allUnits.filter { it.isWreck }

	companion object {
		fun fromScanner(input: Scanner): Input {
			// unused
			val myScore = input.nextInt()
			val enemyScore1 = input.nextInt()
			val enemyScore2 = input.nextInt()
			val myRage = input.nextInt()
			val enemyRage1 = input.nextInt()
			val enemyRage2 = input.nextInt()

			val unitCount = input.nextInt()
			val units = Array(unitCount, {
				GameUnit.fromScanner(input)
			})

			return Input(
					myScore = myScore,
					myRage = myRage,
					enemyScore1 = enemyScore1,
					enemyScore2 = enemyScore2,
					allUnits = units
			)
		}
	}
}
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

fun getDoofAction(input: Input, gameTurn: Int): String {
	val doof = input.myDoof
	val reaper = input.myReaper
	val rage = input.myRage
	val enemies = doof.getObjectByDistance(input.enemyReapers)

	val wrecks = doof.getObjectByDistance(input.wrecks)
	val wrecksInRange = wrecks.filter { it.distance < 2000 }

	if (wrecksInRange.count() > 0 && rage >= COST_OIL_RAGE) {
		// enough rage and wrecks in range, spill oil
		// target is the wreck with the largest distance to our reaper
		val wreck = reaper.getObjectByDistance(wrecksInRange.map { it.target }.toList()).last().target
		return doof.getOutputForSkill(wreck)
	}

	// crash nearest enemy
	val (distance, target) = enemies.first()
	val vector = doof.getVectorForTarget(target)
	val thrust = doof.getThrustForTarget(distance)

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