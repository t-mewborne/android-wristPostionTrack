package com.example.bluetoothwristpostiontracker;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.Date;
import java.util.Objects;


//This class represents one single device. Each time the RSSI is updated, the file should be updated as well
public class BTDeviceData {

    //TODO signal data about the device

    //TODO min and max recorded strength. The bigger the difference, the less trustworthy
    private float minimumRecordedSignalStrength;
    private float maximumRecordedSignalStrength;

    /*TODO  a point system value that indicates how much this signal value would be trusted (based on significant fluctuations, etc).
     * Used to weight device location towards other specific devices
     * Factors: Time between updates (more is bad), large fluctuations, big difference between min and max*/
    private int signalTrustworthiness;

    //TODO change from float, indicates the last time the device was updated
    private float recentTimeStamp;

    private String debugTag = "BTDeviceData";

    private String macAddress, name;
    private int rssi, updateCount;
    private Date lastUpdateTime;


    public BTDeviceData(BluetoothDevice device, int rssi, Date currentTime) {
        setName(device.getName());
        this.macAddress = device.getAddress();
        this.rssi = rssi;
        updateCount = 1;
        lastUpdateTime = currentTime;
        minimumRecordedSignalStrength = maximumRecordedSignalStrength = rssi;
        signalTrustworthiness = 50;
    }

    public void updateDevice(BluetoothDevice device,int rssi,Date currentTime){
        lastUpdateTime=currentTime;
        this.rssi=rssi;
        updateCount++;
        minimumRecordedSignalStrength = rssi < minimumRecordedSignalStrength ? rssi : minimumRecordedSignalStrength;
        maximumRecordedSignalStrength = rssi > maximumRecordedSignalStrength ? rssi : maximumRecordedSignalStrength;
        //TODO update trustworthiness
    }

    public String getName() {return name;}
    public int getRSSI() {return rssi;}

    private void setName(String name) {
        int desiredLength = 20;
        if (name.length() == desiredLength) this.name = name;
        else if (name.length() > desiredLength) this.name = name.substring(0,desiredLength-3) + "...";
        else {
            String newName=name;
            while (newName.length() < desiredLength) newName = newName + " ";
            this.name = newName;
        }
        this.name+="\t";
    }

    public boolean equals(String macAddress) {
        return this.macAddress.equals(macAddress);
    }

    public boolean equals(BluetoothDevice other) {
        return other.getAddress().equals(macAddress);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BTDeviceData that = (BTDeviceData) o;
        return macAddress.equals(that.macAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macAddress);
    }
}
