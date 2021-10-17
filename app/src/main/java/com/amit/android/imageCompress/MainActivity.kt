
package com.amit.android.imageCompress

import android.animation.ValueAnimator
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.keyframe1.*

class MainActivity : AppCompatActivity() {

    private val constraintSet1 = ConstraintSet()
    private val constraintSet2 = ConstraintSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.keyframe1)

        constraintSet1.clone(constraintLayout) //1
        constraintSet2.clone(this, R.layout.activity_main) //2

        switch1.setOnCheckedChangeListener { _, isChecked ->
            switch1.setText(if (isChecked) R.string.round_trip else R.string.one_way)
        }

        departButton.setOnClickListener {
            //1
            val layoutParams = rocketIcon.layoutParams as ConstraintLayout.LayoutParams
            val startAngle = layoutParams.circleAngle
            val endAngle = startAngle + (if (switch1.isChecked) 360 else 180)

            //2
            val anim = ValueAnimator.ofFloat(startAngle, endAngle)
            anim.addUpdateListener { valueAnimator ->

                //3
                val animatedValue = valueAnimator.animatedValue as Float
                val layoutParams = rocketIcon.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.circleAngle = animatedValue
                rocketIcon.layoutParams = layoutParams

                //4
                rocketIcon.rotation = (animatedValue % 360 - 270)
            }
            //5
            anim.duration = if (switch1.isChecked) 2000 else 1000

            //6
            anim.interpolator = LinearInterpolator()
            anim.start()
        }
    }

    override fun onEnterAnimationComplete() { //1
        super.onEnterAnimationComplete()

        constraintSet2.clone(this, R.layout.activity_main) //2

        //apply the transition
        val transition = AutoTransition() //3
        transition.duration = 1000 //4
        TransitionManager.beginDelayedTransition(constraintLayout, transition) //5

        constraintSet2.applyTo(constraintLayout) //6
    }
}
