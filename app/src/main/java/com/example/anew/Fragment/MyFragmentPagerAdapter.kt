package com.example.anew.Fragment


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.anew.Activity.MainActivity
import com.example.anew.Message


/**
 * Created by 懵逼的杨定宇 on 2017/9/10.
 */
class MyFragmentPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {
     var myFragment1: MissionListFragment? = null
     var myFragment2: MissionListFragment? = null
     var myFragment3: MissionListFragment? = null



    init {
        myFragment1 = MissionListFragment();
        myFragment1?.setData(Message.missionListToday)
        myFragment2 = MissionListFragment();
        myFragment2?.setData(Message.missionListTomorro)
        myFragment3 = MissionListFragment();
        myFragment3?.setData(Message.missionListHoutian)
    }
    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return myFragment1
            1 -> return myFragment2
            2 -> return myFragment3
        }
        return null;
    }

    override fun getCount(): Int {
        return 3;

    }

    override fun getPageTitle(position: Int): CharSequence {
        var titleArray = arrayListOf<String>("今天","明天","后天");
        return titleArray[position];
    }
}