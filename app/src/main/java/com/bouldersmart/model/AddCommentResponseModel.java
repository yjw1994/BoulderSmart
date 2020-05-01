package com.bouldersmart.model;

import java.io.Serializable;

/*// add comment data response
* "data": {
        "route_id": "1",
        "route_name": "Test1",
        "location_id": "1",
        "user_id": "1",
        "grade_opinion": "61",
        "photo": "",
        "is_admin_confirm": "1",
        "created_date": "2020-01-20 13:39:27",
        "updated_date": "2020-01-23 05:20:31",
        "ratting": "5.00"
    }*/
public class AddCommentResponseModel implements Serializable {
    private String ResponseCode = "";
    private String ResponseMsg = "";
    private String Result = "";
    private String ServerTime = "";

    private CommnetDataModel data;

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

    public CommnetDataModel getData() {
        return data;
    }

    public void setData(CommnetDataModel data) {
        this.data = data;
    }
}
