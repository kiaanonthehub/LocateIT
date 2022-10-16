package com.locateitteam.locateit.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.locateitteam.locateit.Constant.AllConstant;
import com.locateitteam.locateit.GoogleAPI.FetchData;
import com.locateitteam.locateit.Model.PlaceModel;
import com.locateitteam.locateit.R;
import com.locateitteam.locateit.SavedPlaceModel;
import com.locateitteam.locateit.Util.FirebaseUtil;
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
    public static LatLng deviceLatlong, destinationLatLong;

    // test instance of class for - SavedLocation
    SavedPlaceModel savedPlaceModel = new SavedPlaceModel();
    private boolean mLocationPermissionGranted = false;
    private boolean isLocationPermissionOk, isTrafficEnable;

    // map fields
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceModel selectedPlaceModel;

    // component fields
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Location services check
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            //here do what you want when the GPS service is enabled

            Toast.makeText(MapsActivity.this, "GPS is enabled", Toast.LENGTH_SHORT).show();

        } else {

            // dialog prompt box
            MaterialAlertDialogBuilder locationDialog = new MaterialAlertDialogBuilder(MapsActivity.this);
            locationDialog.setTitle("Attention");
            locationDialog.setMessage("Location settings must be enabled from the settings to use the application");
            locationDialog.setCancelable(false);
            locationDialog.setPositiveButton("Open settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            locationDialog.create().show();
        }

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        searchView = findViewById(R.id.svlocation);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // horizontal scroll view for places
        for (PlaceModel placeModel : AllConstant.placesName) {

            Chip chip = new Chip(this);
            chip.setText(placeModel.getName());
            chip.setId(placeModel.getId());
            chip.setPadding(8, 8, 8, 8);
            chip.setTextColor(getResources().getColor(R.color.white, null));
            chip.setChipBackgroundColor(getResources().getColorStateList(R.color.blue_grey, null));
            chip.setChipIcon(ResourcesCompat.getDrawable(getResources(), placeModel.getDrawableId(), null));
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);

            binding.placesGroup.addView(chip);


        }

        binding.placesGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                if (checkedId != -1) {
                    PlaceModel placeModel = AllConstant.placesName.get(checkedId - 1);
                    selectedPlaceModel = placeModel;
                    FindPlaceType(placeModel.getPlaceType());
                }
            }
        });

        // traffic fab
        binding.enableTraffic.setOnClickListener(view -> {

            if (isTrafficEnable) {
                if (mMap != null) {
                    mMap.setTrafficEnabled(false);
                    isTrafficEnable = false;
                }
            } else {
                if (mMap != null) {
                    mMap.setTrafficEnabled(true);
                    isTrafficEnable = true;
                }
            }

        });

        // location fab
        binding.btnFavLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // write to firebase
                FirebaseUtil.WriteToFirebase(savedPlaceModel);

                Intent i = new Intent(MapsActivity.this, SavedLocationsActivity.class);
                startActivity(i);
                //Toast.makeText(MapsActivity.this, "Coming soon bi-otch", Toast.LENGTH_SHORT).show();


            }
        });

        // map type fab
        binding.btnMapType.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.map_type_menu, popupMenu.getMenu());


            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.btnNormal:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;

                    case R.id.btnSatellite:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;

                    case R.id.btnTerrain:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                }
                return true;
            });

            popupMenu.show();
        });

        // get direction fab
        binding.btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapsActivity.this, DirectionActivity.class);
                startActivity(i);
            }
        });

        // settings fab
        binding.enableSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MapsActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        // location fab
        binding.currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(deviceLatlong, 15));
            }
        });

        // search view editor
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                mMap.clear();
                String location = searchView.getQuery().toString();
                List<Address> addressLst = null;

                if (location != null || location == "") {

                    //https://youtu.be/R6hev9p_qW8
                    Geocoder geocoder = new Geocoder(MapsActivity.this);

                    try {
                        addressLst = geocoder.getFromLocationName(location, 1);
                        if (addressLst.size() != 0) {
                            Address address = addressLst.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in " + location));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            destinationLatLong = latLng;

                            // demo
                            if (address.getThoroughfare() != null) {
                                savedPlaceModel.setName(address.getFeatureName() + ", " + address.getThoroughfare());

                            } else {
                                savedPlaceModel.setName(address.getFeatureName());
                            }
                            savedPlaceModel.setAddress(address.getAddressLine(0));
                            savedPlaceModel.setLat(latLng.latitude);
                            savedPlaceModel.setLng(latLng.longitude);

                        } else {

                        }
                    } catch (IOException e) {

                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
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
        } else {
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

    public void FindPlaceType(String place) {

        mMap.clear();
        // deaclare var
        double lat = deviceLatlong.latitude, lng = deviceLatlong.longitude;

        StringBuilder stringBuilder = new
                StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        stringBuilder.append("location=" + lat + "," + lng);
        stringBuilder.append("&radius=1000");
        stringBuilder.append("&type=" + place);
        stringBuilder.append("&sensor=true");
        stringBuilder.append("&key=" + getResources().getString(R.string.google_api_key_places));

        String url = stringBuilder.toString();
        Object dataFetch[] = new Object[2];
        dataFetch[0] = mMap;
        dataFetch[1] = url;

        FetchData fetchData = new FetchData();
        fetchData.execute(dataFetch);
    }
}