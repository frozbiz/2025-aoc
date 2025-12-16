import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.absoluteValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(folder: String, name: String = "input") = Path("input/$folder/$name.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

/// Multipurpose point class
data class Point(
    val x: Int,
    val y: Int
) {
    enum class Relationship {
        HORIZONTAL, VERTICAL, DIAGONAL, SKEW, EQUAL
    }

    /// Operators
    operator fun plus(other: Point): Point {
        return Point(other.x + x, other.y + y)
    }

    operator fun times(multiplier: Int): Point {
        return Point(x * multiplier, y * multiplier)
    }

    operator fun plus(direction: CardinalDirection): Point {
        return when (direction) {
            CardinalDirection.NORTH -> Point(x, y - 1)
            CardinalDirection.EAST -> Point(x + 1, y)
            CardinalDirection.SOUTH -> Point(x, y + 1)
            CardinalDirection.WEST -> Point(x - 1, y)
        }
    }

    val CardinalDirection.unitPoint: Point
        get() {
            return when (this) {
                CardinalDirection.NORTH -> Point(0, -1)
                CardinalDirection.EAST -> Point(1, 0)
                CardinalDirection.SOUTH -> Point(0, 1)
                CardinalDirection.WEST -> Point(-1, 0)
            }
        }

    companion object {
        fun fromString(str: String): Point{
            val (x, y) = str.split(',').map { it.trim().toInt() }
            return Point(x,y)
        }

        fun fromStringOrNull(str: String): Point? {
            return try { fromString(str) } catch (e: Exception) { null }
        }

        fun adjacentToLine(start: Point, end: Point): Set<Point> {
            // simple and stupid
            val line = start to end
            val allPoints = line.flatMap { it.pointsAdjacent() }.toSet()
            return allPoints - line
        }
    }

    fun relationshipTo(point: Point): Relationship {
        return if (point.x == x) {
            if (point.y == y) {
                Relationship.EQUAL
            } else {
                Relationship.VERTICAL
            }
        } else if (point.y == y) {
            Relationship.HORIZONTAL
        } else if ((point.y - y).absoluteValue == (point.x - x).absoluteValue) {
            Relationship.DIAGONAL
        } else {
            Relationship.SKEW
        }
    }

    infix fun to(point: Point): Set<Point> {
        val relationship = relationshipTo(point)
        return when (relationship) {
            Relationship.EQUAL, Relationship.VERTICAL -> {
                if (point.y >= y) {
                    (y..point.y).map { Point(x, it) }
                } else {
                    (y downTo point.y).map { Point(x, it) }
                }
            }
            Relationship.HORIZONTAL -> {
                if (point.x >= x) {
                    (x..point.x).map { Point(it, y) }
                } else {
                    (x downTo point.x).map { Point(it, y) }
                }
            }
            Relationship.DIAGONAL -> {
                val xRange =
                    if (point.x >= x) {
                        (x..point.x)
                    } else {
                        (x downTo point.x)
                    }
                val yRange =
                    if (point.y >= y) {
                        (y..point.y)
                    } else {
                        (y downTo point.y)
                    }
                (xRange zip yRange).map { (x,y) -> Point(x, y) }
            }
            Relationship.SKEW -> {
                throw IllegalArgumentException("Points are non-linear")
            }
        }.toSet()
    }

    fun neighbors(includeDiagonal: Boolean = true): Set<Point> = pointsAdjacent(!includeDiagonal)

    fun pointsAdjacent(cardinalOnly: Boolean = false): Set<Point> {
        val cardinalPoints = setOf(
            Point(x-1, y),
            Point(x+1, y),
            Point(x, y-1),
            Point(x, y+1)
        )
        if (cardinalOnly) return cardinalPoints

        val diagonalPoints = setOf(
            Point(x-1, y-1),
            Point(x-1, y+1),
            Point(x+1, y-1),
            Point(x+1, y+1)
        )
        return cardinalPoints + diagonalPoints
    }

    fun manhattanDistanceTo(point: Point): Int {
        return (point.x - x).absoluteValue + (point.y - y).absoluteValue
    }

    override fun toString(): String {
        return "Point(${x}, ${y})"
    }
}

fun IntGrid(default: Int = 0) = Grid(default)

fun BooleanGrid(default: Boolean = false) = Grid(default)

class Grid<T>(
    val default: T
) {
    operator fun get(x: Int, y: Int): T {
        return grid[y]?.get(x) ?: default
    }

    private fun updateBounds(x: Int, y: Int) {
        if (x < minX) {
            minX = x
        } else if (x > maxX) {
            maxX = x
        }
        if (y < minY) {
            minY = y
        } else if (y > maxY) {
            maxY = y
        }
    }

    operator fun set(x: Int, y: Int, value: T) {
        grid.getOrPut(y) { mutableMapOf() }[x] = value
        updateBounds(x, y)
    }

    operator fun get(point: Point): T {
        return get(point.x, point.y)
    }

    operator fun set(point: Point, value: T) {
        set(point.x, point.y, value)
        updateBounds(point.x, point.y)
    }

    operator fun contains(point: Point): Boolean {
        val xBounds = minX..maxX
        val yBounds = minY..maxY
        return point.x in xBounds && point.y in yBounds
    }

    private val grid = mutableMapOf<Int, MutableMap<Int, T>>()

    override fun toString(): String {
        return grid.toString()
    }

    fun allPoints(): List<Pair<Point, T>> {
        return grid.flatMap { (y, dict) -> dict.map { (x, value) -> Pair(Point(x,y), value) } }
    }

    fun allPointsFiltered(predicate: (T) -> Boolean): List<Pair<Point, T>> {
        return grid.flatMap { (y, dict) -> dict.filter { (_, value) -> predicate(value) }.map { (x, value) -> Pair(Point(x,y), value) } }
    }

    var maxX: Int = 0
    var minX: Int = 0
    var maxY: Int = 0
    var minY: Int = 0

    override fun hashCode(): Int {
        return default.hashCode() + allPoints().sumOf {
            if (it.second != default) {
                it.second.hashCode() + it.first.hashCode()
            } else 0
        }
    }

    override operator fun equals(other: Any?): Boolean {
        if (other !is Grid<*>) return false
        if (other.default != default) return false
        // check our points in theirs
        for (point in allPoints()) {
            if (other[point.first] != point.second) return false
        }

        // and theirs in ours
        for (point in other.allPoints()) {
            if (get(point.first) != point.second) return false
        }

        return true
    }

    fun copy(minimumCopy: Boolean = true): Grid<T> {
        val duplicate = Grid(default)
        // REVIEW: Should I make sure that we maintain minX/minY? Should we handle the bounds of a
        // grid differently?
        for (y in grid) {
            for (x in y.value) {
                if (!minimumCopy || x.value != default) {
                    duplicate[x.key, y.key] = x.value
                }
            }
        }

        return duplicate
    }
}

fun LongRange(start: Long, length: Long): LongRange = start..<(start+length)

data class MultiRange (
    val rangeList: MutableList<LongRange> = mutableListOf()
){
    operator fun plusAssign(range: LongRange) {
        add(range)
    }

    operator fun plusAssign(newRange: MultiRange) {
        for (range in newRange.rangeList) {
            add(range)
        }
    }

    operator fun plus(range: LongRange): MultiRange {
        val ret = MultiRange()
        ret.rangeList.addAll(rangeList)
        ret.add(range)
        return ret
    }

    fun add(multiRange: MultiRange) {
        for (range in multiRange.rangeList) {
            add(range)
        }
    }

    fun add(range: LongRange) {
        if (range.size <= 0) return
        var ix = 0
        var firstOverlapIx: Int? = null
        while ((ix < rangeList.size) && (rangeList[ix].first - 1 <= range.last)) {
            // if they overlap
            if ((range.first - 1 <= rangeList[ix].last) && (firstOverlapIx == null)) {
                firstOverlapIx = ix
            }
            ++ix
        }
        if (firstOverlapIx == null) {
            rangeList.add(ix, range)
        } else {
            val lastOverlap = ix - 1
            val newRange = minOf(range.first, rangeList[firstOverlapIx].first)..maxOf(range.last, rangeList[lastOverlap].last)
            rangeList[firstOverlapIx] = newRange
            if (firstOverlapIx < lastOverlap) {
                rangeList.subList(firstOverlapIx+1, ix).clear()
            }
        }
    }

    fun first(): Long = rangeList.first().first

    fun firstOrNull(): Long? = rangeList.firstOrNull()?.first

    val LongRange.size: Long
        get() = maxOf(last - first + 1, 0)

    operator fun minusAssign(points: Collection<Long>) {
        for (point in points) {
            subtract(point)
        }
    }

    operator fun minusAssign(point: Long) {
        subtract(point)
    }

    fun subtract(point: Long) {
        val ix = rangeList.indexOfFirst { point in it }
        if (ix >= 0) {
            val range = rangeList[ix]
            if (range.count() == 1) {
                rangeList.removeAt(ix)
            } else if (range.first == point) {
                rangeList[ix] = (point + 1)..range.last
            } else if (range.last == point) {
                rangeList[ix] = range.first..<point
            } else {
                rangeList[ix] = range.first..<point
                rangeList.add(ix+1, (point + 1)..range.last)
            }
        }
    }

    fun subtract(range: LongRange): MultiRange {
        val startIx = rangeList.indexOfFirst { range.first <= it.last }
        val endIx = rangeList.indexOfLast { range.last >= it.first }
        if (startIx < 0 || endIx < 0) return MultiRange()

        val firstValue = rangeList[startIx].first
        val lastValue = rangeList[endIx].last

        val ret = MultiRange(rangeList.subList(startIx, endIx+1).toMutableList())
        ret.trimToRange(range)
        rangeList.subList(startIx, endIx+1).clear()
        if (lastValue > range.last) {
            rangeList.add(startIx, range.last+1..lastValue)
        }
        if (firstValue < range.first) {
            rangeList.add(startIx, firstValue..<range.first)
        }

        return ret
    }

    fun clear() {
        rangeList.clear()
    }

    operator fun contains(v: Int): Boolean {
        return rangeList.any { v in it }
    }

    fun count(): Int {
        return rangeList.sumOf { it.count() }
    }

    fun trimToRange(range: LongRange) {
        rangeList.removeIf { it.last < range.first || range.last < it.first }
        if (rangeList.isEmpty()) return
        val first = rangeList.first()
        if (first.first < range.first) {
            rangeList[0] = range.first .. first.last
        }
        val last = rangeList.last()
        if (last.last > range.last) {
            rangeList[rangeList.size - 1] = last.first .. range.last
        }
    }
}

fun <K> MutableMap<K, Int>.increment(index: K, step: Int = 1): Int {
    val next = getOrDefault(index, 0) + step
    set(index, next)
    return next
}

enum class CardinalDirection {
    NORTH, EAST, SOUTH, WEST;

    val opposite: CardinalDirection
        get() {
            return when (this) {
                NORTH -> SOUTH
                SOUTH -> NORTH
                EAST -> WEST
                WEST -> EAST
            }
        }
}

fun Point.pointToThe(direction: CardinalDirection): Point {
    return when (direction) {
        CardinalDirection.NORTH -> Point(x, y - 1)
        CardinalDirection.EAST -> Point(x + 1, y)
        CardinalDirection.SOUTH -> Point(x, y + 1)
        CardinalDirection.WEST -> Point(x - 1, y)
    }
}

fun CardinalDirection.char(): Char {
    return when(this) {
        CardinalDirection.NORTH -> '^'
        CardinalDirection.EAST -> '>'
        CardinalDirection.SOUTH -> 'v'
        CardinalDirection.WEST -> '<'
    }
}

fun Grid<CardinalDirection>.print() {
    val xStart = minX
    val xEnd = maxX
    for (row in minY..maxY) {
        (xStart..xEnd).forEach { print(get(it, row).char()) }
        kotlin.io.println()
    }
}
