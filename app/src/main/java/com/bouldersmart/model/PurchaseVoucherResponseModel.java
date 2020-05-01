package com.bouldersmart.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PurchaseVoucherResponseModel implements Serializable {
    private String ResponseCode = "";
    private String ResponseMsg = "";
    private String Result = "";
    private String ServerTime = "";
    private String reward_point = "";
    private ArrayList<VoucherDataModel> data = new ArrayList<>();

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

    public String getReward_point() {
        return reward_point;
    }

    public void setReward_point(String reward_point) {
        this.reward_point = reward_point;
    }

    public ArrayList<VoucherDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<VoucherDataModel> data) {
        this.data = data;
    }
}
