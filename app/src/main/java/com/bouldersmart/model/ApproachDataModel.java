package com.bouldersmart.model;

import java.io.Serializable;

public class ApproachDataModel implements Serializable {
    private String user_id = "";
    private String approach_id = "";
    private String fname = "";
    private String lname = "";
    private String session_time = "";
    private String image = "";
    private String date = "";

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getApproach_id() {
        return approach_id;
    }

    public void setApproach_id(String approach_id) {
        this.approach_id = approach_id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getSession_time() {
        return session_time;
    }

    public void setSession_time(String session_time) {
        this.session_time = session_time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
