enum class Direction(val convert: (Int) -> Int) {
    RIGHT({it}),
    LEFT({100 - it})
}

fun main() {
    fun Int.rotate(direction: Direction, steps: Int): Pair<Int, Int> {
        val distance = steps % 100
        val interim = this + direction.convert(distance)
        val rotations = steps / 100 +  when (direction) {
            Direction.RIGHT if (interim >= 100) -> 1
            Direction.LEFT if (interim <= 100 && this != 0) -> 1
            else -> 0
        }
        return rotations to ((interim) % 100)
    }

    fun String.parse(): Pair<Direction, Int> {
        val direction = when (get(0).lowercaseChar()) {
            'l' -> Direction.LEFT
            'r' -> Direction.RIGHT
            else -> throw IllegalArgumentException("Invalid direction")
        }
        val steps = substring(1).toInt()
        return direction to steps
    }

    fun part1(start: Int, input: List<String>): Int {
        var location = start
        var zeros = 0
        // parse the string that starts with 'l' or 'r' followed by a number
        for (line in input) {
            val (direction, steps) = line.parse()
            location = location.rotate(direction, steps).second
            if (location == 0) {
                ++zeros
            }
        }

        return zeros
    }

    fun part2(start: Int, input: List<String>): Int {
        var location = start
        var zeros = 0
        // parse the string that starts with 'l' or 'r' followed by a number
        for (line in input) {
            val (direction, steps) = line.parse()
            val (rotations, newLocation) = location.rotate(direction, steps)
            zeros += rotations
            location = newLocation
        }

        return zeros
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day01", "test")
    check(part1(50, testInput) == 3)
    check(part2(50, testInput) == 6)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(50, input).println()
    part2(50, input).println()
}
