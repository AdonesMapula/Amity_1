package com.example.amity_1;

public class VitalSigns {
    private String bloodPressure;
    private String pulseRate;
    private String respiratoryRate;
    private String weight;
    private String temperature;

    public VitalSigns(String bloodPressure, String pulseRate, String respiratoryRate, String weight, String temperature) {
        this.bloodPressure = bloodPressure;
        this.pulseRate = pulseRate;
        this.respiratoryRate = respiratoryRate;
        this.weight = weight;
        this.temperature = temperature;
    }

    // Getters
    public String getBloodPressure() { return bloodPressure; }
    public String getPulseRate() { return pulseRate; }
    public String getRespiratoryRate() { return respiratoryRate; }
    public String getWeight() { return weight; }
    public String getTemperature() { return temperature; }

    // Setters
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }
    public void setPulseRate(String pulseRate) { this.pulseRate = pulseRate; }
    public void setRespiratoryRate(String respiratoryRate) { this.respiratoryRate = respiratoryRate; }
    public void setWeight(String weight) { this.weight = weight; }
    public void setTemperature(String temperature) { this.temperature = temperature; }

    @Override
    public String toString() {
        return "Blood Pressure: " + bloodPressure + " mmHg, Pulse Rate: " + pulseRate + " bpm, Respiratory Rate: " + respiratoryRate +
                " breaths/min, Weight: " + weight + " kg, Temperature: " + temperature + " °C";
    }
}
