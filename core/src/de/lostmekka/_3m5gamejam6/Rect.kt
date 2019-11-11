package de.lostmekka._3m5gamejam6

import kotlin.random.Random

data class Rect(val x: Int, val y: Int, val w: Int, val h: Int)

fun Rect.contains(x: Int, y: Int) =
    x in ((this.x + 1) until (this.x + w)) && y in ((this.y + 1) until (this.y + h))

fun Rect.touches(x: Int, y: Int) =
    x in (this.x..(this.x + w)) && y in (this.y..(this.y + h))

fun Rect.touchesBorder(x: Int, y: Int) = touches(x, y) && !contains(x, y)

fun Rect.splitHorizontal(): List<Rect> {
    if (h <= 6) return listOf(this)
    val pos = 3 + Random.nextInt(h - 6)
    return listOf(
        Rect(x, y, w, pos),
        Rect(x, y + pos, w, h - pos)
    )
}

fun Rect.splitVertical(): List<Rect> {
    if (w <= 6) return listOf(this)
    val pos = 3 + Random.nextInt(w - 6)
    return listOf(
        Rect(x, y, pos, h),
        Rect(x + pos, y, w - pos, h)
    )
}
