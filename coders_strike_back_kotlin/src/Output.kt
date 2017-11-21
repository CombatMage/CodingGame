class Output(
		private val target: Position,
		private val thrust: Int
) {
	fun write() {
		println("${this.target.x} ${this.target.y} ${this.thrust}")
	}
}