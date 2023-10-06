package com.example.healthappelderly;

public class Elderly {
    private String User_ID;
    private String Name;
    private String Mobile_nr;
    private String Address;
    private String Allergies;
    private String Email;

    public Elderly() {

    }

    public void setAllergies(String allergies) {Allergies = allergies;}
    public String getAllergies(){return Allergies;}
    public void setEmail(String email){Email = email;};

    public String getEmail() {return Email;}

    public String getUser_ID() {
        return User_ID;
    }
    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }
    public String getName() {
        return Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }

    public String getMobile_nr() {
        return Mobile_nr;
    }
    public void setMobile_nr(String mobile_nr) {
        Mobile_nr = mobile_nr;
    }

    public String getAddress() {
        return Address;
    }
    public void setAddress(String address) {
        Address = address;
    }

}
