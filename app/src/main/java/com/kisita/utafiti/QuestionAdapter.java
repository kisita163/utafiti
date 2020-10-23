package com.kisita.utafiti;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;

/*
 * Created by HuguesKi on 01-12-17.
 */

public class QuestionAdapter extends RecyclerView.Adapter< QuestionAdapter.ViewHolder>{

    private final static String  TAG = "QuestionAdapter";
    private ArrayList<QuestionNew> mQuestions;
    private Context             mContext;

    public QuestionAdapter(Context context, ArrayList<QuestionNew> questions) {
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
        final QuestionNew d = mQuestions.get(position);

        Log.i(TAG,d.getAnswers().get(0).getAnswerType().toString());

        if(d.isMandatory()){
            holder.mQuestion.setText(position + 1 + ". " + d.getQuestionText()+ " *");
        }else{
            holder.mQuestion.setText(position + 1 + ". " + d.getQuestionText());
        }

        holder.mAnswers.setRowCount(d.getAnswers().size());

        for (int k=0; k < d.getAnswers().size(); k++) {
            TextView label = new TextView(mContext);
            final Answer answer = d.getAnswers().get(k);
            label.setText(answer.getAnswerLabel());
            label.setTextAppearance(R.style.simpleTextLabel);
            GridLayout.LayoutParams paramsLabel = new GridLayout.LayoutParams( GridLayout.spec(k, GridLayout.CENTER),
                    GridLayout.spec(0, GridLayout.LEFT));
            paramsLabel.topMargin = 5;

            holder.mAnswers.addView(label,new GridLayout.LayoutParams(paramsLabel));

            if (answer.getAnswerType().equals(QuestionNew.AnswerType.CHOICES) ||
                    answer.getAnswerType().equals(QuestionNew.AnswerType.MULTIPLE_CHOICES)){
                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_dropdown_item_1line,answer.getAnswerChoices());
                MaterialBetterSpinner spinner = new MaterialBetterSpinner(mContext);
                spinner.setHint(R.string.press_here_to_choice);
                spinner.setTextSize(15);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams( GridLayout.spec(k, GridLayout.RIGHT),
                        GridLayout.spec(1, GridLayout.RIGHT));
                spinner.setTextAppearance(R.style.simpleTextValue);
                spinner.setAdapter(adapter);
                params.width = GridLayout.LayoutParams.MATCH_PARENT;
                params.topMargin = 20;
                params.bottomMargin = 20;
                params.leftMargin = 30;
                holder.mAnswers.addView(spinner,params);

                spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Log.i(TAG, "item selected ..." + d.getChoices().get(d.getChoicesSet()).get(position));
                        String choice = answer.getAnswerChoices().get(position);
                        Log.i(TAG, "item selected ..." + answer.getAnswerChoices().get(position));
                        answer.setChoice(choice);
                        answer.setChoicePos(position);
                    }
                });
            }

            if (answer.getAnswerType().equals(QuestionNew.AnswerType.TEXT) ||
                    answer.getAnswerType().equals(QuestionNew.AnswerType.INTEGER) ||
                    answer.getAnswerType().equals(QuestionNew.AnswerType.DECIMAL)){
                EditText editText= new EditText(mContext);

                if (answer.getAnswerType().equals(QuestionNew.AnswerType.INTEGER)){
                    editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                }

                if (answer.getAnswerType().equals(QuestionNew.AnswerType.DECIMAL)){
                    editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
                }
                editText.setTextSize(15);
                editText.setTextAppearance(R.style.simpleTextValue);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams( GridLayout.spec(k, GridLayout.RIGHT),
                        GridLayout.spec(1, GridLayout.RIGHT));
                params.width = GridLayout.LayoutParams.MATCH_PARENT;
                params.topMargin = 20;
                params.bottomMargin = 20;
                params.leftMargin = 30;
                holder.mAnswers.addView(editText,params);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Log.i(TAG,"onTextChanged" + s.toString());
                        answer.setChoice(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }
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
        public final GridLayout mAnswers;

        public ViewHolder(View view) {
            super(view);
            mView      = view;
            mQuestion  = (TextView) view.findViewById(R.id.question);
            mAnswers = view.findViewById(R.id.answers);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mQuestion.getText() + "'";
        }
    }
}
