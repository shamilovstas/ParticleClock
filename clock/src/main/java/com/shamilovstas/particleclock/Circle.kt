package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import kotlin.math.cos
import kotlin.math.sin


data class Circle(
    val center: Point = Point(),
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
            left = (center.x - radius).toFloat()
            right = (center.x + radius).toFloat()
            top = (center.y - radius).toFloat()
            bottom = (center.y + radius).toFloat()
        }

    var x: Int
        set(value) {
            center.x = value
        }
        get() = center.x

    var y: Int
        set(value) {
            center.y = value
        }
        get() = center.y

    fun getPoint(angle: Float, point: Point) {
        val pointX = center.x + radius * cos(angle.toRadian())
        val pointY = center.y + radius * sin(angle.toRadian())
        point.x = pointX.toInt()
        point.y = pointY.toInt()
    }

    override fun refresh() {
        center.refresh()
        radius = 0
    }
}

fun Circle.draw(canvas: Canvas, paint: Paint) {
    canvas.drawCircle(center.x.toFloat(), center.y.toFloat(), radius.toFloat(), paint)
}

fun Number.toRadian() = Math.toRadians(this.toDouble())