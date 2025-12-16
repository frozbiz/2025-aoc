fun main() {

    fun List<String>.parse(): Pair<MultiRange, List<Long>> {
        val range = MultiRange()
        val numbers = mutableListOf<Long>()
        val iterator = this.iterator()
        while (iterator.hasNext()) {
            val line = iterator.next()
            if (line.isBlank()) break
            val (start, end) = line.split('-').map { it.toLong() }
            range.add(start..end)
        }
        while (iterator.hasNext()) {
            val line = iterator.next()
            numbers.add(line.toLong())
        }
        return range to numbers
    }

    fun part1(input: List<String>): Int {
        val (fresh, ingredients) = input.parse()
        return ingredients.count { fresh.contains(it) }
    }

    fun part2(input: List<String>): Long {
        val (fresh, _) = input.parse()
        return fresh.size
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day05", "test")
    check(part1(testInput) == 3)
    val part2Out = part2(testInput)
    part2Out.println()
    check(part2Out == 14L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
