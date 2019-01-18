package com.kisita.utafiti;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;

import static com.kisita.utafiti.services.LocationService.startActionGetCity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends Activity {

    /** Check if the app is running. */
    private boolean isRunning;

    private static final int REQUEST_COARSE_LOCATION = 100;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        isRunning = true;

        requestPermissionsForLocalisation();
        startSplash();

    }

    /**
     * Starts the count down timer for 3-seconds. It simply sleeps the thread
     * for 3-seconds.
     */
    private void startSplash()
    {

        new Thread(new Runnable() {
            @Override
            public void run()
            {

                try
                {

                    Thread.sleep(3000);

                } catch (Exception e)
                {
                    e.printStackTrace();
                } finally
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            doFinish();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * If the app is still running than this method will start the LoginActivity
     * activity and finish the Splash.
     */
    private synchronized void doFinish()
    {

        if (isRunning)
        {
            isRunning = false;
            Intent i = new Intent(SplashScreenActivity.this,LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            isRunning = false;
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void requestPermissionsForLocalisation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
        }else{
            // get current city
            startActionGetCity(this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // get current city
                    startActionGetCity(this);
                }
            }
        }
    }
}
