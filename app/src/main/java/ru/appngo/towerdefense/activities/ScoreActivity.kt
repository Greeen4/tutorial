package ru.appngo.towerdefense.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_score.*
import ru.appngo.tankstutorial.R

const val REQUEST_CODE = 111

class ScoreActivity: AppCompatActivity() {

    companion object{
        const val EXTRA_SCORE = "extra_score"

        fun createInten(context: Context, score:Int):Intent{
            return Intent(context, ScoreActivity::class.java)
                .apply {
                    putExtra(EXTRA_SCORE, score)
                }
        }
    }

    var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)
        score = intent.getIntExtra(EXTRA_SCORE, 0)
        back.setOnClickListener{
            setResult(Activity.RESULT_OK)
            finish()
        }
        score_text_view.text = score.toString()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }
}