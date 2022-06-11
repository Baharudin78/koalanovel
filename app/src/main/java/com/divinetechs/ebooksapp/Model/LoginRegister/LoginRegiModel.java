package com.divinetechs.ebooksapp.Model.LoginRegister;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginRegiModel {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private List<com.divinetechs.ebooksapp.Model.LoginRegister.Result> result = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<com.divinetechs.ebooksapp.Model.LoginRegister.Result> getResult() {
        return result;
    }

    public void setResult(List<com.divinetechs.ebooksapp.Model.LoginRegister.Result> result) {
        this.result = result;
    }

}
