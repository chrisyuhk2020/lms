package com.spit.lms.System.Fragment

import android.util.Log
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.spit.lms.MainActivity
import com.spit.lms.R


class SharedBottomHandler : BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.bottom_stock_take -> {
                Log.i("bottom_stock_take", "bottom_stock_take")
                (MainActivity.mContext as MainActivity).changeFragment(StockTakeListingFragment())
            }

            R.id.bottom_search -> {
                Log.i("bottom_search", "bottom_search")
                BookListingFragment.query = ""
                (MainActivity.mContext as MainActivity).changeFragment(BookListingFragment())
            }

            R.id.bottom_setting -> {
                Log.i("bottom_setting", "bottom_setting")
                (MainActivity.mContext as MainActivity).changeFragment(AdminSettingFragment())
            }

        }
        return true

    }

}