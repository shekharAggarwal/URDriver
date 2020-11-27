package com.urdriver.urdriver.model;


public class RequestData {
    private String Id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String sourceAddress;
    private String destinationAddress;
    private String pickupDate;
    private String dropDate;
    private String pickupTime;
    private String source;
    private String destination;
    private String Cabs;
    private String BookAccount;
    private String CabFare;
    private String CabDriver;
    private String CabStatus;
    private String CabModel;
    private String CabTnxId;
    private String cabType;
    private String RequestTime;

    public RequestData() {
    }

    public RequestData(String id, String fullName, String phoneNumber, String email, String sourceAddress, String destinationAddress, String pickupDate, String dropDate, String pickupTime, String source, String destination, String cabs, String bookAccount, String cabFare, String cabDriver, String cabStatus, String cabModel, String cabTnxId, String cabType, String requestTime) {
        Id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.pickupDate = pickupDate;
        this.dropDate = dropDate;
        this.pickupTime = pickupTime;
        this.source = source;
        this.destination = destination;
        Cabs = cabs;
        BookAccount = bookAccount;
        CabFare = cabFare;
        CabDriver = cabDriver;
        CabStatus = cabStatus;
        CabModel = cabModel;
        CabTnxId = cabTnxId;
        this.cabType = cabType;
        RequestTime = requestTime;
    }

    public String getRequestTime() {
        return RequestTime;
    }

    public void setRequestTime(String requestTime) {
        RequestTime = requestTime;
    }

    public String getCabType() {
        return cabType;
    }

    public void setCabType(String cabType) {
        this.cabType = cabType;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getDropDate() {
        return dropDate;
    }

    public void setDropDate(String dropDate) {
        this.dropDate = dropDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCabs() {
        return Cabs;
    }

    public void setCabs(String cabs) {
        Cabs = cabs;
    }

    public String getBookAccount() {
        return BookAccount;
    }

    public void setBookAccount(String bookAccount) {
        BookAccount = bookAccount;
    }

    public String getCabFare() {
        return CabFare;
    }

    public void setCabFare(String cabFare) {
        CabFare = cabFare;
    }

    public String getCabDriver() {
        return CabDriver;
    }

    public void setCabDriver(String cabDriver) {
        CabDriver = cabDriver;
    }

    public String getCabStatus() {
        return CabStatus;
    }

    public void setCabStatus(String cabStatus) {
        CabStatus = cabStatus;
    }

    public String getCabModel() {
        return CabModel;
    }

    public void setCabModel(String cabModel) {
        CabModel = cabModel;
    }

    public String getCabTnxId() {
        return CabTnxId;
    }

    public void setCabTnxId(String cabTnxId) {
        CabTnxId = cabTnxId;
    }
}
