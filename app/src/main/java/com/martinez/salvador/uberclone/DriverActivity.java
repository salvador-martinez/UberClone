package com.martinez.salvador.uberclone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class DriverActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private Marker marker;
    private String uid;
    private Firebase root;
    private boolean requestState;
    private String rideID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();

        root = new Firebase(getString(R.string.dbroot));
        root.authAnonymously(new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                uid = authData.getUid();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                firebaseError.toException().printStackTrace();
            }
        });
        requestState = false;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
            catch (SecurityException e) {e.printStackTrace();}
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                removeOldMarker();
                LatLng sydney = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                marker = mMap.addMarker(new MarkerOptions().position(sydney).title("You are Here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, (float) (mMap.getMinZoomLevel() /*/ 2.0*/)));
                Log.i("initial place", "Got location" + mLastLocation.toString());
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            // Add a marker in Sydney and move the camera
            if (mLastLocation != null) {
                removeOldMarker();
                Log.i("initial place", "Got location" + mLastLocation.toString());
                LatLng sydney = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                marker = mMap.addMarker(new MarkerOptions().position(sydney).title("You are Here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, (float) (mMap.getMinZoomLevel() /*/ 2.0*/)));
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
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
        mCurrentLocation = location;
        updateUI();
    }

    private void updateUI() {
        removeOldMarker();
        LatLng sydney = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        marker = mMap.addMarker(new MarkerOptions().position(sydney).title("You are Here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, (float) (mMap.getMinZoomLevel() /*/ 2.0*/)));
    }

    private void removeOldMarker() {
        if (marker != null) {
            marker.remove();
        }
    }

    public void postRideRequest(View v) {
        TextView status = (TextView) findViewById(R.id.ride_status);
        final Button toggle = (Button) findViewById(R.id.requestRideButton);
        Map<String, Object> temp = new HashMap<>();
        temp.put("requestor", uid);
        if (mCurrentLocation == null && mLastLocation == null && !requestState) {
            status.setText("Could not get location data");
            return;
        }
        double lat = (mCurrentLocation != null) ? mCurrentLocation.getLatitude() : (mLastLocation != null) ? mLastLocation.getLatitude() : 0;
        double lon = (mCurrentLocation != null) ? mCurrentLocation.getLongitude() : (mLastLocation != null) ? mLastLocation.getLongitude() : 0;
        temp.put("lat", lat);
        temp.put("lon", lon);
        Log.i("user id", uid);

        String message = "";
        String buttonmessage = "";
        if (requestState) {
            Firebase ride = root.child("rides/" + rideID);
            ride.removeValue();
            root.child("open_rides/" + rideID).removeValue(null);
            message = "Ride Canceled";
            buttonmessage = "Request Ride";
        } else {
            Firebase ride = root.child("rides").push();
            rideID = ride.getKey();
            ride.setValue(temp);
            root.child("open_rides/" + rideID).setValue(true);
            message = "Waiting On Driver";
            buttonmessage = "Cancel Request";
        }

        status.setText(message);
        toggle.setText(buttonmessage);
        requestState = !requestState;
        toggle.setClickable(false);
        toggle.setEnabled(false);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                toggle.setClickable(true);
                toggle.setEnabled(true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

    }
}