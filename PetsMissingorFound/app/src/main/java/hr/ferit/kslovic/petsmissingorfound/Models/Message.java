package hr.ferit.kslovic.petsmissingorfound.Models;

public class Message {
    private String uname;
    private String text;
    private String time;
    private String uPid;
    private String email;
    private Boolean read;

    public Message(){}

    public Message(String uname, String text, String time, String uPid, String email, Boolean read) {
        this.uname = uname;
        this.text = text;
        this.time = time;
        this.uPid = uPid;
        this.email = email;
        this.read = read;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getuPid() {
        return uPid;
    }

    public void setuPid(String uPid) {
        this.uPid = uPid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }
}
