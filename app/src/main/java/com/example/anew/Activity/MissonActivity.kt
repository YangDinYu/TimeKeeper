package com.example.anew.Activity

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.Toast
import com.example.anew.Message
import com.example.anew.R
import kotlinx.android.synthetic.main.activity_lock.*
import kotlinx.android.synthetic.main.activity_mission.*
import rx.Observable
import rx.Subscriber
import java.util.*

/**
 * Created by 懵逼的杨定宇 on 2017/8/17.
 */
class MissonActivity : Activity() {

   class myTimerTask() : TimerTask(){
       override fun run() {

       }
   }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission);

        lock_title2.text = "${Message.missionListToday[0].missonName} " +
                "(${Message.missionListToday[0].startHour+(Message.missionListToday[0].duration/60)}:${Message.missionListToday[0].startMin+(Message.missionListToday[0].duration%60)}结束)";
        lock_detail2.text = Message.missionListToday[0].missonDetail;

    }






}