package com.urdriver.urdriver.model;

public class DriverRequestModel {
    private String Id, Name, Email, Phone, Password, driverImage, AadharNumber, AadharImage, LicenseImage;
    private int DriverStatus;


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getDriverImage() {
        return driverImage;
    }

    public void setDriverImage(String driverImage) {
        this.driverImage = driverImage;
    }

    public String getAadharNumber() {
        return AadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        AadharNumber = aadharNumber;
    }

    public String getAadharImage() {
        return AadharImage;
    }

    public void setAadharImage(String aadharImage) {
        AadharImage = aadharImage;
    }

    public String getLicenseImage() {
        return LicenseImage;
    }

    public void setLicenseImage(String licenseImage) {
        LicenseImage = licenseImage;
    }

    public int getDriverStatus() {
        return DriverStatus;
    }

    public void setDriverStatus(int driverStatus) {
        DriverStatus = driverStatus;
    }
}
