package com.example.user.spanish.Objects;

public class WordObject {

    private String Word;
    private String Translation;
    private Boolean IsLearntWT;
    private Boolean IsLearntTW;
    private Boolean IsLearntCT;
    private Boolean IsLearntMT;


    public  WordObject(){

    }


    public WordObject(String word, String translation, Boolean isLearntWT, Boolean isLearntTW, Boolean isLearntCT, Boolean isLearntMT){
        this.Word = word;
        this.Translation = translation;
        IsLearntWT = isLearntWT;
        IsLearntTW = isLearntTW;
        IsLearntCT = isLearntCT;
        IsLearntMT = isLearntMT;
    }

    public String getWord(){
        return Word;
    }

    public void setWord(String word){
        this.Word = word;
    }

    public String getTranslation(){
        return Translation;
    }

    public void setTranslation(String translation){
        this.Translation = translation;
    }

    public Boolean getIsLearntWT() {
        return IsLearntWT;
    }

    public void setIsLearntWT(Boolean isLearntWT) {
        IsLearntWT = isLearntWT;
    }

    public Boolean getIsLearntTW() {
        return IsLearntTW;
    }

    public void setIsLearntTW(Boolean isLearntTW) {
        IsLearntTW = isLearntTW;
    }

    public Boolean getIsLearntCT() {
        return IsLearntCT;
    }

    public void setIsLearntCT(Boolean isLearntCT) {
        IsLearntCT = isLearntCT;
    }

    public Boolean getIsLearntMT() {
        return IsLearntMT;
    }

    public void setIsLearntMT(Boolean isLearntMT) {
        IsLearntMT = isLearntMT;
    }
}
