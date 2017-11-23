package com.example.anew.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import cn.bmob.sms.BmobSMS
import cn.bmob.sms.exception.BmobException

import com.example.anew.R

import cn.bmob.sms.listener.RequestSMSCodeListener
import kotlinx.android.synthetic.main.activity_bind.*


class BindContactsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bind)

        oldphoneNum_bind.text = "已绑定联系人：" + com.example.anew.Message.contactsPhoneNum;



        MessageButton_bind.setOnClickListener({
            BmobSMS.requestSMSCode(this, ""+phoneNum_bind.text, "默认", object : RequestSMSCodeListener() {

                override fun done(smsId: Int?, ex: BmobException?) {
                    // TODO Auto-generated method stub
                    if (ex == null) {//验证码发送成功
                        Log.i("bmob", "短信id：" + smsId!!)//用于查询本次短信发送详情
                        var intent = Intent(this@BindContactsActivity, BindContactsActivity2::class.java);
                        val bundle = Bundle()
                        bundle.putString("contactsPhoneNum", phoneNum_bind.text.toString())
                        intent.putExtras(bundle)
                        startActivity(intent);
                    }else{
                        Log.i("bmob","errorCode = "+ex.getErrorCode()+",errorMsg = "+ex.getLocalizedMessage());
                    }
                }
            })
        })
    }


}
