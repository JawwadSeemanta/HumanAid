package com.humanaid.models;

public class DataRequestModel {
    private String requestID,
            requesterName,
            requesterPhone,
            requesterUserID,
            requestType,
            requestStatus,
            submissionTime,
            ImageURL;
    private double latitude,
            longitude;
    private String helperName,
            helperPhone;

    public DataRequestModel() {
    }

    public DataRequestModel(String requestID, String requesterName, String requesterPhone, String requesterUserID, String requestType, String requestStatus, String submissionTime, String imageURL, double latitude, double longitude, String helperName, String helperPhone) {
        this.requestID = requestID;
        this.requesterName = requesterName;
        this.requesterPhone = requesterPhone;
        this.requesterUserID = requesterUserID;
        this.requestType = requestType;
        this.requestStatus = requestStatus;
        this.submissionTime = submissionTime;
        this.ImageURL = imageURL;
        this.latitude = latitude;
        this.longitude = longitude;
        this.helperName = helperName;
        this.helperPhone = helperPhone;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterPhone() {
        return requesterPhone;
    }

    public void setRequesterPhone(String requesterPhone) {
        this.requesterPhone = requesterPhone;
    }

    public String getRequesterUserID() {
        return requesterUserID;
    }

    public void setRequesterUserID(String requesterUserID) {
        this.requesterUserID = requesterUserID;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(String submissionTime) {
        this.submissionTime = submissionTime;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getHelperName() {
        return helperName;
    }

    public void setHelperName(String helperName) {
        this.helperName = helperName;
    }

    public String getHelperPhone() {
        return helperPhone;
    }

    public void setHelperPhone(String helperPhone) {
        this.helperPhone = helperPhone;
    }
}
