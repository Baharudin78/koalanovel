
package com.divinetechs.ebooksapp.Model.GeneralSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeneralSettings {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private List<com.divinetechs.ebooksapp.Model.GeneralSettings.Result> result = null;

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

    public List<com.divinetechs.ebooksapp.Model.GeneralSettings.Result> getResult() {
        return result;
    }

    public void setResult(List<com.divinetechs.ebooksapp.Model.GeneralSettings.Result> result) {
        this.result = result;
    }

}
