package OrdersControl;

import java.io.Serializable;
import java.util.ArrayList;

public class Box implements Serializable {
    private ArrayList<Unit> unitsList = new ArrayList<>();
    private String boxID;
    private int unitsInBox;

    public Box(){

    }

    public void addUnit(Unit unit){
        unitsList.add(unit);
    }

    public void deleteUnit(Unit unit){
        unitsList.remove(unit);
    }

    public void setBoxID(String boxID) { this.boxID = boxID; }

    public int getUnitsInBox(){
        unitsInBox = unitsList.size();
        return unitsInBox;
    }

    public String getBoxID() { return boxID; }

    public ArrayList<Unit> getUnitsList() {
        return unitsList; }
}
