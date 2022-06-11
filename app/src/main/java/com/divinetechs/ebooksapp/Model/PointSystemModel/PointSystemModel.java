package com.divinetechs.ebooksapp.Model.PointSystemModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PointSystemModel {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("daily_login")
    @Expose
    private List<com.divinetechs.ebooksapp.Model.PointSystemModel.DailyLogin> dailyLogin = null;
    @SerializedName("free_coin")
    @Expose
    private List<com.divinetechs.ebooksapp.Model.PointSystemModel.FreeCoin> freeCoin = null;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<com.divinetechs.ebooksapp.Model.PointSystemModel.DailyLogin> getDailyLogin() {
        return dailyLogin;
    }

    public void setDailyLogin(List<com.divinetechs.ebooksapp.Model.PointSystemModel.DailyLogin> dailyLogin) {
        this.dailyLogin = dailyLogin;
    }

    public List<com.divinetechs.ebooksapp.Model.PointSystemModel.FreeCoin> getFreeCoin() {
        return freeCoin;
    }

    public void setFreeCoin(List<com.divinetechs.ebooksapp.Model.PointSystemModel.FreeCoin> freeCoin) {
        this.freeCoin = freeCoin;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
