package com.maxkrass.stundenplan.objects;

import android.databinding.Observable;

import com.orm.SugarRecord;

import java.io.Serializable;

public class Teacher extends SugarRecord {
    private String name;
    private String phone;
    private String email;

    public Teacher() {
        name = "";
        phone = "";
        email = "";
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Teacher(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Teacher && ((Teacher) o).getName().equals(getName()) && ((Teacher) o).getEmail().equals(getEmail()) && ((Teacher) o).getPhone().equals(getPhone()) && ((Teacher) o).getId().equals(getId());
    }
}
