package com.example.blauwetandensturen;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private final BluetoothSend bluetoothSend = new BluetoothSend();
    private Button btn, btnb, btnc;
    private ListView list;
    private ArrayAdapter<String> pairedDevicesArrayAdapter;
    private RadioButton on, off, flip;
    private TextView textView, tvStatus;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        btnb = findViewById(R.id.button2);
        btnc = findViewById(R.id.button3);
        list = findViewById(R.id.listView);
        textView = findViewById(R.id.textView);
        tvStatus = findViewById(R.id.textView2);

        hourlyTask();
        monthlyTask();

        on = findViewById(R.id.on);
        off = findViewById(R.id.of);
        flip = findViewById(R.id.flip);
        pairedDevicesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        btn.setOnClickListener(view -> {
            bluetoothSend.showPairedDevices(list, pairedDevicesArrayAdapter);
            Log.i("jejrkl", "jhejfhkas");
            sendNotification("yes sir", "does ringtone work");

        });

        btnc.setOnClickListener(view -> {
            try {
                if (bluetoothSend.isConnected()) {
                    bluetoothSend.closeConnection();
                    if (!bluetoothSend.isConnected()) {
                        sendNotification("No connection closed", "Connection open?");
                        tvStatus.setText("Geen idee of led je aan is.\nGeen verbinding.");
                        textView.setText("???");
                    }
                }
            } catch (IOException e) {
                Log.e("IOException e", e.getMessage());
            } catch (NullPointerException e) {
                Log.e("NullPointerException e", e.getMessage());
            }

        });


        Thread getData = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (bluetoothSend.isConnected()) {
                        String recieve = bluetoothSend.getBluetoothw();
                        Toast.makeText(MainActivity.this, recieve, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Log.e("IOException", e.getMessage());
                } catch (NullPointerException e){
                    Log.e("NullPointerException", e.getMessage());
                }
            }
        });
        getData.start();

        btnb.setOnClickListener(view -> {
            if (on.isChecked()) {
                try {
                    bluetoothSend.send("21");
                    textView.setText("");
                    if (bluetoothSend.getBluetooth().contains("2")) {
                        sendNotification("Ja, led is aan", "Is led aan?");
                        textView.setText("Led is aan");
                    }
                } catch (IOException e) {
                    Log.e("IOExeptoin", e.getMessage());
                }
            }
            if (off.isChecked()) {
                try {
                    bluetoothSend.send("22");
                    if (bluetoothSend.getBluetooth().contains("2")) {
                        sendNotification("Nee, led is uit", "Is led aan?");
                        textView.setText("Led is uit");
                    }
                } catch (IOException e) {
                    Log.e("IOExeptoin", e.getMessage());
                }
            }
            if (flip.isChecked()) {
                try {
                    bluetoothSend.send("23");
                    if (bluetoothSend.getBluetooth().contains("2")) {
                        sendNotification("Ja, led is aan het flippen", "Is led aan?");
                        textView.setText("Ja, led is aan het flippen");
                    }
                } catch (IOException e) {
                    Log.e("IOExeptoin", e.getMessage());
                }
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
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
                    try {
                        if (bluetoothSend.isConnected()) {
                            tvStatus.setText("Er is verbinding.");
                            pairedDevicesArrayAdapter.clear();
                        }
                    } catch (IOException e) {
                        Log.e("IOException e", e.getMessage());
                    }
                }
            }
        });
    }

    @SuppressLint("ObsoleteSdkInt")
    public void sendNotification(String message, String title) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "some_channel_id";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
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


    public void hourlyTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                sendNotification("Je moet draaien!", "Het is weer tijd!");
            }
        }, 0, 1, TimeUnit.SECONDS); // TimeUnit.HOURS als je de het per uur wilt doen
    }

    public void monthlyTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();

        // Calculate the delay until the next month
        LocalDateTime nextMonth = now.plusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        Duration duration = Duration.between(now, nextMonth);
        long initialDelay = duration.getSeconds();

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("MM");
                Date date = new Date();
                Log.d("Month", dateFormat.format(date));
                String mon = "";
                if (date.toString().equals("12")) {
                    mon = "DEC";
                } else if (date.toString().equals("01")) {
                    mon = "JAN";
                } else if (date.toString().equals("02")) {
                    mon = "FEB";
                } else if (date.toString().equals("03")) {
                    mon = "MAR";
                } else if (date.toString().equals("04")) {
                    mon = "APR";
                } else if (date.toString().equals("05")) {
                    mon = "MAY";
                } else if (date.toString().equals("06")) {
                    mon = "JUN";
                } else if (date.toString().equals("07")) {
                    mon = "JUL";
                } else if (date.toString().equals("08")) {
                    mon = "AUG";
                } else if (date.toString().equals("09")) {
                    mon = "SEP";
                } else if (date.toString().equals("10")) {
                    mon = "OCT";
                } else if (date.toString().equals("11")) {
                    mon = "NOV";
                }
                String url = "url" + mon;

            }
        }, initialDelay, 30, TimeUnit.DAYS);
    }
}