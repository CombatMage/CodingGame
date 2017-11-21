import java.util.*

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