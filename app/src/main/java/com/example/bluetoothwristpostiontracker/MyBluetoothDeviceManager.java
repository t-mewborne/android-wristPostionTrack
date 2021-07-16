package com.example.bluetoothwristpostiontracker;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

//This class is in charge of updating the file and managing the list of bluetooth devices
public class MyBluetoothDeviceManager {
    private ArrayList<MyBluetoothDevice> deviceList;
    //ArrayList<DataRowBluetooth> table; //TODO change to queue<DataRow>
    private MyQueue<DataPointBluetooth> bluetoothData;
    private String debugTag = "MyBluetoothDeviceManager";
    private MainActivity main;
    private Context context;
    private Date creationTime;
    private long creationTimeMillis;
    private String filename;
    private boolean writingFile;
    private File file;
    private int totalDataPoints;

    public MyBluetoothDeviceManager(MainActivity main, Context context) {
        this.main = main;
        this.context=context;

        deviceList = new ArrayList<MyBluetoothDevice>();
        bluetoothData = new MyQueue<DataPointBluetooth>();

        creationTime = main.getCreationTime();
        creationTimeMillis=main.getCreationTimeMillis();

        File path = context.getFilesDir();
        filename = main.getDateForFilename() + "_bluetooth.csv";
        file = new File(path,filename);
        Log.d(debugTag,"MyBluetoothDeviceManager -- new file will be named \"" + filename+"\"");

        writingFile = false;

        //table = new ArrayList<DataPointBluetooth>();
        //table.add(new DataPointBluetooth("time(ms)","device_name","signal_strength"));
        bluetoothData.enqueue(new DataPointBluetooth("time(ms)","device_name","signal_strength"));
    }

    public void addOrUpdate(BluetoothDevice device, int rssi) {
        MyBluetoothDevice data = find(device);
        String deviceName = device.getName();
        if (deviceName==null) return; //no real device was passed
        if (data==null && deviceName != null){ //If data is null, device does not exist yet
            Log.d(debugTag,"addIfDoesNotExist -- new device found: \"" + deviceName + "\"\tAddress ["+device.getAddress()+"]  RSSI: " + rssi);
            data=add(device, rssi);
            updateData(data);
            totalDataPoints++;
        } else if (data!=null) { //Device already exists, update it
            data.updateDevice(device, rssi,main.millisecondsSinceStart());
            updateData(data);
            totalDataPoints++;
        }
        main.updateTable(); //Refresh the main table
        if (!writingFile && bluetoothData.getSize()>=100) {
            Log.d(debugTag,"Exceeded data limit, transferring data to file.");
            updateFile();
        }
    }



    public MyBluetoothDevice add(BluetoothDevice device, int rssi) {
        MyBluetoothDevice newDevice = new MyBluetoothDevice(device, rssi, main.millisecondsSinceStart());
        deviceList.add(newDevice);
        return newDevice;
    }

    public MyBluetoothDevice find(BluetoothDevice device) {
        if (device==null) return null;
        String macAddress = device.getAddress();
        for (MyBluetoothDevice data:deviceList) {
            if (data.equals(macAddress)) return data;
        }
        return null;
    }

    public void updateData(MyBluetoothDevice device) {
        //table.add(new DataPointBluetooth(device.getLastUpdateTime(),device.getName(),device.getRSSI()));
        bluetoothData.enqueue(new DataPointBluetooth(device.getLastUpdateTime(),device.getName(),device.getRSSI()));
        //Log.d(debugTag,"added new data. size: " + table.size());
    }

    /*To access the created file:
     * Android Studio > View > Tool Windows > Device File Explorer
     * Files are saved on device at: data/data/com.example.bluetoothwristpositiontracker/files
     * Right click on the app folder and click "synchronize" to update
     * Right click and select "save as" to download to computer
     * Saved on mac in documents/androidStudio/deviceExplorer/<device>/data/data/<app>/files
     */
    public void updateFile() {
        if (writingFile || bluetoothData.isEmpty()){
            Log.i(debugTag, "updateFile -- cannot write file for the following reasons: " +
                    (writingFile ? "\nalready writing to a file" : "") +
                    (bluetoothData.isEmpty() ? "\nthere is no data available to write to the file." : ""));
            return;
        }

        Log.d(debugTag,"updateFile -- attempting to write data to file \""+filename+"\"...");
        writingFile=true;
        //TODO things that get added to the table while the file is being written are lost
        try {
            int dataCount = bluetoothData.getSize();
            FileWriter writer = new FileWriter(file,true);
            while(!bluetoothData.isEmpty()) {
                writer.append(bluetoothData.dequeue().getRow()+'\n');
            }
            writer.close();
            Log.d(debugTag,"updateFile -- file updated with " + dataCount + " data point(s).");
        } catch (IOException e) {
            Log.e(debugTag,"updateFile -- An IO exception occurred:\n"+e);
        } catch (Exception e) {
            Log.e(debugTag,"updateFile -- An exception occurred:\n"+e);
        } finally {
            writingFile=false;
        }
    }

    public int getTotalDataPoints() {
        return totalDataPoints;
    }

    public ArrayList<MyBluetoothDevice> getDeviceList() {
        return deviceList;
    }
}
