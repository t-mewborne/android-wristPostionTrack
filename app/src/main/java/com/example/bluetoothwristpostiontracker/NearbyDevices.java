package com.example.bluetoothwristpostiontracker;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

//TODO a prioritized linked list of nearby bluetooth devices
//TODO priority is based on signal strength and trustworthiness
public class NearbyDevices {
    ArrayList<BTDeviceData> data;

    public NearbyDevices() {
        data = new ArrayList<BTDeviceData>();
    }

    public void addDevice(BluetoothDevice device) {

    }
}
