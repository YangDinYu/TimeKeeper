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

import kotlinx.android.synthetic.main.activity_sign_up2.*


class SignUpActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)
        var phoneNum = intent.extras.getString("phoneNum");
        registButton.setOnClickListener({
            if (password.text.length<6){
                Toast.makeText(this,"密码长度小于6位",Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }
            if (!password.text.toString().equals(repeatPassword.text.toString())){
                Toast.makeText(this,"两次密码输入不一致",Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            BmobSMS.verifySmsCode(this, phoneNum, verificationcode.text.toString(), object : VerifySMSCodeListener() {

                override fun done(ex: BmobException?) {
                    // TODO Auto-generated method stub
                    if (ex == null) {//短信验证码已验证成功
                        Log.i("bmob", "验证通过")
                        var userTable = User();
                        userTable.mobilePhoneNumber = phoneNum;
                        userTable.password = password.text.toString();

                        userTable.save(object :SaveListener<String>(){
                            override fun done(p0: String?, p1: cn.bmob.v3.exception.BmobException?) {
                               Toast.makeText(this@SignUpActivity2,"注册成功",Toast.LENGTH_SHORT).show();
                                startActivity(Intent(this@SignUpActivity2,LoginActivity::class.java));
                            }

                        });
                    } else {
                        Log.i("bmob", "验证失败：code =" + ex!!.getErrorCode() + ",msg = " + ex!!.getLocalizedMessage())
                    }
                }
            })
        })


    }
}