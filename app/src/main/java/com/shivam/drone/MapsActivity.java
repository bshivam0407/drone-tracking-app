package com.shivam.drone;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLoadedCallback{

    private GoogleMap mMap;
    DatabaseReference reference;
    LatLng src,dest;
    private ProgressDialog progressDialog;
    String droneId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        final Intent intent=getIntent();
        progressDialog=new ProgressDialog(MapsActivity.this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Maps, please wait...");
        progressDialog.show();
        droneId=intent.getStringExtra("droneId");
        Log.i("Drone",droneId);
        mapFragment.getMapAsync(this);

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
        mMap.setOnMapLoadedCallback(this);
    }
    public void getCurrentLocation()
    {
        reference.child("current").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    Location cur=snapshot.getValue(Location.class);
                    LatLng curLocation=new LatLng(cur.getLatitude(),cur.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(src).title("Source Location"));
                    mMap.addMarker(new MarkerOptions().position(dest).title("Destination Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    mMap.addMarker(new MarkerOptions().position(curLocation).title("Drone is here")
                            .icon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN)));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onMapLoaded() {

        reference=FirebaseDatabase.getInstance().getReference().child(droneId);
        reference.child("src").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    Location sr=dataSnapshot.getValue(Location.class);
                    src=new LatLng(sr.getLatitude(),sr.getLongitude());
                    reference.child("dest").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                Location des=snapshot.getValue(Location.class);
                                dest=new LatLng(des.getLatitude(),des.getLongitude());
                                getCurrentLocation();
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                builder.include(src);
                                builder.include(dest);
                                LatLngBounds bounds = builder.build();
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
                                mMap.moveCamera(cu);
                                progressDialog.dismiss();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(MapsActivity.this, "Invalid Drone Id", Toast.LENGTH_SHORT).show();
                    Intent intent1= new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent1);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}