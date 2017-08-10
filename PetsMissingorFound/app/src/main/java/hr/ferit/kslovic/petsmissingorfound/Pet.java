package hr.ferit.kslovic.petsmissingorfound;


public class Pet {
    private String pid;
    private String etPname;
    private String etPbreed;
    private String etPdetails;
    private String etPcontact;
    private String sStatus;

    public Pet(String pid, String etPname, String etPbreed, String etPdetails, String etPcontact, String sStatus) {
        this.pid = pid;
        this.etPname = etPname;
        this.etPbreed = etPbreed;
        this.etPdetails = etPdetails;
        this.etPcontact = etPcontact;
        this.sStatus = sStatus;
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
