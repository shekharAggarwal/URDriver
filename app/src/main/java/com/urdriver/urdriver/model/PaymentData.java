package com.urdriver.urdriver.model;

public class PaymentData {

    private String TxnType, TxnId, OrderId, TxnStatus, RefundId, TnxAmount, UID, Cash;

    private boolean CheckBox;

    public PaymentData() {
    }

    public PaymentData(String txnType, String txnId, String orderId, String txnStatus, String refundId, String tnxAmount) {
        TxnType = txnType;
        TxnId = txnId;
        OrderId = orderId;
        TxnStatus = txnStatus;
        RefundId = refundId;
        TnxAmount = tnxAmount;
    }

    public boolean getCheckBox() {
        return CheckBox;
    }

    public void setCheckBox(boolean checkBox) {
        CheckBox = checkBox;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getCash() {
        return Cash;
    }

    public void setCash(String cash) {
        Cash = cash;
    }

    public String getTnxAmount() {
        return TnxAmount;
    }

    public void setTnxAmount(String tnxAmount) {
        TnxAmount = tnxAmount;
    }

    public String getTxnType() {
        return TxnType;
    }

    public void setTxnType(String txnType) {
        TxnType = txnType;
    }

    public String getTxnId() {
        return TxnId;
    }

    public void setTxnId(String txnId) {
        TxnId = txnId;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getTxnStatus() {
        return TxnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        TxnStatus = txnStatus;
    }

    public String getRefundId() {
        return RefundId;
    }

    public void setRefundId(String refundId) {
        RefundId = refundId;
    }
}
