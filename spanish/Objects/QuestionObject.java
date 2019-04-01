package com.example.user.spanish.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class QuestionObject implements Parcelable{

    private ArrayList<String> Phrases;
    private ArrayList<String> Translations;
    private ArrayList<Boolean> IsLearnt;

    public  QuestionObject(){

    }


    public QuestionObject(ArrayList<String> phrases, ArrayList<String> translations, ArrayList<Boolean> isLearnt){
        Phrases = phrases;
        Translations = translations;
        IsLearnt = isLearnt;
    }


    protected QuestionObject(Parcel in) {
        Phrases = in.createStringArrayList();
        Translations = in.createStringArrayList();
        byte tmpIsForgotten = in.readByte();
    }

    public static final Creator<QuestionObject> CREATOR = new Creator<QuestionObject>() {
        @Override
        public QuestionObject createFromParcel(Parcel in) {
            return new QuestionObject(in);
        }

        @Override
        public QuestionObject[] newArray(int size) {
            return new QuestionObject[size];
        }
    };

    public ArrayList<String> getPhrases() {
        return Phrases;
    }

    public void setPhrases(ArrayList<String> phrases) {
        Phrases = phrases;
    }

    public ArrayList<String> getTranslations() {
        return Translations;
    }

    public void setTranslations(ArrayList<String> translations) {
        Translations = translations;
    }

    public ArrayList<Boolean> getIsLearnt() { return IsLearnt; }

    public void setIsLearnt(ArrayList<Boolean> isLearnt) { IsLearnt = isLearnt; }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeStringList(Phrases);
        parcel.writeStringList(Translations);
    }

}