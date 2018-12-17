package com.kisita.utafiti;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CurrentSurveyService extends IntentService {

    private static final String ACTION_FETCH_SURVEY  = "com.kisita.caritas.action.FETCH_SURVEY";

    public  static final String BROADCAST_SURVEY     = "com.kisita.caritas.action.BROADCAST_SURVEY";

    public  static final String CURRENT_SURVEY       = "com.kisita.caritas.action.CURRENT_SURVEY";

    private static final String TAG                  = "CurrentSurveyService";

    private static final String SURVEY_URL           = "https://firebasestorage.googleapis.com/v0/b/caritas-50fab.appspot.com/o/current_survey%2Fsurvey.json?alt=media";

    public CurrentSurveyService() {
        super("CurrentSurveyService");
    }

    /**
     * Starts this service to perform action FETCH_SURVEY with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFetchSurvey(Context context) {

        Intent intent = new Intent(context, CurrentSurveyService.class);
        intent.setAction(ACTION_FETCH_SURVEY);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_SURVEY.equals(action)) {
                handleActionFetchSurvey();
            }
        }
    }

    /**
     * Handle action FetchSurvey in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFetchSurvey() {
        try {
            StringBuilder surveyJson = new StringBuilder();
            InputStream input = new URL(SURVEY_URL).openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            //Log.i(TAG,"Reading line ...");
            while ((line = reader.readLine()) != null) {
                //Log.i(TAG,"New line  : "+line);
                surveyJson.append(line);
            }
            //Log.i(TAG,surveyJson.toString());
            Intent localIntent =
                    new Intent(BROADCAST_SURVEY)
                            // Puts the status into the Intent
                            .putExtra(CURRENT_SURVEY,surveyJson.toString());

            sendBroadcast(localIntent);
        } catch (IOException e) {
            //Log.e(TAG,"Unable to load the most recent survey\n" + e.getMessage() );
        }
    }
}
