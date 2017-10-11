package com.example.anew.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import cn.bmob.sms.BmobSMS
import cn.bmob.sms.exception.BmobException

import com.example.anew.R
import kotlinx.android.synthetic.main.activity_sign_up.*
import cn.bmob.sms.listener.RequestSMSCodeListener






class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        MessageButton.setOnClickListener({
            BmobSMS.requestSMSCode(this, ""+phoneNum.text, "默认", object : RequestSMSCodeListener() {

                override fun done(smsId: Int?, ex: BmobException?) {
                    // TODO Auto-generated method stub
                    if (ex == null) {//验证码发送成功
                        Log.i("bmob", "短信id：" + smsId!!)//用于查询本次短信发送详情
                        var intent = Intent(this@SignUpActivity, SignUpActivity2::class.java);
                        val bundle = Bundle()
                        bundle.putString("phoneNum", phoneNum.text.toString())
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
