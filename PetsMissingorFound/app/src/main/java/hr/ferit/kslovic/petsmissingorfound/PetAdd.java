package hr.ferit.kslovic.petsmissingorfound;


public class PetAdd {
    private String pid;
    private String pName;
    private  String pBreed;
    private String pStatus;
    private String pPic;

    public PetAdd(){}

    public PetAdd(String pid, String pName, String pBreed, String pStatus, String pPic) {
        this.pid = pid;
        this.pName = pName;
        this.pBreed = pBreed;
        this.pStatus = pStatus;
        this.pPic = pPic;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpBreed() {
        return pBreed;
    }

    public void setpBreed(String pBreed) {
        this.pBreed = pBreed;
    }

    public String getpStatus() {
        return pStatus;
    }

    public void setpStatus(String pStatus) {
        this.pStatus = pStatus;
    }

    public String getpPic() {
        return pPic;
    }

    public void setpPic(String pPic) {
        this.pPic = pPic;
    }
}

