package me.unibike.unilock.data.lock;

/**
 * @author LuoLiangchen
 * @since 16/11/5
 */

public class Lock {

    private int id;
    private int status;
    private String macAddr;
    private int battery;
    private int bikeId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getBikeId() {
        return bikeId;
    }

    public void setBikeId(int bikeId) {
        this.bikeId = bikeId;
    }
}
