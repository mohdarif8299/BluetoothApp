package com.latticeapplication.models;

public class Devices {
    private String deviceName;
    private String deviceAddress;
    private boolean isPaired;
    private boolean isConnected;

    public Devices(String deviceName, String deviceAddress, boolean isPaired, boolean isConnected) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.isPaired = isPaired;
        this.isConnected = isConnected;
    }

    public Devices() {
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public boolean isPaired() {
        return isPaired;
    }

    public void setPaired(boolean paired) {
        isPaired = paired;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
