package com.ss.instagramdownloader.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View


object AnimUtils {
    fun animateArtStyle(view: View) {
        AnimatorSet().apply {
            duration = 200
            play(ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.95f, 1.0f))
                .with(ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.95f, 1.0f))
            start()
        }
    }

    fun toastAnimation(view: View) {
        view.apply {
            alpha = 0f
            visibility = View.VISIBLE
        }

        val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        fadeIn.duration = 200

        val animator2 = ObjectAnimator.ofFloat(view, "alpha", 1f, 1f)
        animator2.duration = 3000

        val fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        fadeOut.duration = 200

        val animatorSet = AnimatorSet()
        animatorSet.play(fadeIn).before(animator2)
        animatorSet.play(animator2).before(fadeOut)
        animatorSet.start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                view.apply {
                    alpha = 0f
                    visibility = View.GONE
                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
    }
}