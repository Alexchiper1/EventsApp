package com.example.eventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

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
    Spinner spinner;
    ArrayList<String> allTitles;
    ArrayList<String> eventCategories;

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
}