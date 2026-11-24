package com.example.eventsapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference dbRef;
    ListView eventList;
    ArrayList<String> titles;
    ArrayAdapter<String> adapter;
    Spinner spinner;
    ArrayList<String> allTitles;
    ArrayList<String> eventCategories;
    //Location
    Location userlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        ArrayList<String> filter = new ArrayList<>();
        filter.add("All Categories");

        ArrayAdapter<String> filteradapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filter);
        spinner.setAdapter(filteradapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (allTitles != null) {
                    filterEvents(filter.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        eventList = findViewById(R.id.eventList);
        titles = new ArrayList<>();
        allTitles = new ArrayList<>();
        eventCategories = new ArrayList<>();
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
                allTitles.clear();
                eventCategories.clear();

                filter.clear();
                filter.add("All Categories");

                for (DataSnapshot event : snapshot.getChildren()) {
                    String title = event.child("title").getValue(String.class);
                    String category = event.child("category").getValue(String.class);



                    allTitles.add(title);
                    eventCategories.add(category);

                    if (category != null && !filter.contains(category)) {
                        filter.add(category);
                    }
                }

                filteradapter.notifyDataSetChanged();

                filterEvents(spinner.getSelectedItem().toString());
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

        Button adminBut = findViewById(R.id.adminButton);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.getReference("admins").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(Boolean.TRUE.equals(snapshot.getValue(Boolean.class))){
                    adminBut.setVisibility(View.VISIBLE);
                }else{
                    adminBut.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("EventsApp", "Admin failed", error.toException());
            }
        });
    }

    private void filterEvents(String string) {
        titles.clear();

        for (int i = 0; i < allTitles.size(); i++) {
            if (string.equals("All Categories") ||
                    string.equals(eventCategories.get(i))) {
                titles.add(allTitles.get(i));
            }
        }

        adapter.notifyDataSetChanged();
    }

    public void openMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }


    public void nearMe(View view) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            userlocation = location;

            double lat = location.getLatitude();
            double lng = location.getLongitude();
            Toast.makeText(this, lat + ", " + lng, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            return;
        }

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                titles.clear();

                for (DataSnapshot event : snapshot.getChildren()) {
                    String title = event.child("title").getValue(String.class);

                    Double lat = event.child("location").child("coordinates").child("lat").getValue(Double.class);
                    Double lng = event.child("location").child("coordinates").child("lng").getValue(Double.class);

                    if (lat != null && lng != null) {
                        Location eventLocation = new Location("event");
                        eventLocation.setLatitude(lat);
                        eventLocation.setLongitude(lng);

                        float distance = userlocation.distanceTo(eventLocation);

                        if (distance <= 5000) {
                            titles.add(title);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("EventsApp", "Firebase Fail", error.toException());
            }
        });
    }
    public void openAdmin(View view) {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    public void openCalendar(View view) {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

}