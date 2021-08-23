package com.spit.lms.System.Model;

public class StockTakeListItem {
    private String stockTakeNo;
    private int progress;
    private int total;
    private String name;
    private String startDate;
    private String endDate;
    private String createdDate;

    public String getStockTakeNo() {
        return stockTakeNo;
    }

    public void setStockTakeNo(String stockTakeNo) {
        this.stockTakeNo = stockTakeNo;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
