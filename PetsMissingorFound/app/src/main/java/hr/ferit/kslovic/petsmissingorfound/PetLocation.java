package hr.ferit.kslovic.petsmissingorfound;

import com.google.android.gms.maps.model.LatLng;

class PetLocation {

    private String locId;
    private LatLng petLocation;

    public PetLocation(){}

    public PetLocation(String locId, LatLng petLocation) {
        this.locId = locId;
        this.petLocation = petLocation;
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
}
