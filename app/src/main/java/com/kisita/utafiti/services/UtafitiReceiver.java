package com.kisita.utafiti.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.util.Log;

import com.kisita.utafiti.interfaces.OnUtafitiEventReceived;

import static com.kisita.utafiti.services.FetchSurveyService.CURRENT_SURVEY;
import static com.kisita.utafiti.services.LocationService.CURRENT_ADDRESS;

public class UtafitiReceiver extends BroadcastReceiver {

    private static String TAG       = "UtafitiReceiver";
    private OnUtafitiEventReceived  mListener;

    public UtafitiReceiver(OnUtafitiEventReceived listener) {
        this.mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String survey    = intent.getStringExtra(CURRENT_SURVEY);

        if (survey != null) {
            Log.d(TAG, "New survey received here : \n" + survey);
            if(mListener != null){
                mListener.onSurveyReceived();
            }
        }

        Address address =  intent.getParcelableExtra(CURRENT_ADDRESS);

        if (address != null){
            Log.d(TAG, "New location received here : \n" + address.getCountryName());
            if(mListener != null){
                mListener.onLocalisationReceived(address);
            }
        }
    }
}
