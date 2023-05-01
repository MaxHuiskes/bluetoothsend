package com.example.blauwetandensturen;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.security.cert.CertPathBuilder;
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
            sendNotification("yes sir", "does ringtone work");

        });

        btnb.setOnClickListener(view -> {
            if (on.isChecked()) {
                try {
                    bluetoothSend.send("21");
                    textView.setText("");
                    if (bluetoothSend.getBluetooth().contains("2")){
                        sendNotification("Ja, led is aan","Is led aan?");
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
                        sendNotification("Nee, led is uit","Is led aan?");
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
                } finally {
                    pairedDevicesArrayAdapter.clear();
                }
            }
        });
    }

    public void sendNotification (String message, String title ){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "some_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}