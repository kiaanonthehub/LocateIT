package com.locateitteam.locateit.Model;

public class UserModel {

    // declare fields
    String Name;
    String Surname;
    String Email;
    String Password;

    // class constructor - signup
    public UserModel(String name, String surname, String email, String password) {
        Name = name;
        Surname = surname;
        Email = email;
        Password = password;
    }

    // class constructor - login
    public UserModel(String email, String password) {
        Email = email;
        Password = password;
    }

    // blank constructor
    public UserModel() {
    }

    // accessors and mutators
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
