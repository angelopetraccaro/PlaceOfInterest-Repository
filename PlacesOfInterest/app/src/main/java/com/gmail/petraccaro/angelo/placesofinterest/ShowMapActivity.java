package com.gmail.petraccaro.angelo.placesofinterest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Visualizza la posizione mostrata su maps(google) relativa alla foto scattata
 */
public class ShowMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        double latitude = getIntent().getDoubleExtra("Latitude",0);
        double longitude = getIntent().getDoubleExtra("Longitude",0);

        // Add a marker and move the camera
        LatLng place = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(place));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
    }
}
