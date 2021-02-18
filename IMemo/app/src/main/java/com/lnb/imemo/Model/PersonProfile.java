package com.lnb.imemo.Model;

public class PersonProfile {
    private String id;
    private String sub;
    private String username;
    private String email;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;
    private String gender;
    private String birthday;
    private static PersonProfile mInstance;

    private PersonProfile() {
    }

    public static PersonProfile getInstance() {
        if (mInstance == null) {
            mInstance = new PersonProfile();
        }
        return mInstance;
    }


    public PersonProfile(String id, String sub, String username, String email, String name, String givenName, String familyName, String picture, String gender, String birthday) {
        this.id = id;
        this.sub = sub;
        this.username = username;
        this.email = email;
        this.name = name;
        this.givenName = givenName;
        this.familyName = familyName;
        this.picture = picture;
        this.gender = gender;
        this.birthday = birthday;
    }

    public void updateInstance(PersonProfile newPersonProfile) {
        mInstance.setPicture(newPersonProfile.getPicture());
        mInstance.setBirthday(newPersonProfile.getBirthday());
        mInstance.setUsername(newPersonProfile.getUsername());
        mInstance.setGender(newPersonProfile.getGender());
        mInstance.setEmail(newPersonProfile.getEmail());
        mInstance.setFamilyName(newPersonProfile.getFamilyName());
        mInstance.setGivenName(newPersonProfile.getGivenName());
        mInstance.setId(newPersonProfile.getId());
        mInstance.setName(newPersonProfile.getName());
        mInstance.setSub(newPersonProfile.getSub());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "PersonProfile{" +
                "id=" + id +
                ", sub='" + sub + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", givenName='" + givenName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", picture='" + picture + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }
}
