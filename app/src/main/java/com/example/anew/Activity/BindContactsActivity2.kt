package com.example.anew.Activity

import android.content.Intent
import android.os.Bundle


import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import cn.bmob.sms.BmobSMS
import cn.bmob.sms.exception.BmobException
import cn.bmob.v3.Bmob
import com.example.anew.R
import cn.bmob.v3.b.i
import cn.bmob.sms.listener.VerifySMSCodeListener
import cn.bmob.v3.listener.SaveListener
import com.example.anew.BmobTable.User
import com.example.anew.Message

import kotlinx.android.synthetic.main.activity_bind2.*




class BindContactsActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind2)
        var phoneNum = intent.extras.getString("phoneNum");
        registButton_bind.setOnClickListener({

            BmobSMS.verifySmsCode(this, intent.extras.getString("contactsPhoneNum"), verificationcode_bind.text.toString(), object : VerifySMSCodeListener() {

                override fun done(ex: BmobException?) {
                    // TODO Auto-generated method stub
                    if (ex == null) {//短信验证码已验证成功
                        Log.i("bmob", "验证通过")
                        var userTable = User();
                        Message.contactsPhoneNum = intent.extras.getString("contactsPhoneNum");



                    } else {
                        Log.i("bmob", "绑定失败：code =" + ex!!.getErrorCode() + ",msg = " + ex!!.getLocalizedMessage())
                    }
                }
            })
        })


    }
}