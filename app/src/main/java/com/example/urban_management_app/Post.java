package com.example.urban_management_app;

public class Post {
    private String userId;
    private String referencedReportId;
    private String postId;
    private String timestamp;
    private String postTitle;
    private String postContent;
    private String postTags;
    private String postCode;

    public Post() {
        // default constructor required for Firebase
    }

    public Post(String userId, String referencedReportId, String postId, String timestamp,
                String postTitle, String postContent, String postTags, String postCode) {
        this.userId = userId;
        this.referencedReportId = referencedReportId;
        this.postId = postId;
        this.timestamp = timestamp;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postTags = postTags;
        this.postCode = postCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReferencedReportId() {
        return referencedReportId;
    }

    public void setReferencedReportId(String referencedReportId) {this.referencedReportId = referencedReportId;}

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public String getPostTags() {
        return postTags;
    }

    public void setPostTags(String postTags) {
        this.postTags = postTags;
    }

    public String getPostCode() {return postCode;}

    public void setPostCode(String postCode) {this.postCode = postCode;}
}
