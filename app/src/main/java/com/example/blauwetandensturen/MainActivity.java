package com.example.blauwetandensturen;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button btn, btnb;
    private ListView list;
    private ArrayAdapter<String> pairedDevicesArrayAdapter;
    private BluetoothSend bluetoothSend = new BluetoothSend();
    private RadioButton on, off;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        btnb = findViewById(R.id.button2);
        list = findViewById(R.id.listView);
        textView = findViewById(R.id.textView);

        on = findViewById(R.id.on);
        off = findViewById(R.id.of);
        pairedDevicesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);


        btn.setOnClickListener(view -> {
            bluetoothSend.showPairedDevices(list, pairedDevicesArrayAdapter);
            Log.i("jejrkl", "jhejfhkas");

        });

        btnb.setOnClickListener(view -> {
            if (on.isChecked()) {
                try {
                    bluetoothSend.send("21");
                    textView.setText("");
                    if (bluetoothSend.getBluetooth().contains("2")){
                    textView.setText("Led is aan");
                    }
                } catch (IOException e) {
                    Log.e("IOExeptoin", e.getMessage());
                }
            }
            if (off.isChecked()) {
                try {
                    bluetoothSend.send("22");
                    if (bluetoothSend.getBluetooth().contains("2")){
                        textView.setText("Led is uit");
                    }
                } catch (IOException e) {
                    Log.e("IOExeptoin", e.getMessage());
                }
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String send;
                String device = list.getAdapter().getItem(i).toString();
                Log.i("diveice name", device);

                try {
                    bluetoothSend.createConnection(device);
                } catch (IOException e) {
                    Log.e("IOExeptoin", e.getMessage());
                }
            }
        });
    }
}