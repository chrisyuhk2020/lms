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
import com.spit.lms.R
import com.spit.lms.System.Base.BaseFragment
import com.spit.lms.System.Base.BaseUtils.utf
import com.spit.lms.System.Base.StatusConverter
import com.spit.lms.System.Model.Book
import com.spit.lms.System.Model.StockTakeListBook
import org.greenrobot.eventbus.EventBus
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
        view.findViewById<TextView>(R.id.book_detail_name).text = utf(bookObj.callNo)
        view.findViewById<TextView>(R.id.book_detail_name).text = utf(bookObj.name)
        view.findViewById<TextView>(R.id.book_detail_author).text = utf(bookObj.author)
        view.findViewById<TextView>(R.id.book_detail_isbn).text = utf(bookObj.isbn)
        view.findViewById<TextView>(R.id.book_detail_publisher).text = utf(bookObj.publisher)
        Log.i("data", "data [" + bookObj.publishingDate + "]")

        if(bookObj.publishingDate != null && bookObj.publishingDate != "null") {
            view.findViewById<TextView>(R.id.book_detail_publisher_date).text = utf(bookObj.publishingDate)
        } else {
            view.findViewById<TextView>(R.id.book_detail_publisher_date).text = ""
        }
        view.findViewById<TextView>(R.id.book_detail_category).text = utf(bookObj.category)
        //view.findViewById<TextView>(R.id.book_detail_l).text = bookObj.location
        view.findViewById<TextView>(R.id.book_detail_description).text = utf(bookObj.description)

        view.findViewById<TextView>(R.id.book_detail_status).text = StatusConverter.getStatusById(
            bookObj.status
        )

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

}