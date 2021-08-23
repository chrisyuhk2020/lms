package com.spit.lms.Network

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.moshi.moshiDeserializerOf
import com.spit.lms.MainActivity
import com.spit.lms.R
import com.spit.lms.Rfidbase.NewMainActivity
import com.spit.lms.System.Base.BaseUtils
import com.spit.lms.System.Base.SharedPrefsUtils
import com.spit.lms.System.Event.DialogEvent
import com.spit.lms.System.Event.LoginDownloadProgressEvent
import com.spit.lms.System.Event.ResponseEvent
import com.spit.lms.System.Response.BookArrayListMoshiAdapter
import com.spit.lms.System.Response.SearchQueryResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object APIUtils {

    fun <T : Any> getArrayList(url: String, parameters: Parameters? = null, context: Context?, clazz: Class<T>) {
        if(SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL").isNullOrEmpty()) {
            return
        }

        if(url == null)
            return

        Log.i("APIUtils", "APIUtils requests " + url)

        val moshi = Moshi.Builder().build()
        val type = Types.newParameterizedType(List::class.java, clazz)
        val jsonAdapter: JsonAdapter<List<T>> = moshi.adapter(type)

        try {
            url.httpGet(parameters)
                .responseObject(moshiDeserializerOf(jsonAdapter))
                { _, response, result ->
                    when (result) {

                        is com.github.kittinunf.result.Result.Failure -> {
                            Log.i("failure", "failure");

                            val ex = result.getException()
                            println(ex)
                            EventBus.getDefault().post(DialogEvent(context!!.getString(R.string.app_name), ex.toString()))
                        }
                        is com.github.kittinunf.result.Result.Success -> {

                            val data = result.get()

                            Log.i("success", "success " + url + " " + data);
                            when ((data as ArrayList<*>).firstOrNull()) {
                            }


                            EventBus.getDefault().post(
                                ResponseEvent(
                                    url,
                                    (data)
                                )
                            )


                            println(data)

                        }

                    }
                }
        } catch (e : Exception) {
            EventBus.getDefault().post(DialogEvent(context!!.getString(R.string.app_name), e.toString()))
        }
    }

    fun <T : Any> get(url: String, parameters: Parameters? = null, context: Context?, clazz: Class<T>) {

        if(SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL").isNullOrEmpty()) {
            return
        }

        if(!NewMainActivity.isConnected()){
            EventBus.getDefault().post(DialogEvent(context!!.getString(R.string.app_name), MainActivity.mContext.getString(R.string.no_internet_connection)))
            return
        }

        if(url == null)
            return

        Log.i("url", "url " + url)

        try {
            url.httpGet(parameters)
                .responseObject(moshiDeserializerOf(clazz))
                { _, response, result ->
                    when (result) {

                        is com.github.kittinunf.result.Result.Failure -> {
                            Log.i("failure", "failure" + response.url);

                            val ex = result.getException()
                            println(ex)
                            EventBus.getDefault().post(DialogEvent(context!!.getString(R.string.app_name), ex.toString()))
                        }
                        is com.github.kittinunf.result.Result.Success -> {
                            Log.i("success", "success" + response.url);

                            val data = result.get()
                            println(data)

                            EventBus.getDefault().post(
                                ResponseEvent(
                                    url,
                                    (data)
                                )
                            )
                        }

                    }
                }
        } catch (e : Exception) {
            EventBus.getDefault().post(DialogEvent(context!!.getString(R.string.app_name), e.toString()))
        }
    }

    fun download(url: String, stocktakeno : String) {
        if(SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL").isNullOrEmpty()) {
            return
        }

        Fuel.download(url).destination { response, url ->
            File.createTempFile("temp", ".tmp")
        }
            .progress { readBytes, totalBytes ->
                val progress = readBytes.toFloat() / totalBytes.toFloat()

                if(progress > 0) {
                    EventBus.getDefault().post(LoginDownloadProgressEvent(progress))
                }

            }.
            response { request, response, result ->
                val (data, error) = result
                if (error != null) {
                    Log.e("download", "error: ${error}")
                } else {
                    result.fold({ bytes ->

                        try {
                            val outputStreamWriter = OutputStreamWriter(MainActivity.mContext!!.openFileOutput("master.json", Context.MODE_PRIVATE))
                            outputStreamWriter.write(java.lang.String(response.data).toString())
                            outputStreamWriter.close()

                            AsyncTask.execute {
                                BaseUtils.parseLargeJson(MainActivity.mContext!!.getFilesDir().toString() + "/"+ ("master.json"), stocktakeno);
                            }

                        } catch (e: java.lang.Exception) {
                        }

                    }, {err ->
                    })
                }
            }
    }
}