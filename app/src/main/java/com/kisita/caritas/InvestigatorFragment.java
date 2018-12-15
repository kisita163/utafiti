package com.kisita.caritas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnInvestigatorInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InvestigatorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvestigatorFragment extends Fragment {

    /* UI references */

    private EditText mDate;

    private EditText mStartTime;

    private EditText mInvestigator;

    private EditText mLocation;

    private static final String ARG_SECTION = "section";

    private static final String TAG         = "InvestigatorFragment";

    private OnInvestigatorInteractionListener mListener;

    private String date;

    private String localAddress;

    private String start;

    public InvestigatorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InvestigatorFragment.
     */
    public static InvestigatorFragment newInstance(Section sec) {
        InvestigatorFragment fragment = new InvestigatorFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SECTION,(Serializable) sec);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG,"onCreateView  ");
        final Section section = (Section) getArguments().getSerializable(ARG_SECTION);
        View v = inflater.inflate(R.layout.fragment_investigator, container, false);

        mDate         = v.findViewById(R.id.now);
        mInvestigator = v.findViewById(R.id.investigator);
        mStartTime    = v.findViewById(R.id.start_time);
        mLocation     = v.findViewById(R.id.location);

        SharedPreferences sharedPref = getActivity()
                .getSharedPreferences(getResources()
                        .getString(R.string.caritas_keys), Context.MODE_PRIVATE);

        date         = sharedPref.getString(getResources().getString(R.string.start_date_key),"");
        start        = sharedPref.getString(getResources().getString(R.string.start_time_key),"");
        localAddress = sharedPref.getString(getResources().getString(R.string.address_key),"null");

        //Log.i(TAG,"" + date + " " + start);

        mLocation.setText(localAddress);
        mLocation.setEnabled(false);
        section.setLocation(localAddress);

        mDate.setText(date); // get the date
        mDate.setEnabled(false);
        section.setDate(date);


        section.setStart(start);
        Log.i(TAG,"Section Start time is  : " + section.getStart());
        //Log.i(TAG,"Start time is  : " + start);
        mStartTime.setText(start);
        mStartTime.setEnabled(false);

        String investigator = sharedPref.getString(getString(R.string.investigator),"");
        mInvestigator.setText(investigator);
        //TODO get investigator name fro firebase

        section.setInvestigator(investigator);

        final SharedPreferences.Editor editor = sharedPref.edit();

        mInvestigator.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                section.setInvestigator(charSequence.toString());
                editor.putString(getString(R.string.investigator), charSequence.toString());
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return v;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onInvestigatorInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnInvestigatorInteractionListener) {
            mListener = (OnInvestigatorInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInvestigatorInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnInvestigatorInteractionListener {
        void onInvestigatorInteraction();
    }

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

}
