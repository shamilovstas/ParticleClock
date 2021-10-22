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
    override val x: Int, override val y: Int
) : CartesianCoordinate {

    override fun toPolar(): PolarCoordinate {
        val angle = atan2(y.toDouble(), x.toDouble()).toDegrees()
        val radius = sqrt(x.toDouble().pow(2) + y.toDouble().pow(2))

        return PolarPoint(angle.toFloat(), radius.toInt())
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
        var result = x
        result = 31 * result + y
        return result
    }

    override fun toString(): String {
        return "CartesianPoint(x=$x, y=$y)"
    }
}

class PolarPoint(
    override val angle: Float, override val radius: Int
) : PolarCoordinate {
    override fun toCartesian(): CartesianCoordinate {
        val x = radius * cos(angle.toRadian())
        val y = radius * sin(angle.toRadian())
        return CartesianPoint(x.toInt(), y.toInt())
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
        result = 31 * result + radius
        return result
    }

    override fun toString(): String {
        return "PolarPoint(angle=$angle, radius=$radius)"
    }
}


interface CartesianCoordinate {
    val x: Int
    val y: Int

    fun toPolar(): PolarCoordinate
}

interface PolarCoordinate {
    val angle: Float
    val radius: Int

    fun toCartesian(): CartesianCoordinate
}