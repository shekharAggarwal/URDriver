package com.urdriver.urdriver.model;


public class RequestDataOneWay {
    private String Id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String sourceAddress;
    private String destinationAddress;
    private String pickupDate;
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
    private String RequestTime;

    public RequestDataOneWay() {
    }

    public RequestDataOneWay(String id, String fullName, String phoneNumber, String email, String sourceAddress, String destinationAddress, String pickupDate, String pickupTime, String source, String destination, String cabs, String bookAccount, String cabFare, String cabDriver, String cabStatus, String cabModel, String cabTnxId) {
        Id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.pickupDate = pickupDate;
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
    }

    public String getRequestTime() {
        return RequestTime;
    }

    public void setRequestTime(String requestTime) {
        RequestTime = requestTime;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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

    public String getCabTnxId() {
        return CabTnxId;
    }

    public void setCabTnxId(String cabTnxId) {
        CabTnxId = cabTnxId;
    }
}
