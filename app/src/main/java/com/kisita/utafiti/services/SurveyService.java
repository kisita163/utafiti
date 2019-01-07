package com.kisita.utafiti.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SurveyService extends Service {
    public SurveyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
