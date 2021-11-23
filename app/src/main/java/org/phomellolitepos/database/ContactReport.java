package org.phomellolitepos.database;

public class ContactReport
{
    private String srno;
    private String contactCode;
    private String title;
    private String name;
    private String gender;
    private String Dob;
    private String contact1;
    private String email;
    private String creditLimit;
    private String Gstn;

    public ContactReport(String srno, String contactCode, String title, String name, String gender, String dob, String contact1, String email, String creditLimit, String gstn)
    {
        this.srno = srno;
        this.contactCode = contactCode;
        this.title = title;
        this.name = name;
        this.gender = gender;
        Dob = dob;
        this.contact1 = contact1;
        this.email = email;
        this.creditLimit = creditLimit;
        Gstn = gstn;
    }

    public String getSrno() {
        return srno;
    }

    public void setSrno(String srno) {
        this.srno = srno;
    }

    public String getContactCode() {
        return contactCode;
    }

    public void setContactCode(String contactCode) {
        this.contactCode = contactCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return Dob;
    }

    public void setDob(String dob) {
        Dob = dob;
    }

    public String getContact1() {
        return contact1;
    }

    public void setContact1(String contact1) {
        this.contact1 = contact1;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getGstn() {
        return Gstn;
    }

    public void setGstn(String gstn) {
        Gstn = gstn;
    }


}
