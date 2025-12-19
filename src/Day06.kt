enum class Operator(val symbol: Char, val predicate: (Long, Long) -> Long) {
    ADD('+', { a, b -> a + b }),
    MULTIPLY('*', { a, b -> a * b });
    companion object {
        private val operatorMap = entries.associateBy { it.symbol }
        fun fromChar(char: Char): Operator? = operatorMap[char]
    }
    operator fun invoke(a: Long, b: Long): Long = predicate(a,b)
}

fun main() {
    val whitespaceRegex = "\\s+".toRegex()
    fun String.split() = split(whitespaceRegex)

    fun part1(input: List<String>): Long {
        val reversed = input.reversed()
        val iterator = reversed.iterator()
        val operators = iterator.next().trim().split().mapNotNull { Operator.fromChar(it[0]) }
        val totalStrings = iterator.next().trim().split()
        val totals = totalStrings.map { it.toLong() }.toLongArray()
        for (line in iterator) {
            for ((ix, num) in line.trim().split().map { it.toLong() }.withIndex()) {
                totals[ix] = operators[ix](totals[ix], num)
            }
        }

        return totals.sum()
    }

    fun part2(input: List<String>): Long {
        // build the grid
        var total = 0L
        val charGrid = Grid<Char>(' ')
        for ((col, line) in input.withIndex()) {
            for ((row, char) in line.withIndex()) {
                charGrid[col, row] = char
            }
        }
        val opCol = charGrid.maxX
        var currentTotal = 0L
        var currentOperator = Operator.ADD // arbitrary, will get set below
        for (row in 0 .. charGrid.maxY) {
            Operator.fromChar(charGrid[opCol, row])?.let {
                total += currentTotal
                currentOperator = it
                if (it == Operator.MULTIPLY) {
                    currentTotal = 1L
                } else {
                    currentTotal = 0L
                }
            }
            val new = (charGrid.minY .. charGrid.maxX).map { charGrid[it, row] }.filter(Char::isDigit).joinToString("").toLongOrNull()?.let { it } ?: continue
            currentTotal = currentOperator(currentTotal, new)
        }
        return total + currentTotal
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day06", "test")
    check(part1(testInput) == 4277556L)
    val part2Out = part2(testInput)
    part2Out.println()
    check(part2Out == 3263827L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
