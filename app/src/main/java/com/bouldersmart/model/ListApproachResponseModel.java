package com.bouldersmart.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ListApproachResponseModel implements Serializable {
    private String ResponseCode = "";
    private String ResponseMsg = "";
    private String Result = "";
    private String ServerTime = "";
    private ArrayList<ApproachDataModel> data = new ArrayList<>();

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

    public ArrayList<ApproachDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<ApproachDataModel> data) {
        this.data = data;
    }
}
