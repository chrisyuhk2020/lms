package com.spit.lms.System.Database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SearchHistory extends RealmObject {
    @PrimaryKey
    private int hashCode;
    private String value;
    private String userid;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
