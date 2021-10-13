package com.shamilovstas.particleclock

data class Point(
    var x: Int = 0,
    var y: Int = 0
) : Refreshable {
    override fun refresh() {
        x = 0
        y = 0
    }
}