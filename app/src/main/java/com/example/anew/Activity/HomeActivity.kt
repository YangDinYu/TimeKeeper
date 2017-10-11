package com.example.anew.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.content.DialogInterface
import android.content.ComponentName
import com.example.anew.Message


/**
 * Created by 懵逼的杨定宇 on 2017/8/8.
 */

class HomeActivity : Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        var sp = applicationContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        var editor = sp.edit();


        var intent = Intent(this, MainActivity::class.java);
        var intent2 = packageManager.getLaunchIntentForPackage("com.tsf.shell");




        //startActivity(intent2);

        if (sp.getString("packageName",null)==null) {
            val mainIntent = Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_HOME);
            var names: Array<String>;
            var pkgNamesT = ArrayList<String>();
            var actNamesT = ArrayList<String>();
            var resolveInfos = applicationContext.packageManager.queryIntentActivities(mainIntent, 0);

            names = Array(resolveInfos.size - 1, { i -> "" });
            for (i in resolveInfos.indices) {
                var string = resolveInfos.get(i).activityInfo.packageName;



                if (!string.equals(applicationContext.getPackageName())) {
                    //排除自己的包名
                    pkgNamesT.add(string);
                    string = resolveInfos.get(i).activityInfo.name;
                    actNamesT.add(string);
                    names[i] = string;
                    Log.i("pkgName:", string);

                }
            }

            AlertDialog.Builder(this).setTitle("请选择解锁后的屏幕").setCancelable(false).setSingleChoiceItems(names, 0, DialogInterface.OnClickListener { dialog, which ->
                editor.putString("packageName", pkgNamesT.get(which))
                editor.putString("activityName", actNamesT.get(which))
                editor.commit()
                //originalHome()
                dialog.dismiss();
                finish();
            }).show()


        }




        if ((sp.getString("packageName",null)!=null && !Message.isLock) || Message.isPause){
            Log.i("tell","我这里过去的！")
            var packageName = sp.getString("packageName","");
            Log.i("pagname",packageName);
            var activityName = sp.getString("activityName","");
            val componentName = ComponentName(packageName,activityName);
            val intent = Intent()
            intent.component = componentName
            startActivity(intent);
            finish();
        }

        if (Message.isLock && !Message.isPause){
            var intent = Intent(this, LockActivity::class.java);
            startActivity(intent);
            finish();

        }



        //finish();
    }

    override fun onStart() {
        super.onStart()
    }
}