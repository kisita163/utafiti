package com.kisita.utafiti;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Created by HuguesKi on 01-12-17.
 */

public class Question implements Serializable {
    private static final String TAG = "Question";

    public  enum AnswerType {
        CHOICES,
        MULTIPLE_CHOICES,
        NUMERIC,
        TEXT
    }
    private boolean mandatory         = false;
    private String  question          = "";
    private ArrayList<ArrayList<String>> choices = new ArrayList<>(); // array of array
    private String choice             = "";
    private String comment            = "";
    // Current position in spinner
    private int pos                   = 0;
    // Last position selected
    private int lastPos               = 0;
    // Expected type of answer
    private AnswerType mAnswerType    = AnswerType.CHOICES;
    private String dependsOn          = "";
    private String influenceOn        = "";
    private int    choicesSet         = 0;


    public Question(String question) {
        this.question = question;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public void addChoice(ArrayList<String> choice) {
        this.choices.add(choice);
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<ArrayList<String>> getChoices() {
        return choices;
    }

    public String getChoice() {
        return choice;
    }

    public AnswerType getEntryType() {
        return mAnswerType;
    }

    public void setEntryType(String type) {
        switch (type.toLowerCase()){
            case "numeric":
                this.mAnswerType = AnswerType.NUMERIC;
                break;
            case "text":
                this.mAnswerType = AnswerType.TEXT;
                break;
            case "multiple choices":
                this.mAnswerType = AnswerType.MULTIPLE_CHOICES;
                break;
            case "choices":
                this.mAnswerType = AnswerType.CHOICES;
                break;
            default:
                this.mAnswerType = AnswerType.CHOICES;
                break;
        }
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(String dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getInfluenceOn() {
        return influenceOn;
    }

    public void setInfluenceOn(String influenceOn) {
        this.influenceOn = influenceOn;
    }

    public void setDependingQuestion(ArrayList<String> questions , ArrayList<Question> mQuestions,QuestionAdapter adapter){
        Log.i(TAG,"setDependingQuestion " + choice.toString());

        //
        //    int inf = Integer.parseInt(zz);
        //    Log.i(TAG,"zz = "+inf+ " length = "+mQuestions.size());
        //    Log.i(TAG, "item selected ... influence  on   : " + mQuestions.get(inf - 1).getQuestion() + " selected is  : " + mQuestions.get(inf - 1).getPos() );
        //d.setDependingQuestion(mQuestions.get(inf - 1),QuestionAdapter.this);
        //}
        if(!choice.equalsIgnoreCase("")){
            Log.i(TAG,"lastPos - Pos " + lastPos + " " + pos);
            if(lastPos != pos) {
                for(String zz : questions) {

                    int inf = Integer.parseInt(zz);

                    Log.i(TAG, mQuestions.get(inf - 1).getQuestion() + " " + mQuestions.get(inf - 1).getChoices().get(this.getPos() - 1));
                    mQuestions.get(inf - 1).setChoicesSet(this.getPos() - 1);
                    mQuestions.get(inf - 1).setPos(0);
                    adapter.notifyDataSetChanged();
                }
                lastPos = pos;
            }
        }
    }

    public int getChoicesSet() {
        return choicesSet;
    }

    public void setChoicesSet(int choicesSet) {
        this.choicesSet = choicesSet;
    }
}
