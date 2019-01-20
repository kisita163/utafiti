package com.kisita.utafiti;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.BufferedReader;
import java.io.FileReader;


import static com.kisita.utafiti.services.SurveyService.scheduleJob;

public class Utafiti extends Application {

    private static final String SURVEY_URL           = "https://firebasestorage.googleapis.com/v0/b/caritas-50fab.appspot.com/o/current_survey%2Fsurvey.json?alt=media";
    private static final String SURVEY_URL_TEST      = "https://firebasestorage.googleapis.com/v0/b/caritas-50fab.appspot.com/o/current_survey_test%2Fsurvey.json?alt=media";
    private static String TAG     = "Utafiti";
    private String currentSurvey  = "";
    private String dbName;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getSurveyUrl() {
        return surveyUrl;
    }

    public void setSurveyUrl(String surveyUrl) {
        this.surveyUrl = surveyUrl;
    }

    private String topicName;
    private String surveyUrl;

    public String getCurrentSurvey() {
        return currentSurvey;
    }

    @Override
    public void onCreate() {
        initFirebaseFeatures();

        readSurvey();
        super.onCreate();
    }

    private void initFirebaseFeatures() {
        Log.d(TAG,"Initiating firebase");
        // Enabling disk persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Subscription sur survey topic
        if (BuildConfig.DEBUG) {
            setSurveyUrl(SURVEY_URL_TEST);
            setTopicName("surveyTest");
            setDbName("surveyTest");
        }else{
            setSurveyUrl(SURVEY_URL);
            setTopicName("survey");
            setDbName("survey");
        }

        FirebaseMessaging.getInstance().subscribeToTopic(getTopicName())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                    }
                });
    }

    private void readSurvey() {

        String filename  = getFilesDir() + "/survey.json";
        StringBuilder surveyJson = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.d(TAG,"New line  : "+line);
                surveyJson.append(line);
            }
        } catch (Exception e) {
            Log.e(TAG,"Unable to open the survey file");
            scheduleJob(this);
            e.printStackTrace();
        }

        currentSurvey =  surveyJson.toString();
    }

    public void setCurrentSurvey(String survey) {
        this.currentSurvey = survey;
    }
}
