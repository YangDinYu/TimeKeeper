package com.example.anew.Activity

import android.app.Activity
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