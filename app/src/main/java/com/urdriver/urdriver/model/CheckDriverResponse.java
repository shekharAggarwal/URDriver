package com.urdriver.urdriver.model;

public class CheckDriverResponse {

    private String error_msg;

    public CheckDriverResponse() {
    }

    public CheckDriverResponse(String error_msg) {

        this.error_msg = error_msg;
    }


    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
