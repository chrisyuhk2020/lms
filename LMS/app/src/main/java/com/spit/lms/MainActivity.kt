package com.spit.lms

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.zxing.integration.android.IntentIntegrator
import com.spit.lms.Rfidbase.NewMainActivity
import com.spit.lms.System.Base.BaseEvent
import com.spit.lms.System.Base.SharedPrefsUtils
import com.spit.lms.System.Event.DialogEvent
import com.spit.lms.System.Event.ScanEvent
import com.spit.lms.System.Fragment.LoginFragment
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class MainActivity : NewMainActivity() {
    companion object {
        lateinit var mContext : Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        mContext = this

        Realm.init(mContext)
        FirebaseApp.initializeApp(this)

        if(SharedPrefsUtils.getStringPreference(MainActivity.mContext, "token") == null) {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }

                    val token = task.result
                    val msg = token

                    SharedPrefsUtils.setStringPreference(MainActivity.mContext, "token", token)

                    //Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                })
        } else {
            Log.d("TAG", SharedPrefsUtils.getStringPreference(MainActivity.mContext, "token") + "")

        }


        var handler = Handler()

        handler.postDelayed(Runnable {
            var l = SharedPrefsUtils.getStringPreference(MainActivity.mContext, "locale")
            var d = SharedPrefsUtils.getStringPreference(MainActivity.mContext, "defaultlocale")

            //if(d != l) {
            if(l == null) {
                l = "en-us"
            }

            val locale = Locale(l)
            SharedPrefsUtils.setStringPreference(MainActivity.mContext, "locale", l)

            Locale.setDefault(locale)
            val resources: Resources = getResources()
            val config: Configuration = resources.getConfiguration()
            config.setLocale(locale)
            resources.updateConfiguration(config, resources.getDisplayMetrics())

            //(MainActivity.mContext as MainActivity).changeFragment(LoginFragment())
            //}
            changeFragment(LoginFragment())

        }, 10)
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: BaseEvent?) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: DialogEvent?) {
        AlertDialog.Builder(this)
            .setTitle(event!!.title)
            .setMessage(event!!.message)
            .setPositiveButton(android.R.string.ok) { dialog, which -> }
            .show()
    }

    // Get the results:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Handler().postDelayed(Runnable {
                    EventBus.getDefault().post(ScanEvent(result.contents))
                }, 200)
                //Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}