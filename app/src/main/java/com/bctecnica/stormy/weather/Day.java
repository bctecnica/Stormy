package com.bctecnica.stormy.weather;

import java.io.Serializable;
import java.text.SimpleDateFormat;

// Used to help build an array of data for each hour
public class Day implements Serializable {
    private String icon;
    private long time;

    public Day() {
    }

    // Returns formatter dt (day, date, month)
    public String getTime() {
        java.util.Date unix = new java.util.Date(time*1000);

        return (new SimpleDateFormat("EEEE \nd, MMM").format(unix));
    }

    public void setTime(long time) {
        this.time = time;
    }

    // Returns the chance of lightning depending on the json data
    public String getIcon() {
        String chance;

        if(icon == "11d") {
            chance = "High";
        }
        else {
            chance = "Low";
        }

        return chance;
    }

    public void setIcon(String icon) { this.icon = icon; }
}
