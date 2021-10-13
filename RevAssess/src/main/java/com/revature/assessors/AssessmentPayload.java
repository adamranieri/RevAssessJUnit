package com.revature.assessors;

import java.util.ArrayList;
import java.util.List;

public class AssessmentPayload {
    private String email;
    private int exerciseId;
    private List<Spec> tests = new ArrayList<Spec>();
    private String sourceCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public List<Spec> getTests() {
        return tests;
    }

    public void setTests(List<Spec> tests) {
        this.tests = tests;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    @Override
    public String toString() {
        return "AssessmentPayload{" +
                "email='" + email + '\'' +
                ", exerciseId=" + exerciseId +
                ", tests=" + tests +
                ", sourceCode='" + sourceCode + '\'' +
                '}';
    }
}
