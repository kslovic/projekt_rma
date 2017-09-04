package hr.ferit.kslovic.petsmissingorfound.Models;

import com.google.android.gms.maps.model.LatLng;

public class PetLocation {

    private String locId;
    private LatLng petLocation;
    private String email;

    public PetLocation(){}

    public PetLocation(String locId, LatLng petLocation, String email) {
        this.locId = locId;
        this.petLocation = petLocation;
        this.email = email;
    }

    public String getLocId() {
        return locId;
    }

    public void setLocId(String locId) {
        this.locId = locId;
    }

    public LatLng getPetLocation() {
        return petLocation;
    }

    public void setPetLocation(LatLng petLocation) {
        this.petLocation = petLocation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
