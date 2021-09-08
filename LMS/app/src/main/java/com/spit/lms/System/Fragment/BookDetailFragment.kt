package com.spit.lms.System.Fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.spit.lms.MainActivity
import com.spit.lms.Network.APIUtils
import com.spit.lms.R
import com.spit.lms.System.Base.BaseFragment
<<<<<<< HEAD
import com.spit.lms.System.Base.BaseUtils.utf
import com.spit.lms.System.Base.SharedPrefsUtils
=======
>>>>>>> parent of 687fe26 (commit)
import com.spit.lms.System.Base.StatusConverter
import com.spit.lms.System.Event.DialogEvent
import com.spit.lms.System.Event.ResponseEvent
import com.spit.lms.System.Model.Book
import com.spit.lms.System.Model.StockTakeListBook
import com.spit.lms.System.Response.BorrowHistoryResponse
import com.spit.lms.System.Response.ReserveBookResponse
import com.spit.lms.System.Response.ReservedHistoryResponse
import com.spit.lms.System.Response.UserDetailResponse
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class BookDetailFragment : BaseFragment() {
    lateinit var appbar_layout : AppBarLayout;
    lateinit var collaspse_appbar_layout : CollapsingToolbarLayout;
    public var bookObj : Book = Book()
    public var stBookObj : StockTakeListBook = StockTakeListBook()

    override fun onDestroy() {
        super.onDestroy()
        Log.i("destroy", "destory")
        //Handler().postDelayed(Runnable {
        EventBus.getDefault().post(stBookObj)
        //}, 500)
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        view = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.layout_book_detail, null)

        appbar_layout = view.findViewById(R.id.appbar_layout)
        collaspse_appbar_layout = view.findViewById(R.id.collaspse_appbar_layout)

        val toolbar: Toolbar = view.findViewById(R.id.toolbar) as Toolbar

        (MainActivity.mContext as MainActivity).setSupportActionBar(toolbar)
        (MainActivity.mContext as MainActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (MainActivity.mContext as MainActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)


       //view.findViewById<TextView>(R.id.book_detail_name).text = bookObj.bookNo
        view.findViewById<TextView>(R.id.book_detail_name).text = bookObj.callNo
        view.findViewById<TextView>(R.id.book_detail_name).text = bookObj.name
        view.findViewById<TextView>(R.id.book_detail_author).text = bookObj.author
        view.findViewById<TextView>(R.id.book_detail_isbn).text = bookObj.isbn
        view.findViewById<TextView>(R.id.book_detail_publisher).text = bookObj.publisher
        Log.i("data", "data [" + bookObj.publishingDate + "]")

        if(bookObj.publishingDate != null && bookObj.publishingDate != "null") {
            view.findViewById<TextView>(R.id.book_detail_publisher_date).text = bookObj.publishingDate
        } else {
            view.findViewById<TextView>(R.id.book_detail_publisher_date).text = ""
        }
        view.findViewById<TextView>(R.id.book_detail_category).text = bookObj.category
        //view.findViewById<TextView>(R.id.book_detail_l).text = bookObj.location
        view.findViewById<TextView>(R.id.book_detail_description).text = bookObj.description

        view.findViewById<TextView>(R.id.book_detail_status).text = StatusConverter.getStatusById(
            bookObj.status
        )

        if(bookObj.status == 4) {
            view.findViewById<LinearLayout>(R.id.reserve_wrapper).visibility = View.GONE
        } else {
            view.findViewById<LinearLayout>(R.id.reserve_wrapper).visibility = View.VISIBLE
        }

        view.findViewById<LinearLayout>(R.id.reserve_wrapper).setOnClickListener {

            APIUtils.get(
                SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/reserveBook",
                listOf(
                    "userid" to SharedPrefsUtils.getStringPreference(MainActivity.mContext, "USERID"),
                    "bookRoNo" to bookObj.rono
                ), MainActivity.mContext, ReserveBookResponse::class.java
            )
        }

        view.findViewById<LinearLayout>(R.id.status_wrapper).setOnClickListener {
            Log.i("status", "status  " + stBookObj.status + " " + stBookObj.tempType)
            if(stBookObj != null && stBookObj.status == 10 && stBookObj.tempType != null && stBookObj.tempType != "rfid" && stBookObj.tempType != "barcode"){
                val builderSingle: AlertDialog.Builder = AlertDialog.Builder(activity)

                val arrayAdapter = ArrayAdapter<String>(
                    MainActivity.mContext,
                    android.R.layout.select_dialog_item
                )

                arrayAdapter.add(MainActivity.mContext.getString(R.string.stocktake_instock))
                arrayAdapter.add(MainActivity.mContext.getString(R.string.stocktake_not_instock))


                builderSingle.setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.dismiss()}


                builderSingle.setAdapter(arrayAdapter,    DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        0 -> {
                            Log.i("pos", "pos 0")
                            stBookObj.status = 2
                            stBookObj.tempType = "manually"
                            //stBookObj.tempScanDate = Date().time.toString()


                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            try {
                                var date = sdf.format(Date())
                                stBookObj.tempScanDate = date//Date().time.toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            view.findViewById<TextView>(R.id.book_detail_status).text = StatusConverter.getStatusById(
                                stBookObj.status
                            )

                            EventBus.getDefault().post(stBookObj)
                        }
                        1 ->{
                            Log.i("pos", "pos 2")
                            stBookObj.status = 10
                            stBookObj.tempType = ""//"manually"
                            stBookObj.tempScanDate = ""//Date().time.toString()


                            view.findViewById<TextView>(R.id.book_detail_status).text = StatusConverter.getStatusById(
                                stBookObj.status
                            )

                            EventBus.getDefault().post(stBookObj)
                        }
                        else -> { // Note the block
                            print("x is neither 1 nor 2")
                        }
                    }
                });

                builderSingle.show()

            }
        }

        Glide.with(MainActivity.mContext).load(bookObj.image).into(view.findViewById<ImageView>(R.id.book_detail_image))

        if(bookObj.image == null ||bookObj.image == "null" || bookObj.image.isEmpty()) {
            Glide.with(MainActivity.mContext).load(R.drawable.login_frontal_bg).into(
                view!!.findViewById<ImageView>(
                    R.id.book_detail_image
                )
            )
        }

        toolbar.setNavigationOnClickListener { view: View? -> (MainActivity.mContext as MainActivity).onBackPressed() }
       // collaspse_appbar_layout.title = "Skectching User Experience"

        var isShow = true
        var scrollRange = -1
        appbar_layout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0) {
                collaspse_appbar_layout.title = bookObj.name//"Skectching User Experience"
                print("case 0")
                isShow = true
            } else if (isShow) {
                collaspse_appbar_layout.title =
                    " " //careful there should a space between double quote otherwise it wont work
                print("case 1")

                isShow = false
            } else {
                print("case 2")
            }
        })


    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {

        return view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                Log.i("home", "home ")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ResponseEvent?) {

        if (event!!.url.contains("reserveBook")) {
            var reserveBookResponse = (event!!.response as ReserveBookResponse);

            if(reserveBookResponse != null) {
                if(reserveBookResponse.status == "success"){
                    EventBus.getDefault().post(DialogEvent(MainActivity.mContext.getString(R.string.app_name), MainActivity.mContext.getString(R.string.success)))
                } else if(reserveBookResponse.status == "failure"){
                    /*
                    100：当前年度爽约次数已经达到上限
                    101：当前预约次数已经达到上限
                    102：当前书籍已经被预约
                    103：当前书籍已被自己借出

                     */
                    var result = ""

                    if(reserveBookResponse.message == "100") {
                        result = MainActivity.mContext.getString(R.string.reach_skip_limit)
                    }

                    if(reserveBookResponse.message == "101") {
                        result = MainActivity.mContext.getString(R.string.reach_appointmentt_limit)
                    }

                    if(reserveBookResponse.message == "102") {
                        result = MainActivity.mContext.getString(R.string.reach_reserved)
                    }

                    if(reserveBookResponse.message == "103") {
                        result = MainActivity.mContext.getString(R.string.reach_borrowed)
                    }

                    EventBus.getDefault().post(DialogEvent(MainActivity.mContext.getString(R.string.app_name),  result))
                }
            }
        }

    }

}