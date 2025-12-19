fun main() {
    fun part1(input: List<String>): Int {
        var currRow = input[0].map { it == 'S' }.toBooleanArray()
        var nextRow = BooleanArray(currRow.size)
        var splits = 0
        for (line in input.drop(1)) {
            for ((ix, value) in currRow.withIndex()) {
                if (!value) continue
                // if we hit a splitter, split.
                if (line[ix] == '^') {
                    nextRow[ix + 1] = true
                    nextRow[ix - 1] = true
                    ++splits
                } else {
                    // otherwise, continue
                    nextRow[ix] = true
                }
            }
            currRow = nextRow
            nextRow = BooleanArray(currRow.size)
        }
        return splits
    }

    fun part2(input: List<String>): Long {
        val grid = Grid(1L)

        for ((row, line) in input.withIndex().reversed()) {
            for ((col, value) in line.withIndex()) {
                when (value) {
                    '.' -> grid[row, col] = grid[row + 1, col]
                    '^' -> grid[row, col] = grid[row + 1, col + 1] + grid[row + 1, col - 1]
                    'S' -> return grid[row + 1, col]
                }
            }
        }
        return -1 // There was an error -- we didn't see 'S'
    }

    // Read a large test input from the `input/Dayxx/test.txt` file:
    val testInput = readInput("Day07", "test")
    check(part1(testInput) == 21)
    val part2Out = part2(testInput)
//    part2Out.println()
    check(part2Out == 40L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
