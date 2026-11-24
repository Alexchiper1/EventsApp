package com.example.eventsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void addEvent(View view) {
        String title = ((EditText) findViewById(R.id.eventTitle)).getText().toString();
        String description = ((EditText) findViewById(R.id.eventDescription)).getText().toString();
        String category = ((EditText) findViewById(R.id.eventCategory)).getText().toString();
        String start = ((EditText) findViewById(R.id.eventStart)).getText().toString();
        String end = ((EditText) findViewById(R.id.eventEnd)).getText().toString();
        String venue = ((EditText) findViewById(R.id.eventVenue)).getText().toString();
        String address = ((EditText) findViewById(R.id.eventAddress)).getText().toString();
        String lat = ((EditText) findViewById(R.id.eventLat)).getText().toString();
        String lng = ((EditText) findViewById(R.id.eventLng)).getText().toString();

        if (title.isEmpty() || description.isEmpty() || category.isEmpty() || start.isEmpty() || end.isEmpty() || venue.isEmpty() || address.isEmpty() || lat.isEmpty() || lng.isEmpty()) {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://eventsapp-8d3ef-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference dbRef = database.getReference("events");

        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("description", description);
        event.put("category", category);

        Map<String, Object> datetime = new HashMap<>();
        datetime.put("start", start);
        datetime.put("end", end);
        datetime.put("recurrence", "none");
        event.put("datetime", datetime);

        Map<String, Object> coordinates = new HashMap<>();
        coordinates.put("lat", Double.parseDouble(lat));
        coordinates.put("lng", Double.parseDouble(lng));

        Map<String, Object> location = new HashMap<>();
        location.put("venue_name", venue);
        location.put("address", address);
        location.put("coordinates", coordinates);
        event.put("location", location);

        dbRef.push().setValue(event);
    }
}