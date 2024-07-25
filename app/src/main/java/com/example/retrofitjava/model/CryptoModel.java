package com.example.retrofitjava.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.xml.transform.sax.SAXResult;

public class CryptoModel {

    @SerializedName("data")
    public List<PositionData> data;
    public List<PositionData> getData() {
        return data;
    }

    public static class PositionData implements Parcelable {

        @SerializedName("instId")
        public String instId;
        @SerializedName("last")
        public String last;


        public String getInstrumentId() {return instId;}
        public String getLastPrice() {return last;}

        protected PositionData(Parcel in) {
            instId = in.readString();
            last = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(instId);
            dest.writeString(last);
        }

        @Override
        public int describeContents() {
            return 0;
        }
        public static final Creator<PositionData> CREATOR = new Creator<PositionData>() {
            @Override
            public PositionData createFromParcel(Parcel in) {
                return new PositionData(in);
            }

            @Override
            public PositionData[] newArray(int size) {
                return new PositionData[size];
            }
        };
    }
}

