package com.locateitteam.locateit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.locateitteam.locateit.Model.GeoLatLongModel;
import com.locateitteam.locateit.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // declare and initialise fields
    private static final String TAG = "MapActivity";

    // location fields
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private LatLng deviceLatlong;
    private boolean mLocationPermissionGranted = false;

    // map fields
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // component fields
    private Button btnAtm,btnRestaurants,btnPetrol,btnSettings;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnAtm = findViewById(R.id.goTo1);
        btnRestaurants = findViewById(R.id.goTo2);
        btnPetrol = findViewById(R.id.goTo3);
        btnSettings = findViewById(R.id.btnSettings);
        searchView = findViewById(R.id.svlocation);
        mapFragment = (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressLst = null;

                if (location != null || location == ""){

                    //https://youtu.be/R6hev9p_qW8
                    Geocoder geocoder = new Geocoder(MapsActivity.this);

                    try{
                        addressLst = geocoder.getFromLocationName(location,1);
                        if(addressLst.size() != 0){
                            Address address = addressLst.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in "+ location));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        }else{

                        }
                    }catch(IOException e){

                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });



        btnAtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // deaclare var
                double lat = deviceLatlong.latitude, lng=deviceLatlong.longitude;

                StringBuilder stringBuilder = new
                        StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

                stringBuilder.append("location="+lat+","+lng);
                stringBuilder.append("&radius=1000");
                stringBuilder.append("&type=Petrol");
                stringBuilder.append("&sensor=true");
                stringBuilder.append("&key="+getResources().getString(R.string.google_api_key_places));

                String url = stringBuilder.toString();
                Object dataFetch[] = new Object[2];
                dataFetch[0] = mMap;
                dataFetch[1] = url;

                FetchData fetchData = new FetchData();
                fetchData.execute(dataFetch);

//                FindPlaceType(btnAtm.getText().toString());
//                Toast.makeText(MapsActivity.this, ""+btnAtm.getText().toString(), Toast.LENGTH_SHORT).show();
//                // Add a marker and cordinates in to find atm and move the camera
//                LatLng london = new LatLng(51.5072, 0.1276);
//                mMap.addMarker(new MarkerOptions().position(london).title("Marker in London"));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(london, 15));
            }
        });

        btnRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add a marker and cordinates in to find atm and move the camera
                LatLng london = new LatLng(51.5072, 0.1276);
                mMap.addMarker(new MarkerOptions().position(london).title("Marker in London"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(london, 15));
            }
        });

        btnPetrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add a marker and cordinates in to find atm and move the camera
                LatLng london = new LatLng(51.5072, 0.1276);
                mMap.addMarker(new MarkerOptions().position(london).title("Marker in London"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(london, 15));
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapsActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {


                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
    }


    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        // check if the user has granted persmission
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task location = mFusedLocationProviderClient.getLastLocation();

            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location cLocation = (Location) task.getResult();
                        LatLng currentLatLng = new LatLng(cLocation.getLatitude(), cLocation.getLongitude());
                        deviceLatlong = currentLatLng;
                        moveCamera(currentLatLng, 15);

                    } else {
                        Toast.makeText(MapsActivity.this, "Unable to find current location", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (SecurityException e) {

        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocationPermission();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        getDeviceLocation();
    }

    public void FindPlaceType(String place){

        // deaclare var
        double lat = 0.0, lng=0.0;

        StringBuilder stringBuilder = new
                StringBuilder("http://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        stringBuilder.append("location="+lat+","+lng);
        stringBuilder.append("&radius=1000");
        stringBuilder.append("&type=ATMs");
        stringBuilder.append("&sensor=true");
        stringBuilder.append("&key="+getResources().getString(R.string.google_api_key));

        String url = stringBuilder.toString();
        Object dataFetch[] = new Object[2];
        dataFetch[0] = mMap;
        dataFetch[1] = url;

        FetchData fetchData = new FetchData();
        fetchData.execute(dataFetch);
    }
}