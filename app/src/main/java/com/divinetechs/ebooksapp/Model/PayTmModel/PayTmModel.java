package com.divinetechs.ebooksapp.Model.PayTmModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayTmModel {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private com.divinetechs.ebooksapp.Model.PayTmModel.Result result;

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

    public com.divinetechs.ebooksapp.Model.PayTmModel.Result getResult() {
        return result;
    }

    public void setResult(com.divinetechs.ebooksapp.Model.PayTmModel.Result result) {
        this.result = result;
    }

}
