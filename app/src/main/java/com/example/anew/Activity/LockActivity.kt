package com.example.anew.Activity

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import com.example.anew.Message
import com.example.anew.R
import kotlinx.android.synthetic.main.activity_lock.*
import rx.Observable
import rx.Subscriber
import java.util.*
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import cn.bmob.sms.BmobSMS
import cn.bmob.sms.exception.BmobException
import cn.bmob.sms.listener.RequestSMSCodeListener
import cn.bmob.v3.Bmob
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import cn.bmob.sms.listener.VerifySMSCodeListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.messageforunlock_layout.*


/**
 * Created by 懵逼的杨定宇 on 2017/8/17.
 */
class LockActivity : Activity() {

   class myTimerTask() : TimerTask(){
       override fun run() {

       }
   }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock);

        BmobSMS.initialize(this,"3c9eebb309b4a67465abda4522db94e1");
        Bmob.initialize(this,"3c9eebb309b4a67465abda4522db94e1");

        lock_title.text = "${Message.missionListToday[0].missonName} " +
                "(${Message.missionListToday[0].startHour+(Message.missionListToday[0].duration/60)}:${Message.missionListToday[0].startMin+(Message.missionListToday[0].duration%60)}结束)";
        lock_detail.text = Message.missionListToday[0].missonDetail;

        pausebtn.setOnClickListener({

            var timer : Timer = Timer();
            var observable :Observable<String>  ;

            //锁定，但非暂停（休息）时点击按钮，进入暂停逻辑
            if (Message.isPause == false) {
                //启动定时器，每秒更新一次剩余的休息时间
                timer.schedule(object :TimerTask(){
                    override fun run() {
                        runOnUiThread(Runnable {
                            pausebtn.progress = (Message.restTime /300.0 * 100).toInt();
                            pausebtn.text = "剩余 ${Message.restTime /60}:${Message.restTime %60}"
                        })
                    }
                },0,1000);


                //利用rxjava进行网络操作
                 observable = Observable.create(object : Observable.OnSubscribe<String> {
                    override fun call(t: Subscriber<in String>?) {

                        t?.onNext("a");

                    }

                });
                observable.subscribe(object : Subscriber<String>() {
                    override fun onError(e: Throwable?) {

                    }

                    override fun onCompleted() {

                    }

                    override fun onNext(t: String?) {
                        Log.i("test",t);
                    }
                }).unsubscribe();
            }

            //锁定，在暂停（休息）时间点击按钮，结束UI更新进入正常锁定逻辑
            if (Message.isPause == true){
                //取消每秒更新UI显示剩余时间
                timer.cancel();
            }

            Message.isPause = !Message.isPause;
            Log.i("","change to pause");
        });


        unlock.setOnClickListener({
            var builder = AlertDialog.Builder(this@LockActivity);
            val view = LayoutInflater.from(this@LockActivity).inflate(R.layout.messageforunlock_layout, null, false);
            builder.setView(view);
            builder.setTitle("短信解锁")

            var sendMessageButton = view.findViewById(R.id.sendMessage) as Button;
            var yanzhengma = view.findViewById(R.id.jiesuoyanzhengma) as EditText;
            sendMessageButton.setOnClickListener({
                BmobSMS.requestSMSCode(this@LockActivity,"15528351376","默认",object :RequestSMSCodeListener(){
                    override fun done(p0: Int?, p1: BmobException?) {

                        Toast.makeText(this@LockActivity,"发送短信成功",Toast.LENGTH_SHORT).show()
                    }

                })

            })

            builder.setPositiveButton("解锁",object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    BmobSMS.verifySmsCode(this@LockActivity, "15528351376", yanzhengma.text.toString(), object : VerifySMSCodeListener() {

                        override fun done(ex: BmobException?) {

                            if (ex == null) {//短信验证码已验证成功
                                Toast.makeText(this@LockActivity,"验证成功，解锁",Toast.LENGTH_SHORT).show()
                                Message.isLock = false;
                                Message.missionList[0].isLock = false;
                                startActivity(Intent(this@LockActivity,MainActivity::class.java));

                            } else {
                                Log.i("bmob", "验证失败：code =" + ex.errorCode + ",msg = " + ex.localizedMessage)
                            }
                        }
                    })

                }
            })

            builder.setNegativeButton("取消",object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {

                }

            })

            builder.create().show();
        })


    }



    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Toast.makeText(this,"back", Toast.LENGTH_LONG).show();
            //屏蔽之后的操作

            return true;
        }





        return super.onKeyDown(keyCode, event)
    }


}

