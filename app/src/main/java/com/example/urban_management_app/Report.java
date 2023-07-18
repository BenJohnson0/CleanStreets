package com.example.urban_management_app;

public class Report {
    private String reportId;
    private String timestamp;
    private String xCoordinates;
    private String yCoordinates;
    private String size;
    private String urgency;
    private String imageUrl;
    private String userId;
    private String title;

    public Report() {
        // Default constructor required for Firebase
    }

    public Report(String reportId, String timestamp, String xCoordinates, String yCoordinates,
                  String size, String urgency, String imageUrl, String userId, String title) {
        this.reportId = reportId;
        this.timestamp = timestamp;
        this.xCoordinates = xCoordinates;
        this.yCoordinates = yCoordinates;
        this.size = size;
        this.urgency = urgency;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.title = title;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getXCoordinates() {
        return xCoordinates;
    }

    public void setXCoordinates(String xCoordinates) {
        this.xCoordinates = xCoordinates;
    }

    public String getYCoordinates() {
        return yCoordinates;
    }

    public void setYCoordinates(String yCoordinates) {
        this.yCoordinates = yCoordinates;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() { return title;}

    public void setTitle(String title) {
        this.title = title;
    }

    }

