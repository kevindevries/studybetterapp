package com.example.kevdevries.studybetter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.support.constraint.Placeholder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowCloseListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowLongClickListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnInfoWindowClickListener,
        LocationListener {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference, nameRef, yearRef, subject1Ref, subject2Ref, subject3Ref;
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation, currentLocation;
    Marker mCurrLocationMarker;
    String value_lat = null;
    String value_lng = null;

    @Override
    public void onResume() {
        super.onResume();

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {

        if (mGoogleMap == null) {
            getMapAsync(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mGoogleMap.setOnInfoWindowClickListener(this);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Users").child("Locations");

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String id = marker.getId();
        Log.d("Info Window Clicked", id);
    }

    //create markers for all users
    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {
        return mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title("Other User")
                .snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        //mLocationRequest.setInterval(1000);
        //mLocationRequest.setFastestInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        /*database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Users");

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();*/

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (currentLocation != null) {

            //Save Latitude and Longitude to Firebase. Update once location changes
            value_lat = String.valueOf(currentLocation.getLatitude());
            value_lng = String.valueOf(currentLocation.getLongitude());

            final String userId = auth.getCurrentUser().getUid();

            nameRef = database.getReference().child("Users").child(userId).child("firstName");
            yearRef = database.getReference().child("Users").child(userId).child("year");
            subject1Ref = database.getReference().child("Users").child(userId).child("subject1");
            subject2Ref = database.getReference().child("Users").child(userId).child("subject2");
            subject3Ref = database.getReference().child("Users").child(userId).child("subject3");

            final DatabaseReference currentUserDb = databaseReference.child(userId);

            nameRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    currentUserDb.child("firstName").setValue(name);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("Failed to read value.", error.toException());
                }

            });

            yearRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    currentUserDb.child("year").setValue(name);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("Failed to read value.", error.toException());
                }

            });

            subject1Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    currentUserDb.child("subject1").setValue(name);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("Failed to read value.", error.toException());
                }

            });

            subject2Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    currentUserDb.child("subject2").setValue(name);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("Failed to read value.", error.toException());
                }

            });

            subject3Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    currentUserDb.child("subject3").setValue(name);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w("Failed to read value.", error.toException());
                }

            });
            currentUserDb.child("latitude").setValue(value_lat);
            currentUserDb.child("longitude").setValue(value_lng);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("You");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Locations");

        childRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int size = (int) dataSnapshot.getChildrenCount();
                Marker[] allMarkers = new Marker[size];
                //mGoogleMap.clear();

                for (DataSnapshot s : dataSnapshot.getChildren()) {

                    CoordinatesModel coordinatesModel = new CoordinatesModel();

                    for (int i = 0; i <= size; i++) {
                        try {
                            coordinatesModel.setLatitude(s.getValue(CoordinatesModel.class).getLatitude());
                            coordinatesModel.setLongitude(s.getValue(CoordinatesModel.class).getLongitude());
                            coordinatesModel.setFirstName(s.getValue(CoordinatesModel.class).getFirstName());
                            coordinatesModel.setYear(s.getValue(CoordinatesModel.class).getYear());
                            coordinatesModel.setSubject1(s.getValue(CoordinatesModel.class).getSubject1());
                            coordinatesModel.setSubject2(s.getValue(CoordinatesModel.class).getSubject2());
                            coordinatesModel.setSubject3(s.getValue(CoordinatesModel.class).getSubject3());

                            double latt = coordinatesModel.getLatitude();
                            double lngg = coordinatesModel.getLongitude();
                            String firstName = coordinatesModel.getFirstName();
                            String year = coordinatesModel.getYear();
                            String subject1 = coordinatesModel.getSubject1();
                            String subject2 = coordinatesModel.getSubject2();
                            String subject3 = coordinatesModel.getSubject3();
                            //LatLng latLng1 = new LatLng(latt, lngg);
                            allMarkers[i] =
                                    mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latt, lngg))
                                            .title(firstName + year + " year student.")
                                            .snippet("Study Subjects. Click to Connect"));

                            InfoWindowModel info = new InfoWindowModel();
                            info.setSubject1(subject1);
                            info.setSubject2(subject2);
                            info.setSubject3(subject3);

                            CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(getActivity());
                            mGoogleMap.setInfoWindowAdapter(customInfoWindow);

                        } catch (Exception ex) {
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failed to read value.", error.toException());
            }
        });


        //CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(getActivity());

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /*@Override
    public void onStop() {
        super.onStop();

        final String userId = auth.getCurrentUser().getUid();

        final DatabaseReference currentUserDb = databaseReference.child(userId);
        currentUserDb.child("latitude").setValue(0);
        currentUserDb.child("longitude").setValue(0);
    }*/

}