package com.example.eventsapp;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance("https://eventsapp-8d3ef-default-rtdb.europe-west1.firebasedatabase.app");
        dbRef = database.getReference("events");
        //read the events from firebase
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("EventsApp", "Firebase data received");
                for (DataSnapshot event : snapshot.getChildren()){
                    String title = event.child("title").getValue(String.class);
                    Log.d("EventsApp",title);
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
}