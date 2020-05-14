package com.fci_zu_eng_gemy_96.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.fci_zu_eng_gemy_96.Model.Requests;
import com.fci_zu_eng_gemy_96.OrderStatus;
import com.fci_zu_eng_gemy_96.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class ListenOrder extends Service implements ChildEventListener {

    FirebaseDatabase db;
    DatabaseReference orders;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseDatabase.getInstance();
        orders = db.getReference("requests");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        orders.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Requests requests = dataSnapshot.getValue(Requests.class);
        if (requests.getStatus().equals("0")){
            showNotification(dataSnapshot.getKey(),requests);
        }
    }

    private void showNotification(String key, Requests requests) {
        Intent intent = new Intent(this, OrderStatus.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"ServerStatusService");
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setTicker("FoodsOrder Server")
                .setContentTitle("Foods Order Message")
                .setContentText("You have New Order #"+key)
                .setContentInfo("New Order")
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        //if you want to many notification show , you need to give unique id for each notification
        int randomInt = new Random().nextInt(9999-1)+1;
        manager.notify(randomInt,builder.build());

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
