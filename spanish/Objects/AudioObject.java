package com.example.user.spanish.Objects;

import java.util.ArrayList;

public class AudioObject {

    private String TitleRussian;
    private String TitleSpanish;
    private String Uri;
    private ArrayList<String> Translations;
    private ArrayList<String> Words;
    private ArrayList<AudioQuestionObject> Questions;


    public AudioObject(){

    }

    public AudioObject(String titleRussian, String titleSpanish, String uri, ArrayList<String> translations, ArrayList<String> words, ArrayList<AudioQuestionObject> questions){
        TitleRussian = titleRussian;
        TitleSpanish = titleSpanish;
        Uri = uri;
        Translations = translations;
        Words = words;
        Questions = questions;
    }



    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public String getTitleRussian() {
        return TitleRussian;
    }

    public void setTitleRussian(String titleRussian) {
        TitleRussian = titleRussian;
    }

    public String getTitleSpanish() {
        return TitleSpanish;
    }

    public void setTitleSpanish(String titleSpanish) {
        TitleSpanish = titleSpanish;
    }

    public ArrayList<String> getTranslations() {
        return Translations;
    }

    public void setTranslations(ArrayList<String> translations) {
        Translations = translations;
    }

    public ArrayList<String> getWords() {
        return Words;
    }

    public void setWords(ArrayList<String> words) {
        Words = words;
    }

    public ArrayList<AudioQuestionObject> getQuestions() {
        return Questions;
    }

    public void setQuestions(ArrayList<AudioQuestionObject> questions) {
        Questions = questions;
    }
}
