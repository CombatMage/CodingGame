import java.util.*

class Input(
	val myRage: Int,
	val enemyScore1: Int,
	val enemyScore2: Int,
	private val allUnits: Array<GameUnit>
) {
	val myReaper: GameUnit = this.allUnits.first { it.isReaper && it.isOwned }
	val myDestroyer: GameUnit = this.allUnits.first { it.isDestroyer && it.isOwned }
	val myDoof: GameUnit = this.allUnits.first { it.isDoof && it.isOwned }

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
					myRage = myRage,
					enemyScore1 = enemyScore1,
					enemyScore2 = enemyScore2,
					allUnits = units
			)
		}
	}
}