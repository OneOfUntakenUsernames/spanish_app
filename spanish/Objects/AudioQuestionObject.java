package com.example.user.spanish.Objects;

import android.app.DownloadManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class AudioQuestionObject implements Parcelable{

    private ArrayList<String> Answers;
    private String Correct;
    private String Question;

    public AudioQuestionObject(){

    }

    public AudioQuestionObject(ArrayList<String> answers, String correct, String question){
        Answers = answers;
        Correct = correct;
        Question = question;
    }

    protected AudioQuestionObject(Parcel in) {
        Answers = in.createStringArrayList();
        Correct = in.readString();
        Question = in.readString();
    }

    public static final Creator<AudioQuestionObject> CREATOR = new Creator<AudioQuestionObject>() {
        @Override
        public AudioQuestionObject createFromParcel(Parcel in) {
            return new AudioQuestionObject(in);
        }

        @Override
        public AudioQuestionObject[] newArray(int size) {
            return new AudioQuestionObject[size];
        }
    };

    public ArrayList<String> getAnswers() {
        return Answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        Answers = answers;
    }

    public String getCorrect() {
        return Correct;
    }

    public void setCorrect(String correct) {
        Correct = correct;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(Answers);
        parcel.writeString(Correct);
        parcel.writeString(Question);
    }
}
