package com.revature.assessors;

public class Spec {

    private String testName;
    private int points;
    private boolean isSuccessful;
    private String errorMessage;

    public Spec() {
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "Spec{" +
                "name='" + testName + '\'' +
                ", points=" + points +
                ", isSuccessful=" + isSuccessful +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
