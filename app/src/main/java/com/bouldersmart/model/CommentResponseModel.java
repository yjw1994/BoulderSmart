package com.bouldersmart.model;

import java.io.Serializable;
import java.util.ArrayList;

public class CommentResponseModel implements Serializable {
    private String ResponseCode = "";
    private String ResponseMsg = "";
    private String Result = "";
    private String ServerTime = "";
    private String grade_opinion = "";
    private ArrayList<CommnetDataModel> data = new ArrayList<>();
    private CommnetDataModel user_ratting;

    public String getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(String responseCode) {
        ResponseCode = responseCode;
    }

    public String getResponseMsg() {
        return ResponseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        ResponseMsg = responseMsg;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getServerTime() {
        return ServerTime;
    }

    public void setServerTime(String serverTime) {
        ServerTime = serverTime;
    }

    public ArrayList<CommnetDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<CommnetDataModel> data) {
        this.data = data;
    }

    public CommnetDataModel getUser_ratting() {
        return user_ratting;
    }

    public void setUser_ratting(CommnetDataModel user_ratting) {
        this.user_ratting = user_ratting;
    }

    public String getGrade_opinion() {
        return grade_opinion;
    }

    public void setGrade_opinion(String grade_opinion) {
        this.grade_opinion = grade_opinion;
    }
}
