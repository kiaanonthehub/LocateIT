package com.locateitteam.locateit.Model;

public class UserModel {

    // declare private variables
    private String firstname, lastname , email;

    // declare fields
    String Name;
    String Surname;
    String Email;
    String Password;

    // class constructor - signup
    public UserModel(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    // getters and setters
    public String getFirstname() {

        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
