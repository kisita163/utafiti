package com.kisita.caritas;

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

    private String location     = "";


    private ArrayList<Question> questions = new ArrayList<>();

    public Section(String name){
        this.name = name;
    }
    public void addNewQuestion(Question question){
        if(question != null)
            this.questions.add(question);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Question> getQuestions() {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
