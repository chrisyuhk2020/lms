package com.spit.lms.System.Fragment

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButton
import com.spit.lms.MainActivity
import com.spit.lms.Network.APIUtils
import com.spit.lms.R
import com.spit.lms.Rfidbase.NewMainActivity
import com.spit.lms.System.Base.BaseFragment
import com.spit.lms.System.Base.SharedPrefsUtils
import com.spit.lms.System.Database.LoginRecord
import com.spit.lms.System.Encryption
import com.spit.lms.System.Event.DialogEvent
import com.spit.lms.System.Event.ResponseEvent
import com.spit.lms.System.Response.LoginResponse
import io.realm.Realm
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*


class LoginFragment : BaseFragment() {
    lateinit var username: EditText;
    lateinit var password: EditText;
    lateinit var checkbox: CheckBox;

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        view = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.fragment_login, null)

        username = view.findViewById(R.id.username)
        password = view.findViewById(R.id.password)
        checkbox = view.findViewById(R.id.save_password)

    }

    override fun onCreateView(
            layoutInflater: LayoutInflater,
            viewGroup: ViewGroup?,
            bundle: Bundle?
    ): View? {
        (view.findViewById<LinearLayout>(R.id.setting)).setOnClickListener {
            (MainActivity.mContext as MainActivity).replaceFragment(SettingFragment())
        }

        view.findViewById<MaterialButton>(R.id.login).setOnClickListener {

            if (SharedPrefsUtils.getStringPreference(
                            MainActivity.mContext,
                            "BASE_URL"
                    ) == null || SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL")
                    .isEmpty()
            ) {
                EventBus.getDefault().post(
                        DialogEvent(
                                MainActivity.mContext.getString(R.string.app_name),
                                MainActivity.mContext.getString(R.string.input_api_address)
                        )
                )
                return@setOnClickListener
            }

            if (username.text.toString().isEmpty()) {
                EventBus.getDefault().post(
                        DialogEvent(
                                getString(R.string.app_name),
                                getString(R.string.empty_username)
                        )
                )
                return@setOnClickListener
            }
            if (password.text.toString().isEmpty()) {
                EventBus.getDefault().post(
                        DialogEvent(
                                getString(R.string.app_name),
                                getString(R.string.empty_password)
                        )
                )
                return@setOnClickListener
            }

            Log.i("abc", "abc " + Encryption.encrypt(password.text.toString()))
            //mIDYQ2t72x9vNAlfFVf0MA==
            //mIDYQ2t72x9vNAlfFVf0MA==

            if(NewMainActivity.isConnected()) {
                APIUtils.get(SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/userAppLogin",
                        listOf("username" to username.text.toString(), "password" to Encryption.encrypt(password.text.toString())), MainActivity.mContext, LoginResponse::class.java
                )
            } else {
                /*
                var source = Realm.getDefaultInstance().where(LoginRecord::class.java)
                    .equalTo("username", username.text.toString()).and()
                    .equalTo("password", (password.text.toString()))
                    .findAll()

                Log.i("source", "source " + source)

                if (source.count() > 0) {
                    SharedPrefsUtils.setStringPreference(
                            MainActivity.mContext,
                            "USERID",
                            source[0]!!.userid
                    )
                    passwordIfNeeded()

                    processData(
                            username.text.toString(),
                            password.text.toString(),
                            source[0]!!.userid
                    )

                    (MainActivity.mContext as MainActivity).changeFragment(BookListingFragment())

                } else {
                    EventBus.getDefault().post(DialogEvent(MainActivity.mContext.getString(R.string.app_name), MainActivity.mContext.getString(R.string.no_internet_connection)))

                    //APIUtils.get(SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/login",
                    //        listOf("username" to username.text.toString(), "password" to Encryption.encrypt(password.text.toString())), MainActivity.mContext, LoginResponse::class.java
                    //)

                }*/
                EventBus.getDefault().post(DialogEvent(MainActivity.mContext.getString(R.string.app_name), MainActivity.mContext.getString(R.string.no_internet_connection)))

            }
            //(MainActivity.mContext as MainActivity).changeFragment(BookListingFragment())
        }


        if (SharedPrefsUtils.getBooleanPreference(MainActivity.mContext, "SAVE_PASSWORD", false)) {
            username.setText(
                    SharedPrefsUtils.getStringPreference(
                            MainActivity.mContext,
                            "USERNAME"
                    )
            )
            password.setText(
                    SharedPrefsUtils.getStringPreference(
                            MainActivity.mContext,
                            "PASSWORD"
                    )
            )

            checkbox.isChecked = true
        } else {
            checkbox.isChecked = false
        }

        return view
    }

    override fun onResume() {
        super.onResume()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ResponseEvent?) {
        //if (event!!.url != null && event!!.url.contains("61161b8253ca131484a88a0e")) {

        var loginResponse: LoginResponse = (event!!.response as LoginResponse)

        if (loginResponse.message != null) {
            if (loginResponse.message.equals("success")) {
                SharedPrefsUtils.setStringPreference(
                        MainActivity.mContext,
                        "USERID",
                        loginResponse.userid
                )
                passwordIfNeeded()

                processData(
                        username.text.toString(),
                        password.text.toString(),
                        loginResponse.userid
                )

                (MainActivity.mContext as MainActivity).changeFragment(BookListingFragment())
            } else {
                var stringData = ""
                if((event!!.response as LoginResponse).message == "user_not_exists") {
                    stringData = getString(R.string.user_not_exists)
                }

                if((event!!.response as LoginResponse).message == "password_incorrect") {
                    stringData = getString(R.string.password_incorrect)
                }

                EventBus.getDefault().post(
                        DialogEvent(
                                getString(R.string.app_name), stringData
                        )
                )
            }
        }
        //}
    }

    fun passwordIfNeeded() {
        if (checkbox.isChecked) {
            SharedPrefsUtils.setStringPreference(
                    MainActivity.mContext,
                    "USERNAME",
                    username.text.toString()
            )
            SharedPrefsUtils.setStringPreference(
                    MainActivity.mContext,
                    "PASSWORD",
                    password.text.toString()
            )
        }
        SharedPrefsUtils.setBooleanPreference(
                MainActivity.mContext,
                "SAVE_PASSWORD",
                checkbox.isChecked
        )
    }

    fun processData(username: String, password: String, userid: String) {
        Realm.getDefaultInstance().beginTransaction()

        Realm.getDefaultInstance().where(LoginRecord::class.java).equalTo("username", username).findAll().deleteAllFromRealm()

        var lr = LoginRecord();
        lr.username = username
        lr.password = password

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try{
            var date = sdf.format(Date())
            lr.loginTime = date
        } catch (e: Exception) {
            e.printStackTrace()
        }

        lr.userid = userid
        lr.pk = lr.userid + lr.loginTime


        Realm.getDefaultInstance().insert(lr)
        Realm.getDefaultInstance().commitTransaction()
    }
}