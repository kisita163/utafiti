package com.kisita.utafiti;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HuguesKi on 01-12-17.
 *
 * A placeholder fragment containing a simple view.
 */

public class PlaceholderFragment extends Fragment {

    private  static final String TAG = "PlaceholderFragment";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_INDEX    = "index";

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     */
    public static PlaceholderFragment newInstance(int index) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_INDEX, index);
            fragment.setArguments(args);
            return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPref = getActivity()
                .getSharedPreferences(getResources()
                        .getString(R.string.caritas_keys), Context.MODE_PRIVATE);

        int index = getArguments().getInt(ARG_INDEX);
        Section sectionQuestions = ((MainActivity)getActivity()).getSections().get(index);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recList = rootView.findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        StaggeredGridLayoutManager llm = new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.VERTICAL);

        recList.setLayoutManager(llm);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recList.getContext(),
                DividerItemDecoration.VERTICAL);
        recList.addItemDecoration(dividerItemDecoration);

        QuestionAdapter adapter = new QuestionAdapter(getContext(),sectionQuestions.getQuestions());
        recList.setAdapter(adapter);

        printQuestions(sectionQuestions.getQuestions());

        TextView textView =  rootView.findViewById(R.id.section_label);
        textView.setText(sharedPref.getString(getResources().getString(R.string.survey_title_key),""));

        return rootView;
    }

    private void printQuestions(ArrayList<QuestionNew> questions){
        for(Question q : questions){
            Log.d(TAG,q.getQuestion());
        }
    }
}
