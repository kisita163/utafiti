package com.kisita.utafiti;


import java.util.ArrayList;

class Answer {
    private QuestionNew.AnswerType mAnswerType    = QuestionNew.AnswerType.CHOICES;
    private String id;
    private String mAnswerLabel;
    private ArrayList<String> mAnswerChoices = new ArrayList<>();
    private String choice = "";
    private int choicePos = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public QuestionNew.AnswerType getAnswerType() {
        return mAnswerType;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public int getChoicePos() {
        return choicePos;
    }

    public void setChoicePos(int choicePos) {
        this.choicePos = choicePos;
    }

    public void setAnswerType(String mAnswerType) {
        int type = Integer.parseInt(mAnswerType);
        switch (type){
            case 2:
                this.mAnswerType = QuestionNew.AnswerType.INTEGER;
                break;
            case 3:
                this.mAnswerType = QuestionNew.AnswerType.TEXT;
                break;
            case 1:
                this.mAnswerType = QuestionNew.AnswerType.MULTIPLE_CHOICES;
                break;
            case 0:
                this.mAnswerType = QuestionNew.AnswerType.CHOICES;
                break;
            case 4:
                this.mAnswerType = QuestionNew.AnswerType.DECIMAL;
                break;
            default:
                this.mAnswerType = QuestionNew.AnswerType.TEXT;
                break;
        }
        Integer.parseInt(mAnswerType);
    }

    public String getAnswerLabel() {
        return mAnswerLabel;
    }

    public void setAnswerLabel(String mAnswerLabel) {
        this.mAnswerLabel = mAnswerLabel;
    }

    public ArrayList<String> getAnswerChoices() {
        return mAnswerChoices;
    }

    public void setAnswerChoices(ArrayList<String> mAnswerChoices) {
        this.mAnswerChoices = mAnswerChoices;
    }
}
