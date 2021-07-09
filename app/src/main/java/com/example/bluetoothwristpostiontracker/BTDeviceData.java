package com.example.bluetoothwristpostiontracker;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

import java.util.Objects;

public class BTDeviceData {

    //TODO signal data about the device
    private float minimumRecordedSignalStrength;
    private float maximumRecordedSignalStrength;

    private float signalTrustworthiness;
    /*TODO  a value that indicates how much this signal value would be trusted (based on significant fluctuations, etc).
        Used to weight device location towards other specific devices
     */


    private String macAddress;
    private String name;
    private int state;
    private int RSSI;
    private ParcelUuid[] UUIDS;
    private boolean signalDetected;



    public BTDeviceData(BluetoothDevice device) {
        this.name = device.getName();
        this.macAddress = device.getAddress();
        this.state = device.getBondState();
        this.UUIDS = device.getUuids();
        signalDetected=false;


        //int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
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
