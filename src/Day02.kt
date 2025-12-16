fun main() {
    fun String.parse(): List<LongRange> {
        return split(',').map {
            val (first, second) = it.split('-').map{ it.toLong() }
            first..second
        }
    }

    fun String.startingPrefix(): String {
        return if (length % 2 == 0) {
            substring(0, length / 2)
        } else {
            "1${"0".repeat(length / 2)}"
        }
    }

    fun String.endingPrefix(): String {
        return if (length % 2 == 0) {
            substring(0, length / 2)
        } else {
            "9".repeat(length / 2)
        }
    }

    fun Int.toRange(): LongRange {
        return "1${"".repeat(this-1)}".toLong().. "9".repeat(this).toLong()
    }

    fun LongRange.prefixCandidates() : Collection<Long> {
        val candidates = mutableSetOf<Long>()
        val lenFirst = first.toString().length
        val lenLast = last.toString().length
        for ( i in lenFirst .. lenLast) {
            for (j in 1..i / 2) {
                if (i % j == 0) {
                    candidates.addAll(j.toRange().map { it.toString().repeat(i/j).toLong() })
                }
            }
        }

        return candidates
    }

    fun part1(input: List<LongRange>): Long {
        var sum = 0L
        for (range in input) {
            val startLong = range.first.toString().startingPrefix().toLong()
            val endLong = range.last.toString().endingPrefix().toLong()
            for (i in startLong..endLong) {
                val test = i.toString().repeat(2).toLong()
                if (i.toString().repeat(2).toLong() in range) {
                    sum += test
                }
            }
        }

        return sum
    }

    fun part2(input: List<LongRange>): Long {
        return input.sumOf { range ->
            range.prefixCandidates().sumOf { if (it in range) it else 0 }
        }
    }

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day02", "test").first().parse()
    check(part1(testInput) == 1227775554L)
    check(part2(testInput) == 4174379265L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day02").first().parse()
    part1(input).println()
    part2(input).println()
}
