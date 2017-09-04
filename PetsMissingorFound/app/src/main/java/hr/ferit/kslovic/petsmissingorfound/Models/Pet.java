package hr.ferit.kslovic.petsmissingorfound.Models;


import com.google.android.gms.maps.model.LatLng;

public class Pet {
    private String pid;
    private String etPname;
    private String etPbreed;
    private String etPdetails;
    private String etPcontact;
    private String sStatus;
    private String uid;
    private double lastLatitude;
    private double lastLongitude;
    private String Picture;
    public Pet(){}

    public Pet(String pid, String etPname, String etPbreed, String etPdetails, String etPcontact, String sStatus, String uid, double lastLatitude, double lastLongitude, String picture) {
        this.pid = pid;
        this.etPname = etPname;
        this.etPbreed = etPbreed;
        this.etPdetails = etPdetails;
        this.etPcontact = etPcontact;
        this.sStatus = sStatus;
        this.uid = uid;
        this.lastLatitude = lastLatitude;
        this.lastLongitude = lastLongitude;
        Picture = picture;
    }


    public double getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public double getLastLongitude() {
        return lastLongitude;
    }

    public void setLastLongitude(double lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getEtPname() {
        return etPname;
    }

    public void setEtPname(String etPname) {
        this.etPname = etPname;
    }

    public String getEtPbreed() {
        return etPbreed;
    }

    public void setEtPbreed(String etPbreed) {
        this.etPbreed = etPbreed;
    }

    public String getEtPdetails() {
        return etPdetails;
    }

    public void setEtPdetails(String etPdetails) {
        this.etPdetails = etPdetails;
    }

    public String getEtPcontact() {
        return etPcontact;
    }

    public void setEtPcontact(String etPcontact) {
        this.etPcontact = etPcontact;
    }

    public String getsStatus() {
        return sStatus;
    }

    public void setsStatus(String sStatus) {
        this.sStatus = sStatus;
    }
}
