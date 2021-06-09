package com.example.tulibv2.userServices;

import java.util.ArrayList;

public class RegisterResponse {
    private boolean succeeded;
    private ArrayList<RegisterErrorResponse> errors;

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public ArrayList<RegisterErrorResponse> getListaErrores() {
        return errors;
    }

    public void setListaErrores(ArrayList<RegisterErrorResponse> errors) {
        this.errors = errors;
    }
}
