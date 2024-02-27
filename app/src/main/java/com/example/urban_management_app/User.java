package com.example.urban_management_app;

public class User {

    private String username;
    private String email;
    private String isAdmin;

    public User() {
        // default constructor required for Firebase
    }

    public User(String username, String email, String isAdmin) {
        this.username = username;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }
}

