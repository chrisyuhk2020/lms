package com.spit.lms.System.Event;

public class FilteringCellClickEvent {
    private boolean selected;
    private String type;
    private String data;

    public FilteringCellClickEvent(boolean selected, String type, String data) {
        this.selected = selected;
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
