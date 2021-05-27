package OrdersControl;

import java.io.Serializable;

public class Unit implements Serializable {
    private String serialNumber;
    private String macAddress;
    private String intID;

    public Unit(String serialNumber, String macAddress, String intID) {
        this.serialNumber = serialNumber;
        this.macAddress = macAddress;
        this.intID = intID;
    }

    public Unit(){
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getIntID() {
        return intID;
    }

    public void setIntID(String intID) {
        this.intID = intID;
    }
}
