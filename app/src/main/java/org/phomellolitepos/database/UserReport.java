package org.phomellolitepos.database;

public class UserReport
{
    private String userCode;
    private String username;
    private String userEmail;
    private String maxDiscount;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(String maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public UserReport(String userCode, String username, String userEmail, String maxDiscount) {
        this.userCode = userCode;
        this.username = username;
        this.userEmail = userEmail;
        this.maxDiscount = maxDiscount;
    }


}
