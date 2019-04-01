package com.example.user.spanish.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class InfoGrammarObject implements Parcelable {

    private String Date;
    private Integer DaysCount;
    private String Image;
    private Boolean IsForgotten;
    private Integer progress;


    public InfoGrammarObject(){

    }

    public InfoGrammarObject(String date, Integer daysCount, String image, Boolean isForgotten, Integer progress) {
        Date = date;
        DaysCount = daysCount;
        Image = image;
        IsForgotten = isForgotten;
        this.progress = progress;
    }

    protected InfoGrammarObject(Parcel in) {
        Date = in.readString();
        if (in.readByte() == 0) {
            DaysCount = null;
        } else {
            DaysCount = in.readInt();
        }
        Image = in.readString();
        byte tmpIsForgotten = in.readByte();
        IsForgotten = tmpIsForgotten == 0 ? null : tmpIsForgotten == 1;
        if (in.readByte() == 0) {
            progress = null;
        } else {
            progress = in.readInt();
        }
    }

    public static final Creator<InfoGrammarObject> CREATOR = new Creator<InfoGrammarObject>() {
        @Override
        public InfoGrammarObject createFromParcel(Parcel in) {
            return new InfoGrammarObject(in);
        }

        @Override
        public InfoGrammarObject[] newArray(int size) {
            return new InfoGrammarObject[size];
        }
    };

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public Integer getDaysCount() {
        return DaysCount;
    }

    public void setDaysCount(Integer daysCount) {
        DaysCount = daysCount;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public Boolean getIsForgotten() {
        return IsForgotten;
    }

    public void setIsForgotten(Boolean isForgotten) {
        IsForgotten = isForgotten;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Date);
        if (DaysCount == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(DaysCount);
        }
        parcel.writeString(Image);
        parcel.writeByte((byte) (IsForgotten == null ? 0 : IsForgotten ? 1 : 2));
        if (progress == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(progress);
        }
    }
}
