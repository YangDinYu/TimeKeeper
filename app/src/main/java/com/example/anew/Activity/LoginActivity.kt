package com.example.anew.Activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import cn.bmob.sms.BmobSMS
import cn.bmob.v3.Bmob


import com.example.anew.R
import kotlinx.android.synthetic.main.activity_login.*

import cn.bmob.v3.b.i
import cn.bmob.v3.BmobQuery


import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import com.example.anew.BmobTable.User

import kotlinx.android.synthetic.main.activity_sign_up2.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var sp = applicationContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        var editor = sp.edit();
        /*
        if (sp.getString("loginPhoneNum","").length>0){
            startActivity(Intent(this@LoginActivity,MainActivity::class.java));
            finish();
        }
        */

        BmobSMS.initialize(this,"3c9eebb309b4a67465abda4522db94e1");
        Bmob.initialize(this,"3c9eebb309b4a67465abda4522db94e1");

        LoginButton.setOnClickListener({
            val query = BmobQuery<User>()

            query.addWhereEqualTo("mobilePhoneNumber", phoneNum.text)

            query.setLimit(50)

            query.findObjects(object : FindListener<User>() {
                override fun done(`object`: List<User>, e: cn.bmob.v3.exception.BmobException?) {
                    if (e == null) {
                        Toast.makeText(this@LoginActivity,"查询成功：共" +`object`.size + "条数据。",Toast.LENGTH_SHORT).show();
                        for (user in `object`) {
                            //获得playerName的信息
                            Toast.makeText(this@LoginActivity,user.mobilePhoneNumber+","+user.password,Toast.LENGTH_SHORT).show();
                            if (user.mobilePhoneNumber.equals(phoneNum.text.toString()) && user.password.equals(Password.text.toString())){

                                //利用shareperference记录账号，自动登录
                                editor.putString("loginPhoneNum",user.mobilePhoneNumber);
                                editor.commit();
                                startActivity(Intent(this@LoginActivity,MainActivity::class.java));
                                finish();
                            }

                        }
                    } else {
                        Log.i("bmob", "验证失败：code =" + e!!.getErrorCode() + ",msg = " + e!!.getLocalizedMessage())
                    }
                }
            })
        })

        fetchPassword.setOnClickListener({

            var userTable = User();
            userTable.mobilePhoneNumber = "15528351376";
            userTable.password = "123456"

            userTable.save(object : SaveListener<String>(){
                override fun done(p0: String?, p1: cn.bmob.v3.exception.BmobException?) {
                    Toast.makeText(this@LoginActivity,"注册成功,id是"+p0,Toast.LENGTH_SHORT).show();
                    startActivity(Intent(this@LoginActivity,LoginActivity::class.java));
                }

            });

            //var intent = Intent(this, MainActivity::class.java);
            //startActivity(intent);
        })

        regist.setOnClickListener({
            var intent = Intent(this, SignUpActivity::class.java);
            startActivity(intent);
        })
    }
}
