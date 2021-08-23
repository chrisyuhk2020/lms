package com.spit.lms.System.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Location extends RealmObject {
    @PrimaryKey
    private String RoNo;
    private String FatherRoNo;
    private String LocationName;

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

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

}
