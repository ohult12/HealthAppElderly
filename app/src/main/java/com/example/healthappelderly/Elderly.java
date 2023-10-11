package com.example.healthappelderly;

public class Elderly {
    private String user_ID;
    private String name;
    private String mobile_nr;
    private String address;
    private String allergies;
    private String email;
    private Meal meal;

    public Elderly() {

    }

    public void setAllergies(String allergies) {this.allergies = allergies;}
    public String getAllergies(){return this.allergies;}
    public void setEmail(String email){this.email = email;};

    public String getEmail() {return this.email;}

    public String getUser_ID() {
        return this.user_ID;
    }
    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String Name) {
        this.name = Name;
    }

    public String getMobile_nr() {
        return this.mobile_nr;
    }
    public void setMobile_nr(String mobile_nr) {
        this.mobile_nr = mobile_nr;
    }

    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

}
