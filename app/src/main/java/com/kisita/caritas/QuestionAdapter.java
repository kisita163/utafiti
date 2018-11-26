package com.kisita.caritas;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Created by HuguesKi on 01-12-17.
 */

public class QuestionAdapter extends RecyclerView.Adapter< QuestionAdapter.ViewHolder>{

    private final static String  TAG = "QuestionAdapter";
    private ArrayList<Question> mQuestions;
    private Context             mContext;

    public QuestionAdapter(Context context, ArrayList<Question> questions) {
        this.mContext   = context;
        this.mQuestions = questions;
    }

    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Question d = mQuestions.get(position);
        holder.mQuestion.setText(position + 1 + ". " + d.getQuestion());
        holder.mValues.setFocusable(false);

        if(d.isMandatory()){
            holder.mQuestion.setText(position + 1 + ". " + d.getQuestion() + " " + mContext.getString(R.string.required));
        }else{
            holder.mQuestion.setText(position + 1 + ". " + d.getQuestion());
        }

        if(d.getEntryType().equals(Question.AnswerType.CHOICES)) {
            holder.mTextInput.setVisibility(View.GONE);
            holder.mValues.setVisibility(View.VISIBLE);
            //Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, d.getChoices().get(d.getChoicesSet()));

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.mValues.setAdapter(adapter);
            holder.mValues.setSelection(d.getPos());
            holder.mValues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.i(TAG, "item selected ..." + d.getChoices().get(d.getChoicesSet()).get(i));
                    String choice  = d.getChoices().get(d.getChoicesSet()).get(i);
                    // Set the choices
                    d.setChoice(choice);
                    // Set the position of the current choice  from the choice list
                    // It will help to set properly the spinner when we create it again
                    d.setPos(i);
                    //Log.i(TAG, "item selected ... depends on : " + d.getDependsOn());
                    if(!d.getInfluenceOn().equalsIgnoreCase("")){
                        ArrayList<String> questions =
                                new ArrayList<String>(Arrays.asList(d.getInfluenceOn().split(",")));
                        d.setDependingQuestion(questions,mQuestions,QuestionAdapter.this);
                    }

                    /*if(!d.getDependsOn().equalsIgnoreCase("")){
                        int inf = Integer.valueOf(d.getDependsOn());
                        Log.i(TAG, "item selected ... The question depends on   : " + mQuestions.get(inf - 1).getQuestion() + " selected is  : " + mQuestions.get(inf - 1).getPos() );
                    }*/

                    if(choice.equalsIgnoreCase("autre")){
                        holder.mComment.setVisibility(View.VISIBLE);
                    }else{
                        holder.mComment.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

        if(d.getEntryType().equals(Question.AnswerType.TEXT)){
            holder.mValues.setVisibility(View.GONE);
            holder.mTextInput.setVisibility(View.VISIBLE);
            holder.mTextInput.setInputType(InputType.TYPE_CLASS_TEXT);
            holder.mTextInput.setText(d.getChoice());
            holder.mTextInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    //Log.i(TAG,"onTextChanged" + charSequence.toString());
                    d.setChoice(charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

        if(d.getEntryType().equals(Question.AnswerType.NUMERIC)){
            holder.mValues.setVisibility(View.GONE);
            holder.mTextInput.setVisibility(View.VISIBLE);
            holder.mTextInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            holder.mTextInput.setText(d.getChoice());
            holder.mTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Log.i(TAG,"onTextChanged" + charSequence.toString());
                d.setChoice(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        }

        holder.mComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                d.setComment(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if(mQuestions == null)
            return 0;
        //Log.i(TAG,"Questions size is  : " + mQuestions.size() );
        return mQuestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mQuestion;
        public final EditText mTextInput;
        public final AppCompatSpinner mValues;
        public final EditText mComment;


        public ViewHolder(View view) {
            super(view);
            mView      = view;
            mQuestion  = (TextView) view.findViewById(R.id.question);
            mValues    = (AppCompatSpinner)view.findViewById(R.id.values);
            mTextInput = (EditText) view.findViewById(R.id.text_input);
            mComment   = (EditText) view.findViewById(R.id.text_comment);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mQuestion.getText() + "'";
        }
    }
}
