package com.spit.lms.System.Response;

import com.spit.lms.System.Model.Book;
import com.squareup.moshi.JsonClass;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SearchQueryResponse {
    private Boolean isLast;
    private List<Book> data = new ArrayList<>();

    public Boolean isLast() {
        return isLast;
    }

    public void setLast(Boolean last) {
        isLast = last;
    }

    public List<Book> getData() {
        return data;
    }

    public void setData(ArrayList<Book> data) {
        this.data = data;
    }
}
