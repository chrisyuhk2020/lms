package com.spit.lms.System.Response;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class StockListingResponse extends RealmObject {
    private String stocktakeno;
    private int progress;
    private int total;
    private String startDate;
    private String endDate;
    private String createDate;
    private String name;
    private int status;

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    @PrimaryKey
    private String pk;


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    private String userid;


    public int getStatus() {
        //if(foundStatus > 0) {
        //    return 2;
        //}
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStocktakeno() {
        return stocktakeno;
    }

    public void setStocktakeno(String stocktakeno) {
        this.stocktakeno = stocktakeno;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStartdate() {
        return startDate;
    }

    public void setStartdate(String startdate) {
        this.startDate = startdate;
    }

    public String getExpirydate() {
        return endDate;
    }

    public void setExpirydate(String expirydate) {
        this.endDate = expirydate;
    }

    public String getCreateddate() {
        return createDate;
    }

    public void setCreateddate(String createddate) {
        this.createDate = createddate;
    }
}
