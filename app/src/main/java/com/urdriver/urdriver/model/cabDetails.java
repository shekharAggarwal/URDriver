package com.urdriver.urdriver.model;

public class cabDetails {
    private String Id, CabBrand, CabModel, CabNumber, CabImage, CabSitting, CabDriver, CabType, CabLocation;

    public cabDetails(String id, String cabBrand, String cabModel, String cabNumber, String cabImage, String cabSitting, String cabDriver) {
        Id = id;
        CabBrand = cabBrand;
        CabModel = cabModel;
        CabNumber = cabNumber;
        CabImage = cabImage;
        CabSitting = cabSitting;
        CabDriver = cabDriver;
    }

    public String getCabType() {
        return CabType;
    }

    public void setCabType(String cabType) {
        CabType = cabType;
    }

    public String getCabLocation() {
        return CabLocation;
    }

    public void setCabLocation(String cabLocation) {
        CabLocation = cabLocation;
    }

    public String getCabNumber() {
        return CabNumber;
    }

    public void setCabNumber(String cabNumber) {
        CabNumber = cabNumber;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCabBrand() {
        return CabBrand;
    }

    public void setCabBrand(String cabBrand) {
        CabBrand = cabBrand;
    }

    public String getCabModel() {
        return CabModel;
    }

    public void setCabModel(String cabModel) {
        CabModel = cabModel;
    }

    public String getCabImage() {
        return CabImage;
    }

    public void setCabImage(String cabImage) {
        CabImage = cabImage;
    }

    public String getCabSitting() {
        return CabSitting;
    }

    public void setCabSitting(String cabSitting) {
        CabSitting = cabSitting;
    }

    public String getCabDriver() {
        return CabDriver;
    }

    public void setCabDriver(String cabDriver) {
        CabDriver = cabDriver;
    }
}
