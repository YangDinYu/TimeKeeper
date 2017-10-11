package com.example.anew

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.anew.Activity.MainActivity


/**
 * Created by 懵逼的杨定宇 on 2017/8/7.
 */

class MyBroadcastReceiver:BroadcastReceiver(){
    private val ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";

    override fun onReceive(p0: Context?, p1: Intent?) {

        Log.i("test",p1?.action);
        if (ACTION_BOOT == p1?.action) {
            Toast.makeText(p0, "开机完毕~", Toast.LENGTH_LONG).show()
        }


        if (p1?.action == Intent.ACTION_SCREEN_ON){

            Log.i("test","打开屏幕");
        }

        if (p1?.action == Intent.ACTION_USER_PRESENT){
            val intent2 = Intent(p0, MainActivity::class.java)
            val intent = Intent(p0, checkService::class.java)
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            p0?.startActivity(intent2)
            p0?.startService(intent);
            Log.i("test","打开屏幕~~");
        }
    }



}