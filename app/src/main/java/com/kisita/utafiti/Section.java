package com.kisita.utafiti;

import android.location.Address;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HuguesKi on 01-12-17.
 */

public class Section implements Serializable{

    private String name         = "";

    private String date         = "";

    private String investigator = "";

    private String start        = "";

    private Address address;


    private ArrayList<QuestionNew> questions = new ArrayList<>();

    public Section(String name){
        this.name = name;
    }
    public void addNewQuestion(QuestionNew question){
        if(question != null)
            this.questions.add(question);
    }

    public String getName() {
        return name;
    }

    public ArrayList<QuestionNew> getQuestions() {
        return questions;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInvestigator() {
        return investigator;
    }

    public void setInvestigator(String investigator) {
        this.investigator = investigator;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
