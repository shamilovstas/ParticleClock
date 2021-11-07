package com.shamilovstas.particleclock

import kotlin.math.*


class CartesianPoint(
    override var x: Float = 0f, override var y: Float = 0f
) : CartesianCoordinate, Refreshable {

    constructor(x: Int, y: Int): this(x.toFloat(), y.toFloat())

    override fun toPolar(): PolarCoordinate {
        return PolarPoint().also {
            toPolar(it)
        }
    }

    override fun toPolar(obj: PolarCoordinate) {
        val angle = Angle((atan2(y.toDouble(), x.toDouble()).toDegrees()).toFloat())
        val radius = Radius(hypot(x, y))
        obj.angle = angle
        obj.radius = radius
    }

    override fun copyFrom(other: CartesianCoordinate) {
        this.x = other.x
        this.y = other.y
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
    override var radius: Radius = Radius(0f)
) : PolarCoordinate, Refreshable {

    override var angle: Angle = Angle(0f)
        set(value) {
            field = value
            val pi = field.toRadians()
            angleCos = cos(pi)
            angleSin = sin(pi)
        }


    private var angleCos: Double = 1.0
    private var angleSin: Double = 0.0
    override fun toCartesian(): CartesianCoordinate {
        return CartesianPoint().also {
            toCartesian(it)
        }
    }

    override fun toCartesian(obj: CartesianCoordinate) {
        val x = radius.value * angleCos
        val y = radius.value * angleSin
        obj.x = x.toFloat()
        obj.y = y.toFloat()
    }

    override fun copyFrom(other: PolarCoordinate) {
        this.angle = other.angle
        this.radius = other.radius
    }

    override fun refresh() {
        angle = Angle(0f)
        radius = Radius(0f)
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

    fun copyFrom(other: CartesianCoordinate)
    fun toPolar(): PolarCoordinate
    fun toPolar(obj: PolarCoordinate)
}

interface PolarCoordinate {
    var angle: Angle
    var radius: Radius

    fun copyFrom(other: PolarCoordinate)
    fun toCartesian(): CartesianCoordinate
    fun toCartesian(obj: CartesianCoordinate)
}