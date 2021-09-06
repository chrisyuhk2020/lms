package com.spit.lms.System.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import com.spit.lms.MainActivity
import com.spit.lms.R
import com.spit.lms.System.Base.BaseFragment

class HistoryFragment : BaseFragment() {
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        view = LayoutInflater.from(MainActivity.mContext).inflate(R.layout.layout_stocktakelisting, null)
    }

}