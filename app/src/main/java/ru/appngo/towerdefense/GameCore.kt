package ru.appngo.towerdefense

import android.app.Activity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import ru.appngo.tankstutorial.R
import ru.appngo.towerdefense.activities.REQUEST_CODE
import ru.appngo.towerdefense.activities.ScoreActivity

class GameCore (private val activity: Activity){
    var baseDestroy = false
    var isWin = false

    @Volatile
    var isPlay = false
        get() = field && !baseDestroy && !isWin


    fun destroy(score: Int){
        baseDestroy = true
        pause()
        animateEnd(score)
    }

    private fun animateEnd(score: Int) {
        activity.runOnUiThread {
            val endText = activity.findViewById<TextView>(R.id.game_over_anim_text)
            endText.visibility = View.VISIBLE
            val slideUp = AnimationUtils.loadAnimation(activity, R.anim.slide_up)
            endText.startAnimation(slideUp)
            slideUp.setAnimationListener(object :Animation.AnimationListener{
                override fun onAnimationRepeat(p0: Animation?) {
                }

                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    activity.startActivityForResult(ScoreActivity.createInten(activity, score), REQUEST_CODE)
                }
            })
        }
    }

    fun pause(){
        isPlay = false
    }

    fun startOrPause(){
        isPlay = !isPlay
    }

    fun resumeGame(){
        isPlay = true
    }

    fun win(score: Int){
        isWin = true
        activity.startActivityForResult(ScoreActivity.createInten(activity, score), REQUEST_CODE)

    }

}