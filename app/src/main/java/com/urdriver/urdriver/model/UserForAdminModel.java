package com.urdriver.urdriver.model;

public class UserForAdminModel {
    String sourceAddress, destinationAddress, StartTrip, CabDriver, DriverName, DriverPhone,
            DriverCabModel, DriverCabBrand, DriverCabNumber, TotalTrip, Name, Email;

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getDriverPhone() {
        return DriverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        DriverPhone = driverPhone;
    }

    public String getDriverCabModel() {
        return DriverCabModel;
    }

    public void setDriverCabModel(String driverCabModel) {
        DriverCabModel = driverCabModel;
    }

    public String getDriverCabBrand() {
        return DriverCabBrand;
    }

    public void setDriverCabBrand(String driverCabBrand) {
        DriverCabBrand = driverCabBrand;
    }

    public String getDriverCabNumber() {
        return DriverCabNumber;
    }

    public void setDriverCabNumber(String driverCabNumber) {
        DriverCabNumber = driverCabNumber;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getStartTrip() {
        return StartTrip;
    }

    public void setStartTrip(String startTrip) {
        StartTrip = startTrip;
    }

    public String getCabDriver() {
        return CabDriver;
    }

    public void setCabDriver(String cabDriver) {
        CabDriver = cabDriver;
    }

    public String getTotalTrip() {
        return TotalTrip;
    }

    public void setTotalTrip(String totalTrip) {
        TotalTrip = totalTrip;
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
}
