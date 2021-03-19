package com.example.erailadmin.Model;

import com.google.gson.annotations.SerializedName;

public class CommonModel {

    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
