package com.awake.gps;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MainActivity extends Activity implements android.content.DialogInterface.OnClickListener {

    EditText editTextShowLocation;
    ProgressBar progress;

    LocationManager locManager;
    LocationListener locListener = new MyLocationListener();

    boolean gps_enabled = false;
    boolean network_enabled = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextShowLocation = (EditText) findViewById(R.id.editTextShowLocation);
        //progress = new ProgressBar(this);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);
        //get location manager service
        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    //on click button
    public void where(View v) {


        progress.setVisibility(View.VISIBLE);//start progress
        // exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        //atleast one should be enabled


        // don't start listeners if no provider is enabled
        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder builder = new Builder(this);
            builder.setTitle("Attention!");
            builder.setMessage("Sorry, location is not determined. Please enable location providers");
            builder.setPositiveButton("OK", this);
            builder.setNeutralButton("Cancel", this);
            builder.create().show();
            progress.setVisibility(View.GONE);
        }
        //if providers are enabled, get location
        if (gps_enabled) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
        }
        if (network_enabled) {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
        } //end

    }//end


    //thios class listens for location
    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                // This needs to stop getting the location data and save the battery power.
                //locManager.removeUpdates(locListener);
                //here we get the location
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                editTextShowLocation.setText("You are Here\n\n"+"Longitude:"+longitude + "\nLatitude" + latitude + "\n");
                progress.setVisibility(View.GONE);


                //TO DO your logic
                //show location on a map
			Intent x = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+latitude+","+longitude));
		    startActivity(x);
                //Send as SMS
                //Save to DataBase
                //compare with another location
                //


            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_NEUTRAL){
            editTextShowLocation.setText("Sorry, location is not determined. To fix this please enable location providers");
        }else if (which == DialogInterface.BUTTON_POSITIVE) {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

}

