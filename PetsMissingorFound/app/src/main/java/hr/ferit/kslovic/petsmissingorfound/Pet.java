package hr.ferit.kslovic.petsmissingorfound;


public class Pet {
    private String etPname;
    private String etPbreed;
    private String etPdetails;
    private String Picture;
    private String Location;
    private String etPcontact;
    private String sStatus;

    public Pet(String etPname, String etPbreed, String etPdetails, String picture, String location, String etPcontact, String sStatus) {
        this.etPname = etPname;
        this.etPbreed = etPbreed;
        this.etPdetails = etPdetails;
        Picture = picture;
        Location = location;
        this.etPcontact = etPcontact;
        this.sStatus = sStatus;
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

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
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
