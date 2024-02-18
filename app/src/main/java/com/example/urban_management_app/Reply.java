package com.example.urban_management_app;

public class Reply {
    private String userId;
    private String username;
    private String message;

    public Reply() {
        // default constructor required for Firebase
    }

    public Reply(String userId, String username, String message) {
        this.userId = userId;
        this.username = username;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

