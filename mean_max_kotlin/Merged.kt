import kotlin.coroutines.experimental.buildSequence
import java.util.*



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
		val waterQuantity: Int
) {
	val isReaper = this.unitType == T_REAPER
	val isDestroyer = this.unitType == T_DESTROYER
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

	fun getVectorForTarget(target: GameUnit): Vector {
		return Vector(
				target.x - this.speedX + target.speedX,
				target.y - this.speedY + target.speedY
		)
	}

	fun getDistanceToTarget(target: GameUnit): Double {
		return Math.pow((x - target.x).toDouble(), 2.0) + Math.pow((y - target.y).toDouble(), 2.0)
	}

	fun getThrustForTarget(distance: Double): Int {
		if (this.isDestroyer) {
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

	companion object {
		fun fromScanner(input: Scanner): GameUnit {
			val gameUnit = GameUnit(
					unitId = input.nextInt(),
					unitType = input.nextInt(),
					player = input.nextInt(),
					mass = input.nextFloat(),
					radius = input.nextInt(),
					x = input.nextInt(),
					y = input.nextInt(),
					speedX = input.nextInt(),
					speedY = input.nextInt(),
					waterQuantity = input.nextInt()
			)
			// unused
			val extra2 = input.nextInt()

			return gameUnit
		}
	}
}
class Input(
	private val allUnits: Array<GameUnit>
) {
	val myReapers: List<GameUnit> = this.allUnits.filter { it.isReaper && it.isOwned }
	val myDestroyers: List<GameUnit> = this.allUnits.filter { it.isDestroyer && it.isOwned }
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

			return Input(units)
		}
	}
}
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