package hr.ferit.kslovic.petsmissingorfound.Models;

public class UploadPicture {
    public String uid;
    public String url;

    public UploadPicture(){

    }
    public UploadPicture(String uid, String url) {
        this.uid = uid;
        this.url = url;
    }

    public String getuid() {
        return uid;
    }

    public void setuid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
