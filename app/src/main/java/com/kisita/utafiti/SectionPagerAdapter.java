package com.kisita.utafiti;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by HuguesKi on 01-12-17.
 *
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */

public class SectionPagerAdapter extends FragmentPagerAdapter {
    private final static String TAG = "SectionPagerAdapter";
    /**
     JSON Array holding the survey
     */
    private ArrayList<Section> survey;

    private MainActivity activity;

    SectionPagerAdapter(MainActivity activity, ArrayList<Section> survey) {
        super(activity.getSupportFragmentManager());
        this.survey   = survey;
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Log.i(TAG,"position is  : " + position);
        if(position == 0 ){
            return InvestigatorFragment.newInstance();
        }
        if(position == (survey.size())) {
            return PublishFragment.newInstance();
        }
        return PlaceholderFragment.newInstance(position);
    }

    @Override
    public int getItemPosition(Object object) {
        Log.d(TAG,"getItemPosition");
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        if(survey == null)
            return 0;
        return (survey.size() + 1); // publish button
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
            case 2:
                return "SECTION 3";
        }
        return null;
    }
}
