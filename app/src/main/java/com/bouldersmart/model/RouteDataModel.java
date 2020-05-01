package com.bouldersmart.model;

import java.io.Serializable;

public class RouteDataModel implements Serializable {
    // Add route OR Add Comment data response
    /*"data": {
                "route_id": "7",
                "route_name": "Test Postman",
                "location_id": "1",
                "user_id": "1",
                "grade_opinion": "V10",
                "photo": "",
                "is_admin_confirm": "0",
                "created_date": "2020-01-23 09:41:23",
                "updated_date": "2020-01-23 09:41:23",
                "ratting": "5.00"
    }*/

    private String route_id = "";
    private String route_name = "";
    private String grade_opinion = "";
    private String photo = "";
    private String ratting = "";

    public String getRoute_id() {
        return route_id;
    }

    public void setRoute_id(String route_id) {
        this.route_id = route_id;
    }

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    public String getGrade_opinion() {
        return grade_opinion;
    }

    public void setGrade_opinion(String grade_opinion) {
        this.grade_opinion = grade_opinion;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRatting() {
        return ratting;
    }

    public void setRatting(String ratting) {
        this.ratting = ratting;
    }
}
