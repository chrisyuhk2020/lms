package com.spit.lms.System.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.spit.lms.MainActivity
import com.spit.lms.Network.APIUtils
import com.spit.lms.R
import com.spit.lms.System.Base.BaseFragment
import com.spit.lms.System.Base.SharedPrefsUtils
import com.spit.lms.System.Event.ResponseEvent
import com.spit.lms.System.Response.BorrowHistoryResponse
import com.spit.lms.System.Response.ReservedHistoryResponse
import com.spit.lms.System.Response.UserDetailResponse
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HistoryFragment : BaseFragment() {
    var borrowHistoryResponse : ArrayList<BorrowHistoryResponse>? = ArrayList();
    var reservedHistoryResponse : ArrayList<ReservedHistoryResponse>? = ArrayList();

    lateinit var listview : ListView;

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        view = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.fragment_history, null)

        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = (R.id.bottom_stock_take);
        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener(SharedBottomHandler())

        listview = view.findViewById<ListView>(R.id.listview)

        var tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        APIUtils.get(
            SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/userDetail",
            listOf(
                "userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID")
            ), MainActivity.mContext, UserDetailResponse::class.java
        )

        APIUtils.getArrayList(
            SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/borrowHistory",
            listOf(
                "userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID")
            ), MainActivity.mContext, BorrowHistoryResponse::class.java
        )

        APIUtils.getArrayList(
            SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/reservedHistory",
            listOf(
                "userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID")
            ), MainActivity.mContext, ReservedHistoryResponse::class.java
        )

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                Log.i("position", "position " + p0!!.position)

                if(p0!!.position == 0) {

                    APIUtils.getArrayList(
                        SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/borrowHistory",
                        listOf(
                            "userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID")
                        ), MainActivity.mContext, BorrowHistoryResponse::class.java
                    )

                } else {

                    APIUtils.getArrayList(
                        SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/reservedHistory",
                        listOf(
                            "userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID")
                        ), MainActivity.mContext, ReservedHistoryResponse::class.java
                    )

                }

                listview.adapter = ListAdapter(p0!!.position)
            }
        })
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View? {
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ResponseEvent?) {
        if (event!!.response is UserDetailResponse) {
            var user = (event!!.response as UserDetailResponse)
            view.findViewById<TextView>(R.id.name).text = user.name
            view.findViewById<TextView>(R.id.email).text = user.email
            Glide.with(MainActivity.mContext).load(user.img).error(R.drawable.account)
                .into(view.findViewById<ImageView>(R.id.image))
        }

        if (event!!.url.contains("borrowHistory")) {
            borrowHistoryResponse = event!!.response as ArrayList<BorrowHistoryResponse>
            if((view.findViewById<TabLayout>(R.id.tab_layout)).selectedTabPosition == 0){
                listview.adapter = ListAdapter(0)
            }
        }

        if (event!!.url.contains("reservedHistory")) {
            reservedHistoryResponse = event!!.response as ArrayList<ReservedHistoryResponse>
            if((view.findViewById<TabLayout>(R.id.tab_layout)).selectedTabPosition == 1) {
                listview.adapter = ListAdapter(1)
            }
        }
    }

    inner class ListAdapter(var pos : Int) : BaseAdapter() {
        override fun getCount(): Int {
            if(pos == 0) {
                return this@HistoryFragment.borrowHistoryResponse!!.size
            }

            return this@HistoryFragment.reservedHistoryResponse!!.size
        }

        override fun getItem(position: Int): Any {
            if(pos == 0) {
                return this@HistoryFragment.borrowHistoryResponse!![position] as Any
            }

            return this@HistoryFragment.reservedHistoryResponse!![position] as Any
        }

        override fun getItemId(position: Int): Long {
            return getItem(position).hashCode().toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view: View? = null

            if (convertView == null) {
                if (pos == 0) {
                    view = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.borrow_history_cell, null)
                } else {
                    view = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.appointment_history_cell, null)
                }
            } else {
                view = convertView
            }

            //BorrowHistoryResponse
            //ReservedHistoryResponse

            if (pos == 0) {
                var data = getItem(position) as BorrowHistoryResponse
                view!!.findViewById<TextView>(R.id.bookNo).text = data.bookNo
                view!!.findViewById<TextView>(R.id.callNo).text = data.callNo
                view!!.findViewById<TextView>(R.id.title).text = data.name

                view!!.findViewById<TextView>(R.id.borrow_date).text = data.borrowDate
                view!!.findViewById<TextView>(R.id.return_date).text = data.expirationDate


                var status = "";
                if(data.renew.equals("1")) {
                    status = MainActivity.mContext.getString(R.string.renew)
                }

                if(data.waitingID.equals("1")) {
                    status += " " + MainActivity.mContext.getString(R.string.reserved_by_others)
                }

                view!!.findViewById<TextView>(R.id.status).text = status

                /*
                ReNew：是否可续借1是0否
                WaitingID：是否被预约0无，1有
                 */

                if(data.borrowDate == null || data.borrowDate.isEmpty()){
                    (view!!.findViewById<TextView>(R.id.borrow_date).parent as LinearLayout).visibility = View.GONE
                } else {
                    (view!!.findViewById<TextView>(R.id.borrow_date).parent as LinearLayout).visibility = View.VISIBLE
                }

                if(data.expirationDate == null || data.expirationDate.isEmpty()){
                    (view!!.findViewById<TextView>(R.id.return_date).parent as LinearLayout).visibility = View.GONE
                } else {
                    (view!!.findViewById<TextView>(R.id.return_date).parent as LinearLayout).visibility = View.VISIBLE
                }

                Glide.with(MainActivity.mContext).load("").into(view!!.findViewById<ImageView>(R.id.image))
                Glide.with(MainActivity.mContext).load(R.drawable.login_frontal_bg).into(view!!.findViewById<ImageView>(R.id.image))

            } else {
                var data = getItem(position) as ReservedHistoryResponse
                view!!.findViewById<TextView>(R.id.bookNo).text = data.bookNo
                view!!.findViewById<TextView>(R.id.callNo).text = data.callNo
                view!!.findViewById<TextView>(R.id.title).text = data.name
                view!!.findViewById<TextView>(R.id.status).text = data.status

                view!!.findViewById<TextView>(R.id.appointment_date).text = data.waitingDate

                Glide.with(MainActivity.mContext).load("").into(view!!.findViewById<ImageView>(R.id.image))
                Glide.with(MainActivity.mContext).load(R.drawable.login_frontal_bg).into(view!!.findViewById<ImageView>(R.id.image))

            }

            return view!!;
        }
    }
}