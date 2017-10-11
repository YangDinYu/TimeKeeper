package com.example.anew.Activity

import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.anew.Fragment.MyFragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import com.example.anew.Message
import com.example.anew.Misson
import com.example.anew.R
import com.example.anew.checkService
import com.github.mzule.fantasyslide.SimpleFantasyListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*
import ru.noties.scrollable.CanScrollVerticallyDelegate
import android.support.v4.view.ViewCompat.setTranslationY
import com.example.anew.R.id.header
import android.support.v4.view.ViewCompat.setAlpha
import com.google.gson.reflect.TypeToken
import ru.noties.scrollable.OnScrollChangedListener
import android.support.v4.widget.PopupWindowCompat.showAsDropDown
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TimePicker
import com.necer.ncalendar.calendar.WeekCalendar

import com.scalified.fab.ActionButton
import kotlinx.android.synthetic.main.addmission_layout.*
import org.joda.time.DateTime
import com.necer.ncalendar.listener.OnCalendarChangedListener
import com.necer.ncalendar.listener.OnClickWeekViewListener
import com.necer.ncalendar.listener.OnWeekCalendarChangedListener


class MainActivity : AppCompatActivity() {

    //var myReceiver:MyBroadcastReceiver2? = null;
    lateinit var sp :SharedPreferences;
    lateinit var editor:SharedPreferences.Editor;
    lateinit var pagerAdapter:MyFragmentPagerAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED,FLAG_HOMEKEY_DISPATCHED);
        setContentView(R.layout.activity_main);



         sp = applicationContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
         editor = sp.edit();

        scrollable_layout.setDraggableView(indicator);

        scrollable_layout.setCanScrollVerticallyDelegate(CanScrollVerticallyDelegate { direction ->
            // Obtain a View that is a scroll container (RecyclerView, ListView, ScrollView, WebView, etc)
            // and call its `canScrollVertically(int) method.
            // Please note, that if `ViewPager is used, currently displayed View must be obtained
            // because `ViewPager` doesn't delegate `canScrollVertically` method calls to it's children
            val view = view_pager;
            view.canScrollVertically(direction)
        })
        pagerAdapter = MyFragmentPagerAdapter(supportFragmentManager)
        view_pager.adapter = pagerAdapter;
        view_pager.currentItem = 1;

        indicator.setViewPager(view_pager);

        //tabs.set
        scrollable_layout.addOnScrollChangedListener(object : OnScrollChangedListener {
            override fun onScrollChanged(y: Int, oldY: Int, maxY: Int) {

                // `ratio` of current scroll state (from 0.0 to 1.0)
                // 0.0 - means fully expanded
                // 1.0 - means fully collapsed
                val ratio = y.toFloat() / maxY

                // for example, we can hide header, if we are collapsed
                // and show it when we are expanded (plus intermediate state)
                header.alpha = 1f - ratio

                // to create a `sticky` effect for tabs this calculation can be used:
                val tabsTranslationY: Float
                if (y < maxY) {
                    // natural position
                    tabsTranslationY = .0f
                    addMissions.show();
                } else {
                    // sticky position
                    tabsTranslationY = (y - maxY).toFloat()
                    addMissions.hide();
                }
                indicator.setTranslationY(tabsTranslationY)
            }
        })



        leftSideBar.setFantasyListener(object :SimpleFantasyListener(){
            fun onHover( view: View): Boolean {
                return false;
            }

            fun onSelect(view: View): Boolean {
                return false;
            }

            override fun onCancel() {

            }
        });


        addMissions.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                //startActivity(Intent(this@MainActivity,AddMissionActivity::class.java));
                //initPopWindow(p0!!);
                initAlertDialog(this@MainActivity);
            }

        })

        updateMission.setOnClickListener(object :View.OnClickListener{
            override fun onClick(p0: View?) {
                updateData();
                pagerAdapter.myFragment1?.recyclerView?.adapter?.notifyDataSetChanged();
                pagerAdapter.myFragment2?.recyclerView?.adapter?.notifyDataSetChanged();
                pagerAdapter.myFragment3?.recyclerView?.adapter?.notifyDataSetChanged();

            }

        })

        seeMission.setOnClickListener({
            Log.i("Len",""+ Message.missionList.size)
            Log.i("sp",sp.getString("MissionList",""))
            for (mission: Misson in Message.missionList){

                Toast.makeText(applicationContext,"任务，"+"${Message.missionList[0].month} , ${Message.missionList[0].day} , ${Message.missionList[0].startHour} , ${Message.missionList[0].startMin}, 持续时间 ${Message.missionList[0].duration}",Toast.LENGTH_SHORT).show();
            }
        })

        start.setOnClickListener({

            var intent = Intent(this, checkService::class.java);
            startService(intent);
        })

        changePause.setOnClickListener({
            Message.isLock = !Message.isLock;
        })

    }

    val FLAG_HOMEKEY_DISPATCHED = 0x80000000.toInt()


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Log.i("test",""+keyCode);
        if(keyCode == KeyEvent.KEYCODE_HOME){
            Toast.makeText(this,"Home",Toast.LENGTH_LONG).show();
            //屏蔽之后的操作

            return true;
        }




        return super.onKeyDown(keyCode, event)

    }

    override fun onDestroy() {
        super.onDestroy()
        //unregisterReceiver(myReceiver);
    }

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


            }
            else {
                editor.putString("MissionList", gson.toJson(Message.missionList));
                editor.commit()
                break;
            }
        }

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

    private fun initAlertDialog(context: Context){
        var builder = AlertDialog.Builder(context);
        val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.addmission_layout, null, false);
        builder.setView(view);
        builder.setTitle("添加新任务");


        //设置取消按钮的点击事件。。反正啥都不干
        builder.setNegativeButton("取消",object :DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {

            }

        })

        //设置添加按钮的点击事件
        var mission = Misson("");
        builder.setPositiveButton("添加",object :DialogInterface.OnClickListener{
            override fun onClick(p0: DialogInterface?, p1: Int) {
                //获得当前时间
                var time = android.text.format.Time();
                time.setToNow();
                var month = time.month;
                var day = time.monthDay;
                var hour = time.hour;
                var min = time.minute;


                mission.missonName = (view.findViewById(R.id.missionName) as EditText).text.toString();
                mission.missonDetail = (view.findViewById(R.id.missionDetail) as EditText).text.toString();

                mission.startHour = (view.findViewById(R.id.timePicker) as TimePicker).hour
                mission.startMin = (view.findViewById(R.id.timePicker) as TimePicker).minute
                mission.duration = 3;
                mission.isLock = true;


                Message.missionList.add(mission);
                Collections.sort(Message.missionList);

                var gson = Gson();
                var str = gson.toJson(Message.missionList);
                editor.putString("MissionList",str);
                editor.commit();

                updateData();
                pagerAdapter.myFragment1?.recyclerView?.adapter?.notifyDataSetChanged();
                pagerAdapter.myFragment2?.recyclerView?.adapter?.notifyDataSetChanged();
                pagerAdapter.myFragment3?.recyclerView?.adapter?.notifyDataSetChanged();
            }

        })
        //设置日历选择的监听事件
        (view.findViewById(R.id.ncalendar) as WeekCalendar).setOnWeekCalendarChangedListener(object :OnWeekCalendarChangedListener{
            override fun onWeekCalendarChanged(dateTime: DateTime?) {
                mission.month = dateTime!!.monthOfYear;
                mission.day = dateTime!!.dayOfMonth;
                Log.i("datetime",""+dateTime!!.monthOfYear+","+dateTime!!.dayOfMonth);
            }

        })
        builder.create().show();
    }

}
