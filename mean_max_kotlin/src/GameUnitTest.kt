import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GameUnitTest {

	private lateinit var unit: GameUnit

	@Before
	fun setUp() {
		this.unit = GameUnit(
				1,
				1,
				1,
				1f,
				1,
				0,
				0,
				1,
				1,
				1,
				1
		)
	}

	@Test
	fun getDistanceToTargetShouldRoundResult() {
		// arrange
		val distance105 = GameUnit(
				1,
				1,
				1,
				1f,
				1,
				105,
				0,
				1,
				1,
				10,
				1
		)

		// action
		val result = this.unit.getDistanceToTarget(distance105)

		// verify
		assertEquals(110, result.toInt())
	}

	@Test
	fun getObjectByDistanceReturnsNearest() {
		// arrange
		val near = GameUnit(
				1,
				1,
				1,
				1f,
				1,
				0,
				0,
				1,
				1,
				10,
				1
		)
		val far = GameUnit(
				1,
				1,
				1,
				1f,
				1,
				100,
				100,
				1,
				1,
				5,
				1
		)

		// action
		val result = this.unit.getObjectByDistance(listOf(near, far), { it.target.waterQuantity }).first().target

		// verify
		assertEquals(near, result)
	}

	@Test
	fun getObjectByDistanceBreaksTie() {
		// arrange
		val plentyWater = GameUnit(
				1,
				1,
				1,
				1f,
				1,
				0,
				0,
				1,
				1,
				10,
				1
		)
		val lessWater = GameUnit(
				1,
				1,
				1,
				1f,
				1,
				0,
				0,
				1,
				1,
				5,
				1
		)

		// action
		val result = this.unit.getObjectByDistance(listOf(plentyWater, lessWater), { it.target.waterQuantity }).first().target

		// verify
		assertEquals(plentyWater, result)
	}

}