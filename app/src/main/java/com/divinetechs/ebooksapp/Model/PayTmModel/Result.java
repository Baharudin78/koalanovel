package com.divinetechs.ebooksapp.Model.PayTmModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("paytmChecksum")
    @Expose
    private String paytmChecksum;
    @SerializedName("verifySignature")
    @Expose
    private Boolean verifySignature;

    public String getPaytmChecksum() {
        return paytmChecksum;
    }

    public void setPaytmChecksum(String paytmChecksum) {
        this.paytmChecksum = paytmChecksum;
    }

    public Boolean getVerifySignature() {
        return verifySignature;
    }

    public void setVerifySignature(Boolean verifySignature) {
        this.verifySignature = verifySignature;
    }

}
