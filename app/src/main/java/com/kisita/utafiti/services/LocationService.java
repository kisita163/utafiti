package com.kisita.utafiti.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class LocationService extends IntentService implements OnSuccessListener {

    public  static final String BROADCAST_LOCATION   = "com.kisita.caritas.action.BROADCAST_LOCATION";
    public  static final String CURRENT_ADDRESS      = "com.kisita.caritas.action.CURRENT_ADDRESS";;
    private FusedLocationProviderClient mFusedLocationClient;

    private static String TAG = "LocationService";
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GET_CITY = "com.kisita.caritas.action.GET_CITY";


    public LocationService() {
        super("LocationService");
    }

    /**
     * Starts this service to perform action GET_CITY with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void startActionGetCity(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(ACTION_GET_CITY);
        context.startService(intent);
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onHandleIntent(Intent intent) {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(this);
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetCity(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        String errorMessage = "";

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "Service not available";
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "Invalid longitude or latitude";
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No address found";
                Log.e(TAG, errorMessage);

                Address address = new Address(Locale.getDefault());
                address.setLatitude(location.getLatitude());
                address.setLongitude(location.getLongitude());
                broadcastAddress(address);
            }
        } else {
            Address address = addresses.get(0);
            printAddress(address,TAG);
            broadcastAddress(address);
        }
    }

    private void broadcastAddress(Address address){
        //Log.i(TAG,surveyJson.toString());
        Intent localIntent =
                new Intent(BROADCAST_LOCATION)
                        // Puts the status into the Intent
                        .putExtra(CURRENT_ADDRESS,address);


        sendBroadcast(localIntent);
    }

    @Override
    public void onSuccess(Object location) {
        if (location != null) handleActionGetCity((Location) location);
    }

    public static void printAddress(Address address , String tag){
        if(address == null){
            Log.i(tag,"Address is null");
            return;
        }
        Log.i(tag,"Admin   Area  : " +address.getAdminArea());
        Log.i(tag,"Country Code  : " +address.getCountryCode());
        Log.i(tag,"Country Name  : " +address.getCountryName());
        Log.i(tag,"No            : " +address.getFeatureName());
        Log.i(tag,"Locality      : " +address.getLocality());
        Log.i(tag,"Sub Admin Area: " +address.getSubAdminArea());
        Log.i(tag,"Sub Locality  : " +address.getSubLocality());
        Log.i(tag,"Postal Code   : " +address.getPostalCode());
        Log.i(tag,"Premises      : " +address.getPremises());
        Log.i(tag,"Thoroughfare  : " +address.getThoroughfare());
        Log.i(tag,"Phone         : " +address.getPhone());
        Log.i(tag,"Latitude      : " +address.getLatitude());
        Log.i(tag,"Longitude     : " +address.getLongitude());
        Log.i(tag,"City          : " +address.getSubThoroughfare());
    }
}
