package com.example.anew

import android.app.ActivityManager
import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import com.example.anew.Activity.HomeActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.DataOutputStream

import java.io.IOException
import java.util.*

/**
 * Created by 懵逼的杨定宇 on 2017/5/13.
 */

class checkService : NotificationListenerService() {

    lateinit var sp : SharedPreferences;
    lateinit var editor:SharedPreferences.Editor;
    internal lateinit var mUsageStatsManager: UsageStatsManager

    override fun onListenerConnected() {
        Toast.makeText(applicationContext,"NotificationListener连接成功",Toast.LENGTH_SHORT).show();
        super.onListenerConnected()
    }

    override fun onListenerDisconnected() {
        Toast.makeText(applicationContext,"NotificationListener连接断开",Toast.LENGTH_SHORT).show();
        super.onListenerDisconnected()
    }

    override fun onBind(intent: Intent?): IBinder {
        return super.onBind(intent)
    }
    override fun onNotificationPosted(sbn: StatusBarNotification?) {

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {

    }

    var timered:Boolean = false;



    //更新任务列表
    fun updateData() {

        var missionListStr = sp.getString("MissionList","");
        Log.i("gson",missionListStr);
        var gson = Gson();

        if(missionListStr != "") {
            Message.missionList.clear();
            Message.missionList.addAll(gson.fromJson<ArrayList<Misson>>(missionListStr, object : TypeToken<ArrayList<Misson>>() {}.type));
            Log.i("ListLen",""+Message.missionList.size);
        }
        Collections.sort(Message.missionList);

        //根据时间清理已过期的任务列表
        var time = android.text.format.Time();
        time.setToNow();
        var month = time.month+1;
        var day = time.monthDay;
        var hour = time.hour;
        var min = time.minute;
        Log.i("时间信息","2017.$month . $day,$hour:$min");


        while (Message.missionList.size>0 && Message.missionList[0]!=null){
            if (Message.missionList[0].month<month || Message.missionList[0].day<day ){
                Message.missionList.removeAt(0);

            }
            if((Message.missionList[0].month==month && Message.missionList[0].day==day && ((hour-Message.missionList[0].startHour)*60 +min-Message.missionList[0].startMin-Message.missionList[0].duration)>0)){
                Message.missionList.removeAt(0);

            }else{
                break;
            }

        }
        editor.putString("MissionList", gson.toJson(Message.missionList));
        editor.commit()

        //根据任务时间分到今天、明天、后天任务中
        Message.missionListHoutian.clear();
        Message.missionListTomorro.clear();
        Message.missionListToday.clear();
        for (obj in Message.missionList){
            Log.i("任务信息","${obj.missonName},${obj.missonDetail}")
            if (obj.month == month && obj.day == day){
                Message.missionListToday.add(obj);
                Log.i("分配情况","今天");
            }
            if ( (obj.month == month && obj.day == day +1) || (obj.month == month+1 && obj.day == 1)  ){
                Message.missionListTomorro.add(obj);
                Log.i("分配情况","明天");
            }
            if (obj.month == month && obj.day == day +2){
                Message.missionListHoutian.add(obj);
                Log.i("分配情况","后天");
            }
        }

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(applicationContext,"onStartCommand（）",Toast.LENGTH_SHORT).show();
        //manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        var thisComponent = ComponentName(this, /*getClass()*/ checkService::class.java);


        updateData()
        //该service未初始化才启动相应的timer
        if (!timered) {
            timered = true;
            Toast.makeText(applicationContext,"timer启动！",Toast.LENGTH_SHORT).show();
            val timerTask = object : TimerTask() {
                override fun run() {

                    packName = getTopActivity();

                    //处理锁定期间但暂停（休息）时的逻辑；
                    if (Message.isLock && Message.isPause) {
                        if (Message.restTime > 0) {
                            Message.restTime--;
                        } else {
                            Message.isPause = false;
                            val intent2 = Intent(this@checkService, HomeActivity::class.java)
                            intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    }

                    //处理锁定期间的逻辑
                    if (Message.isLock && !Message.isPause && "com.example.anew" != packName && packName != "888") {
                        try {
                            //forceStopPackage(packName,getApplicationContext());
                            //manager.killBackgroundProcesses(packName);

                            //另外还需要判定是否是桌面程序
                            try {
                                val process = Runtime.getRuntime().exec("su")

                                val out = process.outputStream
                                val cmd = "am force-stop $packName \n"
                                out.write(cmd.toByteArray())
                                out.flush()
                                val intent2 = Intent(this@checkService, HomeActivity::class.java)
                                intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent2)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    //killOthers(getApplicationContext());
                }
            }
            //处理锁定期间逻辑的计时器
            val timer = Timer()
            timer.schedule(timerTask, 0, 1000)

            //检查是否进入任务时间的计时器
            var timer2 = Timer();
            timer2.schedule(object : TimerTask() {
                override fun run() {
                    //获取系统时间，与任务列表的第一个任务进行比较
                    //如果处于任务时间之内，则将锁定标记改为true
                    var time = android.text.format.Time();
                    time.setToNow();
                    var month = time.month+1;
                    var day = time.monthDay;
                    var hour = time.hour;
                    var min = time.minute;
                    //Log.i("表达式测试：","${Message.missionList.size },${Message.missionList[0]?.isLock == true},${Message.missionList[0]?.month == month},${ Message.missionList[0]?.day == day},${hour>Message.missionList[0]?.startHour},${min>Message.missionList[0]?.startMin},${((hour - Message.missionList[0]?.startHour) * 60 + min - Message.missionList[0]?.startMin - Message.missionList[0]?.duration) < 0},");
                    if (Message.missionList.size > 0
                            && Message.missionList[0].month == month
                            && Message.missionList[0].day == day
                            && hour*60+min>=Message.missionList[0].startHour*60+Message.missionList[0].startMin

                            && ((hour - Message.missionList[0].startHour) * 60 + min - Message.missionList[0].startMin - Message.missionList[0].duration) < 0) {

                        if(Message.missionList[0].isLock == true) {
                            Message.isLock = true;
                        }
                        Message.isMission = true;

                        Log.i("", "处于第一个任务之中，开始锁定");
                    }

                    //若任务已过期，则删除任务
                    if (Message.missionList.size > 0
                            && Message.missionList[0].month <= month
                            && Message.missionList[0].day <= day
                            && ((hour - Message.missionList[0].startHour) * 60 + min - Message.missionList[0].startMin - Message.missionList[0].duration) >= 0) {
                        Message.isLock = false;
                        Message.isMission = false;
                        Message.missionList.removeAt(0);
                        editor.putString("MissionList", Gson().toJson(Message.missionList));
                        editor.commit();
                        updateData();
                        Log.i("", "第一个任务结束了，去掉锁定");
                    }

                    if (Message.missionList.size > 0) {
                        Log.i("第一个任务：", "${Message.missionList[0].month} , ${Message.missionList[0].day} , ${Message.missionList[0].startHour} , ${Message.missionList[0].startMin}, 持续时间 ${Message.missionList[0].duration}");
                        Log.i("当前时间：", "$month , $day , $hour , $min, ");
                    }
                }
            }, 0, 60000);
        }

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onCreate() {
        Toast.makeText(applicationContext,"onCreate（）",Toast.LENGTH_SHORT).show();

        var cmd="chmod 777 " + getPackageCodePath();
        var process = Runtime.getRuntime().exec("su"); //切换到root帐号
        var os =  DataOutputStream(process.getOutputStream());
        os.writeBytes(cmd + "\n");
        os.writeBytes("exit\n");
        os.flush();
        process.waitFor();
        mUsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        super.onCreate()

        sp = applicationContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
         editor = sp.edit();
    }

    //销毁时更新shareperference里的任务列表
    override fun onDestroy() {
        var gson = Gson();
        var str = gson.toJson(Message.missionList);
        editor.putString("MissionList",str);
        editor.commit();

        super.onDestroy()
    }


    private val runningProcesses: List<ActivityManager.RunningAppProcessInfo>? = null
    private var packName: String? = null
    private val pManager: PackageManager? = null
    internal lateinit var manager: ActivityManager

    //android5.0以上获取方式
    //------------不进“有权限查看使用情况的应用”的界面
    //startActivity(intent);
    fun getTopActivity() :String?{
            var topPackageName = "888"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                val time = System.currentTimeMillis()

                val stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time)

                if (stats != null) {
                    val mySortedMap = TreeMap<Long, UsageStats>()

                    for (usageStats in stats) {
                        mySortedMap.put(usageStats.lastTimeUsed, usageStats)
                    }
                    if (mySortedMap != null && !mySortedMap.isEmpty()) {

                        topPackageName = mySortedMap[mySortedMap.lastKey()]!!.getPackageName()

                        //Log.i("TopPackage Name", topPackageName)
                        return topPackageName
                    }

                }

            }
            return topPackageName;
        }


    /**
     * 强制停止应用程序
     * @param pkgName
     */
    @Throws(Exception::class)
    private fun forceStopPackage(pkgName: String, context: Context) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String::class.java)
        method.invoke(am, pkgName)
    }


}
