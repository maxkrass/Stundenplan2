package com.maxkrass.stundenplan.objects;

import com.orm.SugarRecord;

public class Subject extends SugarRecord {
    private Teacher teacher;
    private int colorIndex;
    private String name;
    private String abbreviation;

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Subject(String name, String abbreviation, Teacher teacher, int colorIndex) {
        this.teacher = teacher;
        this.colorIndex = colorIndex;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public Subject() {

    }
}
