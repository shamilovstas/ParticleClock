package com.shamilovstas.particleclock

import kotlin.math.*

data class Point(
    var x: Int = 0,
    var y: Int = 0
) : Refreshable {

    fun copyFrom(other: Point) {
        x = other.x
        y = other.y
    }

    override fun refresh() {
        x = 0
        y = 0
    }
}


class CartesianPoint(
    override var x: Float = 0f, override var y: Float = 0f
) : CartesianCoordinate, Refreshable {

    override fun toPolar(): PolarCoordinate {
        return PolarPoint().also {
            toPolar(it)
        }
    }

    override fun toPolar(obj: PolarCoordinate) {
        val angle = atan2(y.toDouble(), x.toDouble()).toDegrees()
        val radius = sqrt(x.toDouble().pow(2) + y.toDouble().pow(2))
        obj.angle = angle.toFloat()
        obj.radius = radius.toFloat()
    }


    override fun refresh() {
        x = 0f
        y = 0f
    }

    override fun toString(): String {
        return "CartesianPoint(x=$x, y=$y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CartesianPoint

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}

class PolarPoint(
    override var angle: Float = 0f, override var radius: Float = 0f
) : PolarCoordinate, Refreshable {
    override fun toCartesian(): CartesianCoordinate {
        return CartesianPoint().also {
            toCartesian(it)
        }
    }

    override fun toCartesian(obj: CartesianCoordinate) {
        val x = radius * cos(angle.toRadian())
        val y = radius * sin(angle.toRadian())
        obj.x = x.toFloat()
        obj.y = y.toFloat()
    }

    override fun refresh() {
        angle = 0f
        radius = 0f
    }

    override fun toString(): String {
        return "PolarPoint(angle=$angle, radius=$radius)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PolarPoint

        if (angle != other.angle) return false
        if (radius != other.radius) return false

        return true
    }

    override fun hashCode(): Int {
        var result = angle.hashCode()
        result = 31 * result + radius.hashCode()
        return result
    }
}


interface CartesianCoordinate {
    var x: Float
    var y: Float

    fun toPolar(): PolarCoordinate
    fun toPolar(obj: PolarCoordinate)
}

interface PolarCoordinate {
    var angle: Float
    var radius: Float

    fun toCartesian(): CartesianCoordinate
    fun toCartesian(obj: CartesianCoordinate)
}