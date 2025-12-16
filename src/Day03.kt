fun main() {
    fun String.maxIndex(start: Int = 0, end: Int = length): Pair<Int, Int> {
        if (end <= start) return 0 to -1
        var max = get(start).digitToInt()
        var ix = start
        for (i in (start + 1) ..< end) {
            val candidate = get(i).digitToInt()
            if (candidate > max) {
                max = candidate
                ix = i
            }
        }
        return ix to max
    }

    fun part1(input: List<String>): Long {
        var sum = 0L
        for (line in input) {
            val (firstIx, firstVal) = line.maxIndex(end = line.length - 1)
            val (_, lastVal) = line.maxIndex(start = firstIx + 1)
            sum += (firstVal * 10) + lastVal
        }

        return sum
    }

    fun String.joltage(start: Int = 0, size: Int, joltage: Long = 0L): Long {
        if (size == 0) return joltage
        val remainder = size - 1
        val (ix, value) = maxIndex(start = start, end = length - remainder)
        return joltage(start = ix + 1, size = remainder, joltage = joltage * 10 + value)
    }

    fun part2(input: List<String>, size: Int = 12): Long {
        var sum = 0L
        for (line in input) {
            sum += line.joltage(size = size)
        }

        return sum
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day03", "test")
    check(part1(testInput) == 357L)
    check(part2(testInput) == 3121910778619)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
