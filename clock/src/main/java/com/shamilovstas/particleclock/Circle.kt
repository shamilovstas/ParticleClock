package com.shamilovstas.particleclock

import android.graphics.Rect
import android.graphics.RectF
import kotlin.math.roundToInt


data class Circle(
    val center: CartesianPoint = CartesianPoint(),
    var radius: Radius = Radius(0f)
) : Refreshable {

    private val rect = Rect()
    private val rectF = RectF()

    val boundingRect: Rect get() {
        boundingRectF.roundOut(rect)
        return rect
    }

    val boundingRectF: RectF
        get() = rectF.apply {
            left = center.x - radius.value
            right = center.x + radius.value
            top = center.y - radius.value
            bottom = center.y + radius.value
        }

    var x: Int
        set(value) {
            center.x = value.toFloat()
        }
        get() = center.x.roundToInt()

    var y: Int
        set(value) {
            center.y = value.toFloat()
        }
        get() = center.y.roundToInt()

    override fun refresh() {
        center.refresh()
        radius = Radius(0f)
    }
}

fun Number.toDegrees() = Math.toDegrees(this.toDouble())