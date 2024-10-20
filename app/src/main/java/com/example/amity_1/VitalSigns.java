package com.example.amity_1;

public class VitalSigns {
    private String bloodPressure; // Blood pressure value
    private String pulseRate;     // Pulse rate value
    private String respiratoryRate; // Respiratory rate value
    private String weight;        // Weight value
    private String temperature;   // Temperature value
    private String cc;            // Chief complaint
    private String pe;            // Physical examination
    private String dx;            // Diagnosis
    private String meds;          // Medications
    private String labs;          // Lab results

    // Constructor for the VitalSigns class
    public VitalSigns(String bloodPressure, String pulseRate, String respiratoryRate,
                      String weight, String temperature, String cc, String pe, String dx, String meds, String labs) {
        this.bloodPressure = bloodPressure;
        this.pulseRate = pulseRate;
        this.respiratoryRate = respiratoryRate;
        this.weight = weight;
        this.temperature = temperature;
        this.cc = cc;
        this.pe = pe;
        this.dx = dx;
        this.meds = meds;
        this.labs = labs;
    }

    // Getter methods
    public String getBloodPressure() {
        return bloodPressure;
    }

    public String getPulseRate() {
        return pulseRate;
    }

    public String getRespiratoryRate() {
        return respiratoryRate;
    }

    public String getWeight() {
        return weight;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getCc() {
        return cc;
    }

    public String getPe() {
        return pe;
    }

    public String getDx() {
        return dx;
    }

    public String getMeds() {
        return meds;
    }

    public String getLabs() {
        return labs;
    }
}
