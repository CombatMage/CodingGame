import org.junit.Test
import kotlin.test.assertEquals


class MainKtTest {

	@Test
	fun calculateOutput_1() {
		// arrange
		val testData = Input(intArrayOf(3, 5, 8, 9, 10, 42, 0, 34, 35, 100, -24, 2, 1))

		// action
		val start = System.nanoTime()
		val result = calculateOutput(testData)
		val end = System.nanoTime()

		// verify
		println("time: ${end - start}ns")
		assertEquals(1, result)
	}

	@Test
	fun calculateOutputAsync() {
		// arrange
		val testData = Input(intArrayOf(3, 5, 8, 9, 10, 42, 0, 34, 35, 100, -24, 2, 1))

		// action
		val start = System.nanoTime()
		val result = calculateOutput(testData)
		val end = System.nanoTime()

		// verify
		println("time: ${end - start}ns")
		assertEquals(1, result)
	}
}