package com.bouldersmart.model;

import java.io.Serializable;

public class LoginDataModel implements Serializable {
    private String user_id = "";
    private String fname = "";
    private String lname = "";
    private String email = "";
    private String is_confirm = "";
    private String profile = "";
    private String ucode = "";
    private String is_fb = "";
    private String fb_id = "";
    private String is_manual_email = "";
    private String created_date = "";
    private String updated_date = "";
    private String is_delete = "";
    private String device_type = "";
    private String device_token = "";
    private String token = "";
    private String reward_point = "";

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIs_confirm() {
        return is_confirm;
    }

    public void setIs_confirm(String is_confirm) {
        this.is_confirm = is_confirm;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUcode() {
        return ucode;
    }

    public void setUcode(String ucode) {
        this.ucode = ucode;
    }

    public String getIs_fb() {
        return is_fb;
    }

    public void setIs_fb(String is_fb) {
        this.is_fb = is_fb;
    }

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    public String getIs_manual_email() {
        return is_manual_email;
    }

    public void setIs_manual_email(String is_manual_email) {
        this.is_manual_email = is_manual_email;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(String updated_date) {
        this.updated_date = updated_date;
    }

    public String getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(String is_delete) {
        this.is_delete = is_delete;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getReward_point() {
        return reward_point;
    }

    public void setReward_point(String reward_point) {
        this.reward_point = reward_point;
    }
}
