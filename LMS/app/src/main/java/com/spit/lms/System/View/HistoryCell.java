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
import com.spit.lms.System.Event.UpdateSearchEvent;

import org.greenrobot.eventbus.EventBus;

import io.realm.Realm;
import io.realm.RealmResults;

public class HistoryCell extends LinearLayout {
    public SearchHistory data;

    public HistoryCell(Context context, SearchHistory data) {
        super(context);
        this.data = data;
        LayoutInflater.from(context).inflate(R.layout.history_cell_view, this);
        ((TextView)findViewById(R.id.text)).setText(data.getValue());

        findViewById(R.id.clear_history).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Realm.getDefaultInstance().beginTransaction();
                //RealmResults<SearchHistory> source = Realm.getDefaultInstance().where(SearchHistory.class).equalTo("value", data.getValue()).findAll();
                //source.deleteAllFromRealm();
                //Realm.getDefaultInstance().commitTransaction();

                EventBus.getDefault().post(new UpdateSearchEvent(data));
            }
        });
    }

    public HistoryCell(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.history_cell_view, this);
    }

    public HistoryCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.history_cell_view, this);
    }

    public HistoryCell(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
