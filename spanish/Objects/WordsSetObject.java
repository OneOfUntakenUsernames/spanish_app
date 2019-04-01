package com.example.user.spanish.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class WordsSetObject implements Parcelable {

    private ArrayList<String> Words;
    private ArrayList<String> Translations;
    private String Name;
    private Integer Count;
    private Boolean IsAdded;
    private String Image;

    public WordsSetObject(){

    }

    public WordsSetObject(ArrayList<String> words, ArrayList<String> translations, String name, Integer count, Boolean isAdded, String image) {
        Words = words;
        Translations = translations;
        Name = name;
        Count = count;
        IsAdded = isAdded;
        Image = image;
    }

    protected WordsSetObject(Parcel in) {
        Words = in.createStringArrayList();
        Translations = in.createStringArrayList();
        Name = in.readString();
        if (in.readByte() == 0) {
            Count = null;
        } else {
            Count = in.readInt();
        }
        byte tmpIsAdded = in.readByte();
        IsAdded = tmpIsAdded == 0 ? null : tmpIsAdded == 1;
        Image = in.readString();
    }

    public static final Creator<WordsSetObject> CREATOR = new Creator<WordsSetObject>() {
        @Override
        public WordsSetObject createFromParcel(Parcel in) {
            return new WordsSetObject(in);
        }

        @Override
        public WordsSetObject[] newArray(int size) {
            return new WordsSetObject[size];
        }
    };

    public ArrayList<String> getWords() {
        return Words;
    }

    public void setWords(ArrayList<String> words) {
        Words = words;
    }

    public ArrayList<String> getTranslations() {
        return Translations;
    }

    public void setTranslations(ArrayList<String> translations) {
        Translations = translations;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getCount() {
        return Count;
    }

    public void setCount(Integer count) {
        Count = count;
    }

    public Boolean getIsAdded() {
        return IsAdded;
    }

    public void setIsAdded(Boolean isAdded) {
        IsAdded = isAdded;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(Words);
        parcel.writeStringList(Translations);
        parcel.writeString(Name);
        if (Count == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(Count);
        }
        parcel.writeByte((byte) (IsAdded == null ? 0 : IsAdded ? 1 : 2));
        parcel.writeString(Image);
    }
}
