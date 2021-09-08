package com.spit.lms.System.Fragment

import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemLongClickListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spit.lms.MainActivity
import com.spit.lms.Network.APIUtils
import com.spit.lms.R
import com.spit.lms.Rfidbase.NewMainActivity
import com.spit.lms.System.Base.BaseFragment
import com.spit.lms.System.Base.SharedPrefsUtils
import com.spit.lms.System.Base.StatusConverter
import com.spit.lms.System.Event.*
import com.spit.lms.System.Model.StockTakeListBook
import com.spit.lms.System.Response.MessageResponse
import com.spit.lms.System.Response.StockListingResponse
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList


class StockTakeListingFragment : BaseFragment () {
    var downloadPanel: LinearLayout? = null
    var downloadProgress: ProgressBar? = null
    var downloadTextView: TextView? = null
    var selectHash: HashMap<String, Boolean> = HashMap()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        view = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.layout_stocktakelisting, null)
        view.findViewById<ImageView>(R.id.back).visibility = View.GONE
        view.findViewById<ImageView>(R.id.menu).visibility = View.VISIBLE

        view.findViewById<TextView>(R.id.title).text = MainActivity.mContext.getText(R.string.menu_stocktake)

        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = (R.id.bottom_stock_take);
        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener(SharedBottomHandler())

        downloadPanel = view.findViewById<LinearLayout>(R.id.download_panel)
        downloadProgress = view.findViewById<ProgressBar>(R.id.download_progress)
        downloadTextView = view.findViewById<TextView>(R.id.download_progress_text)

        view.findViewById<TextView>(R.id.select_all).visibility = View.VISIBLE

        view.findViewById<TextView>(R.id.select_all).setOnClickListener {
            for (s in stli) {
                selectHash[s.stocktakeno] = true

                if (view.findViewById<ListView>(R.id.listview).adapter != null) {
                    var st = view.findViewById<ListView>(R.id.listview).adapter as StockTakeListAdapter
                    st.notifyDataSetChanged()
                }
            }
        }

        view.findViewById<ImageView>(R.id.menu).setOnClickListener {
            if (view.findViewById<LinearLayout>(R.id.menu_panel).visibility == View.GONE) {
                view.findViewById<LinearLayout>(R.id.menu_panel).visibility = View.VISIBLE
            } else {
                view.findViewById<LinearLayout>(R.id.menu_panel).visibility = View.GONE
            }
        }

        view.findViewById<LinearLayout>(R.id.menu_panel).setOnClickListener {
            view.findViewById<LinearLayout>(R.id.menu_panel).visibility = View.GONE
        }

        if (NewMainActivity.isConnected()) {
            Log.i("data", "data " +  listOf("userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID")))
            APIUtils.getArrayList(SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/stockList",
                    listOf("userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID")),
                    MainActivity.mContext, StockListingResponse::class.java
            )
        } else {
            var source = Realm.getDefaultInstance().where(StockListingResponse::class.java).equalTo("userid", SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID")).findAll()
            stli = source
            view.findViewById<ListView>(R.id.listview).adapter = StockTakeListAdapter(source)
        }


        view.findViewById<TextView>(R.id.finish_stocktaking).setOnClickListener {
            var data = ""
            for (s in selectHash.keys) {
                if(selectHash[s] == true)
                    data += s + ","
            }

            if (data.length > 0) {
                data = data.substring(0, data.length - 1)
            }

            Log.i("parse", "parse " + data)

            APIUtils.get(SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/stockTakeListAction",
                    listOf(
                            "userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID"),
                            "status" to 113,
                            "list" to data
                    ),
                    MainActivity.mContext, MessageResponse::class.java
            )
        }

        view.findViewById<TextView>(R.id.delete_stocktaking).setOnClickListener {
            Log.i("delete_stocktaking", "delete_stocktaking")
            var data = ""
            for (s in selectHash.keys) {
                data += s + ","
            }

            if (data.length > 0) {
                data = data.substring(0, data.length - 1)
            }

            Log.i("parse", "parse " + data)

            APIUtils.get(SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/stockTakeListAction",
                    listOf(
                            "userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID"),
                            "status" to 216,
                            "list" to data
                    ),
                    MainActivity.mContext, MessageResponse::class.java
            )
        }

        view.findViewById<ListView>(R.id.listview).setOnItemLongClickListener(OnItemLongClickListener { arg0, arg1, pos, id -> // TODO Auto-generated method stub
            Log.v("long clicked", "pos: $pos")
            true
        })

    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View? {
        return view
    }

    var total = 0
    inner class StockTakeListAdapter(val stli: List<StockListingResponse>) : BaseAdapter() {
        override fun getCount(): Int {
            return stli.size
        }

        override fun getItem(position: Int): StockListingResponse {
            return stli[position]
        }

        override fun getItemId(position: Int): Long {
            return stli[position].hashCode().toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var view: View? = null

            if (convertView == null) {
                view = LayoutInflater.from(MainActivity.mContext)
                        .inflate(R.layout.stocktake_listing_cell, null)
            } else {
                view = convertView
            }

            view!!.findViewById<TextView>(R.id.stocktakeno).text = getItem(position).stocktakeno
            view!!.findViewById<TextView>(R.id.stocktake_prgoress).text = getItem(position).progress.toString() + "/" + getItem(position).total.toString()
            view!!.findViewById<TextView>(R.id.title).text = getItem(position).name
            view!!.findViewById<TextView>(R.id.start_date).text = getItem(position).startdate
            view!!.findViewById<TextView>(R.id.create_date).text = getItem(position).createddate
            view!!.findViewById<TextView>(R.id.end_date).text = getItem(position).expirydate
            view!!.findViewById<TextView>(R.id.stock_take_status).text =
                    StatusConverter.getStatusById(getItem(position).status)

            var b = selectHash.get(getItem(position).stocktakeno)
            if (b == null) {
                b = false
            }

            view.findViewById<CheckBox>(R.id.checkbox).setOnClickListener {
                selectHash.put(getItem(position).stocktakeno, !b)
                this.notifyDataSetChanged()
            }

            view.findViewById<CheckBox>(R.id.checkbox).isChecked = b

            Log.i("data", "dataname " + getItem(position).name)

            view!!.setOnClickListener {
                var stdf = StockTakeDetailFragment()
                (MainActivity.mContext as MainActivity).replaceFragment(stdf)
            }

            view.setOnLongClickListener {
                selectHash.put(getItem(position).stocktakeno, !b)
                this.notifyDataSetChanged()
                return@setOnLongClickListener true
            }


            view!!.setOnClickListener {
                var source = Realm.getDefaultInstance().where(StockTakeListBook::class.java).equalTo("userid", SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID"))
                        .equalTo("stocktakeno", getItem(position).stocktakeno);

                this@StockTakeListingFragment.total = 0

                if (source.count().toInt() == 0) {

                    if(NewMainActivity.isConnected()) {

                        downloadPanel!!.visibility = View.VISIBLE
                        downloadTextView!!.text = MainActivity.mContext.getString(R.string.start_downloading)
                        downloadPanel!!.setOnClickListener {}

                        this@StockTakeListingFragment.total = getItem(position).total

                        APIUtils.download(
                            SharedPrefsUtils.getStringPreference(
                                MainActivity.mContext,
                                "BASE_URL"
                            ) + "/stocktakeListDetail?stocktakeno=" + getItem(position).stocktakeno,
                            getItem(position).stocktakeno
                        )
                    } else {
                        EventBus.getDefault().post(DialogEvent(MainActivity.mContext.getString(R.string.app_name), MainActivity.mContext.getString(R.string.no_internet_connection)))
                    }
                } else {
                    StockTakeDetailFragment.selectedPos = 0
                    var stdf = StockTakeDetailFragment()
                    stdf.stockListingResponse = getItem(position)
                    (MainActivity.mContext as MainActivity).replaceFragment(stdf)
                }
            }

            return view;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ResponseEvent?) {
        Log.i("ssss", "s " + event + event!!.url)


        if (event!!.url.contains("Action")) {
            Log.i("data", "data " + (event!!.response!! as MessageResponse).message)

            if ((event!!.response!! as MessageResponse).message.toLowerCase().contains( "success")) {
                view.findViewById<LinearLayout>(R.id.menu_panel).visibility = View.GONE
                APIUtils.getArrayList(SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/stockList",
                        listOf("userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID")),
                        MainActivity.mContext, StockListingResponse::class.java
                )
            }
        }

        try {
            if (event!!.url.contains("stockList")) {
                stli = (event!!.response as List<StockListingResponse>)
                view.findViewById<ListView>(R.id.listview).adapter = StockTakeListAdapter(event!!.response as List<StockListingResponse>)

                Realm.getDefaultInstance().beginTransaction()
                Realm.getDefaultInstance().where(StockListingResponse::class.java).equalTo("userid", SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID")).findAll().deleteAllFromRealm()
                for (s in (event!!.response as List<StockListingResponse>)) {
                    s.userid = (SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID"))
                    s.pk = s.userid + "_" + s.stocktakeno
                    Realm.getDefaultInstance().insert(s)
                }
                Realm.getDefaultInstance().commitTransaction()

            }
        } catch (e: Exception) {

        }
    }

    var stli: List<StockListingResponse> = ArrayList()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: FileDoneEvent?) {

        if (stli != null) {
            for (s in stli) {
                if (s.stocktakeno.equals(event!!.getStocktakeno())) {
                    view.findViewById<LinearLayout>(R.id.download_panel).visibility = View.GONE

                    var stdf = StockTakeDetailFragment()
                    stdf.stockListingResponse = s
                    (MainActivity.mContext as MainActivity).replaceFragment(stdf)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LoginDownloadProgressEvent?) {
        var percentage = ((event!!.progress * 100))
        downloadTextView!!.text = percentage.toString() + "%"
        downloadProgress!!.progress = (percentage.toInt())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: InsertEvent?) {
        downloadTextView!!.text = event!!.count.toString() + " / " + total
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: DialogEvent?) {
        if(event!!.message == getString(R.string.stock_take_uploaded)) {
            APIUtils.getArrayList(SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/stockList",
                    listOf("userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID")),
                    MainActivity.mContext, StockListingResponse::class.java
            )
        }
    }
}