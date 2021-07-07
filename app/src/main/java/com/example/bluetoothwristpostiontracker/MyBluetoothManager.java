package com.example.bluetoothwristpostiontracker;


import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;

//Used for devices that are already paired to the device
public class MyBluetoothManager {
    private BluetoothAdapter bluetoothAdapter;
    private Context context;
    private boolean readyToSearch;
    private ArrayList<BluetoothDevice> connectedDevices;
    private ArrayList<BluetoothDevice> pairedDevices;
    private String debugTag = "MyBluetoothManager";
    private MainActivity parent;

    public MyBluetoothManager(Context context,MainActivity parent) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context=context;
        this.parent = parent;
        connectedDevices = new ArrayList<BluetoothDevice>();
        pairedDevices = new ArrayList<BluetoothDevice>();
        Log.d(debugTag,"MyBluetoothManager -- Object Created");

        readyToSearch = bluetoothTestAndEnable();
        if (!readyToSearch) {
            Log.e(debugTag,"getPairedDevices -- Failed to enable bluetooth");
        } else {
            pairedDevices.addAll(bluetoothAdapter.getBondedDevices());//Returns PAIRED devices
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            context.registerReceiver(mReceiver, filter);
            Log.d(debugTag,"constructor -- (" + pairedDevices.size() + ") paired device(s) were located");
            for(BluetoothDevice device : pairedDevices) {
                //device.connectGatt(context, true, new BluetoothGattCallback() {});
                boolean bonded = device.createBond();
                Log.d(debugTag,"constructor -- attempting to bond to \"" + device.getName() + "\"..." + (bonded ? "success" : "failed"));
            }
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Device found
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Device has connected
                connectedDevices.add(device);
                Log.d(debugTag,"onReceive -- a device was located");
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Done searching
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Device is about to disconnect
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Device has disconnected
                connectedDevices.remove(device);
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
        return bluetoothAdapter.isEnabled();
    }

    public ArrayList<BluetoothDevice> getPairedDevices() {
        return pairedDevices;
    }

    public ArrayList<BluetoothDevice> getConnectedDevices() {
        return connectedDevices;
    }
}
