package com.spit.lms.System.Model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Category extends RealmObject {
    @PrimaryKey
    private String RoNo;
    private String FatherRoNo;
    private String CategoryName;

    public String getRoNo() {
        return RoNo;
    }

    public void setRoNo(String roNo) {
        RoNo = roNo;
    }

    public String getFatherRoNo() {
        return FatherRoNo;
    }

    public void setFatherRoNo(String fatherRoNo) {
        FatherRoNo = fatherRoNo;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }
}
