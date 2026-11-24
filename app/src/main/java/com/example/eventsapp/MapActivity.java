package com.example.eventsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mf.getMapAsync(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        LatLng dublin = new LatLng(53.3498, -6.2603);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dublin, 11));

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Intent intent = new Intent(MapActivity.this, EventDetailsActivity.class);
                intent.putExtra("title", marker.getTitle());
                startActivity(intent);
                return true;
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://eventsapp-8d3ef-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference dbRef = database.getReference("events");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                googleMap.clear();

                for (DataSnapshot event : snapshot.getChildren()) {

                    String title = event.child("title").getValue(String.class);

                    Double lat = event.child("location")
                            .child("coordinates")
                            .child("lat").getValue(Double.class);

                    Double lng = event.child("location")
                            .child("coordinates")
                            .child("lng").getValue(Double.class);

                    if (title != null && lat != null && lng != null) {
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(title)
                        );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("EventsApp", "Firebase Fail", error.toException());
            }
        });

    }
}