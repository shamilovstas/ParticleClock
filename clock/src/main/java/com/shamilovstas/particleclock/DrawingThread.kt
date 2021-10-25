package com.shamilovstas.particleclock

import android.graphics.Canvas
import android.view.SurfaceHolder

class DrawingThread(
    private val surfaceHolder: SurfaceHolder,
    private val drawingSurface: ParticleClock
) : Thread() {
    override fun run() {

        while (isInterrupted.not()) {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    drawingSurface.draw(canvas)
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}
