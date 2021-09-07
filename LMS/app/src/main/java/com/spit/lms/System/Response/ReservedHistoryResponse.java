package com.spit.lms.System.Response;

/*
{"bookNo":"500/0000000001","callNo":" 9578340540","name":"化妝派對-老鼠娶親","author":"王蘭","publishingDate":"2021-04-01T00:00:00.000Z","publishingPlace":"香港","status":"預約中",
"borrowDate":null,"expirationDate":null,"rono":"941C9696A7CC41659D88A978B154C862","AppRoNo":"17353C864888460B8855CD542D485E5E"}

 */
public class ReservedHistoryResponse {
    private String bookNo;
    private String callNo;
    private String name;
    private String author;
    private String publishingDate;
    private String publishingPlace;
    private String status;
    private String borrowDate;
    private String expirationDate;
    private String rono;
    private String AppRoNo;
    private String waitingDate;

    public String getWaitingDate() {
        return waitingDate;
    }

    public void setWaitingDate(String waitingDate) {
        this.waitingDate = waitingDate;
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

    public String getPublishingDate() {
        return publishingDate;
    }

    public void setPublishingDate(String publishingDate) {
        this.publishingDate = publishingDate;
    }

    public String getPublishingPlace() {
        return publishingPlace;
    }

    public void setPublishingPlace(String publishingPlace) {
        this.publishingPlace = publishingPlace;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getRono() {
        return rono;
    }

    public void setRono(String rono) {
        this.rono = rono;
    }

    public String getAppRoNo() {
        return AppRoNo;
    }

    public void setAppRoNo(String appRoNo) {
        AppRoNo = appRoNo;
    }
}
