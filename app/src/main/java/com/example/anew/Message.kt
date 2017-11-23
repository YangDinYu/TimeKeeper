package com.example.anew

import android.content.Context

/**
 * Created by 懵逼的杨定宇 on 2017/8/17.
 */
object Message{
    public var myPhoneNum = "";
    public var contactsPhoneNum = "";
    public var isMission : Boolean = false;
    public var isLock : Boolean = false;
    public var isPause : Boolean = false;
    public var restTime : Int = 20;
    public  var missionList :ArrayList<Misson> = ArrayList();
    public var missionListToday:ArrayList<Misson> = ArrayList();
    public var missionListTomorro:ArrayList<Misson> = ArrayList();
    public var missionListHoutian:ArrayList<Misson> = ArrayList();

}