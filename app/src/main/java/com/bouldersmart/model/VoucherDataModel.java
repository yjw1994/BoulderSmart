package com.bouldersmart.model;

import java.io.Serializable;

public class VoucherDataModel implements Serializable {
    private String voucher_id = "";
    private String voucher_name = "";
    private String voucher_code = "";
    private String discount = "";
    private String points = "";
    private String voucher_image = "";
    private String is_purchase = "";

    public String getVoucher_id() {
        return voucher_id;
    }

    public void setVoucher_id(String voucher_id) {
        this.voucher_id = voucher_id;
    }

    public String getVoucher_name() {
        return voucher_name;
    }

    public void setVoucher_name(String voucher_name) {
        this.voucher_name = voucher_name;
    }

    public String getVoucher_code() {
        return voucher_code;
    }

    public void setVoucher_code(String voucher_code) {
        this.voucher_code = voucher_code;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getVoucher_image() {
        return voucher_image;
    }

    public void setVoucher_image(String voucher_image) {
        this.voucher_image = voucher_image;
    }

    public String getIs_purchase() {
        return is_purchase;
    }

    public void setIs_purchase(String is_purchase) {
        this.is_purchase = is_purchase;
    }
}
