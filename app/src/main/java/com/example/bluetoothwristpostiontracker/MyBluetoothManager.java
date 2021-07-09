package com.example.bluetoothwristpostiontracker;


import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;

//Used for devices that are already paired to the device
public class MyBluetoothManager extends MainActivity {
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private boolean readyToSearch;
    private boolean discoveryMode;
    private boolean discoveryUnavailable;
    private ArrayList<BluetoothDevice> connectedDevices;
    private ArrayList<BluetoothDevice> pairedDevices;
    private String debugTag = "MyBluetoothManager";
    private MainActivity parent;
    private int discoveryModeCount; //The number of times the devices has been concurrently in discovery mode

    public MyBluetoothManager(Context context,MainActivity parent) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context=context;
        this.parent = parent;
        connectedDevices = new ArrayList<BluetoothDevice>();
        pairedDevices = new ArrayList<BluetoothDevice>();
        Log.d(debugTag,"MyBluetoothManager -- Object Created");

        discoveryMode=false;
        discoveryUnavailable=false;

        discoveryModeCount=0;

        readyToSearch = bluetoothTestAndEnable();
        if (!readyToSearch) {
            Log.e(debugTag,"getPairedDevices -- Failed to enable bluetooth");
        } else {
            IntentFilter filter = new IntentFilter();
            //filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            context.registerReceiver(mReceiver, filter);
            pairedDevices.addAll(bluetoothAdapter.getBondedDevices());
            Log.d(debugTag,"constructor -- (" + pairedDevices.size() + ") paired device(s) were located");
            //discoveryMode = bluetoothAdapter.startDiscovery();
            beginSearch();
            //Log.d(debugTag,(discoveryMode ? "successfully enabled" : "failed to enable") + " discovery mode");

            /*
            for(BluetoothDevice device : pairedDevices) {
                //device.connectGatt(context, true, new BluetoothGattCallback() {});
                //boolean bonded = device.createBond();
                //Log.d(debugTag,"constructor -- attempting to bond to \"" + device.getName() + "\"..." + (bonded ? "success" : "failed"));
                /*
                Log.d(debugTag,"constructor -- connecting gatt");
                device.connectGatt(context, true, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        Log.d(debugTag, "constructor -- Connection changed-"+gatt+"-"+status+"-"+newState);
                    }
                    @Override
                    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                        if(status == BluetoothGatt.GATT_SUCCESS)
                            Log.d(debugTag, String.format("BluetoothGat ReadRssi[%d]", rssi));
                    }* /
                });
            }*/
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            boolean deviceFound = device!=null;
            int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

            if(deviceFound) Log.d(debugTag,"onReceive -- message received from device \""+device.getName()+"\" on iteration " + discoveryModeCount+"..."); // with rssi " + rssi);
            else Log.d(debugTag,"onReceive -- no devices identified on iteration " +discoveryModeCount);

            switch (action) {
                case BluetoothDevice.ACTION_FOUND:
                    //Device found
                    Log.d(debugTag,"onReceive -- device found: \""+device.getName()+"\"\tRSSI: "+rssi);
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
                    connectedDevices.remove(device);
                    break;
                default:
                    Log.w(debugTag,"onReceive -- unhandled action: \"" + action + "\"");
                    break;
            }
            parent.updateTable();
        }
    };

    private boolean bluetoothTestAndEnable() {
        if (bluetoothAdapter == null) {
            Log.e(debugTag,"bluetoothTestAndEnable -- This device does not have a bluetooth adapter. Cannot continue.");
            return false;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Log.d(debugTag,"bluetoothTestAndEnable -- Attempting to enable bluetooth");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBtIntent);
        }
        Log.d(debugTag,"bluetoothTestAndEnable -- Bluetooth is enabled");
        return bluetoothAdapter.isEnabled();
    }

    private void beginSearch() {
        discoveryModeCount++;
        discoveryMode = bluetoothAdapter.startDiscovery();
        Log.d(debugTag,"begin search -- "+(discoveryMode ? "successfully enabled" : "failed to enable") + " discovery mode. Iteration " + discoveryModeCount);
        discoveryUnavailable=!discoveryMode;
    }

    private void onSuspendSearch() {
        Log.d(debugTag,"onSuspendSearch -- iteration " + discoveryModeCount + " of discovery stopped. Attempting to restart...");
        discoveryMode=false;
        if(!discoveryUnavailable && readyToSearch) beginSearch(); //Restart discovery mode
        else Log.e(debugTag,"Unable to restart search because: " +
                (discoveryUnavailable ? "\ndiscovery is unavailable (previously failed to enable)" : "") +
                (!readyToSearch ? "\ndevice not ready to search, " : ""));
    }

    public ArrayList<BluetoothDevice> getPairedDevices() {
        return pairedDevices;
    }

    //TODO not finished, does not return correct value
    public ArrayList<BluetoothDevice> getConnectedDevices() {
        return connectedDevices;
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
