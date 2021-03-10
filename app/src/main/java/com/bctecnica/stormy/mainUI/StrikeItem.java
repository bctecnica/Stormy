package com.bctecnica.stormy.mainUI;

public class StrikeItem {
    private String time;
    private String distance;
    public StrikeItem(String time, String distance) {
        this.time = time;
        this.distance = distance;
    }

    public String getTime() {
        return time;
    }
    public String getDistance() {
        return distance;
    }
}