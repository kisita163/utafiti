package com.kisita.utafiti.services;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.kisita.utafiti.Utafiti;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class FetchSurveyService extends JobService {

    private static final String TAG                  = "FetchSurveyService";
    public  static final String BROADCAST_SURVEY     = "com.kisita.caritas.action.BROADCAST_SURVEY";
    public  static final String CURRENT_SURVEY       = "com.kisita.caritas.action.CURRENT_SURVEY";


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"Starting Job");

        try {
            new DownloadFilesTask().execute(new URL(((Utafiti)getApplication()).getSurveyUrl()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"Ending Job");
        return false;
    }

    private class DownloadFilesTask extends AsyncTask<URL, Integer, String> {
        protected String doInBackground(URL... urls) {

            StringBuilder surveyJson = new StringBuilder();

            try {
                InputStream input = urls[0].openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line;

                while ((line = reader.readLine()) != null) {
                    //Log.d(TAG,"New line  : "+line);
                    surveyJson.append(line);
                }
            }catch(MalformedURLException e){
                Log.e(TAG,"Malformed URL");
            }catch(IOException e){
                Log.e(TAG,"IO Exception");
            }

            return surveyJson.toString();
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(String result) {
            Log.d(TAG,"Post execution is : " + result );

            //Save the survey in app directory for the next start
            saveSurveyInFile(result);
            // Update Survey App field
            Utafiti application = (Utafiti) getApplication();
            application.setCurrentSurvey(result);
            // Broadcast survey
            Intent localIntent =
                    new Intent(BROADCAST_SURVEY)
                            // Puts the status into the Intent
                            .putExtra(CURRENT_SURVEY,result);

            sendBroadcast(localIntent);
        }
    }

    private void saveSurveyInFile(String result) {
        String filename = "survey.json";
        File file = new File(getFilesDir(), filename);

        try{
            FileWriter writer = new FileWriter(file);
            writer.append(result);
            writer.flush();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();

        }

    }
}
