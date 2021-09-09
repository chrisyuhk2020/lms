package com.spit.lms.System.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.spit.lms.R;
import com.spit.lms.System.Database.SearchHistory;
import com.spit.lms.System.Event.FilteringCellClickEvent;
import com.spit.lms.System.Event.UpdateSearchEvent;
import com.spit.lms.System.Model.Category;
import com.spit.lms.System.Model.Location;

import org.greenrobot.eventbus.EventBus;

public class FilteringCell extends LinearLayout {
    public String data;
    public boolean selected = false;

    public FilteringCell(Context context, Location data, String type) {
        super(context);
        this.data = data.getLocationName();
        LayoutInflater.from(context).inflate(R.layout.filtering_cell_view, this);
        ((TextView)findViewById(R.id.text)).setText(data.getLocationName());
        ((TextView)findViewById(R.id.text)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected) {
                    ((TextView) findViewById(R.id.text)).setBackground(context.getResources().getDrawable(R.drawable.rounded_filter_selected));
                } else {
                    ((TextView) findViewById(R.id.text)).setBackground(context.getResources().getDrawable(R.drawable.rounded_filter));
                }
                selected = !selected;
                EventBus.getDefault().post(new FilteringCellClickEvent(selected, type, data.getRoNo()));
            }
        });
    }


    public FilteringCell(Context context, Category data, String type) {
        super(context);
        this.data = data.getCategoryName();
        LayoutInflater.from(context).inflate(R.layout.filtering_cell_view, this);
        ((TextView)findViewById(R.id.text)).setText(data.getCategoryName());
        ((TextView)findViewById(R.id.text)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected) {
                    ((TextView) findViewById(R.id.text)).setBackground(context.getResources().getDrawable(R.drawable.rounded_filter_selected));
                } else {
                    ((TextView) findViewById(R.id.text)).setBackground(context.getResources().getDrawable(R.drawable.rounded_filter));
                }
                selected = !selected;
                EventBus.getDefault().post(new FilteringCellClickEvent(selected, type, data.getRoNo()));
            }
        });
    }


    public FilteringCell(Context context, String data, String type) {
        super(context);
        this.data = data;
        LayoutInflater.from(context).inflate(R.layout.filtering_cell_view, this);
        ((TextView)findViewById(R.id.text)).setText(data);
        ((TextView)findViewById(R.id.text)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected) {
                    ((TextView) findViewById(R.id.text)).setBackground(context.getResources().getDrawable(R.drawable.rounded_filter_selected));
                } else {
                    ((TextView) findViewById(R.id.text)).setBackground(context.getResources().getDrawable(R.drawable.rounded_filter));
                }
                selected = !selected;
                EventBus.getDefault().post(new FilteringCellClickEvent(selected, type, data));
            }
        });
    }

    public FilteringCell(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.filtering_cell_view, this);
    }

    public FilteringCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.filtering_cell_view, this);
    }

    public FilteringCell(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
