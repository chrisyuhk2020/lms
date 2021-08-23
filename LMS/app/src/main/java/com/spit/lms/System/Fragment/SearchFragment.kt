package com.spit.lms.System.Fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.zxing.integration.android.IntentIntegrator
import com.nex3z.flowlayout.FlowLayout
import com.spit.lms.MainActivity
import com.spit.lms.Network.APIUtils
import com.spit.lms.R
import com.spit.lms.System.Base.BaseEvent
import com.spit.lms.System.Base.BaseFragment
import com.spit.lms.System.Base.SharedPrefsUtils
import com.spit.lms.System.Database.SearchHistory
import com.spit.lms.System.Event.ResponseEvent
import com.spit.lms.System.Event.ScanEvent
import com.spit.lms.System.Event.UpdateSearchEvent
import com.spit.lms.System.Response.LoginResponse
import com.spit.lms.System.Response.SearchQueryResponse
import com.spit.lms.System.Scan.CaptureActivityPortrait
import com.spit.lms.System.View.HistoryCell
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class SearchFragment : BaseFragment() {
    var flowlayout : FlowLayout? = null


    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        view = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.layout_search, null)

        view.findViewById<TextView>(R.id.title).text = MainActivity.mContext.getString(R.string.book_listing)
        view.findViewById<ImageView>(R.id.menu).visibility = View.INVISIBLE

        ///view.findViewById<ImageView>(R.id.back).visibility = View.GONE
        //view.findViewById<ImageView>(R.id.menu).visibility = View.VISIBLE

        view.findViewById<ImageView>(R.id.back).setOnClickListener {
            (MainActivity.mContext as MainActivity).onBackPressed()
        }

        flowlayout = view.findViewById(R.id.flowlayout)

        view.findViewById<ImageView>(R.id.search_bar).setOnClickListener {
            Log.i("replace", "replacesearch")

            var query = (view.findViewById(R.id.keyword_search) as EditText).text.toString()
            addHistory(query)
            BookListingFragment.query = query

            (MainActivity.mContext as MainActivity).replaceFragment(BookListingFragment())
        }

        (view.findViewById(R.id.keyword_search) as EditText).setOnKeyListener { v, keyCode, event ->
                when {
                        ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_UP)) -> {
                            var query = (view.findViewById(R.id.keyword_search) as EditText).text.toString()
                            addHistory(query)
                            BookListingFragment.query = query

                            (MainActivity.mContext as MainActivity).replaceFragment(BookListingFragment())

                        return@setOnKeyListener true
                    } else -> {
                        false
                    }
                }
            }
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View? {
        view.findViewById<ImageView>(R.id.barcode).setOnClickListener {
            IntentIntegrator(this@SearchFragment.activity)
                .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                .setPrompt("")
                .setCameraId(0)
                .setBeepEnabled(true)
                .setBarcodeImageEnabled(true)
                .setCaptureActivity(CaptureActivityPortrait::class.java)
                .initiateScan()
        }


        updatePanel()

        return view
    }

    fun updatePanel() {
        flowlayout!!.removeAllViews()

        var source = Realm.getDefaultInstance().where(SearchHistory::class.java).equalTo("userid", SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID")).findAll().sort("hashCode", Sort.DESCENDING)

        for (s in source) {
            var historyCell = HistoryCell(MainActivity.mContext, s)
            historyCell.setOnClickListener{
                var query = historyCell.data.value

                BookListingFragment.query = query

                (MainActivity.mContext as MainActivity).replaceFragment(BookListingFragment())
            }
            flowlayout!!.addView(historyCell)
        }
    }

    fun addHistory(data: String) {
        if(data == null || data.isEmpty()) {
            return
        }

        Realm.getDefaultInstance().beginTransaction()

        var source = Realm.getDefaultInstance().where(SearchHistory::class.java).equalTo("value", data).equalTo("userid", SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID")).findAll()
        source.deleteAllFromRealm()

        var searchHistory = SearchHistory()

        searchHistory.value = data.trim()
        searchHistory.hashCode = Date().time.toInt()
        searchHistory.userid = (SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID"))

        Realm.getDefaultInstance().insert(searchHistory)

        Realm.getDefaultInstance().commitTransaction()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: UpdateSearchEvent?) {
        Log.i("asd","UpdateSearchEvent")
        Realm.getDefaultInstance().beginTransaction();
        var source = Realm.getDefaultInstance().where(SearchHistory::class.java).equalTo("value", event!!.data.value).equalTo("userid", SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID")).findAll();
        source.deleteAllFromRealm();
        Realm.getDefaultInstance().commitTransaction();

        updatePanel()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ScanEvent?) {
        Log.i("scanEvent", "scanEvent")
        addHistory(event!!.barcode)

        BookListingFragment.query = event!!.barcode

        (MainActivity.mContext as MainActivity).replaceFragment(BookListingFragment())
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ResponseEvent?) {
        Log.i("ResponseEvent", "ResponseEvent " + (event!!.response as SearchQueryResponse).data.size )
        var booklistingfragment = BookListingFragment()
        booklistingfragment.isLast =  (event!!.response as SearchQueryResponse).isLast
        booklistingfragment.bookList = (event!!.response as SearchQueryResponse).data

        (MainActivity.mContext as MainActivity).replaceFragment(booklistingfragment)
    }
}