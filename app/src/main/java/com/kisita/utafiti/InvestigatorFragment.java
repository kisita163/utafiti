package com.kisita.utafiti;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.kisita.utafiti.interfaces.OnUtafitiEventReceived;
import com.kisita.utafiti.services.UtafitiReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import static com.kisita.utafiti.services.LocationService.BROADCAST_LOCATION;
import static com.kisita.utafiti.services.LocationService.startActionGetCity;


public class InvestigatorFragment extends Fragment implements OnUtafitiEventReceived {

    /* UI references */

    private EditText mDate;

    private EditText mStartTime;

    private static final String TAG  = "InvestigatorFragment";

    private String date;

    private String start;

    private Address mAddress;

    private EditText mLocation;

    private UtafitiReceiver mReceiver;

    public InvestigatorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InvestigatorFragment.
     */
    public static InvestigatorFragment newInstance() {
        return new InvestigatorFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,"onCreateView  ");
        final MainActivity activity = (MainActivity) getActivity();

        View v = inflater.inflate(R.layout.fragment_investigator, container, false);

        mDate         = v.findViewById(R.id.now);
        EditText mInvestigator = v.findViewById(R.id.investigator);
        mStartTime    = v.findViewById(R.id.start_time);
        mLocation = v.findViewById(R.id.location);

        SharedPreferences sharedPref = getActivity()
                .getSharedPreferences(getResources()
                        .getString(R.string.caritas_keys), Context.MODE_PRIVATE);

        date  = getToday(true);
        start = sharedPref.getString(getResources().getString(R.string.start_time_key),"");

        if (mAddress != null)
            mLocation.setText(mAddress.getCountryName());
        else
            mLocation.setText(getString(R.string.undefined));
        mLocation.setEnabled(false);

        mDate.setText(date); // get the date
        mDate.setEnabled(false);
        activity.getSections().get(0).setDate(date);


        activity.getSections().get(0).setStart(start);
        Log.d(TAG,"Section Start time is  : " +  activity.getSections().get(0).getStart());

        mStartTime.setText(start);
        mStartTime.setEnabled(false);

        String investigator = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        mInvestigator.setText(investigator);

        activity.getSections().get(0).setInvestigator(investigator);

        mInvestigator.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                activity.getSections().get(0).setInvestigator(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // get current city
        startActionGetCity(getContext());

         mReceiver = new UtafitiReceiver(this);

        getActivity().registerReceiver(mReceiver, new IntentFilter(BROADCAST_LOCATION));

        return v;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getToday(boolean date){
        Date presentTime_Date = Calendar.getInstance().getTime();

        SimpleDateFormat dateFormat;

        if(date){
            dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        }
        else{
            dateFormat = new SimpleDateFormat("HH:mm:ss");
        }

        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        return dateFormat.format(presentTime_Date);
    }

    @Override
    public void onResume() {
        super.onResume();
        mStartTime.setText(start);
        mDate.setText(date);
    }

    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroyView();
    }

    @Override
    public void onSurveyReceived() {

    }

    @Override
    public void onLocalisationReceived(Address address) {
        mAddress = address;
        ((MainActivity)getActivity()).getSections().get(0).setAddress(mAddress);
        mLocation.setText(mAddress.getCountryName());
    }
}
