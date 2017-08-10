package hr.ferit.kslovic.petsmissingorfound;

class PetLocation {

    private String locId;
    private String petLocation;

    public PetLocation(String locId, String petLocation) {
        this.locId = locId;
        this.petLocation = petLocation;
    }

    public String getLocId() {
        return locId;
    }

    public void setLocId(String locId) {
        this.locId = locId;
    }

    public String getPetLocation() {
        return petLocation;
    }

    public void setPetLocation(String petLocation) {
        this.petLocation = petLocation;
    }
}
