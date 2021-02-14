package com.example.maps_naina_c0789965;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnPolygonClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {
    private GoogleMap naiMap;
    private Marker mainMark;
    private Marker targMark;
    Polygon shape;
    private static final int REQUESTED = 1;
    private static final int POLY_SIDE = 4;
    String[] str =   {"A","B","C","D"};
    List<Marker> markers = new ArrayList();
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        naiMap = googleMap;
        naiMap.setOnMarkerDragListener(this);
        naiMap.setOnMarkerClickListener(this);
        naiMap.setOnPolygonClickListener(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setMainMark(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (!hasLocationPermission())
            requestLocationPermission();
        else
            startUpdateLocation();

        naiMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                setMarker(latLng);
            }
        });
    }
    private void setMarker(LatLng latLng) {
        MarkerOptions options = new MarkerOptions().position(latLng)
                .title(str[markers.size()])
                .draggable(true);
        if (markers.size() == POLY_SIDE)
            crMap();

        markers.add(naiMap.addMarker(options));
        if (markers.size() == POLY_SIDE)
            drawShape();
    }
    private void drawShape() {
        PolygonOptions options = new PolygonOptions()
                .fillColor(0x3300FF00)
                .strokeColor(Color.BLUE)
                .strokeWidth(5);

        for (int i = 0; i< POLY_SIDE; i++) {
            options.add(markers.get(i).getPosition());
        }

        shape = naiMap.addPolygon(options);

    }
    private void crMap() {
        for (Marker marker: markers)
            marker.remove();

        markers.clear();
        shape.remove();
        shape = null;
    }
    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUESTED);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setMainMark(Location location) {
        if (mainMark != null){
            mainMark.remove();
        }

        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("Currently here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
        mainMark = naiMap.addMarker(options);
        naiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (REQUESTED == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }
    @Override
    public void onPolygonClick(Polygon polygon) {

            float[] distance1 = new float[1];
            Location.distanceBetween(markers.get(0).getPosition().latitude,markers.get(0).getPosition().longitude,markers.get(1).getPosition().latitude,markers.get(1).getPosition().longitude,distance1);
            float[] distance2 = new float[1];
            Location.distanceBetween(markers.get(1).getPosition().latitude,markers.get(1).getPosition().longitude,markers.get(2).getPosition().latitude,markers.get(2).getPosition().longitude,distance2);
            float[] distance3 = new float[1];
            Location.distanceBetween(markers.get(2).getPosition().latitude,markers.get(2).getPosition().longitude,markers.get(1).getPosition().latitude,markers.get(1).getPosition().longitude,distance3);
            float total = distance1[0] + distance2[0] + distance3[0];
            Toast.makeText(this, "Total distance "+total,
                    Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String locationAddress;
        try {
            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String postalCode = addresses.get(0).getPostalCode();
            locationAddress = address + ", "+ city+", "+state+", Postal Code :- "+postalCode;
        } catch (IOException e) {
            locationAddress = "Can't loacte";
            e.printStackTrace();
        }
        Toast.makeText(this, locationAddress,
                Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        markers.remove(marker);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if(shape != null){
            shape.remove();
            shape = null;
        }
        markers.add(marker);
        if (markers.size() == POLY_SIDE)
            drawShape();
    }
}