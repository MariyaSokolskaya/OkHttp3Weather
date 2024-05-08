package com.example.okhttp3weather.data;

import com.google.gson.annotations.SerializedName;

public class Main {
    public double temp;
    @SerializedName("feels_like")
    public double feelsLike;
    public int humidity;
}
