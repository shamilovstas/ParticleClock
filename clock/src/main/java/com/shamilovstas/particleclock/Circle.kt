package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


data class Circle(
    val center: CartesianPoint = CartesianPoint(),
    var radius: Int = 0
) : Refreshable {

    private val rect = Rect()
    private val rectF = RectF()

    val boundingRect: Rect get() {
        boundingRectF.roundOut(rect)
        return rect
    }

    val boundingRectF: RectF
        get() = rectF.apply {
            left = center.x - radius
            right = center.x + radius
            top = center.y - radius
            bottom = center.y + radius
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

    fun getPoint(angle: Float, point: CartesianPoint) {
        val pointX = center.x + radius * cos(angle.toRadian())
        val pointY = center.y + radius * sin(angle.toRadian())
        point.x = pointX.toFloat()
        point.y = pointY.toFloat()
    }

    override fun refresh() {
        center.refresh()
        radius = 0
    }
}

fun Circle.draw(canvas: Canvas, paint: Paint) {
    canvas.drawCircle(center.x, center.y, radius.toFloat(), paint)
}

fun Number.toRadian() = Math.toRadians(this.toDouble())
fun Number.toDegrees() = Math.toDegrees(this.toDouble())