package kr.co.inmagic.timer

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.widget.ScrollView
import co.kr.inmagic.timer.R
import kotlinx.android.synthetic.main.activity_main.*
import android.content.SharedPreferences
import android.graphics.Color
import android.os.*
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import android.os.CountDownTimer


class MainActivity : AppCompatActivity()/*Activity()*/ {

    val textcolor_start = Color.parseColor("#4abd36")
    val textcolor_stop = Color.parseColor("#f63f35")

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

    private var setismagic = false

    var myTimer = object : Handler() {
        /**
        이거스은 재귀 함수. 반복문 없이 어떻게 시간이 흐르나 한참 못찾고 있었는데 재귀네... ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ 에라이...
         */
        override fun handleMessage(msg: Message) {
            time_out.text = timeOut
            /**
             * sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
             */
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

    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref = getSharedPreferences("pref", Context.MODE_PRIVATE)

        layout_setismagic.setOnLongClickListener {
            var a: String
            if (setismagic) {
                setismagic = false
                a = "일반"
                //vibrator.vibrate(500000)
            } else {
                setismagic = true
                //vibrator.vibrate(100000)
                a = "마술"
            }
           var toast = Toast.makeText(applicationContext, a, Toast.LENGTH_SHORT)
            //toast.show()

            object : CountDownTimer(1001, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    toast.show()
                }

                override fun onFinish() {
                    toast.cancel()
                }

            }.start()

            return@setOnLongClickListener true
        }

        showDeveloperInfomation.setOnLongClickListener {
            startActivity(Intent(this, Developer_Inform::class.java))

            return@setOnLongClickListener true
        }

        btn_start.background = ContextCompat.getDrawable(this,R.drawable.btn_start_round_2)
        btn_start.setTextColor(textcolor_start)

        btn_rec.isEnabled = false

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

                    btn_start.background = ContextCompat.getDrawable(this, R.drawable.btn_start_round_1)
                    btn_start.setTextColor(textcolor_stop)
                }
                Run -> {
                    myTimer.removeMessages(0) //핸들러 메세지 제거
                    myPauseTime = SystemClock.elapsedRealtime()
                    btn_start.text = "시작"
                    btn_start.setTextColor(textcolor_start)

                    //btn_start.setBackgroundColor(color_start_button)
                    btn_rec.text = "재설정"
                    cur_Status = Pause
                    btn_rec.isEnabled = true

                    btn_start.background = ContextCompat.getDrawable(this, R.drawable.btn_start_round_2)

                    if (setismagic) {
                        time_out.text = pref.getString("time", timeOut)
                    }
                }
                Pause -> {
                    val now = SystemClock.elapsedRealtime()
                    myTimer.sendEmptyMessage(0)
                    myBaseTime += now - myPauseTime
                    btn_start.text = "중단"
                    btn_start.setTextColor(textcolor_stop)

                    btn_start.background = ContextCompat.getDrawable(this, R.drawable.btn_start_round_1)

                    btn_rec.text = "랩"

                    cur_Status = Run
                }
            }
        }

        btn_rec.setOnClickListener {
            when (cur_Status) {
                Run -> {
                    var str = record.text.toString()
                    str += String.format("\n랩 %d : %s", myCount, timeOut)
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

}

