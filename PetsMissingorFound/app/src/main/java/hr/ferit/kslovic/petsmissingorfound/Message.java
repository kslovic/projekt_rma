package hr.ferit.kslovic.petsmissingorfound;

public class Message {
    private String uname;
    private String text;
    private String time;
    private String uPid;

    public Message(){}

    public Message(String uname, String text, String time, String uPid) {
        this.uname = uname;
        this.text = text;
        this.time = time;
        this.uPid = uPid;
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
}
