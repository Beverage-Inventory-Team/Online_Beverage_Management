package com.example.database;


//Define table name

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity
public class MainData {
    //Create Primary Key column

    //@PrimaryKey(autoGenerate = true)
    //private int ID;

    //Create text column
   // @ColumnInfo(name="text")
    private String text;
    private String name;
    private String style;
    private int volume;
    private String brewed;
    private String best;
    private String expdate;
    private String brewery;

    public MainData()
    {

    }


    public MainData(String best,String brewed,String brewery, String expdate,String name,String style,String text,int volume) {
        this.text = text;
        this.name = name;
        this.style = style;
        this.volume = volume;
        this.brewed = brewed;
        this.best = best;
        this.brewery = brewery;
        this.expdate=expdate;
    }

//Getter and Setter

    //public int getID() {
      //  return ID;
    //}

    //public void setID(int ID) {
      //  this.ID = ID;
    //}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getBrewed() {
        return brewed;
    }

    public void setBrewed(String brewed) {
        this.brewed = brewed;
    }

    public String getBest() {
        return best;
    }

    public void setBest(String best) {
        this.best = best;
    }

    public String getExpdate() {
        return expdate;
    }

    public void setExpdate(String expdate) {
        this.expdate = expdate;
    }

    public String getBrewery() {
        return brewery;
    }

    public void setBrewery(String brewery) {
        this.brewery = brewery;
    }
}
