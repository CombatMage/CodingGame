import org.junit.Assert.*
import org.junit.Test

class AttackKtTest {


	@Test
	fun testSorting() {
		// arrange
		val testData = listOf(Card(
				0,
				0,
				0,
				0,
				3,
				0,
				"",
				false
		), Card(
				1,
				0,
				0,
				0,
				4,
				0,
				"",
				false
		), Card(
				2,
				0,
				0,
				0,
				2,
				0,
				"L",
				false
		))

		// action
		val result = testData.sortedWith(
				compareByDescending<Card> { it.hasLethal }
						.thenByDescending { it.attack }
		)

		// verify
		assertEquals(2, result[0].instanceID) // first lethal
		assertEquals(1, result[1].instanceID) // strongest attack
		assertEquals(0, result[2].instanceID)
	}

}