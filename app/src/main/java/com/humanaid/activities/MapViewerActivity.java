package com.humanaid.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.humanaid.R;
import com.humanaid.usefulClasses.ConnectionCheck;

public class MapViewerActivity extends AppCompatActivity {

    private Double requesterLat, requesterLong, responderLat, responderLong;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String webLink;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_viewer);

        // Initialize Location Provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Initialize WebView
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);


        // Get Extras from previous Intent
        requesterLat = getIntent().getDoubleExtra("requesterLat", 0);
        requesterLong = getIntent().getDoubleExtra("requesterLong", 0);

        // Get Responder Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    29);
        }
        else{
            fusedLocationProviderClient
                    .getLastLocation()
                    .addOnSuccessListener(MapViewerActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location != null){
                                responderLat = location.getLatitude();
                                responderLong = location.getLongitude();

                                Toast.makeText(MapViewerActivity.this,
                                        "Responder Location: " + responderLat.toString() + " , " + responderLong.toString(),
                                        Toast.LENGTH_SHORT).show();


                                // If the location was taken successfully, generate the link and open in WebView
                                webLink = generateLink(responderLat,responderLong,requesterLat,requesterLong);
                                Log.d("generatedGoogleLink", webLink);

                                //Open the Link in WebView
                                if(ConnectionCheck.isOnline(MapViewerActivity.this)){
                                    webView.loadUrl(webLink);
                                } else {
                                    Toast.makeText(MapViewerActivity.this, "Please Enable Internet", Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                // TODO: BUG: Open Google Maps First, then this app. Then can get location
                                Toast.makeText(MapViewerActivity.this,
                                        "Please Enable GPS and Visit Google Maps",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private String generateLink(Double responderLat, Double responderLong, Double requesterLat, Double requesterLong) {
        String link = "https://www.google.com/maps/dir/'" +
                responderLat.toString() + "," + responderLong.toString() + "'/'" +
                requesterLat.toString() + "," + requesterLong.toString() + "'/" ;

        return link;
    }
}