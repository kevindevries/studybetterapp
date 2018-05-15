package com.example.kevdevries.studybetter;
/**
 * Created by KevdeVries on 02/04/2018.
 *
 * Model for Study Group data
 */

public class StudyModel {

    public String title;
    public String time;
    public String date;
    public String recurring;
    public String location;
    public String members;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRecurring() {
        return recurring;
    }

    public void setRecurring(String recurring){
        this.recurring = recurring;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members){
        this.members = members;
    }
}
