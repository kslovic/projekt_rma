package hr.ferit.kslovic.petsmissingorfound.Models;


public class Notifications {
    private String uid;
    private String id;
    private String type;
    private Boolean read;

    public Notifications(){}

    public Notifications(String uid, String id, String type, Boolean read) {
        this.uid = uid;
        this.id = id;
        this.type = type;
        this.read = read;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
