package com.example.bluetoothwristpostiontracker;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;

//This class manages the bluetooth adapter
public class MyBluetoothManager extends MainActivity {
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private boolean userReady, readyToSearch, discoveryMode, discoveryUnavailable, permissionGranted;
    private String debugTag = "MyBluetoothManager";
    private MainActivity main;
    private MyBluetoothDeviceManager devices;
    private int discoveryModeCount; //Number of times discovery mode has been started
    //private int permissionRequestConstant=43;

    public MyBluetoothManager(Context context, MainActivity main, int permissionRequestConstant) {

        //Define and assign
        this.context=context;
        this.main = main;
        //this.permissionRequestConstant=permissionRequestConstant;
        devices = new MyBluetoothDeviceManager(main,context);
        discoveryMode=discoveryUnavailable=permissionGranted=userReady=false;
        discoveryModeCount=0;

        //Request Permissions
        main.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH},permissionRequestConstant);
        Log.d(debugTag, "MyBluetoothManager -- Awaiting permission grant...");

        //CALL "permissionsReady" from parent class TO CONTINUE
    }




    //User control
    public void userStartedSearch() {
        //Begin first search iteration, if available
        userReady = true;
        if (!isReady()) {
            Log.e(debugTag,"permissionsReady -- Failed to enable bluetooth. Cannot begin discovery\n" + booleanVals());
        } else {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            context.registerReceiver(mReceiver, filter);
            beginSearch();
        }
    }

    public void userStoppedSearch() {
        userReady=false;
        bluetoothAdapter.cancelDiscovery();
        main.updateTable();
    }




/*
    //Bluetooth
    //Call when permission result has been returned to main class
    @Deprecated //Call bluetooth test and enable instead
    public void permissionsReady(boolean permissionResult) {
        //permissionGranted=permissionResult;
        //readyToSearch = bluetoothTestAndEnable();
    }*/

    //Call
    public boolean bluetoothTestAndEnable(boolean permissionResult) {
        permissionGranted=permissionResult;
        if(!permissionGranted) {
            Log.e(debugTag,"bluetoothTestAndEnable -- permission NOT granted to enable bluetooth");
            readyToSearch = false;
            return false;
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(debugTag,"bluetoothTestAndEnable -- This device does not have a bluetooth adapter. Cannot continue.");
            readyToSearch = false;
            return false;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Log.d(debugTag,"bluetoothTestAndEnable -- Attempting to enable bluetooth");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBtIntent);
        }
        readyToSearch = bluetoothAdapter.isEnabled();;
        Log.d(debugTag,"bluetoothTestAndEnable -- " + (readyToSearch ?
                "Bluetooth is enabled. Waiting for user to begin search." :
                "Failed to enable bluetooth."));
        return readyToSearch;
    }

    private void beginSearch() {
        discoveryModeCount++;
        discoveryMode = bluetoothAdapter.startDiscovery();
        main.updateTable();
        Log.d(debugTag,"Iteration [" + discoveryModeCount +"] begin search -- "+(discoveryMode ? "successfully enabled" : "failed to enable") + " discovery mode");
        discoveryUnavailable=!discoveryMode;
    }

    //TODO remove devices that have not been nearby for more than 2 minutes
    private void onSuspendSearch() {
        Log.d(debugTag,"Iteration [" + discoveryModeCount + "] onSuspendSearch -- Discovery stopped. " + devices.getTotalDataPoints() + " data points located thus far. Attempting to restart search...");
        discoveryMode=false;
        if(!discoveryUnavailable && readyToSearch && userReady) beginSearch(); //Restart discovery mode
        else {
            Log.e(debugTag,"onSuspendSearch -- Unable to restart search because of the following: " +
                    (discoveryUnavailable ? "\ndiscovery is unavailable (previously failed to enable)" : "") +
                    (!readyToSearch ? "\ndevice not ready to search" : "") +
                    (!userReady ? "\nuser cancelled restart":""));
            main.searchStopped();
            devices.updateFile();
            forgetDevices();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            boolean deviceFound = device != null && device.getName()!=null;
            int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

            //if(deviceFound) Log.d(debugTag,"onReceive -- message received from device \""+device.getName()+"\" on iteration " + discoveryModeCount+"..."); // with rssi " + rssi);
            //else Log.d(debugTag,"onReceive -- no devices identified on iteration " +discoveryModeCount);

            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    //Device found
                    if (deviceFound) {
                        devices.addOrUpdate(device,rssi);
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    //Device has connected
                    //connectedDevices.add(device);
                    Log.d(debugTag,"onReceive -- device connected: \""+device.getName()+"\"\tRSSI: "+rssi);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    //Done searching
                    onSuspendSearch();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:
                    //Device is about to disconnect
                    Log.d(debugTag,"onReceive -- message received: disconnect requested from \""+device.getName()+"\"");
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    //Device has disconnected
                    Log.d(debugTag,"onReceive -- message received: device +\""+device.getName()+"\" disconnected");
                    //connectedDevices.remove(device);
                    break;
                default:
                    Log.w(debugTag,"onReceive -- unhandled action: \"" + action + "\"");
                    break;
            }
           // main.updateTable();
        }
    };

    public ArrayList<MyBluetoothDevice> getNearbyDevices() {
        return devices.getDeviceList();
    }

    private String booleanVals() {
        return "discoveryMode........."+discoveryMode+
                "\ndiscoveryUnavailable.."+discoveryUnavailable+
                "\npermissionGranted....."+permissionGranted+
                "\nreadyToSearch........."+readyToSearch;

    }

    public boolean isSearching() {
        return readyToSearch && !discoveryUnavailable && permissionGranted;
    }

    private void forgetDevices() {
        devices.forgetAll();
    }

    public boolean isReady() {
        return readyToSearch && permissionGranted;
    }

    protected void onDestroy(){
        super.onDestroy();
        if (discoveryMode) {
            Log.d(debugTag,"DiscoveryMode Cancelled");
            bluetoothAdapter.cancelDiscovery();
            discoveryMode=false;
        }
    }




}
