package kr.co.inmagic.timer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import android.content.Context
import android.widget.Toast
import co.kr.inmagic.timer.R
import co.kr.inmagic.timer.R.id.btn_Save_time
import kotlinx.android.synthetic.main.activity_developer__inform.*


class Developer_Inform : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer__inform)

        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = pref.edit()

        editText_setTime.setText(pref.getString("time", "초기값"))

        btn_Save_time.setOnClickListener {
            editor.putString("time", editText_setTime.text.toString())
            editor.commit()
            Toast.makeText(applicationContext,"저장되었습니다!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}
