
package com.divinetechs.ebooksapp.Model.BookmarkModel;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookmarkModel {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("total_records")
    @Expose
    private Integer totalRecords;
    @SerializedName("total_page")
    @Expose
    private Integer totalPage;
    @SerializedName("result")
    @Expose
    private List<com.divinetechs.ebooksapp.Model.BookmarkModel.Result> result = null;

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

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<com.divinetechs.ebooksapp.Model.BookmarkModel.Result> getResult() {
        return result;
    }

    public void setResult(List<com.divinetechs.ebooksapp.Model.BookmarkModel.Result> result) {
        this.result = result;
    }

}
