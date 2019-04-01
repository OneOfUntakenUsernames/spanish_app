package com.example.user.spanish.DB;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class DictionaryObject extends RealmObject{

    @Required
    private String word;
    private String transcription;
    private String translation;

    public String getWord(){
        return word;
    }

    public void setWord(final String word){
        this.word = word;
    }

    public String getTranscription(){
        return transcription;
    }

    public void setTranscription(final String transcription){
        this.transcription = transcription;
    }

    public String getTranslation(){
        return translation;
    }

    public void setTranslation(final String translation){
        this.translation = translation;
    }

}
