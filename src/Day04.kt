fun main() {

    fun List<String>.toBooleanGrid(symbol: Char): Grid<Boolean> {
        val grid = BooleanGrid()
        for ((row, line) in this.withIndex()) {
            for ((col, _) in line.withIndex().filter { it.value == symbol }) {
                grid.set(row, col, true)
            }
        }
        return grid
    }

    fun part1(input: List<String>): Int {
        var sum = 0
        val grid = input.toBooleanGrid('@')
        for ((point, value) in grid.allPoints()) {
            if (value && point.neighbors().count { grid[it] } < 4 ) {
                ++sum
            }
        }
        return sum
    }

    fun part2(input: List<String>): Pair<Int, Int> {
        val grid = input.toBooleanGrid('@')
        var sum = 0
        var passes = 0
        do {
            val oldSum = sum
            val allPoints = grid.allPointsFiltered { it }
            for ((point, _) in allPoints) {
                if (point.neighbors().count { grid[it] } < 4 ) {
                    ++sum
                    grid[point] = false
                }
            }
            ++passes
        } while (oldSum < sum)
        return passes to sum
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day04", "test")
    check(part1(testInput) == 13)
    val part2Out = part2(testInput)
    part2Out.println()
    check(part2Out.second == 43)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
