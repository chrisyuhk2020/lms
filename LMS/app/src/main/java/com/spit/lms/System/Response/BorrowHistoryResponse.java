package com.spit.lms.System.Response;

public class BorrowHistoryResponse {
    private String bookNo;
    private String callNo;
    private String name;
    private String borrowDate;
    private String returnDate;
    private String rono;

    private boolean renew;
    private boolean waitingID;
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

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.returnDate = returnDate;
    }

    public String getRono() {
        return rono;
    }

    public void setRono(String rono) {
        this.rono = rono;
    }

    public boolean getRenew() {
        return renew;
    }

    public void setRenew(boolean renew) {
        this.renew = renew;
    }

    public boolean getWaitingID() {
        return waitingID;
    }

    public void setWaitingID(boolean waitingID) {
        this.waitingID = waitingID;
    }


}
