package com.spit.lms.System.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.children
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nex3z.flowlayout.FlowLayout

import com.spit.lms.MainActivity
import com.spit.lms.Network.APIUtils
import com.spit.lms.R
import com.spit.lms.Rfidbase.NewMainActivity
import com.spit.lms.System.Base.BaseFragment
import com.spit.lms.System.Base.BaseUtils.utf
import com.spit.lms.System.Base.SharedPrefsUtils
import com.spit.lms.System.Base.StatusConverter
import com.spit.lms.System.Database.QueryHistory
import com.spit.lms.System.Event.ResponseEvent
import com.spit.lms.System.Model.*
import com.spit.lms.System.View.FilteringCell
import io.realm.Realm
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.collections.ArrayList

class BookListingFragment : BaseFragment() {
    lateinit var listView: ListView

    public var bookList: List<Book> = ArrayList<Book>()
    public var isLast: Boolean = false
    public var adapter : BookListingAdapter? = null

    companion object {
        var isAPICalled = false
        //var page = 0
        var query = ""
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        //page = 0
        isAPICalled = false

        view = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.layout_book_listing, null)
        view.findViewById<TextView>(R.id.title).text = MainActivity.mContext.getString(R.string.book_listing)

        listView = view.findViewById(R.id.listview)

        view.findViewById<ImageView>(R.id.back).setOnClickListener {
            (MainActivity.mContext as MainActivity).onBackPressed()
        }

        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener(SharedBottomHandler())
        view.findViewById<ImageView>(R.id.search).setOnClickListener {
/*
            view.findViewById<FlowLayout>(R.id.category_wrapper).visibility = View.GONE
            view.findViewById<FlowLayout>(R.id.location_wrapper).visibility = View.GONE
            view.findViewById<FlowLayout>(R.id.author_wrapper).visibility = View.GONE
            view.findViewById<FlowLayout>(R.id.publisher_wrapper).visibility = View.GONE

            showAdvancedSearchingPanel()

 */
            (MainActivity.mContext as MainActivity).replaceFragment(SearchFragment())
        }


        if(query.length > 0) {
            view.findViewById<ImageView>(R.id.back).visibility = View.VISIBLE
            view.findViewById<ImageView>(R.id.search).visibility = View.INVISIBLE

            view.findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.GONE
        } else {
            view.findViewById<ImageView>(R.id.back).visibility = View.INVISIBLE
            view.findViewById<ImageView>(R.id.search).visibility = View.VISIBLE

            view.findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.VISIBLE
        }

        if (hideControl) {
            view.findViewById<LinearLayout>(R.id.bottom).visibility = View.GONE
            view.findViewById<RelativeLayout>(R.id.toolbar).visibility = View.GONE
        }


        if(NewMainActivity.isConnected()) {
            APIUtils.getArrayList(
                SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/search",
                listOf(
                    "page" to "0",
                    "query" to query
                ),
                MainActivity.mContext, Book::class.java
            )
            /*
            APIUtils.getArrayList(
                SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/getCategory",
                listOf(),
                MainActivity.mContext, Category::class.java
            )

            APIUtils.getArrayList(
                SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/getLocation",
                listOf(),
                MainActivity.mContext, Location::class.java
            )
            APIUtils.getArrayList(
                SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/getPublisher",
                listOf(),
                MainActivity.mContext, Publisher::class.java
            )

            APIUtils.getArrayList(
                SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/getAuthor",
                listOf(),
                MainActivity.mContext, Author::class.java
            )*/
        } else {
            var source = Realm.getDefaultInstance().where(QueryHistory::class.java).equalTo("query", query).equalTo("userid", SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID")).findAll()
            listView.adapter = QueryHistoryBookListingAdapter(source)
        }
        Log.i("data", "data " + isLast)
    }

    var hideControl = false
    public fun hideControl() {
        hideControl = true
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View? {
        view.findViewById<ImageView>(R.id.back).setOnClickListener {
            (MainActivity.mContext as MainActivity).onBackPressed()
        }

        if (view.findViewById<ImageView>(R.id.back).visibility == View.VISIBLE) {
        } else {
            query = ""
            isAPICalled = false
        }
        return view
    }
    class QueryHistoryBookListingAdapter(val bookList: MutableList<QueryHistory>) : BaseAdapter() {
        public var page = 0
        public var adapterQuery = ""

        override fun getCount(): Int {
            var extraCount = 0;

            if (adapterQuery.length > 0 && bookList.size % 50 == 0 && bookList.size > 0) {
                extraCount += 1;
            } else {
                extraCount += 0;
            }

            return bookList.size + extraCount
        }

        override fun getItem(position: Int): QueryHistory {
            if (bookList.size > position && bookList[position] != null) {
                return bookList[position]
            } else {
                return QueryHistory()//hashCode()
            }
        }

        override fun getItemId(position: Int): Long {
            if (bookList.size > position && bookList[position] != null) {
                return bookList[position].hashCode().toLong()
            } else {
                return Book().hashCode().toLong()
            }
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var view: View? = null;
            if (convertView == null) {
                view = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.book_listing_cell, null)
            } else {
                view = convertView
            }

            var book = getItem(position)

            view!!.findViewById<TextView>(R.id.bookno).text = utf(book.bookNo)
            view!!.findViewById<TextView>(R.id.call_no).text = utf(book.callNo)
            view!!.findViewById<TextView>(R.id.title).text = utf(book.name)
            view!!.findViewById<TextView>(R.id.author).text = utf(book.author)
            view!!.findViewById<TextView>(R.id.isbn).text = utf(book.isbn)
            view!!.findViewById<TextView>(R.id.publisher).text = utf(book.publisher)
            view!!.findViewById<TextView>(R.id.category).text = utf(book.category)
            view!!.findViewById<TextView>(R.id.location).text = utf(book.location)

            view!!.findViewById<TextView>(R.id.status).text = StatusConverter.getStatusById(book.status)

            Glide.with(MainActivity.mContext).load(book.image).into(view!!.findViewById<ImageView>(R.id.book_image))

            if(book.image == null || book.image.isEmpty()) {
                Glide.with(MainActivity.mContext).load(R.drawable.login_frontal_bg).into(view!!.findViewById<ImageView>(R.id.book_image))
            }

            if(book.location == null || book.location.length == 0) {
                view!!.findViewById<TextView>(R.id.location).visibility = View.GONE
            } else {
                view!!.findViewById<TextView>(R.id.location).visibility = View.VISIBLE
            }

            if(book.category == null || book.category.length == 0) {
                view!!.findViewById<TextView>(R.id.category).visibility = View.GONE
            } else {
                view!!.findViewById<TextView>(R.id.category).visibility = View.VISIBLE
            }

            if(position == bookList.size) {
                view.findViewById<ProgressBar>(R.id.progress).visibility = View.VISIBLE
                view.findViewById<CardView>(R.id.cardview).visibility = View.GONE


                if(!isAPICalled) {
                    //page = page + 1

                    APIUtils.getArrayList(
                        SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/search",
                        listOf(
                            "page" to "" + page,
                            "query" to adapterQuery
                        ),
                        MainActivity.mContext, Book::class.java
                    )
                }

            } else {
                view.findViewById<ProgressBar>(R.id.progress).visibility = View.GONE
                view.findViewById<CardView>(R.id.cardview).visibility = View.VISIBLE
            }

            view!!.setOnClickListener {
                var f = BookDetailFragment()
                var b = Book();
                b.publishingDate = book.publishingDate
                b.location = book.location

                b.status = book.status
                b.image = book.image
                b.publisher = book.publisher
                b.description = book.description
                b.isbn = book.isbn

                b.category = book.category
                b.callNo = book.callNo
                b.bookNo = book.bookNo
                b.author = book.author
                b.name = book.name

                f.bookObj = b
                (MainActivity.mContext as MainActivity).replaceFragment(f)
            }

            return view
        }
    }

    class BookListingAdapter(val bookList: MutableList<Book>) : BaseAdapter() {
        public var page = 0
        public var adapterQuery = ""

        override fun getCount(): Int {
            var extraCount = 0;

            if ( bookList.size % 50 == 0 && bookList.size > 0) {
                extraCount += 1;
            } else {
                extraCount += 0;
            }

            return bookList.size + extraCount
        }

        override fun getItem(position: Int): Book {
            if (bookList.size > position && bookList[position] != null) {
                return bookList[position]
            } else {
                return Book()//hashCode()
            }
        }

        override fun getItemId(position: Int): Long {
            if (bookList.size > position && bookList[position] != null) {
                return bookList[position].hashCode().toLong()
            } else {
                return Book().hashCode().toLong()
            }
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var view: View? = null;
            if (convertView == null) {
                view = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.book_listing_cell, null)
            } else {
                view = convertView
            }

            var book = getItem(position)

            view!!.findViewById<TextView>(R.id.bookno).text = utf(book.bookNo)
            view!!.findViewById<TextView>(R.id.call_no).text = utf(book.callNo)
            view!!.findViewById<TextView>(R.id.title).text = utf(book.name)
            view!!.findViewById<TextView>(R.id.author).text = utf(book.author)
            view!!.findViewById<TextView>(R.id.isbn).text = utf(book.isbn)
            view!!.findViewById<TextView>(R.id.publisher).text = utf(book.publisher)
            view!!.findViewById<TextView>(R.id.category).text = utf(book.category)
            view!!.findViewById<TextView>(R.id.location).text = utf(book.location)

            view!!.findViewById<TextView>(R.id.status).text = StatusConverter.getStatusById(book.status)

            Glide.with(MainActivity.mContext).load(book.image).into(view!!.findViewById<ImageView>(R.id.book_image))

            if(book.image == null || book.image.isEmpty()) {
                Glide.with(MainActivity.mContext).load(R.drawable.login_frontal_bg).into(view!!.findViewById<ImageView>(R.id.book_image))
            }

            if(book.location == null || book.location.length == 0) {
                view!!.findViewById<TextView>(R.id.location).visibility = View.GONE
            } else {
                view!!.findViewById<TextView>(R.id.location).visibility = View.VISIBLE
            }

            if(book.category == null || book.category.length == 0) {
                view!!.findViewById<TextView>(R.id.category).visibility = View.GONE
            } else {
                view!!.findViewById<TextView>(R.id.category).visibility = View.VISIBLE
            }

            if(position == bookList.size) {
                view.findViewById<ProgressBar>(R.id.progress).visibility = View.VISIBLE
                view.findViewById<CardView>(R.id.cardview).visibility = View.GONE


                if(!isAPICalled) {
                    //page = page + 1

                    APIUtils.getArrayList(
                        SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/search",
                        listOf(
                            "page" to "" + page,
                            "query" to adapterQuery
                        ),
                        MainActivity.mContext, Book::class.java
                    )
                }

            } else {
                view.findViewById<ProgressBar>(R.id.progress).visibility = View.GONE
                view.findViewById<CardView>(R.id.cardview).visibility = View.VISIBLE
            }

            view!!.setOnClickListener {
                var f = BookDetailFragment()
                f.bookObj = book
                (MainActivity.mContext as MainActivity).replaceFragment(f)
            }

            return view
        }
    }

    fun insertBookAsQueryHistory(b : Book) {
        var queryHistory = QueryHistory();
        queryHistory.query = query
        queryHistory.author = b.author
        queryHistory.bookNo = b.bookNo
        queryHistory.callNo = b.callNo
        queryHistory.category = b.category

        queryHistory.description = b.description
        queryHistory.image = b.image
        queryHistory.isbn = b.isbn
        queryHistory.location = b.location

        queryHistory.name = b.name
        queryHistory.publisher = b.publisher
        queryHistory.publishingDate = b.publishingDate
        queryHistory.status = b.status
        queryHistory.userid = SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID")
        queryHistory.pk = queryHistory.userid + query + b.bookNo

        Realm.getDefaultInstance().insert(queryHistory)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ResponseEvent?) {
        Log.i("url", "ResponseEvent url "+  event!!.url)

        if(event!!.url.contains("getCategory")) {
            var list = (event.response as List<Category>)

            Realm.getDefaultInstance().beginTransaction()
            Realm.getDefaultInstance().where(Category::class.java).findAll().deleteAllFromRealm()
            for(l in list) {
                if(l.categoryName != "Category") {
                    Realm.getDefaultInstance().insert(l)
                }
            }
            Realm.getDefaultInstance().commitTransaction()
        }

        if(event!!.url.contains("getLocation")) {
            var list = (event.response as List<Location>)
            Realm.getDefaultInstance().beginTransaction()
            Realm.getDefaultInstance().where(Location::class.java).findAll().deleteAllFromRealm()
            for(l in list) {
                if(l.locationName != "Location") {
                    Realm.getDefaultInstance().insert(l)
                }
            }
            Realm.getDefaultInstance().commitTransaction()
        }

        if(event!!.url.contains("getAuthor")) {
            var list = (event.response as List<Author>)
            Realm.getDefaultInstance().beginTransaction()
            Realm.getDefaultInstance().where(Author::class.java).findAll().deleteAllFromRealm()
            for(l in list) {
                Realm.getDefaultInstance().insert(l)
            }
            Realm.getDefaultInstance().commitTransaction()
        }

        if(event!!.url.contains("getPublisher")) {
            var list = (event.response as List<Publisher>)
            Realm.getDefaultInstance().beginTransaction()
            Realm.getDefaultInstance().where(Publisher::class.java).findAll().deleteAllFromRealm()
            for(l in list) {
                Realm.getDefaultInstance().insert(l)
            }
            Realm.getDefaultInstance().commitTransaction()
        }

        if(event!!.url.contains("search")){
        //if((event!!.response as ArrayList<Book>).size > 0) {
            isAPICalled = false

            if (adapter == null) {
                Log.i("case0", "case0")

                adapter = BookListingAdapter((event!!.response as ArrayList<Book>))
                adapter!!.page = adapter!!.page + 1
                adapter!!.adapterQuery = query
                listView.adapter = adapter

                Log.i("case0", "case0 " + adapter!!.page )

                Realm.getDefaultInstance().beginTransaction()
                Realm.getDefaultInstance().where(QueryHistory::class.java).equalTo("userid", SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID")).equalTo("query", query).findAll().deleteAllFromRealm()

                for(b in (event!!.response as ArrayList<Book>)) {
                    insertBookAsQueryHistory(b)
                }
                Realm.getDefaultInstance().commitTransaction()

            } else {
                Log.i("case1", "case1")
                adapter!!.page = adapter!!.page + 1
                //adapter!!.adapterQuery = query

                adapter!!.bookList.addAll((event!!.response as ArrayList<Book> ))

                Realm.getDefaultInstance().beginTransaction()
                for(b in (event!!.response as ArrayList<Book>)) {
                    insertBookAsQueryHistory(b)
                }
                Realm.getDefaultInstance().commitTransaction()

                adapter!!.notifyDataSetChanged()

                Log.i("case1", "case1" + adapter!!.page )

            }
        }
    }

    fun showAdvancedSearchingPanel() {

        view.findViewById<View>(R.id.advanced_search_panel_background).setOnClickListener {
            view.findViewById<LinearLayout>(R.id.advanced_search_panel).visibility = View.GONE
        }

        //category_headerlocation_headerlocation_wrapperpublisher_header
        view.findViewById<LinearLayout>(R.id.category_header).setOnClickListener {
            Log.i("click", "click category " + (view.findViewById<FlowLayout>(R.id.category_wrapper).visibility == View.VISIBLE) )

            if(view.findViewById<FlowLayout>(R.id.category_wrapper).visibility == View.VISIBLE) {
                view.findViewById<FlowLayout>(R.id.category_wrapper).visibility = View.GONE

            } else {
                view.findViewById<FlowLayout>(R.id.category_wrapper).visibility = View.VISIBLE
            }
        }

        view.findViewById<LinearLayout>(R.id.location_header).setOnClickListener {
            if(view.findViewById<FlowLayout>(R.id.location_wrapper).visibility == View.VISIBLE) {
                view.findViewById<FlowLayout>(R.id.location_wrapper).visibility = View.GONE
            } else {
                view.findViewById<FlowLayout>(R.id.location_wrapper).visibility = View.VISIBLE
            }
        }

        view.findViewById<LinearLayout>(R.id.author_header).setOnClickListener {
            if(view.findViewById<FlowLayout>(R.id.author_wrapper).visibility == View.VISIBLE) {
                view.findViewById<FlowLayout>(R.id.author_wrapper).visibility = View.GONE
            } else {
                view.findViewById<FlowLayout>(R.id.author_wrapper).visibility = View.VISIBLE
            }
        }

        view.findViewById<LinearLayout>(R.id.publisher_header).setOnClickListener {
            if(view.findViewById<FlowLayout>(R.id.publisher_wrapper).visibility == View.VISIBLE) {
                view.findViewById<FlowLayout>(R.id.publisher_wrapper).visibility = View.GONE
            } else {
                view.findViewById<FlowLayout>(R.id.publisher_wrapper).visibility = View.VISIBLE
            }
        }

        view.findViewById<LinearLayout>(R.id.advanced_search_panel).visibility = View.VISIBLE

        var categorySource = Realm.getDefaultInstance().where(Category::class.java).findAll()
        view.findViewById<FlowLayout>(R.id.category_wrapper).removeAllViews()
        for(l in categorySource) {
            view.findViewById<FlowLayout>(R.id.category_wrapper).addView(FilteringCell(MainActivity.mContext,l.categoryName))
        }

        var locationSource = Realm.getDefaultInstance().where(Location::class.java).findAll()
        view.findViewById<FlowLayout>(R.id.location_wrapper).removeAllViews()
        for(l in locationSource) {
            view.findViewById<FlowLayout>(R.id.location_wrapper).addView(FilteringCell(MainActivity.mContext,l.locationName))
        }

        var authorSource = Realm.getDefaultInstance().where(Author::class.java).findAll()
        view.findViewById<FlowLayout>(R.id.author_wrapper).removeAllViews()
        for(l in authorSource) {
            view.findViewById<FlowLayout>(R.id.author_wrapper).addView(FilteringCell(MainActivity.mContext,l.name))
        }

        var publisherSource = Realm.getDefaultInstance().where(Publisher::class.java).findAll()
        view.findViewById<FlowLayout>(R.id.publisher_wrapper).removeAllViews()
        for(l in publisherSource) {
            view.findViewById<FlowLayout>(R.id.publisher_wrapper).addView(FilteringCell(MainActivity.mContext,l.name))
        }

        view.findViewById<TextView>(R.id.search_panel_reset).setOnClickListener {
            showAdvancedSearchingPanel()
        }

        view.findViewById<TextView>(R.id.search_panel_search).setOnClickListener {
            var v = view.findViewById<FlowLayout>(R.id.category_wrapper)

            for(c in v.children) {
               Log.i ("data", "data " + (c as FilteringCell).data);
            }
        }
    }
}