package com.example.anew.Fragment


import android.os.Bundle
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.anew.R
import android.widget.TextView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.example.anew.Misson
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener
import kotlinx.android.synthetic.main.activity_main.*
import com.example.anew.Activity.MainActivity




/**
 * Created by 懵逼的杨定宇 on 2017/9/10.
 */
class MissionListFragment: Fragment() {
    var dataList = ArrayList<Misson>();
    lateinit var recyclerView :RecyclerView ;
    fun setData(list: ArrayList<Misson>){
        dataList = list;
    }



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater?.inflate(R.layout.fragment_layout, container, false);
        recyclerView = view!!.findViewById(R.id.recyclerView) as RecyclerView;
        recyclerView.layoutManager = LinearLayoutManager(this.activity) as RecyclerView.LayoutManager?;
        recyclerView.adapter = HomeAdapter();


        var onTouchListener = RecyclerTouchListener(this@MissionListFragment.activity, recyclerView);
        onTouchListener
                .setIndependentViews(R.id.rowButton)
                .setViewsToFade(R.id.rowButton)
                .setClickable(object : RecyclerTouchListener.OnRowClickListener {
                    override fun onRowClicked(position: Int) {
                        Toast.makeText(this@MissionListFragment.activity.applicationContext, "Page"+activity.view_pager.currentItem+"Row " + (position + 1) + " clicked!",Toast.LENGTH_SHORT).show();


                        (activity as MainActivity).initAlertDialog(activity.view_pager.currentItem,position);
                    }

                    override fun onIndependentViewClicked(independentViewID: Int, position: Int) {
                        Toast.makeText(this@MissionListFragment.activity.applicationContext, "Button in row " + (position + 1) + " clicked!",Toast.LENGTH_SHORT).show();


                    }
                })
                .setSwipeOptionViews(R.id.delete)
                .setSwipeable(R.id.rowFG, R.id.rowBG, RecyclerTouchListener.OnSwipeOptionsClickListener { viewID, position ->
                    var message = ""
                    if (viewID == R.id.delete) {
                        message += "删除"
                    }
                    message += " clicked for row " + (position + 1)
                    Toast.makeText(this@MissionListFragment.activity.applicationContext, message,Toast.LENGTH_SHORT).show();
                    when(activity.view_pager.currentItem){
                        0 -> com.example.anew.Message.missionListToday.removeAt(position);
                        1 -> com.example.anew.Message.missionListTomorro.removeAt(position);
                        2 -> com.example.anew.Message.missionListHoutian.removeAt(position);
                    }

                    (activity as MainActivity).saveDate();
                    (activity as MainActivity).updateData();
                })
        recyclerView.addOnItemTouchListener(onTouchListener)
        return view!!;
    }


    internal inner class HomeAdapter : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(
                    activity).inflate(R.layout.recyclerview_item, parent,
                    false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.detailText.setText(dataList[position].missonDetail);
            holder.titleText.setText(dataList[position].missonName);
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        internal inner class MyViewHolder(view: View) : ViewHolder(view) {

            var titleText: TextView;
            var detailText :TextView;
            init {
                titleText = view.findViewById(R.id.title1) as TextView
                detailText = view.findViewById(R.id.detailText) as TextView;
            }
        }
    }
}