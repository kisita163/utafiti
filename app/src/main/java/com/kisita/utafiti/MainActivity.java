package com.kisita.utafiti;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kisita.utafiti.interfaces.OnUtafitiEventReceived;
import com.kisita.utafiti.services.UtafitiReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.kisita.utafiti.InvestigatorFragment.getToday;
import static com.kisita.utafiti.services.FetchSurveyService.BROADCAST_SURVEY;
import static com.kisita.utafiti.services.LocationService.startActionGetCity;


public class MainActivity extends AppCompatActivity implements PublishFragment.OnPublishInteractionListener, BottomNavigationView.OnNavigationItemSelectedListener, OnUtafitiEventReceived, Serializable {

    private final static String TAG                  = "MainActivity";
    private final static String SECTIONS             = "sections";
    private String mSurveyTitle                      = "";
    private static final int REQUEST_COARSE_LOCATION = 100;
    private UtafitiReceiver mSurveyReceiver;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private ArrayList<Section> mSections =  new ArrayList<>();

    /* UI button
     */


    private SectionPagerAdapter mSectionPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissionsForLocalisation();
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        //
        if(savedInstanceState != null){
            mSections = (ArrayList<Section>) savedInstanceState.getSerializable(SECTIONS);
        }else{
            //populateSections();
            populateSectionsNew();
        }
        // Create the adapter that will return a fragment for each section of the survey
        mSectionPagerAdapter = new SectionPagerAdapter(this,mSections);
        mViewPager.setAdapter(mSectionPagerAdapter);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        mSurveyReceiver = new UtafitiReceiver(this);

        registerReceiver(mSurveyReceiver, new IntentFilter(BROADCAST_SURVEY));
        setStartTime();
    }

    private void populateSectionsNew() {
        JSONArray jsonSurvey   ;
        JSONArray jsonQuestions;
        JSONObject section     ;
        JSONObject question    ;
        Section sec;
        //
        try {
            Utafiti application = (Utafiti) getApplication();

            JSONObject surveyParser = new JSONObject(application.getCurrentSurvey());

            setSurveyPreference(surveyParser.getString("name"));

            // First section
            Section first = new Section(mSurveyTitle);
            mSections.add(first);

            jsonSurvey = surveyParser.getJSONArray("sections");
            for (int i = 0; i < jsonSurvey.length(); i++) {
                // Get section
                section = jsonSurvey.getJSONObject(i);
                Log.i(TAG,"Section name is : "+ section.getString("name"));
                sec     = new Section(section.getString("name"));

                jsonQuestions = section.getJSONArray("questions");

                QuestionNew  q;
                for(int k = 0 ; k < jsonQuestions.length() ; k++){
                    question = jsonQuestions.getJSONObject(k);
                    // Get Question object
                    q  = new QuestionNew("");

                    q.setQuestionId(question.getString("id"));
                    q.setQuestionText(question.getString("question_text"));
                    q.setMandatory(Boolean.parseBoolean(question.getString("mandatory")));

                    JSONArray jsonAnswers = question.getJSONArray("answers");
                    ArrayList<Answer> answers = new ArrayList<>();
                    for(int l = 0 ; l < jsonAnswers.length() ; l++){
                        Answer answer = new Answer();
                        JSONObject a = jsonAnswers.getJSONObject(l);

                        answer.setId(a.getString("id"));
                        answer.setAnswerType(a.getString("type"));
                        answer.setAnswerLabel(a.getString("label"));

                        JSONArray c = a.getJSONArray("choices"); //Array of choices
                        ArrayList<String> choices = new ArrayList<>();
                        for(int m = 0 ; m < c.length() ; m++){
                            String choice = c.getString(m);
                            choices.add(choice);
                            Log.i(TAG,choice);
                        }
                        answer.setAnswerChoices(choices);
                        answers.add(answer);
                        Log.i(TAG,a.getString("id"));
                        Log.i(TAG,a.getString("type"));
                        Log.i(TAG,a.getString("label"));
                    }

                    Log.i(TAG,question.getString("mandatory"));
                    Log.i(TAG,question.getString("id"));
                    Log.i(TAG,question.getString("question_text"));

                    q.setAnswers(answers);
                    sec.addNewQuestion(q);
                }
                mSections.add(sec);
            }
        }catch (JSONException e){
            // If survey.json is not valid, the first section need to be populated
            Section first = new Section(mSurveyTitle);
            mSections.add(first);
            // Start FetchSurveyService
            e.printStackTrace();
        }
        //printSections();
    }

    private void setStartTime() {
        Log.i(TAG,"setStartDateTime");
        SharedPreferences sharedPref = getSharedPreferences(getResources()
                .getString(R.string.caritas_keys), Context.MODE_PRIVATE);
        String time = getToday(false);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.start_time_key), time);
        editor.apply();
    }

    private void setSurveyPreference(String name) {

        SharedPreferences sharedPref = getSharedPreferences(getResources()
                .getString(R.string.caritas_keys), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.survey_title_key), name);
        editor.apply();

    }

    /*public void printSections(){
        for(Section s : mSections){
            Log.d(TAG,s.getName());
            for(Question q : s.getQuestions()){
                Log.d(TAG,q.getQuestion());
                for (ArrayList c : q.getChoices()){
                    //Log.d(TAG,c);
                }
            }
        }
    }


    public void printFinalSections(){
        for(Section s : mSections){
            Log.d(TAG,s.getName());
            for(Question q : s.getQuestions()){
                Log.d(TAG,q.getQuestion());
                Log.d(TAG,"-->" + q.getChoice());
            }
        }
    }*/

    @Override
    public void onPublishInteraction(String endTime) {
        //Log.i(TAG,"Publish pressed");
        //printFinalSections();
        //
        final Fragment publish = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());
        ((PublishFragment)publish).showProgress(true);
        if(!checkRequiredFields()){
            ((PublishFragment)publish).showProgress(false);
            return;
        }
        String key = getDb(((Utafiti)getApplication()).getDbName()).push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        int i = 1;
        int j = 1;

        for(Section s : mSections){
            if(s.getName().equalsIgnoreCase(mSurveyTitle)){
                childUpdates.put(getUid() + "/" + key + "/section_"+j+"/startTime",s.getStart());
                childUpdates.put(getUid() + "/" + key + "/section_"+j+"/endTime",endTime);
                childUpdates.put(getUid() + "/" + key + "/section_"+j+"/date",s.getDate());
                childUpdates.put(getUid() + "/" + key + "/section_"+j+"/investigator",s.getInvestigator());

                if ( mSections.get(0).getAddress() == null ){

                    childUpdates.put(getUid() + "/" + key + "/section_"+j+"/address/country","Unknown");
                    childUpdates.put(getUid() + "/" + key + "/section_"+j+"/address/city","Unknown");
                    childUpdates.put(getUid() + "/" + key + "/section_"+j+"/address/postal_code","9999");
                    childUpdates.put(getUid() + "/" + key + "/section_"+j+"/address/latitude","0.0000000");
                    childUpdates.put(getUid() + "/" + key + "/section_"+j+"/address/longitude","0.0000000");

                }else{
                    childUpdates.put(getUid() + "/" + key + "/section_"+j+"/address/country",mSections.get(0).getAddress().getCountryName());
                    childUpdates.put(getUid() + "/" + key + "/section_"+j+"/address/city",mSections.get(0).getAddress().getAdminArea());
                    childUpdates.put(getUid() + "/" + key + "/section_"+j+"/address/postal_code",mSections.get(0).getAddress().getPostalCode());
                    childUpdates.put(getUid() + "/" + key + "/section_"+j+"/address/latitude",mSections.get(0).getAddress().getLatitude());
                    childUpdates.put(getUid() + "/" + key + "/section_"+j+"/address/longitude",mSections.get(0).getAddress().getLongitude());
                }
            }
            childUpdates.put(getUid() + "/" + key + "/section_"+j+"/name",s.getName());
            for(QuestionNew q : s.getQuestions()){
                childUpdates.put(getUid() + "/" + key +  "/section_"+j+"/question"+ i +"/question" , q.getQuestionText());
                for(Answer answer:q.getAnswers()) {
                    childUpdates.put(getUid() + "/" + key + "/section_" + j + "/question" + i + "/responses/" + answer.getAnswerLabel(), answer.getChoice());
                }
                if(!q.getComment().equalsIgnoreCase("")){
                    childUpdates.put(getUid() + "/" + key +  "/section_"+j+"/question"+ i +"/comment" , q.getComment());
                }
                i++;
            }
            j++;
        }
        getDb(((Utafiti)getApplication()).getDbName()).updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, R.string.survey_failed,
                        Toast.LENGTH_LONG).show();
                ((PublishFragment)publish).showProgress(false);
                //Log.i(TAG,"Transactions are not persisted across app restarts");
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setStartTime();
                mViewPager.setCurrentItem(0);
                clearSurveyAnswers();
                ((PublishFragment)publish).showProgress(false);
                Toast.makeText(MainActivity.this, R.string.survey_published,
                        Toast.LENGTH_LONG).show();
            }
        }, 3000);
    }

    public static DatabaseReference getDb(String reference) {
        return FirebaseDatabase.getInstance().getReference(reference);
    }

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int i;
        mViewPager.setEnabled(false);
        switch (item.getItemId()) {
            case R.id.navigation_home:
                mViewPager.setCurrentItem(0);
                //Log.i(TAG,"Home selected...");
                return true;
            case R.id.navigation_previous:
                i = mViewPager.getCurrentItem();
                mViewPager.setCurrentItem(i-1);
                //Log.i(TAG,"questions selected...");
                return true;
            case R.id.navigation_next:
                switchRight();
                return true;
            case R.id.navigation_publish:
                mViewPager.setCurrentItem(mSections.size()+1);
                //Log.i(TAG,"notifications selected...");
                return true;
        }
        return false;
    }

    public void switchRight(){
        int i = mViewPager.getCurrentItem(); // i give the section

        if(checkRequiredFieldsInSection(i)){
            mViewPager.setCurrentItem(i+1);
        }
    }

    public void clearSurveyAnswers(){
        for(int index  = 1 ; index < mSections.size() ; index++ ) {
            for(Question q  : mSections.get(index).getQuestions()){
                q.setChoice("");
                q.setPos(0);
            }
        }
        mSectionPagerAdapter.notifyDataSetChanged();
    }
    public boolean checkRequiredFields(){
        for(int index  = 0 ; index < mSections.size() ; index++ ){
           if(!checkRequiredFieldsInSection(index)){
               return false;
           }
        }
        return true;
    }

    public boolean checkRequiredFieldsInSection(int index){
        //Log.i(TAG,"index is  : "+index + " " + (mSections.size() + 1));
        if(index == 0){
            Log.i(TAG,"Date is  : "+mSections.get(index).getDate());
            Log.i(TAG,"Start is  : "+mSections.get(index).getStart());
            Log.i(TAG,"Location is  : "+mSections.get(index).getAddress());
            Log.i(TAG,"investigator is  : "+mSections.get(index).getInvestigator());

            if(mSections.get(index).getInvestigator().equalsIgnoreCase("")){
                Toast.makeText(MainActivity.this, R.string.mandatory_fields,
                        Toast.LENGTH_LONG).show();
                mViewPager.setCurrentItem(index);
                return false;
            }
        }
        if(index > 0 && index < mSections.size()){
            //Log.i(TAG,"Section size is  : "+mSections.size());
            for(QuestionNew q  : mSections.get(index).getQuestions()){
                //Log.i(TAG,"Question : "+q.getQuestion()+" - choice is  : " + q.getChoice() + "***"+ q.getChoice().length());

                if(q.isMandatory()) {
                    for (int k = 0; k < q.getAnswers().size(); k++) {
                        if(q.getAnswers().get(k).getChoice().isEmpty()){
                            Toast.makeText(MainActivity.this, R.string.mandatory_fields,
                                    Toast.LENGTH_LONG).show();
                            mViewPager.setCurrentItem(index);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //Log.i(TAG,"onSaveInstanceState");
        savedInstanceState.putSerializable(SECTIONS,mSections);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mSurveyReceiver);
        super.onStop();
    }

    @Override
    public void onSurveyReceived() {
        mSections.clear();
        //populateSections();
        populateSectionsNew();
        mSectionPagerAdapter.notifyDataSetChanged();

        setStartTime();
        mViewPager.setCurrentItem(0);
        Toast.makeText(MainActivity.this, R.string.survey_updated,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocalisationReceived(Address address) {

    }

    public ArrayList<Section> getSections() {
        return mSections;
    }

    private void requestPermissionsForLocalisation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION: {
                Log.d(TAG,"Permission granted...");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // get current address
                    startActionGetCity(this);
                }
            }
        }
    }
}
