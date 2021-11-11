package com.shamilovstas.particleclock

import android.animation.Animator
import android.animation.ValueAnimator

fun animateFloat(pair: Pair<Float, Float>, block: ValueAnimator.() -> Unit): ValueAnimator {
    val animator = ValueAnimator.ofFloat(pair.first, pair.second)
    block(animator)
    return animator
}

fun animateFloat(from: Float, to: Float, block: ValueAnimator.() -> Unit): ValueAnimator {
    return animateFloat(from to to, block)
}

fun animateInt(pair: Pair<Int, Int>, block: ValueAnimator.() -> Unit): ValueAnimator {
    val animator = ValueAnimator.ofInt(pair.first, pair.second)
    block(animator)
    return animator
}

fun animationAdapter(
    onStart: (Animator) -> Unit = {},
    onEnd: (Animator) -> Unit = {},
    onCancel: (Animator) -> Unit = {},
    onRepeat: (Animator) -> Unit = {},
): Animator.AnimatorListener {
    return object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
            onStart(animation!!)
        }

        override fun onAnimationEnd(animation: Animator?) {
            onEnd(animation!!)
        }

        override fun onAnimationCancel(animation: Animator?) {
            onCancel(animation!!)
        }

        override fun onAnimationRepeat(animation: Animator?) {
            onRepeat(animation!!)
        }
    }
}