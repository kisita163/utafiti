package com.kisita.utafiti.interfaces;

import android.location.Address;

public interface OnUtafitiEventReceived {
    void onSurveyReceived();
    void onLocalisationReceived(Address address);
}
