package com.example.anew

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by 懵逼的杨定宇 on 2017/9/11.
 */
public class MyViewPager: ViewPager{


    constructor(context: Context):super(context){

    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx,attrs) {

    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false;
        //return super.onInterceptTouchEvent(ev)
    }
}