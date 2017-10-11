package com.example.anew

import android.os.Parcel
import android.os.Parcelable


/**
 * Created by 懵逼的杨定宇 on 2017/8/28.
 */
data class Misson (var Time:String): Comparable<Misson> {
    override fun compareTo(other: Misson): Int {

        if (this.month>other.month) return 1
        if (this.month<other.month) return -1

        if (this.day>other.day) return 1
        if (this.day<other.day) return -1

        if (this.startHour>other.startHour) return 1
        if (this.startHour<other.startHour) return -1

        if (this.startMin>other.startMin) return 1
        if (this.startMin<other.startMin) return -1

        return 1;
    }


    var month : Int = 0;
    var day : Int = 0;
    var startHour = -1;
    var startMin = -1;
    var duration = 0;
     var missonName :String = "";
     var missonDetail :String = "";
    var isLock = false;



}