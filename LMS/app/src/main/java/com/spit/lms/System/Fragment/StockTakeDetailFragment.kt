package com.spit.lms.System.Fragment

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.zxing.integration.android.IntentIntegrator
import com.spit.lms.MainActivity
import com.spit.lms.R
import com.spit.lms.Rfidbase.NewMainActivity
import com.spit.lms.Rfidbase.RFIDDataUpdateEvent
import com.spit.lms.System.Base.BaseFragment
import com.spit.lms.System.Base.BaseUtils
import com.spit.lms.System.Base.SharedPrefsUtils
import com.spit.lms.System.Base.StatusConverter
import com.spit.lms.System.Event.DialogEvent
import com.spit.lms.System.Event.ScanEvent
import com.spit.lms.System.Model.Book
import com.spit.lms.System.Model.StockTakeListBook
import com.spit.lms.System.Model.UploadJson
import com.spit.lms.System.Response.StockListingResponse
import com.spit.lms.System.Scan.CaptureActivityPortrait
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.realm.Realm
import io.realm.Sort
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class StockTakeDetailFragment : BaseFragment () {
    public var stocktakeno: String = ""
    public var stockListingResponse: StockListingResponse = StockListingResponse()

    private var fullList : MutableList<StockTakeListBook> = ArrayList<StockTakeListBook>()//arrayOf<StockTakeListBook>()
    private var instockList : MutableList<StockTakeListBook> = ArrayList<StockTakeListBook>()//arrayOf<StockTakeListBook>()
    private var missingList : MutableList<StockTakeListBook> = ArrayList<StockTakeListBook>()//arrayOf<StockTakeListBook>()
    private var abnormalList : MutableList<StockTakeListBook> = ArrayList<StockTakeListBook>()//arrayOf<StockTakeListBook>()
    lateinit var tabLayout : TabLayout;

    var epcList: MutableList<String> = mutableListOf<String>()
    var epcHashMap = HashMap<String, StockTakeListBook>();
    var abnormalHashMap = HashMap<String, StockTakeListBook>();
    var assetNoHashMap = HashMap<String, StockTakeListBook>();
    var isbnoHashMap = HashMap<String, StockTakeListBook>();

    var inStockHashMap = HashMap<String, StockTakeListBook>();

    lateinit var keywordsearch : EditText;

    lateinit var listview : ListView;
    companion object {
        var selectedPos : Int = 0
    }

    override fun onResume (){
        super.onResume()
        (MainActivity.mContext as MainActivity).stop()

    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        selectedPos = 0

        view = LayoutInflater.from(MainActivity.mContext).inflate(
            R.layout.layout_stocktake_detail, null
        )

        view.findViewById<ImageView>(R.id.barcode).setOnClickListener {
            IntentIntegrator(this@StockTakeDetailFragment.activity)
                .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                .setPrompt("")
                .setCameraId(0)
                .setBeepEnabled(true)
                .setBarcodeImageEnabled(true)
                .setCaptureActivity(CaptureActivityPortrait::class.java)
                .initiateScan()
        }

        listview = view.findViewById(R.id.listview)

        keywordsearch = view.findViewById(R.id.keyword_search)

        tabLayout = (view.findViewById<TabLayout>(R.id.tab_layout))

        keywordsearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                filterText = s.toString()
                setupListView(getData())
            }
        })


        (view.findViewById<TabLayout>(R.id.tab_layout)).setOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                selectedPos = p0!!.position
                setupListView(getData())
                if(bookListAdapter != null && bookListAdapter!!.count > 0) {
                    listview.smoothScrollBy(0, 0);
                    listview.setSelection(0)
                }
                BaseUtils.hideKeyboard(view)
            }
        })

        view.findViewById<MaterialButton>(R.id.start).setOnClickListener {
            if(view.findViewById<MaterialButton>(R.id.start).text.equals(
                    MainActivity.mContext.getString(
                        R.string.start
                    )
                )) {
                (MainActivity.mContext as MainActivity).scanEpc()
                view.findViewById<MaterialButton>(R.id.start).text = MainActivity.mContext.getString(
                    R.string.stop
                )
            } else {
                (MainActivity.mContext as MainActivity).stop()
                view.findViewById<MaterialButton>(R.id.start).text = MainActivity.mContext.getString(
                    R.string.start
                )
            }
        }

        view.findViewById<MaterialButton>(R.id.save).setOnClickListener {

            if(!NewMainActivity.isConnected()) {
                EventBus.getDefault().post(DialogEvent(MainActivity.mContext.getString(R.string.app_name), MainActivity.mContext.getString(R.string.no_internet_connection)))
                return@setOnClickListener
            }

            var s = "["
            var count = 0;
            for(f in fullList) {

               // if(f.tempScanDate != null && f.tempScanDate.length > 0) {
                    var uploadJson : UploadJson = UploadJson();

                    uploadJson.bookNo = f.bookNo
                    uploadJson.type = f.tempType
                    uploadJson.scanDate = f.tempScanDate

                    if(f.tempScanDate.length == 0) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        try {
                            var date = sdf.format(Date())
                            uploadJson.scanDate = date//Date().time.toString()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    if(uploadJson.type != null && uploadJson.type.length == 0) {
                        uploadJson.type = null
                    }

                    uploadJson.status = f.status.toString()

                    val moshi = Moshi.Builder().build()

                    val jsonAdapter: JsonAdapter<UploadJson> = moshi.adapter(UploadJson::class.java)

                    val json = jsonAdapter.toJson(uploadJson)
                    s = s + (json + ",")

               // }
                if(count > 0 && count % 500 == 0) {
                    if(s.length > 1) {
                        s = s.substring(0, s.length - 1)
                    }
                    s += "]"

                    submitAPI(s, stockListingResponse.stocktakeno)
                    s = "["
                }
                count++
            }


            if(s.length > 1) {
                s = s.substring(0, s.length - 1)

                s += "]"
                submitAPI(s, stockListingResponse.stocktakeno)
            }

            s = "["
            count = 0;

            for(a in abnormalList) {
                var uploadJson : UploadJson = UploadJson();

                uploadJson.bookNo = a.bookNo
                uploadJson.type = null
                uploadJson.scanDate = a.tempScanDate

                uploadJson.status = "9"
                uploadJson.epc = a.epc

                val moshi = Moshi.Builder().build()
                val jsonAdapter: JsonAdapter<UploadJson> = moshi.adapter(UploadJson::class.java)
                val json = jsonAdapter.toJson(uploadJson)
                s = s + (json + ",")

                if(count > 0 && count % 500 == 0) {
                    if(s.length > 1) {
                        s = s.substring(0, s.length - 1)
                    }
                    s += "]"

                    submitAPI(s, stockListingResponse.stocktakeno)
                    s = "["
                }
                count++
            }

            if(s.length > 1) {
                s = s.substring(0, s.length - 1)

                s += "]"
                submitAPI(s, stockListingResponse.stocktakeno)
            }


            Realm.getDefaultInstance().beginTransaction()
            Realm.getDefaultInstance().where(StockTakeListBook::class.java)
                .equalTo("userid", SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID"))
                .equalTo("stocktakeno", stockListingResponse.stocktakeno)
                .findAll().deleteAllFromRealm()
            Realm.getDefaultInstance().commitTransaction()

            var handler = Handler()

            handler.postDelayed(Runnable {
                EventBus.getDefault().post(DialogEvent(MainActivity.mContext.getString(R.string.app_name),MainActivity.mContext.getString(R.string.stock_take_uploaded)))
            }, 100)

            (MainActivity.mContext as MainActivity).onBackPressed()

            Log.i("data", "data " + s)
            Log.i(
                "data", "datadata " + (SharedPrefsUtils.getStringPreference(
                    MainActivity.mContext,
                    "BASE_URL"
                )) + "/uploadStockTake?userid=" + SharedPrefsUtils.getStringPreference(
                    MainActivity.mContext,
                    "USERNAME"
                ) + "&stocktakeno=" + stockListingResponse.stocktakeno
            )

        }

        view.findViewById<ImageView>(R.id.back).setOnClickListener {
            (MainActivity.mContext as MainActivity).onBackPressed()
        }

        view.findViewById<TextView>(R.id.title).text = stockListingResponse.name

        initList()
        setupListView(getData())
    }

    fun submitAPI(data: String, stocktakeno: String) {

        Log.i("stocktakeno", "stocktakeno " + stocktakeno + " " + data)

        ((SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL")) + "/uploadStockTake")
            .httpPost(
                listOf(
                    "userid" to SharedPrefsUtils.getStringPreference(
                        MainActivity.mContext, "USERID"
                    ), "stocktakeno" to stocktakeno//stockListingResponse.stocktakeno
                )
            )
            .jsonBody(data, Charset.defaultCharset()).requestProgress { readBytes, totalBytes ->
                val progress = readBytes.toFloat() / totalBytes.toFloat()
                Log.i("progress", "progress " + progress)
            }.response { request, response, result ->

                val s: Scanner = Scanner(request.body.toStream()).useDelimiter("\\A");
                val result = if (s.hasNext()) s.next() else ""

                Log.i(
                    "request",
                    "request " + response.responseMessage + " " + response.body() + " " + result
                )

                val moshi = Moshi.Builder().build()

                val type = Types.newParameterizedType(List::class.java, UploadJson::class.java)
                val jsonAdapter: JsonAdapter<List<UploadJson>> = moshi.adapter(type)

                val json = jsonAdapter.fromJson(result)

                Log.i("json", "json " + json!!.size)
            }
    }

    fun initList() {

        if(fullList.isNullOrEmpty()) {


            fullList = Realm.getDefaultInstance().copyFromRealm(
                Realm.getDefaultInstance().where(StockTakeListBook::class.java)
                    .equalTo("userid", SharedPrefsUtils.getStringPreference( MainActivity.mContext,"USERID"))
                    .equalTo("stocktakeno", stockListingResponse.stocktakeno)
                    .sort("bookNo", Sort.ASCENDING)

                    //.notEqualTo("statusid",  (9).toInt())
                    .findAll()
            )//.toTypedArray();

            for(stockTake in fullList) {
                Log.i("epcdata", "epcdata " + stockTake.epc)
                if(!stockTake.epc.isNullOrEmpty()) {
                    epcList.add(stockTake.epc)

                    epcHashMap.put(stockTake.epc, stockTake)
                    Log.i("epcHashMap", "epcHashMap " + stockTake.epc)

                    assetNoHashMap.put(stockTake.bookNo, stockTake)
                    isbnoHashMap.put(stockTake.isbn, stockTake)

                } else {
                }
            }
        }

        if(instockList.isNullOrEmpty()) {
            val list: MutableList<StockTakeListBook> = instockList.toMutableList();

            for(inventory in fullList) {
                Log.i("inventorystatus", "inventorystatus " + inventory.status + " " + inventory.foundStatus)

                if(inventory.status == 2) {
                    inStockHashMap.put(inventory.epc, inventory)
                    list.add(inventory)
                }
            }

            instockList = list//.toTypedArray()
        }


        if(missingList.isNullOrEmpty()) {
            val list: MutableList<StockTakeListBook> = missingList.toMutableList();

            for(inventory in fullList) {
                if(inventory.status == 10) {
                    list.add(inventory)
                }
            }
            missingList = list//.toTypedArray()
        }


        if(abnormalList.isNullOrEmpty()) {
            //abnormalList = RealmUtils.realmInstance.copyFromRealm((RealmUtils.getInventoryList("", "", "", "", "2"))).toTypedArray()
        }

        //Log.i("abnormalList", "abnormalList " + abnormalList.size + " " + RealmUtils.realmInstance.copyFromRealm((RealmUtils.getInventoryList("", "", "", "", "2"))).toTypedArray().size)
    }

    fun filter( filterText : String, stlbook: List<StockTakeListBook>) :List<StockTakeListBook> {

        var list = mutableListOf<StockTakeListBook>();

        for(i in stlbook) {
            if(filterText.isNullOrEmpty()) {
                list.add(i)
            } else if(i.bookNo != null && (i.bookNo.contains(filterText)
                        || (i.callNo != null && i.callNo.contains(filterText))
                        || (i.name != null &&  i.name.contains(filterText))
                        || (i.publisher != null && i.publisher.contains(filterText))
                        || (i.isbn != null && i.isbn.contains(filterText))
                        || (i.location != null && i.location.contains(filterText))
                        || (i.category != null && i.category.contains(filterText))
                        || (i.epc != null && i.epc.contains(filterText) )) ) {
                list.add(i)
            }
        }

        return list
    }

    var filterText = ""

    fun getData(): List<StockTakeListBook> {
         //var filterText = view.findViewById<TextView>(R.id.edittext).text.toString()
        var selectedPos = (view.findViewById<TabLayout>(R.id.tab_layout)).selectedTabPosition


        if (selectedPos == 0) {
            return filter(filterText, fullList);
        }


        if (selectedPos == 1) {
            instockList.sort();
            return filter(filterText, instockList);
        }

        if (selectedPos == 2) {
            missingList.sort();
            return filter(filterText, missingList);
        }

        if (selectedPos == 3) {
            abnormalList.sort();
            return filter(filterText, abnormalList);
        }

        return ArrayList<StockTakeListBook>()
    }

    var bookListAdapter : BookListingAdapter? = null

    fun setupListView(inventory: List<StockTakeListBook>) {
        try {
            (view.findViewById<TextView>(R.id.title) as TextView)!!.text = stockListingResponse.name + " (" + inventory.size + ")"

             if(bookListAdapter == null) {
                 Log.i("data", "data " + getData().size)
                bookListAdapter = BookListingAdapter(getData())
                (view.findViewById<View>(R.id.listview) as ListView)!!.adapter = bookListAdapter
            } else {
                 Log.i("data", "data2 " + getData().size)

                 listview.invalidate()
               bookListAdapter!!.bookList = getData()
               bookListAdapter!!.notifyDataSetChanged()
            }

            (view.findViewById<TextView>(R.id.title) as TextView)!!.text = stockListingResponse.name + " (" + bookListAdapter!!.count + ")"

            //view!!.findViewById<LinearLayout>(R.id.loading).visibility = View.GONE
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View? {
        /*val ft: FragmentTransaction =
            (MainActivity.mContext as MainActivity).getSupportFragmentManager().beginTransaction()

        var st = BookListingFragment()
        st.hideControl()
        ft.replace(R.id.frame, st)

        ft.commit()*/

        return view
    }


    class BookListingAdapter(var bookList: List<StockTakeListBook>) : BaseAdapter() {
        override fun getCount(): Int {
            Log.i("count", "count " + bookList.size)
            return bookList.size
        }

        override fun getItem(position: Int): StockTakeListBook {
            return bookList[position]
        }

        override fun getItemId(position: Int): Long {
            return bookList[position].hashCode().toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            var view : View? = null;
            if (convertView == null) {
                view = LayoutInflater.from(MainActivity.mContext).inflate(
                    R.layout.book_listing_cell,
                    null
                )
            } else {
                view = convertView
            }

            var book = getItem(position)

            view!!.findViewById<TextView>(R.id.bookno).text = book.bookNo
            view!!.findViewById<TextView>(R.id.call_no).text = book.callNo
            view!!.findViewById<TextView>(R.id.title).text = book.name
            view!!.findViewById<TextView>(R.id.author).text = book.author
            view!!.findViewById<TextView>(R.id.isbn).text = book.isbn
            view!!.findViewById<TextView>(R.id.publisher).text = book.publisher
            view!!.findViewById<TextView>(R.id.category).text = book.category
            view!!.findViewById<TextView>(R.id.location).text = book.location

            view!!.findViewById<TextView>(R.id.status).text = StatusConverter.getStatusById(book.status)


            Log.i("book", "book " + book.image)

            if(book.image == null || book.image == "null" || book.image.isEmpty()) {
                Glide.with(MainActivity.mContext).load(R.drawable.login_frontal_bg).into(
                    view!!.findViewById<ImageView>(
                        R.id.book_image
                    )
                )
            } else {
                Glide.with(MainActivity.mContext).load(book.image).into(view!!.findViewById<ImageView>(R.id.book_image))
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

            //var selectedPos = tabLayout.selectedTabPosition
            if (StockTakeDetailFragment.selectedPos == 3) {
                view!!.findViewById<TextView>(R.id.epc).text = book.epc

                view!!.findViewById<RelativeLayout>(R.id.book_panel).visibility = View.GONE
                view!!.findViewById<TextView>(R.id.epc).visibility = View.VISIBLE
            } else {
                view!!.findViewById<RelativeLayout>(R.id.book_panel).visibility = View.VISIBLE
                view!!.findViewById<TextView>(R.id.epc).visibility = View.GONE
            }

            view!!.setOnClickListener {
                if (book.bookNo != null) {
                    var f = BookDetailFragment()
                    f.bookObj = convertStockTakeBookToBook(book)
                    f.stBookObj = book
                    (MainActivity.mContext as MainActivity).replaceFragment(f)
                }
            }
            return  view
        }


        fun convertStockTakeBookToBook(stBook: StockTakeListBook) : Book {
            var b = Book()
            b.name = stBook.name
            b.author = stBook.author
            b.bookNo = stBook.bookNo
            b.callNo = stBook.callNo
            b.category = stBook.category
            b.isbn = stBook.isbn
            b.description = stBook.description
            b.publisher = stBook.publisher
            b.image = stBook.image
            b.status = stBook.status
            b.location = stBook.location
            b.publishingDate = stBook.publishingDate

            Log.i("stBook", "stBook " + stBook.publishingDate)
            return b
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: StockTakeListBook?) {
        Log.i("StockTakeListBook", "StockTakeListBook " + event.hashCode())

        if(event!!.status == 10) {
            if(!missingList.contains(event)) {
                missingList.add(event)//epcHashMap[event!!.data[i]])
            }
            instockList.remove(event)//epcHashMap[event!!.data[i]]!!)

            setupListView(getData())
            return
        }else if(event.status == 2) {
            missingList.remove(event)//epcHashMap[event!!.data[i]])
            //instockList.add(event)//epcHashMap[event!!.data[i]]!!)

            if(!instockList.contains(event)) {
                instockList.add(event)//epcHashMap[event!!.data[i]])
            }

            Log.i("yoyoyo", "yoyoyoyo")
            setupListView(getData())

            return
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RFIDDataUpdateEvent?) {
        var changed : Boolean = false;

        for (i in 0..event!!.data.size - 1) {
            Log.i("case a", "case a " + event!!.data[i] + " ")
            if(event!!.data[i] != null && event!!.data[i].isNotEmpty()) {
                if (epcHashMap[event!!.data[i]] != null) {
                    if (epcHashMap[event!!.data[i]]!!.status == 10 || epcHashMap[event!!.data[i]]!!.foundStatus < 116 ) {
                        //epcHashMap[event!!.data[i]]!!.status = 2
                        missingList.remove(epcHashMap[event!!.data[i]])

                        if (inStockHashMap[epcHashMap[event!!.data[i]]!!] == null) {
                            instockList.add(epcHashMap[event!!.data[i]]!!)
                            inStockHashMap.put(event!!.data[i], epcHashMap[event!!.data[i]]!!)
                        }

                        epcHashMap[event!!.data[i]]!!.status = 2//Date().time.toString()
                        epcHashMap[event!!.data[i]]!!.tempType = "rfid"//Date().time.toString()

                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        try {
                            var date = sdf.format(Date())
                            epcHashMap[event!!.data[i]]!!.tempScanDate =
                                date//Date().time.toString()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        changed = true
                        //bookListAdapter.notifyDataSetChanged()

                    }
                } else if (abnormalHashMap[event!!.data[i]] == null && event!!.data[i].length > 0) {
                    var stbook = StockTakeListBook()
                    stbook.epc = event!!.data[i]
                    stbook.pk = stockListingResponse.stocktakeno + "_" + event!!.data[i]

                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    try {
                        var date = sdf.format(Date())
                        stbook!!.tempScanDate = date
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    abnormalList.add(stbook)

                    changed = true
                    //Realm.getDefaultInstance().insert(stbook)
                }
            }
        }

        if(changed) {
            setupListView(getData())
        }

        //setupTitle();

    }

    public fun setupTitle() {
        if(tabLayout.selectedTabPosition == 0)
            (view.findViewById<TextView>(R.id.title) as TextView)!!.text = stockListingResponse.name + " (" + fullList.size + ")"

        if(tabLayout.selectedTabPosition == 1)
            (view.findViewById<TextView>(R.id.title) as TextView)!!.text = stockListingResponse.name + " (" + instockList.size + ")"

        if(tabLayout.selectedTabPosition == 2)
            (view.findViewById<TextView>(R.id.title) as TextView)!!.text = stockListingResponse.name + " (" + missingList.size + ")"

        if(tabLayout.selectedTabPosition == 3)
            (view.findViewById<TextView>(R.id.title) as TextView)!!.text = stockListingResponse.name + " (" + abnormalList.size + ")"
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ScanEvent?) {
        Log.i("scanEvent", "scanEvent " + event!!.barcode)

        if (isbnoHashMap[event!!.barcode] != null) {
            missingList.remove(isbnoHashMap[event!!.barcode])//epcHashMap[event!!.data[i]])
            if(!instockList.contains(isbnoHashMap[event!!.barcode]!!)) {
                instockList.add(isbnoHashMap[event!!.barcode]!!)//epcHashMap[event!!.data[i]]!!)
            }

            isbnoHashMap[event!!.barcode]!!.status = 2
            isbnoHashMap[event!!.barcode]!!.tempType = "barcode"
            isbnoHashMap[event!!.barcode]!!.tempScanDate = Date().time.toString()

            setupListView(getData())
            return
        }

        if (epcHashMap[event!!.barcode] != null) {
            missingList.remove(epcHashMap[event!!.barcode])//epcHashMap[event!!.data[i]])
            if(!instockList.contains(isbnoHashMap[event!!.barcode]!!)) {
                instockList.add(isbnoHashMap[event!!.barcode]!!)//epcHashMap[event!!.data[i]]!!)
            }

            epcHashMap[event!!.barcode]!!.status = 2
            epcHashMap[event!!.barcode]!!.tempType = "barcode"
            epcHashMap[event!!.barcode]!!.tempScanDate = Date().time.toString()

            setupListView(getData())
            return
        }

        if (assetNoHashMap[event!!.barcode] != null) {
            missingList.remove(assetNoHashMap[event!!.barcode])//epcHashMap[event!!.data[i]])
            if(!instockList.contains(assetNoHashMap[event!!.barcode]!!)) {
                instockList.add(assetNoHashMap[event!!.barcode]!!)//epcHashMap[event!!.data[i]]!!)
            }

            assetNoHashMap[event!!.barcode]!!.status = 2
            assetNoHashMap[event!!.barcode]!!.tempType = "barcode"
            assetNoHashMap[event!!.barcode]!!.tempScanDate = Date().time.toString()

            setupListView(getData())
            return
        }
    }

}