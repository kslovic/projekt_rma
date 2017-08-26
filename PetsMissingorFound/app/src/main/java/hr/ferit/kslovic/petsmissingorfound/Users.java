package hr.ferit.kslovic.petsmissingorfound;

public class Users {
    private String fname;
    private String lname;
    private String uname;
    private String email;
    private String phone;
    private String psw;

    public Users(){}

    public Users(String fname, String lname, String uname, String email, String phone, String psw) {
        this.fname = fname;
        this.lname = lname;
        this.uname = uname;
        this.email = email;
        this.phone = phone;
        this.psw = psw;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }
}
