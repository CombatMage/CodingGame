import java.lang.Math.*
import java.util.*
import kotlin.coroutines.experimental.buildSequence

data class DistanceByTarget(val distance: Double, val target: GameUnit)
data class Vector(val x: Int, val y: Int) {
	val length = sqrt(pow(x.toDouble(), 2.0) + pow(y.toDouble(), 2.0))
}

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
	val isOil = this.unitType == T_OIL

	val isOwned = this.player == 0
	val isNeutral = this.player == -1
	val isEnemy = !this.isOwned && !this.isNeutral

	val skillDuration = this.waterCapacity

	fun getObjectsInDistance(objects: List<GameUnit>, maxDistance: Int): Sequence<DistanceByTarget> {
		val distances = buildSequence {
			objects.forEach { obj ->
				val d = getDistanceToTarget(obj)
				if (d <= maxDistance) {
					yield(DistanceByTarget(d, obj))
				}
			}
		}
		return distances.sortedBy { it.distance }
	}

	fun getObjectByDistance(objects: List<GameUnit>, tieBreak: (DistanceByTarget) -> Comparable<*>?): Sequence<DistanceByTarget> {
		val distances = buildSequence {
			objects.forEach { obj ->
				val d = getDistanceToTarget(obj)
				yield(DistanceByTarget(d, obj))
			}
		}
		return distances.sortedWith(
				compareBy<DistanceByTarget> { it.distance.toInt() }
						.thenByDescending(tieBreak)
		)
	}

	fun getObjectsByDistanceWithSpeed(objects: List<GameUnit>, tieBreak: (DistanceByTarget) -> Comparable<*>?): Sequence<DistanceByTarget> {
		val distances = buildSequence {
			objects.forEach { obj ->
				val d = getDistanceToTargetWithSpeed(obj)
				yield(DistanceByTarget(d, obj))
			}
		}
		return distances.sortedWith(
				compareBy<DistanceByTarget> { it.distance.toInt() }
						.thenByDescending(tieBreak)
		)
	}

	private fun getDistanceToTargetWithSpeed(target: GameUnit): Double{
		val selfX = this.x + this.speedX
		val selfY = this.y + this.speedY

		val otherX = target.x + target.speedX
		val otherY = target.y + target.speedY

		val vector = Vector(otherX - selfX, otherY - selfY)
		return vector.length
	}

	fun getDistanceToTarget(target: GameUnit): Double {
		// round to nearest ten
		return ((sqrt(pow((x - target.x).toDouble(), 2.0) + pow((y - target.y).toDouble(), 2.0)) + 5) / 10) * 10
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