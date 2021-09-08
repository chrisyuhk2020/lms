package com.spit.lms.System.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class StockTakeListBook extends RealmObject {
    private String bookNo;
    private String callNo;
    private String name;
    private String author;
    private String isbn;
    private String publisher;
    private String category;
    private String location;
    private String image;
    private String description;
    private Boolean stocktake;
    private String stocktakeno;
    private String epc;
    private int foundStatus;
    private int status;
    private String publishingDate;
    @PrimaryKey
    private String pk;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    private String userid;


    private String tempScanDate = "";

    public String getPublishingDate() {
        return publishingDate;
    }

    public void setPublishingDate(String publishingDate) {
        this.publishingDate = publishingDate;
    }

    public void setFoundStatus(int foundStatus){
        this.foundStatus = foundStatus;
    }

    public int getFoundStatus() {
        return foundStatus;
    }

    public String getTempType() {
        return tempType;
    }

    public void setTempType(String tempType) {
        this.tempType = tempType;
    }

    private String tempType = "";

    public String getTempScanDate() {
        return tempScanDate;
    }

    public void setTempScanDate(String tempScanDate) {
        this.tempScanDate = tempScanDate;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getCallNo() {
        return callNo;
    }

    public void setCallNo(String callNo) {
        this.callNo = callNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStocktake() {
        return stocktake;
    }

    public void setStocktake(Boolean stocktake) {
        this.stocktake = stocktake;
    }

    public String getStocktakeno() {
        return stocktakeno;
    }

    public void setStocktakeno(String stocktakeno) {
        this.stocktakeno = stocktakeno;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }


}
