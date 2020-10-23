package com.kisita.utafiti;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * Created by HuguesKi on 01-12-17.
 */

public class QuestionNew extends Question  {
    private static final String TAG = "Question";

    public QuestionNew(String question) {
        super(question);
    }

    public  enum AnswerType {
        CHOICES,
        MULTIPLE_CHOICES,
        INTEGER,
        TEXT,
        DECIMAL
    }
    private boolean mandatory         = false;
    private String  questionId        = "";
    private String  questionText      = "";
    private ArrayList<Answer> answers = new ArrayList<>();

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
}
