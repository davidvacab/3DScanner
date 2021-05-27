package OrdersControl;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    private String orderNumber;
    private int orderQuantity;
    private int printCount;
    private int unitsPerBox;
    private String optionNumber;
    private ArrayList<Box> boxesList = new ArrayList<>();

    public Order() {
    }

    public Order(String orderNumber, int orderQuantity, int unitsPerBox, String optionNumber) {
        this.orderNumber = orderNumber;
        this.orderQuantity = orderQuantity;
        this.optionNumber = optionNumber;
        this.unitsPerBox = unitsPerBox;
    }

    public void addBox(Box box) {
        boxesList.add(box);
    }

    public void deleteBox(Box box) {
        boxesList.remove(box);
    }

    public ArrayList<Box> getBoxesList() {
        return boxesList;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public void setOptionNumber(String optionNumber) {
        this.optionNumber = optionNumber;
    }

    public void setUnitsPerBox(int unitsPerBox) {
        this.unitsPerBox = unitsPerBox;
    }

    public void setPrintCount(int printCount) {
        this.printCount = printCount;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public String getOptionNumber() {
        return optionNumber;
    }

    public int getUnitsPerBox() {
        return unitsPerBox;
    }

    public int getPrintCount() {
        int unitsScanned = 0;
        for (Box box : boxesList) {
            unitsScanned += box.getUnitsList().size();
        }
        printCount = orderQuantity - unitsScanned;
        return printCount;
    }

}
