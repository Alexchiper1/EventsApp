package com.example.eventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference dbRef;
    ListView eventList;
    ArrayList<String> titles;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        eventList = findViewById(R.id.eventList);
        titles = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        eventList.setAdapter(adapter);

        //Open details of event
        eventList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, EventDetailsActivity.class);
            intent.putExtra("title", titles.get(position));
            startActivity(intent);
        });

        database = FirebaseDatabase.getInstance("https://eventsapp-8d3ef-default-rtdb.europe-west1.firebasedatabase.app");
        dbRef = database.getReference("events");
        //read the events from firebase
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                titles.clear();

                for (DataSnapshot event : snapshot.getChildren()){
                    String title = event.child("title").getValue(String.class);
                    titles.add(title);
                    Log.d("EventsApp",title);
                }

                adapter.notifyDataSetChanged();
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

    public void openMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}