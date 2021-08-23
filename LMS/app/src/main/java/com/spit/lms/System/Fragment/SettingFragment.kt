package com.spit.lms.System.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.spit.lms.MainActivity
import com.spit.lms.R
import com.spit.lms.System.Base.BaseFragment
import com.spit.lms.System.Base.SharedPrefsUtils
import com.spit.lms.System.Event.DialogEvent
import org.greenrobot.eventbus.EventBus

class SettingFragment : BaseFragment() {
    lateinit var apiAddress : EditText
    lateinit var currentApi : TextView
    lateinit var title : TextView
    lateinit var back : ImageView

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        view = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.fragment_setting, null)
        apiAddress = view.findViewById(R.id.input_api_address)
        currentApi = view.findViewById(R.id.current_api_address)
        title = view.findViewById(R.id.title)
        back = view.findViewById(R.id.back)

        currentApi.text = (SharedPrefsUtils.getStringPreference(MainActivity.mContext, "BASE_URL"));
        title.text = MainActivity.mContext.getText(R.string.setting)
        back.setOnClickListener {
            (MainActivity.mContext as MainActivity).onBackPressed()
        }
    }

    override fun onCreateView(layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?): View? {
        view.findViewById<LinearLayout>(R.id.save).setOnClickListener{

            if (apiAddress.text.isEmpty()) {
                EventBus.getDefault().post(DialogEvent(MainActivity.mContext.getString(R.string.app_name), MainActivity.mContext.getString(R.string.input_api_address)))
                return@setOnClickListener
            }

            SharedPrefsUtils.setStringPreference(MainActivity.mContext, "BASE_URL", apiAddress.text.toString())

            (MainActivity.mContext as MainActivity).onBackPressed()
        }
        return  view
    }
}