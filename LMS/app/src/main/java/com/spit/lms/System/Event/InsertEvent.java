package com.spit.lms.System.Event;

public class InsertEvent {
    public int count = 0;

    public InsertEvent(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
