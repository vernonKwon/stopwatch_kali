package kr.co.inmagic.timer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import co.kr.inmagic.timer.R
import kotlinx.android.synthetic.main.activity_main.*
import android.content.SharedPreferences
import android.widget.Toast


class MainActivity : AppCompatActivity()/*Activity()*/ {


    val color_start_button = Color.parseColor("#1F3421")
    val color_stop_button = Color.parseColor("#391816")
    val color_laptime_button = Color.parseColor("#391816")

    var now: Long = 0
    var outTime: Long = 0
    var easy_outTime: String = ""

    private val Init = 0
    private val Run = 1
    private val Pause = 2

    private var cur_Status = Init //현재의 상태를 저장할변수를 초기화함.
    private var myCount = 1
    private var myBaseTime: Long = 0
    private var myPauseTime: Long = 0

    var myTimer = object : Handler() {
        /**
        이거스은 재귀 함수. 반복문 없이 어떻게 시간이 흐르나 한참 못찾고 있었는데 재귀네... ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ 에라이...
         */
        override fun handleMessage(msg: Message) {
            time_out.text = timeOut


            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
            this.sendEmptyMessage(0)
        }
    }


    val timeOut: String
        get() {
            now = SystemClock.elapsedRealtime() //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
            outTime = now - myBaseTime
            //format_flag = outTime / 1000 / 60 // 시간
            //easy_outTime = String.format("%02d:%02d.%02d", outTime / 1000 / 60, outTime / 1000 % 60/*분*/, outTime % 1000 / 10/*초*/)
            easy_outTime = String.format("%02d.%02d", outTime / 1000/*분*/, outTime % 1000 / 10/*초*/)
            return easy_outTime
        }

    lateinit var pref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        Toast.makeText(applicationContext, pref.getString("time", "초기값"), Toast.LENGTH_LONG).show()




        showDeveloperInfomation.setOnLongClickListener {
            startActivity(Intent(this, Developer_Inform::class.java))

            return@setOnLongClickListener true
        }


       //시작버튼을 클릭했을때 현재 상태값에 따라 다른 동작을 할수있게끔 구현.
        btn_start.setOnClickListener {
            when (cur_Status) {
                Init -> {
                    myBaseTime = SystemClock.elapsedRealtime()
                    println(myBaseTime)
                    //myTimer이라는 핸들러를 빈 메세지를 보내서 호출
                    myTimer.sendEmptyMessage(0)
                    btn_start.text = "중단" //버튼의 문자"시작"을 "중단"으로 변경
                    btn_rec.isEnabled = true //기록버튼 활성
                    cur_Status = Run //현재상태를 런상태로 변경
                }
                Run -> {
                    myTimer.removeMessages(0) //핸들러 메세지 제거
                    myPauseTime = SystemClock.elapsedRealtime()
                    btn_start.text = "시작"
                    btn_start.setBackgroundColor(color_start_button)
                    btn_rec.text = "재설정"
                    cur_Status = Pause
                }
                Pause -> {
                    val now = SystemClock.elapsedRealtime()
                    myTimer.sendEmptyMessage(0)
                    myBaseTime += now - myPauseTime
                    btn_start.text = "중단"
                    btn_start.setBackgroundColor(color_stop_button)
                    btn_rec.text = "랩"
                    btn_rec.setBackgroundColor(color_laptime_button)
                    cur_Status = Run
                }
            }
        }

        btn_start.setBackgroundColor(color_start_button) // 초기
        btn_start

        btn_rec.setOnClickListener {
            when (cur_Status) {
                Run -> {
                    var str = record.text.toString()
                    str += String.format("\n랩 %d. %s", myCount, timeOut)
                    record.text = str.trim()
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    myCount++ //카운트 증가
                }
                Pause -> {
                    //핸들러를 멈춤
                    myTimer.removeMessages(0)

                    btn_start.text = "시작"
                    btn_rec.text = "랩"
                    time_out.text = "00.00"

                    cur_Status = Init
                    myCount = 1
                    record.text = ""
                    btn_rec.isEnabled = false
                }
            }
        }
    }

    override fun onResume() {
        Toast.makeText(applicationContext, pref.getString("time", "초기값"), Toast.LENGTH_LONG).show()
        super.onResume()
    }
}

