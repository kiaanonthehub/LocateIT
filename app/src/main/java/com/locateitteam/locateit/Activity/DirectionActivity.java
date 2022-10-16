package com.locateitteam.locateit.Activity;

import static com.locateitteam.locateit.Activity.MapsActivity.destinationLatLong;
import static com.locateitteam.locateit.Activity.MapsActivity.deviceLatlong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.locateitteam.locateit.Adapter.DirectionStepAdapter;
import com.locateitteam.locateit.Constant.AllConstant;
import com.locateitteam.locateit.Model.DirectionPlaceModel.DirectionLegModel;
import com.locateitteam.locateit.Model.DirectionPlaceModel.DirectionResponseModel;
import com.locateitteam.locateit.Model.DirectionPlaceModel.DirectionRouteModel;
import com.locateitteam.locateit.Model.DirectionPlaceModel.DirectionStepModel;
import com.locateitteam.locateit.Permissions.AppPermissions;
import com.locateitteam.locateit.R;
import com.locateitteam.locateit.Util.FirebaseUtil;
import com.locateitteam.locateit.WebServices.RetrofitAPI;
import com.locateitteam.locateit.WebServices.RetrofitClient;
import com.locateitteam.locateit.databinding.ActivityDirectionBinding;
import com.locateitteam.locateit.databinding.BottomSheetLayoutBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback {

    // declarations
    private ActivityDirectionBinding binding;
    private GoogleMap mGoogleMap;
    private AppPermissions appPermissions;
    private boolean isLocationPermissionOk, isTrafficEnable;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
    private BottomSheetLayoutBinding bottomSheetLayoutBinding;
    private RetrofitAPI retrofitAPI;
    private Location currentLocation;
    private Double endLat, endLng;
    private String placeId;
    private int currentMode;
    private DirectionStepAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDirectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initialise the fields
        endLat = getIntent().getDoubleExtra("lat", 0.0);
        endLng = getIntent().getDoubleExtra("lng", 0.0);
        placeId = getIntent().getStringExtra("placeId");

        // property initialization
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appPermissions = new AppPermissions();

        // initialise json retrofit object
        retrofitAPI = RetrofitClient.getRetrofitClient().create(RetrofitAPI.class);

        // initialise ui properties for directions
        bottomSheetLayoutBinding = binding.bottomSheet;
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayoutBinding.getRoot());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // initialise the ui adapter
        adapter = new DirectionStepAdapter();

        // set the layout for the directions
        bottomSheetLayoutBinding.stepRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bottomSheetLayoutBinding.stepRecyclerView.setAdapter(adapter);

        // decalaring a support map fragment object
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.directionMap);

        // initialise obj
        mapFragment.getMapAsync(this);

        // initialise viewing the current updated traffic
        binding.enableTraffic.setOnClickListener(view -> {
            if (isTrafficEnable) {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(false);
                    isTrafficEnable = false;
                }
            } else {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(true);
                    isTrafficEnable = true;
                }
            }
        });

        // user can select the mode of traffic
        binding.travelMode.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId != -1) {
                    switch (checkedId) {
                        case R.id.btnChipDriving:
                            getDirection("driving");
                            break;
                        case R.id.btnChipWalking:
                            getDirection("walking");
                            break;
                        case R.id.btnChipBike:
                            getDirection("bicycling");
                            break;
                        case R.id.btnChipTrain:
                            getDirection("transit");
                            break;
                    }
                }
            }
        });

    }

    // method used for the user to get the directions to a specific location
    private void getDirection(String mode) {

        // check if the permissions has been enabled
        if (isLocationPermissionOk) {
            // read from firebase
            FirebaseUtil.mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String url="",units="",traveltype="";
                    // declare list
                    List<String> list = new ArrayList<>();

                    // iterate through the obj pulled from firebase
                    for (DataSnapshot mySnapshot : snapshot.getChildren()) {

                        list.add(mySnapshot.getValue().toString());
                    }

                    if(Boolean.parseBoolean(list.get(0))){

                        switch (list.get(1)){
                            case "Kilometers":
                                units = "km";
                                break;

                            case "Miles":
                                units = "imperial";
                                break;

                        }

                        switch (list.get(2)) {
                            case "Default":
                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                break;

                            case "Satellite":
                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;

                            case "Terrain":
                                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                        }

                        switch (list.get(3)) {
                            case "Driving":
                                traveltype ="driving";
                                break;
                            case "Walking":
                                traveltype = "walking";
                                break;
                            case "Cycling":
                                traveltype = "bicycling";
                                break;
                            case "Transit":
                                traveltype = "transit";
                                break;
                        }

                        url = "https://maps.googleapis.com/maps/api/directions/json?" +
                                "origin=" + deviceLatlong.latitude + "," + deviceLatlong.longitude +
                                "&destination=" + destinationLatLong.latitude + "," + destinationLatLong.longitude +
                                "&mode=" + traveltype +
                                "&units=" + units+
                                "&key=" + getResources().getString(R.string.google_api_key_places);
                    }
                    else{
                        url = "https://maps.googleapis.com/maps/api/directions/json?" +
                                "origin=" + deviceLatlong.latitude + "," + deviceLatlong.longitude +
                                "&destination=" + destinationLatLong.latitude + "," + destinationLatLong.longitude +
                                "&mode=" + mode +
                                "&key=" + getResources().getString(R.string.google_api_key_places);
                    }

                    // retrofit obj reading from the google api
                    retrofitAPI.getDirection(url).enqueue(new Callback<DirectionResponseModel>() {
                        @Override
                        public void onResponse(Call<DirectionResponseModel> call, Response<DirectionResponseModel> response) {
                            Gson gson = new Gson();
                            String res = gson.toJson(response.body());
                            Log.d("TAG", "onResponse: " + res);

                            if (response.errorBody() == null) {
                                if (response.body() != null) {
                                    clearUI();

                                    if (response.body().getDirectionRouteModels().size() > 0) {
                                        DirectionRouteModel routeModel = response.body().getDirectionRouteModels().get(0);

                                        getSupportActionBar().setTitle(routeModel.getSummary());

                                        DirectionLegModel legModel = routeModel.getLegs().get(0);
                                        binding.txtStartLocation.setText(legModel.getStartAddress());
                                        binding.txtEndLocation.setText(legModel.getEndAddress());

                                        bottomSheetLayoutBinding.txtSheetTime.setText(legModel.getDuration().getText());
                                        bottomSheetLayoutBinding.txtSheetDistance.setText(legModel.getDistance().getText());


                                        mGoogleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(legModel.getEndLocation().getLat(), legModel.getEndLocation().getLng()))
                                                .title("End Location"));

                                        mGoogleMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng()))
                                                .title("Start Location"));

                                        adapter.setDirectionStepModels(legModel.getSteps());


                                        List<LatLng> stepList = new ArrayList<>();

                                        PolylineOptions options = new PolylineOptions()
                                                .width(25)
                                                .color(Color.BLUE)
                                                .geodesic(true)
                                                .clickable(true)
                                                .visible(true);

                                        List<PatternItem> pattern;
                                        if (mode.equals("walking")) {
                                            pattern = Arrays.asList(
                                                    new Dot(), new Gap(10));

                                            options.jointType(JointType.ROUND);
                                        } else {
                                            pattern = Arrays.asList(
                                                    new Dash(30));
                                        }

                                        options.pattern(pattern);

                                        for (DirectionStepModel stepModel : legModel.getSteps()) {
                                            List<com.google.maps.model.LatLng> decodedLatLng = decode(stepModel.getPolyline().getPoints());
                                            for (com.google.maps.model.LatLng latLng : decodedLatLng) {
                                                stepList.add(new LatLng(latLng.lat, latLng.lng));
                                            }
                                        }

                                        options.addAll(stepList);

                                        Polyline polyline = mGoogleMap.addPolyline(options);
                                        LatLng startLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());
                                        LatLng endLocation = new LatLng(legModel.getStartLocation().getLat(), legModel.getStartLocation().getLng());


                                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(startLocation, endLocation), 17));

                                    } else {
                                        Toast.makeText(DirectionActivity.this, "No route found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(DirectionActivity.this, "No route found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d("TAG", "onResponse: " + response);
                            }

                        }

                        @Override
                        public void onFailure(Call<DirectionResponseModel> call, Throwable t) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        }

    }

    // clean and initialise the ui
    private void clearUI() {
        mGoogleMap.clear();
        binding.txtStartLocation.setText("");
        binding.txtEndLocation.setText("");
        getSupportActionBar().setTitle("");
        bottomSheetLayoutBinding.txtSheetDistance.setText("");
        bottomSheetLayoutBinding.txtSheetTime.setText("");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        if (appPermissions.isLocationOk(this)) {
            isLocationPermissionOk = true;
            setupGoogleMap();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("Required location permission to show you near by places")
                        .setPositiveButton("Ok", (dialog, which) -> appPermissions.requestLocationPermission(DirectionActivity.this))
                        .create().show();
            } else {
                appPermissions.requestLocationPermission(DirectionActivity.this);
            }
        }
    }

    // user permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AllConstant.LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isLocationPermissionOk = true;
                setupGoogleMap();
            } else {
                isLocationPermissionOk = false;
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // initialise the google map
    private void setupGoogleMap() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);

        getCurrentLocation();
    }

    // method to get the users current location
    private void getCurrentLocation() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;

                getDirection("driving");

            } else {
                Toast.makeText(DirectionActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else
            super.onBackPressed();
    }

    private List<com.google.maps.model.LatLng> decode(String points) {

        int len = points.length();

        final List<com.google.maps.model.LatLng> path = new ArrayList<>(len / 2);
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new com.google.maps.model.LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;

    }
}