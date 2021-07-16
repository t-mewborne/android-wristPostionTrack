package com.example.bluetoothwristpostiontracker;

public class DataPointAccelerometer {
    private String time,x,y,z;

    public DataPointAccelerometer(String time, float x, float y, float z) {
        this.time=time;
        this.x=""+x;
        this.y=""+y;
        this.z=""+z;
    }

    public DataPointAccelerometer(String time, String x, String y, String z) {
        this.time=time;
        this.x=""+x;
        this.y=""+y;
        this.z=""+z;
    }

    public String getRow() {
        return time+'\t'+x+'\t'+y+'\t'+z;
    }
}
