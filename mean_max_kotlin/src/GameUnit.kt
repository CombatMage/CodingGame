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