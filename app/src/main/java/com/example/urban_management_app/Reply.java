package com.example.urban_management_app;

public class Reply {
    private String username;
    private String message;

    public Reply() {
        // default constructor required for Firebase
    }

    public Reply(String username, String message) {
        this.username = username;
        this.message = message;
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

