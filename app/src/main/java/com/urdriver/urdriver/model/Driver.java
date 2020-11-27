package com.urdriver.urdriver.model;

public class Driver {

    private String id, name, email, phone, password, image, aadharNumber, aadharImage, licenseImage, error_msg;
    private int driverStatus;

    public Driver() {
    }

    public Driver(String name, String email, String phone, String password, String aadharNumber, String aadharImage, String licenseImage, int driverStatus, String error_msg) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.aadharNumber = aadharNumber;
        this.aadharImage = aadharImage;
        this.licenseImage = licenseImage;
        this.driverStatus = driverStatus;
        this.error_msg = error_msg;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getAadharImage() {
        return aadharImage;
    }

    public void setAadharImage(String aadharImage) {
        this.aadharImage = aadharImage;
    }

    public String getLicenseImage() {
        return licenseImage;
    }

    public void setLicenseImage(String licenseImage) {
        this.licenseImage = licenseImage;
    }

    public int getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(int driverStatus) {
        this.driverStatus = driverStatus;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
