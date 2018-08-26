package kr.co.inmagic.timer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import android.content.Context
import co.kr.inmagic.timer.R
import kotlinx.android.synthetic.main.content_developer__inform.*


class Developer_Inform : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer__inform)

        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        btn_Save_time.setOnClickListener {
            editor.putString("time", editText_setTime.text.toString())
            editor.commit()
        }
    }

}
