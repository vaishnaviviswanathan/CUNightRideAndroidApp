package com.gmaps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class ViewMaps extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    TextView mainLabel;
    GPSTracker gps;
    Context context;
    public void loadDestination(View view) {
        System.out.println("insei dest");
        Activity activity = (Activity) context;
        Intent intent = new Intent(this, Destination.class);
        System.out.println("Intent:"+intent+"context:"+context);
        //finish();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        Button btnShowLocation;
        btnShowLocation = (Button) findViewById(R.id.location);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gps = new GPSTracker(ViewMaps.this);

                if(gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    Toast.makeText(
                            getApplicationContext(),
                            "Your Location is -\nLat: " + latitude + "\nLong: "
                                    + longitude, Toast.LENGTH_LONG).show();
                    LatLng latLngDriver = new LatLng(gps.getLatitude(), gps.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLngDriver).title("Driver").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLngDriver).zoom(13).bearing(45).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    System.out.println("*****Location not fetched*****");
                }
            }
        });

    }


    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        System.out.println("************mLastLocation*********"+mLastLocation+mGoogleApiClient);
        if (mLastLocation != null) {
            System.out.println((String.valueOf(mLastLocation.getLatitude())));
            System.out.println(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            System.out.println("Perm error");
            return;
        }
        mMap.setMyLocationEnabled(true);
        int passengerNum=0;
        List<String> passengerList = Arrays.asList("Boulder High School Boulder CO", "2300 Arapahoe Ave Boulder CO", "University Memorial Center Boulder CO");
        Iterator<String> iterator = passengerList.iterator();
        try {

            while(iterator.hasNext()) {
                String test = iterator.next();
                passengerNum++;
                System.out.println("String*******"+ test);
                Geocoder geocoder = new Geocoder(this);
                List<android.location.Address> addressList;
                int numOfSuggestions=2;
                addressList = geocoder.getFromLocationName(test, numOfSuggestions);
                android.location.Address address = addressList.get(0);

                if (address!=null) {
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(latLng).title("Passenger" + passengerNum));
                    mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
                } else {
                    System.err.print("Cannot Fetch Location");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onStart() {
        //mGoogleApiClient.connect();
        super.onStart();

        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "ViewMaps Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.gmaps/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "ViewMaps Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.gmaps/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
