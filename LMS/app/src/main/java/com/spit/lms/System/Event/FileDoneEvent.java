package com.spit.lms.System.Event;

public class FileDoneEvent {
    public FileDoneEvent(String stocktakeno) {
        this.setStocktakeno(stocktakeno);
    }

    public String getStocktakeno() {
        return stocktakeno;
    }

    public void setStocktakeno(String stocktakeno) {
        this.stocktakeno = stocktakeno;
    }

    private String stocktakeno;
}
