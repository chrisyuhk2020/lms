package com.spit.lms.System.Response

import com.spit.lms.System.Model.Book
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class BookArrayListMoshiAdapter {
    @ToJson
    fun arrayListToJson(list: ArrayList<Book>): List<Book> = list

    @FromJson
    fun arrayListFromJson(list: List<Book>): ArrayList<Book> = ArrayList(list)

}