package com.spit.lms.System.Fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.spit.lms.MainActivity
import com.spit.lms.Network.APIUtils
import com.spit.lms.R
import com.spit.lms.System.Base.BaseFragment
import com.spit.lms.System.Base.BaseUtils
import com.spit.lms.System.Base.SharedPrefsUtils
import com.spit.lms.System.Base.StatusConverter
import com.spit.lms.System.Encryption
import com.spit.lms.System.Event.DialogEvent
import com.spit.lms.System.Response.MessageResponse
import com.spit.lms.System.Response.StockListingResponse
import org.greenrobot.eventbus.EventBus
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class  AdminSettingFragment : BaseFragment() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        view = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.fragment_admin_setting, null)

        view.findViewById<TextView>(R.id.title).text = MainActivity.mContext.getString(R.string.admin_setting_wrap)
        view.findViewById<ImageView>(R.id.back).visibility = View.GONE
        view.findViewById<ImageView>(R.id.menu).visibility = View.INVISIBLE

        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = (R.id.bottom_setting);
        view.findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener(SharedBottomHandler())

        var s = view.findViewById<TextView>(R.id.login_panel).text
        s = s.toString().replace("%s",             SharedPrefsUtils.getStringPreference(
            MainActivity.mContext,
            "USERNAME"))

        view.findViewById<TextView>(R.id.login_panel).text = s

        view.findViewById<LinearLayout>(R.id.change_language).setOnClickListener {
            val builderSingle: AlertDialog.Builder = AlertDialog.Builder(activity)

            val arrayAdapter = ArrayAdapter<String>(
                MainActivity.mContext,
                android.R.layout.select_dialog_item
            )

            arrayAdapter.add(MainActivity.mContext.getString(R.string.tchinese))
            arrayAdapter.add(MainActivity.mContext.getString(R.string.schinese))
            arrayAdapter.add(MainActivity.mContext.getString(R.string.english))


            builderSingle.setNegativeButton(getString(R.string.cancel)) { dialog, which -> dialog.dismiss()}


            builderSingle.setAdapter(arrayAdapter,    DialogInterface.OnClickListener { _, which ->
                when (which) {
                    0 -> {
                        Log.i("pos", "pos 0")
                        val locale = Locale("en-us")
                        Locale.setDefault(locale)
                        val resources: Resources = getResources()
                        val config: Configuration = resources.getConfiguration()
                        config.setLocale(locale)
                        resources.updateConfiguration(config, resources.getDisplayMetrics())

                        SharedPrefsUtils.setStringPreference(MainActivity.mContext, "locale","en-us")

                        (MainActivity.mContext as MainActivity).changeFragment(AdminSettingFragment())
                    }
                    1 ->{
                        Log.i("pos", "pos 2")
                        val locale = Locale("zh")
                        Locale.setDefault(locale)
                        val resources: Resources = getResources()
                        val config: Configuration = resources.getConfiguration()
                        config.setLocale(locale)
                        resources.updateConfiguration(config, resources.getDisplayMetrics())
                        SharedPrefsUtils.setStringPreference(MainActivity.mContext, "locale","zh")

                        (MainActivity.mContext as MainActivity).changeFragment(AdminSettingFragment())
                    }
                    2 ->{
                        Log.i("pos", "pos 2")
                        val locale = Locale("en")
                        Locale.setDefault(locale)
                        val resources: Resources = getResources()
                        val config: Configuration = resources.getConfiguration()
                        config.setLocale(locale)
                        resources.updateConfiguration(config, resources.getDisplayMetrics())

                        SharedPrefsUtils.setStringPreference(MainActivity.mContext, "locale","en")

                        (MainActivity.mContext as MainActivity).changeFragment(AdminSettingFragment())
                    }
                    else -> { // Note the block
                        print("x is neither 1 nor 2")
                    }
                }
            });

            builderSingle.show()
        }
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View? {
        view.findViewById<LinearLayout>(R.id.logout).setOnClickListener {
            (MainActivity.mContext as MainActivity).changeFragment(LoginFragment())
        }

        view.findViewById<LinearLayout>(R.id.api_setting).setOnClickListener {
            (MainActivity.mContext as MainActivity).replaceFragment(SettingFragment())
        }


        view.findViewById<LinearLayout>(R.id.change_password).setOnClickListener {
            view.findViewById<LinearLayout>(R.id.change_password_panel).visibility = View.VISIBLE
            view.findViewById<EditText>(R.id.new_password).setText("")
            view.findViewById<EditText>(R.id.original_password).setText("")
            view.findViewById<EditText>(R.id.confirm_password).setText("")
        }

        view.findViewById<MaterialButton>(R.id.cancel_change_password).setOnClickListener {
            BaseUtils.hideKeyboard(view)
            view.findViewById<LinearLayout>(R.id.change_password_panel).visibility = View.GONE
        }

        view.findViewById<LinearLayout>(R.id.change_password_panel_inside).setOnClickListener {
        }

        view.findViewById<MaterialButton>(R.id.save_change_password).setOnClickListener {
            if(view.findViewById<EditText>(R.id.new_password).text.toString().isEmpty()) {
                EventBus.getDefault().post(DialogEvent(getString(R.string.app_name), getString(R.string.plz_new_password)))
                return@setOnClickListener
            }

            if(view.findViewById<EditText>(R.id.original_password).text.toString().isEmpty()) {
                EventBus.getDefault().post(DialogEvent(getString(R.string.app_name), getString(R.string.plz_original_passowrd)))
                return@setOnClickListener
            }

            if(view.findViewById<EditText>(R.id.confirm_password).text.toString().toString().isEmpty()) {
                EventBus.getDefault().post(DialogEvent(getString(R.string.app_name), getString(R.string.plz_confirm_password)))
                return@setOnClickListener
            }

            if(!view.findViewById<EditText>(R.id.original_password).text.toString().equals(view.findViewById<EditText>(R.id.confirm_password).text.toString()) ) {
                EventBus.getDefault().post(DialogEvent(getString(R.string.app_name), getString(R.string.confirm_password_incorrect)))
                return@setOnClickListener
            }

            if(view.findViewById<EditText>(R.id.new_password).text.toString().equals(view.findViewById<EditText>(R.id.confirm_password).text.toString()) ) {
                EventBus.getDefault().post(DialogEvent(getString(R.string.app_name), getString(R.string.same_password)))
                return@setOnClickListener
            }

            if(view.findViewById<EditText>(R.id.new_password).text.toString().length < 8) {
                EventBus.getDefault().post(DialogEvent(getString(R.string.app_name), getString(R.string.password_at_least_eight)))
                return@setOnClickListener
            }

            BaseUtils.hideKeyboard(view)
            view.findViewById<LinearLayout>(R.id.change_password_panel).visibility = View.GONE

            Log.i("user","user " + SharedPrefsUtils.getStringPreference(
                    MainActivity.mContext,
                    "USERID"
            )  + " " + (view.findViewById<EditText>(R.id.new_password).text.toString()).trim()
            );

            APIUtils.get(
                SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL") + "/changePassword",
                listOf("userid" to SharedPrefsUtils.getStringPreference(
                    MainActivity.mContext,
                    "USERID"
                ), "password" to (view.findViewById<EditText>(R.id.new_password).text.toString()).trim() ),
                MainActivity.mContext, MessageResponse::class.java
            )
        }
        view.findViewById<LinearLayout>(R.id.change_password_panel).setOnClickListener {
            BaseUtils.hideKeyboard(view)
            view.findViewById<LinearLayout>(R.id.change_password_panel).visibility = View.GONE
        }

        return  view
    }
}