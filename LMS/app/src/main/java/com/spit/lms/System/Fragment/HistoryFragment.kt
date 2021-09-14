package com.spit.lms.System.Fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.spit.lms.MainActivity
import com.spit.lms.Network.APIUtils
import com.spit.lms.R
import com.spit.lms.System.Base.BaseFragment
import com.spit.lms.System.Base.SharedPrefsUtils
import com.spit.lms.System.Event.DialogEvent
import com.spit.lms.System.Event.ResponseEvent
import com.spit.lms.System.Response.BorrowHistoryResponse
import com.spit.lms.System.Response.RenewBookResponse
import com.spit.lms.System.Response.ReservedHistoryResponse
import com.spit.lms.System.Response.UserDetailResponse
import org.greenrobot.eventbus.EventBus
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

        Log.i("userid", "userid userid " + SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID"));

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
        } else if (event!!.url.contains("borrowHistory")) {
            borrowHistoryResponse = event!!.response as ArrayList<BorrowHistoryResponse>
            if((view.findViewById<TabLayout>(R.id.tab_layout)).selectedTabPosition == 0){
                listview.adapter = ListAdapter(0)
            }
        } else if (event!!.url.contains("reservedHistory")) {
            reservedHistoryResponse = event!!.response as ArrayList<ReservedHistoryResponse>
            if((view.findViewById<TabLayout>(R.id.tab_layout)).selectedTabPosition == 1) {
                listview.adapter = ListAdapter(1)
            }
        } else if(event.url.contains("renewBook")){
            var response = event!!.response as RenewBookResponse

            if(response != null) {
                if(response.status.equals("0")) {
                    EventBus.getDefault().post(DialogEvent(MainActivity.mContext.getString(R.string.app_name), MainActivity.mContext.getString(R.string.renew_failure) ));
                } else if(response.status.equals("1")) {
                    EventBus.getDefault().post(DialogEvent(MainActivity.mContext.getString(R.string.app_name), MainActivity.mContext.getString(R.string.renew_success) ));

                    APIUtils.getArrayList(
                        SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/borrowHistory",
                        listOf(
                            "userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID")
                        ), MainActivity.mContext, BorrowHistoryResponse::class.java
                    )

                } else if(response.status.equals("2")) {
                    EventBus.getDefault().post(DialogEvent(MainActivity.mContext.getString(R.string.app_name), MainActivity.mContext.getString(R.string.renew_partial_success)));

                    APIUtils.getArrayList(
                        SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/borrowHistory",
                        listOf(
                            "userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID")
                        ), MainActivity.mContext, BorrowHistoryResponse::class.java
                    )
                }
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
                view!!.findViewById<TextView>(R.id.return_date).text = data.returnDate


                var status = "";
                if(data.renew){//.equals("1")) {
                    status = MainActivity.mContext.getString(R.string.renew)
                }

                if(data.waitingID) {
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

                if(data.returnDate == null || data.returnDate.isEmpty()){
                    (view!!.findViewById<TextView>(R.id.return_date).parent as LinearLayout).visibility = View.GONE
                } else {
                    (view!!.findViewById<TextView>(R.id.return_date).parent as LinearLayout).visibility = View.VISIBLE
                }

                if(data.renew) {
                    view.setOnClickListener {
                        showDialog(position);
                    }
                } else {
                    view.setOnClickListener {

                    }
                }
                //Glide.with(MainActivity.mContext).load("").into(view!!.findViewById<ImageView>(R.id.image))
                //Glide.with(MainActivity.mContext).load(R.drawable.login_frontal_bg).into(view!!.findViewById<ImageView>(R.id.image))

            } else {
                var data = getItem(position) as ReservedHistoryResponse
                view!!.findViewById<TextView>(R.id.bookNo).text = data.bookNo
                view!!.findViewById<TextView>(R.id.callNo).text = data.callNo
                view!!.findViewById<TextView>(R.id.title).text = data.name
                view!!.findViewById<TextView>(R.id.status).text = data.status

                view!!.findViewById<TextView>(R.id.appointment_date).text = data.waitingDate

                //Glide.with(MainActivity.mContext).load("").into(view!!.findViewById<ImageView>(R.id.image))
                //Glide.with(MainActivity.mContext).load(R.drawable.login_frontal_bg).into(view!!.findViewById<ImageView>(R.id.image))

            }

            return view!!;
        }

        private fun showDialog(position: Int){
            lateinit var dialog: AlertDialog

            val builder = AlertDialog.Builder(MainActivity.mContext)

            builder.setTitle(MainActivity.mContext.getString(R.string.go_renew))

            builder.setMessage(MainActivity.mContext.getString(R.string.confirm_renew_book))


            // On click listener for dialog buttons
            val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
                when(which){
                    DialogInterface.BUTTON_POSITIVE ->
                    {
                        Log.i("data","Data yes " + SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID") + " " + (getItem(position) as BorrowHistoryResponse).rono)
                        APIUtils.get(
                            SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/renewBook",
                            listOf(
                                "userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID"),
                                "bookRoNo" to (getItem(position) as BorrowHistoryResponse).rono
                            ), MainActivity.mContext, RenewBookResponse::class.java
                        )
                    }

                    DialogInterface.BUTTON_NEGATIVE -> {
                        Log.i("data", "Data");
                    }
                    DialogInterface.BUTTON_NEUTRAL -> {
                        Log.i("data","Data")
                    }
                }
            }

            builder.setPositiveButton(MainActivity.mContext.getString(R.string.confirm),dialogClickListener)

            // Set the alert dialog negative/no button
            //builder.setNegativeButton("NO",dialogClickListener)
            builder.setNeutralButton(MainActivity.mContext.getString(R.string.cancel),dialogClickListener)

            dialog = builder.create()
            dialog.show()
        }
    }

}