package com.example.yurich.mega_f_test_2;

import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    // Objects in parcelables
    public static final String PINNED_LOCATION = "pinnedLocation";
    public static final String CAMERA_POSITION = "cameraPosition";

    private LatLng pinnedLocation;
    private CameraPosition cameraPosition;

    // Zoom value to show whole city
    public static final float DEFAULT_ZOOM = 9;

    private GoogleMap mMap;

    Geocoder geocoder = new Geocoder(this, Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (savedInstanceState != null) {
            pinnedLocation = savedInstanceState.getParcelable(PINNED_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(CAMERA_POSITION);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        if (pinnedLocation != null) {
            pinDesiredPlace(pinnedLocation);
        }

        float zoom;
        LatLng myPlace;
        if (cameraPosition != null) {
            myPlace = cameraPosition.target;
            zoom = cameraPosition.zoom;
        } else {
            // Moscow Coordinates
            myPlace = new LatLng(55.75f, 37.6f);
            zoom = DEFAULT_ZOOM;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, zoom));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        pinnedLocation = latLng;

        pinDesiredPlace(latLng);
    }

    private void pinDesiredPlace(LatLng latLng) {
        try {
            List<Address> addresses
                    = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            mMap.clear();
            Bitmap markerBitmap = new IconGenerator(this).makeIcon(
                    getHumanReadableLatLng(latLng) +
                    "\n" +
                    addresses.get(0).getCountryName() +
                    ", " +
                    addresses.get(0).getAdminArea() +
                    ", " +
                    addresses.get(0).getLocality() +
                    "\n" +
                    addresses.get(0).getAddressLine(0)
            );

            BitmapDescriptor marker = BitmapDescriptorFactory.fromBitmap(markerBitmap);

            mMap.addMarker(new MarkerOptions().position(latLng).icon(marker));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getHumanReadableLatLng(LatLng latLng) {
        StringBuilder builder = new StringBuilder();

        // Adding latitude
        double latitude = latLng.latitude;
        builder.append(String.format(Locale.getDefault(), "%.4f° ", Math.abs(latitude)));
        if (latitude >= 0) {
            builder.append("с.ш.");
        } else {
            builder.append("ю.ш.");
        }

        builder.append(", ");

        // Adding longitude
        double longitude = latLng.longitude;
        builder.append(String.format(Locale.getDefault(), "%.4f° ", Math.abs(longitude)));
        if (longitude >= 0) {
            builder.append("в.д.");
        } else {
            builder.append("з.д.");
        }

        return builder.toString();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(PINNED_LOCATION, pinnedLocation);
        outState.putParcelable(CAMERA_POSITION, mMap.getCameraPosition());
    }
}
