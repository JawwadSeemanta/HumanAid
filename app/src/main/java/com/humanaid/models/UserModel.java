package com.humanaid.models;

public class UserModel {
    private String user_name, mobile_number, pin_number, user_type, user_id;
    private int citizen_points;

    public UserModel(){}

    public UserModel(String user_name, String MobileNumber, String PIN, String UType, String uID){
        this.user_name = user_name;
        this.mobile_number = MobileNumber;
        this.pin_number = PIN;
        this.user_type = UType;
        this.user_id = uID;
    }

    public UserModel(String user_name, String MobileNumber, String PIN, String UType, String uID, int points){
        this.user_name = user_name;
        this.mobile_number = MobileNumber;
        this.pin_number = PIN;
        this.user_type = UType;
        this.user_id = uID;
        this.citizen_points = points;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getPin_number() {
        return pin_number;
    }

    public void setPin_number(String pin_number) {
        this.pin_number = pin_number;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getCitizen_points() {
        return citizen_points;
    }

    public void setCitizen_points(int citizen_points) {
        this.citizen_points = citizen_points;
    }
}
