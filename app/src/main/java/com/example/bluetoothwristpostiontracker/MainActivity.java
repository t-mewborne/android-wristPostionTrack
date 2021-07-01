package com.example.bluetoothwristpostiontracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.bluetoothwristpostiontracker.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    private TextView txtBluetoothInfo;
    private TableLayout tblBluetoothData;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        txtBluetoothInfo = binding.txtBluetoothInfo;
        tblBluetoothData = binding.tblBluetoothData;
    }
}