package com.example.eventsapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class EventDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_details);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        TextView eventTitle = findViewById(R.id.eventTitle);
        eventTitle.setText(title);

        //description
        TextView eventDetails = findViewById(R.id.eventDetails);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://eventsapp-8d3ef-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference dbRef = database.getReference("events");

        //Categories
        TextView eventCat = findViewById(R.id.eventCat);

        //Date
        TextView eventDate = findViewById(R.id.eventDate);

        //Location
        TextView eventLocation = findViewById(R.id.eventLocation);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot event: snapshot.getChildren()){
                    String name = event.child("title").getValue(String.class);

                    if(title.equals(name)){
                        String details = event.child("description").getValue(String.class);
                        eventDetails.setText(details);

                        String category = event.child("category").getValue(String.class);
                        eventCat.setText(category);

                        String date = event.child("datetime").child("start").getValue(String.class);
                        eventDate.setText(date);

                        String location = event.child("location").child("address").getValue(String.class);
                        eventLocation.setText(location);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("EventsApp", "Firebase Fail", error.toException());
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void subscribe(View view) {
        String title = ((TextView) findViewById(R.id.eventTitle)).getText().toString();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://eventsapp-8d3ef-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference dbRef = database.getReference("subscriptions");
        dbRef.child(uid).push().setValue(title).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Subscribed", Toast.LENGTH_SHORT).show();
            sendNotification(title);
        });
    }

    public void sendNotification(String title) {

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("events", "Event Notifications", NotificationManager.IMPORTANCE_DEFAULT);
        manager.createNotificationChannel(channel);

        Notification notification = new Notification.Builder(this, "events")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Event Subscription")
                .setContentText("Subscribed to " + title)
                .setAutoCancel(true)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2);
            return;
        }
        manager.notify(1, notification);
    }

}