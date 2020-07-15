package com.shivam.drone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    //FirebaseDatabase database;
    //DatabaseReference reference;
    EditText drone;
    String droneId;
    public  void getLocation(View view)
    {
        droneId=drone.getText().toString();
        Log.i("drone",droneId);
        Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
        intent.putExtra("droneId",droneId);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //database=FirebaseDatabase.getInstance();
        drone=findViewById(R.id.droneId);

        //reference=database.getReference();


    }
}