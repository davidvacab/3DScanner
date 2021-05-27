package ViewController;

import OrdersControl.Box;
import OrdersControl.Order;
import OrdersControl.Unit;
import Printing.LabelPrinter;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

public class ScanOrderController implements Initializable {
    public Button printButton;
    public Button rePrintButton;
    public Button clearButton;
    public Button backButton;
    public ChoiceBox<String> scanOptionsChoiceBox;
    public Label displayArea;
    public TextField inputField;
    public Label orderNumberLabel;
    public ScrollPane scrollDisplay;
    public TextField autoZyxelTextField;
    public CheckBox autoIntIDCheckBox;
    public CheckBox printingCartCheckBox;

    private final Order order;
    private final LabelPrinter printer;

    private int wayOfScan = 0;
    private int autoZyxelValue = 12;

    private ArrayList<String> SNList = new ArrayList<>();
    private ArrayList<String> MACList = new ArrayList<>();
    private ArrayList<String> intIDList = new ArrayList<>();
    private ArrayList<Box> boxesList = new ArrayList<>();
    private ArrayList<Box> previousBoxesList = new ArrayList<>();

    private int unitsPerBox;
    private int stage = 1;
    private int SNMACStage = 1;
    private int intIDCounter = 0;
    private String scanVariable = "";
    private String scanError = "";
    private boolean finish = false;

    public ScanOrderController(Order order) throws AWTException {
        this.order = order;
        unitsPerBox = order.getUnitsPerBox();
        printer = new LabelPrinter(order);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        autoZyxelTextField.setText(Integer.toString(autoZyxelValue));
        orderNumberLabel.setText("Order: " + order.getOrderNumber());
        scanOptionsChoiceBox.setItems(FXCollections.observableArrayList("SmartRG", "Zyxel", "Singles", "ADVA", "Auto Zyxel", "No MAC", "Double MAC"));
        scanOptionsChoiceBox.setValue("SmartRG");
        refreshView();
        listeners();
        checkEmptyTextFields();
        validateInput();
        Platform.runLater(()->inputField.requestFocus());
    }

    private void validateInput(){
        autoZyxelTextField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("([1-9][0-9]*)?")) ? change : null));
    }

    private void checkEmptyTextFields(){
        BooleanBinding checkEmptyFields = autoZyxelTextField.textProperty().isEmpty();
        inputField.disableProperty().bind(checkEmptyFields);
    }

    private void listeners(){
        scanOptionsChoiceBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldValue, newValue) -> {
            wayOfScan = (int) newValue;
            scanError = "";
            refreshView();
            inputField.requestFocus();
            autoZyxelTextField.setText(Integer.toString(autoZyxelValue));
            if(wayOfScan == 4){
                autoZyxelTextField.setDisable(false);
                autoZyxelTextField.requestFocus();
            } else {
                autoZyxelTextField.setDisable(true);
            }
        });

        autoZyxelTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(!newVal){
                if(autoZyxelTextField.getText() == null || autoZyxelTextField.getText().trim().isEmpty()){
                    errorMessage();
                    autoZyxelTextField.requestFocus();
                } else {
                    autoZyxelValue = Integer.parseInt(autoZyxelTextField.getText());
                    inputField.requestFocus();
                }
            }
        });
    }

    public void onAutoZyxelTextField(){
        inputField.requestFocus();
    }

    public void onAutoIntIDCheckBox(){
        inputField.requestFocus();
    }

    public void onPrintingCartCheckBox(){
        if (printingCartCheckBox.isSelected()){
            printButton.setDisable(false);
        } else {
            Optional<ButtonType> result = askConfirmation("Clear Boxes List?", "Do you want to clear the boxes List?");
            if(result.isPresent()) {
                if (result.get() == ButtonType.OK) {
                    for (Box box: boxesList){
                        order.deleteBox(box);
                    }
                    boxesList.clear();
                    printButton.setDisable(true);
                } else {
                    printingCartCheckBox.setSelected(true);
                }
            }
        }
        refreshView();
        inputField.requestFocus();
    }

    public void onClearButton(){
        SNList.clear();
        MACList.clear();
        intIDList.clear();
        SNMACStage = 1;
        refreshView();
        inputField.requestFocus();
    }

    public void onRePrintButton(){
        if(stage == 2){
            printer.printLabels(boxesList, false);
        } else{
            if (wayOfScan == 3){
                printer.printLabels(previousBoxesList,false);
            } else{
                printer.printBoxID(previousBoxesList);
            }
        }
        inputField.requestFocus();
    }

    public void onPrintButton() {
        printButton.setDisable(true);
        rePrintButton.setDisable(false);
        printingCartCheckBox.setDisable(true);
        if(wayOfScan != 3){
            stage = 2;
        }
        printer.printLabels(boxesList, false);
        refreshView();
        inputField.requestFocus();
    }

    public void onBackButton(){
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }

    public void onClick() {
        inputField.requestFocus();
    }

    public void onInputField()  {
        String input = inputField.getText().toUpperCase();
        inputField.clear();
        scanError = "";
        switch(stage) {
            case 1:
                switch (wayOfScan) {
                    case 0:
                        if (order.getPrintCount() >= unitsPerBox) {
                            smartRGBarcodeHandler(input);
                        } else { singlesBarcodeHandler(input); }
                        break;
                    case 1:
                        if (order.getPrintCount() >= unitsPerBox) {
                            zyxelBarcodeHandler(input);
                        } else { singlesBarcodeHandler(input); }
                        break;
                    case 2:
                        singlesBarcodeHandler(input);
                        break;
                    case 3:
                        advaBarcodeHandler(input);
                        break;
                    case 4:
                        if (order.getPrintCount() >= unitsPerBox) {
                            autoZyxelBarcodeHandler(input);
                        } else { singlesBarcodeHandler(input); }
                        break;
                    case 5:
                        noMACBarcodeHandler(input);
                        break;
                    case 6:
                        doubleMACBarcodeHandler(input);
                        break;
                }
                break;
            case 2:
                intIDBarcodeHandler(input);
                break;
        }
        refreshView();
        scrollDisplay.vvalueProperty().bind(displayArea.heightProperty());
        scrollDisplay.vvalueProperty().bind(displayArea.widthProperty());
    }

    private void smartRGBarcodeHandler(String input){
        if(input.length() > 24 && input.contains("S/N:")){
            String[] parts = input.split(" ");
            for(String line: parts){
                if(line.startsWith("S/N:")){
                    addSerialNumber(line.replaceAll("S/N:", "").replaceAll("\\s", ""));
                } else if (line.startsWith("SN:")){
                    addSerialNumber(line.replaceAll("SN:", "").replaceAll("\\s", ""));
                }
                if(line.startsWith("MAC:")){
                    addMACAddress(line.replaceAll("MAC:", "").replaceAll("\\s", ""));
                }
            }
            if (SNList.size() == unitsPerBox && MACList.size() == unitsPerBox){
                printLabels(true);
            }
        } else {
            if(!input.contains("S/N:")){
                scanError = "Invalid Input:\n SmartRG 3D Barcode must contain 'S/N:'";
            } else {
                scanError = "Invalid Input:\n Too short for a 3D Barcode";
            }
            errorBeep();
        }
    }

    private void zyxelBarcodeHandler(String input){
        if(SNMACStage == 1) {
            addSerialNumber(input);
            Collections.sort(SNList);
            if(SNList.size() == unitsPerBox){
                SNMACStage = 2;
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            addMACAddress(input);
            Collections.sort(MACList);
            if(MACList.size() == unitsPerBox){
                SNMACStage = 1;
                printLabels(false);
            }
        }
    }

    private void advaBarcodeHandler (String input){
        if(!input.startsWith("LBAD")){
            if(input.length() > 20){
                int index = input.indexOf("L");
                String serialNumber = input.substring(index, index + 17);
                addSerialNumber(serialNumber);
            }else{
                errorBeep();
                scanError = "Serial Number must start with LBAD";
            }
        } else{
            addSerialNumber(input);
        }
        if(SNList.size() == unitsPerBox || SNList.size() == order.getPrintCount()){
            printLabels(false);
        }
    }

    private void autoZyxelBarcodeHandler(String input){
        String newInput = input;
        if(SNMACStage == 1) {
            if(Character.isDigit(newInput.charAt(newInput.length()-1))){
                for (int i = 0; i < unitsPerBox; i++) {
                    if(addSerialNumber(newInput)){
                        newInput = autoZyxelSNGenerator(newInput);
                    } else {
                        break;
                    }
                }
                if(SNList.size() == unitsPerBox){
                    Toolkit.getDefaultToolkit().beep();
                    SNMACStage = 2;
                }
            } else {
                scanError = "Invalid Input:\n Serial Number must end with a number";
               errorBeep();
            }
        } else {
            for (int i = 0; i < unitsPerBox; i++) {
                if(addMACAddress(newInput)){
                    newInput = autoZyxelMACGenerator(newInput);
                } else {
                    break;
                }
            }
            if(MACList.size() == unitsPerBox){
                String content = "Is this correct? \n" + "S/N: " + SNList.get(0) + "\n" + "MAC: " + MACList.get(0);
                Optional<ButtonType> result = askConfirmation("Confirmation required before printing.", content);
                if(result.isPresent()) {
                    if (result.get() == ButtonType.OK) {
                        printLabels(false);
                        SNMACStage = 1;
                    } else {
                        SNList.clear();
                        MACList.clear();
                        SNMACStage = 1;
                        inputField.requestFocus();
                    }
                }
            }
        }
    }

    private void singlesBarcodeHandler(String input){
        if (SNMACStage == 1) {
            if(addSerialNumber(input)){
                SNMACStage = 2;
            }
        } else {
            if(addMACAddress(input)){
                SNMACStage = 1;
            }
            if(MACList.size() == unitsPerBox || MACList.size() == order.getPrintCount()){
                printLabels(false);
            }
        }
    }

    private void noMACBarcodeHandler(String input){
        addSerialNumber(input);
        if(SNList.size() == unitsPerBox || SNList.size() == order.getPrintCount()){
            printLabels(false);
        }
    }

    private void doubleMACBarcodeHandler(String input){
        addMACAddress(input);
        if(SNList.size() == unitsPerBox || SNList.size() == order.getPrintCount()){
            printLabels(false);
        }
    }

    private void intIDBarcodeHandler(String input)  {
        if(input.startsWith("WA") && !checkDuplicatedIntID(input) && input.length() == 9){
            if (autoIntIDCheckBox.isSelected()){
                String newInput = input;
                for (int i = 0; i < unitsPerBox; i++){
                    intIDList.add(newInput);
                    newInput = autoIntegrationIDGenerator(newInput);
                }
            } else {
                intIDList.add(input);
            }
        } else{
            if(!input.startsWith("WA")){
                scanError = "Invalid Input: \n Integration ID must start with 'WA'.";
            } else{
                if(input.length() != 9){
                    scanError = "Invalid length";
                } else {
                    scanError = "Duplicated Integration ID";
                }
            }
            errorBeep();
        }
        if(intIDList.size() == unitsPerBox){
            if (autoIntIDCheckBox.isSelected()){
                String content = "Is this correct? \n" + "First: " + intIDList.get(0) + "\n" + "Last: " + intIDList.get(unitsPerBox-1);
                Optional<ButtonType> result = askConfirmation("Confirmation required before printing.", content);
                if(result.isPresent()) {
                    if (result.get() == ButtonType.OK) {
                        printBoxID();
                    } else {
                        intIDList.clear();
                        autoIntIDCheckBox.setSelected(false);
                        inputField.requestFocus();
                    }
                }
            } else {
                printBoxID();
            }
        }
        refreshView();
    }

    private String autoZyxelSNGenerator(String input){
        char[] serialNumberArray = input.toCharArray();
        String newSerialNumber;
        for(int i = serialNumberArray.length-1; i >= 0; i--){
            if(serialNumberArray[i] == '9'){
                serialNumberArray[i]= nextNumber(serialNumberArray[i]);
            } else {
                serialNumberArray[i]= nextNumber(serialNumberArray[i]);
                break;
            }
        }
        newSerialNumber = String.copyValueOf(serialNumberArray);
        return newSerialNumber;
    }

    private String autoZyxelMACGenerator(String input){
        char[] macArray = input.toCharArray();
        String newMAC;
        for(int j = 0; j < autoZyxelValue; j++){
            for(int i = macArray.length-1; i >= 0; i--){
                if(macArray[i] == 'F'){
                    macArray[i]= enm6726NextChar(macArray[i]);
                } else {
                    macArray[i]= enm6726NextChar(macArray[i]);
                    break;
                }
            }
        }
        newMAC = String.copyValueOf(macArray);
        return newMAC;
    }

    private String autoIntegrationIDGenerator(String input){
        char[] integrationIDArray = input.toCharArray();
        String newIntegrationID;
        for(int i = integrationIDArray.length-1; i >= 0; i--){
            if(integrationIDArray[i] == 'Z'){
                integrationIDArray[i] = nextChar(integrationIDArray[i]);
            } else {
                integrationIDArray[i]= nextChar(integrationIDArray[i]);
                break;
            }
        }
        newIntegrationID = String.copyValueOf(integrationIDArray);
        return newIntegrationID;
    }

    private boolean addSerialNumber(String input){
        boolean isValid = false;
        if (input.length() > 10 && !checkDuplicatedSN(input)) {
            if (input.length() == 12 && input.matches("-?[0-9a-fA-F]+")){
                scanError = "Invalid Input:\n MAC Address can't be used as Serial Number";
                errorBeep();
            } else {
                SNList.add(input);
                isValid = true;
            }
        } else {
            if(input.length() <= 10){
                scanError = "Invalid Input:\n Too short for a Serial Number";
            } else {
                scanError = "Duplicated Serial Number";
            }
            errorBeep();
        }
        return isValid;
    }

    private boolean addMACAddress(String input){
        boolean isValid = false;
        boolean checkedMAC = checkDuplicatedMAC(input);
        if (input.length() == 12 && !checkedMAC && input.matches("-?[0-9a-fA-F]+")) {
            if(wayOfScan == 6){
                SNList.add(input);
            }
            MACList.add(input);
            isValid = true;
        }
        else {
            if (input.length() != 12) {
                scanError = "Invalid Input:\n MAC Address must be 12 digits long";
            } else {
                if(checkedMAC){
                    scanError = "Duplicated MAC Address";
                } else {
                    scanError = "Invalid Input:\n MAC Address must be in hexadecimal format";
                }
            }
            errorBeep();
        }
        return isValid;
    }

    private boolean checkDuplicatedSN (String input){
        boolean duplicatedSN = false;
        for(Box box: order.getBoxesList()){
            for(Unit unit: box.getUnitsList()){
                if (unit.getSerialNumber().equals(input)) {
                    duplicatedSN = true;
                    break;
                }
            }
        }
        if(SNList.contains(input)){
            duplicatedSN = true;
        }
        return duplicatedSN;
    }

    private boolean checkDuplicatedMAC(String input){
        boolean duplicatedMAC = false;
        for(Box box: order.getBoxesList()){
            for(Unit unit: box.getUnitsList()){
                if (unit.getMacAddress().equals(input)) {
                    duplicatedMAC = true;
                    break;
                }
            }
        }
        if(MACList.contains(input)){
            duplicatedMAC = true;
        }
        return duplicatedMAC;
    }

    private boolean checkDuplicatedIntID(String input){
        boolean duplicatedIntID = false;
        for(Box box: order.getBoxesList()){
            for(Unit unit: box.getUnitsList()){
                if(unit.getIntID() != null){
                    if (unit.getIntID().equals(input)) {
                        duplicatedIntID = true;
                        break;
                    }
                }
            }
        }
        if(intIDList.contains(input)){
            duplicatedIntID = true;
        }
        return duplicatedIntID;
    }

    private void createBox(){
        Box box = new Box();
        box.setBoxID(boxIDGenerator());
        for (int i = 0; i < SNList.size(); i++) {
            Unit unit = new Unit();
            unit.setSerialNumber(SNList.get(i));
            if(!MACList.isEmpty()) {
                unit.setMacAddress(MACList.get(i));
            }
            box.addUnit(unit);
        }
        if(SNList.size() == order.getPrintCount() && SNList.size() != unitsPerBox && !printingCartCheckBox.isSelected()){
            finish = true;
        }
        order.addBox(box);
        boxesList.add(box);
        SNList.clear();
        MACList.clear();
    }

    private void printLabels(boolean wait) {
        createBox();
        if(!printingCartCheckBox.isSelected() || order.getPrintCount() == (order.getOrderQuantity()%order.getUnitsPerBox())){
            printer.printLabels(boxesList, wait);
            rePrintButton.setDisable(false);
            if(wayOfScan != 3){
                stage = 2;
            } else{
                previousBoxesList = boxesList;
                boxesList = new ArrayList<>();
            }
            if(printingCartCheckBox.isSelected()){
                printButton.setDisable(true);
                printingCartCheckBox.setDisable(true);
            }
        }
        Toolkit.getDefaultToolkit().beep();
        refreshView();
    }

    private void printBoxID() {
        Collections.sort(intIDList);
        for(int i = 0; i < intIDList.size(); i++){
            boxesList.get(intIDCounter).getUnitsList().get(i).setIntID(intIDList.get(i));
        }
        intIDCounter++;
        if (intIDCounter == boxesList.size()){
            if(printingCartCheckBox.isSelected()){
                printButton.setDisable(false);
                printingCartCheckBox.setDisable(false);
            }
            printer.printBoxID(boxesList);
            previousBoxesList = boxesList;
            boxesList = new ArrayList<>();
            stage = 1;
            intIDCounter = 0;
            if(order.getPrintCount() == 0) {
                finish = true;
            }
            if(order.getPrintCount() == (order.getOrderQuantity()%order.getUnitsPerBox())){
                printingCartCheckBox.setDisable(true);
                printingCartCheckBox.setSelected(false);
                printButton.setDisable(true);
            }
        }
        intIDList.clear();
        Toolkit.getDefaultToolkit().beep();
        refreshView();
    }

    private void setScanTitle(){
        switch (stage){
            case 1:
                switch (wayOfScan){
                    case 0:
                        if(order.getPrintCount() >= unitsPerBox){
                            scanVariable = "Scan 3D Barcode";
                        } else {
                            scanOptionsChoiceBox.setValue("Singles");
                            if(SNMACStage == 1){
                                scanVariable = "Scan Serial Number";
                            } else{
                                scanVariable = "Scan MAC Address";
                            }
                        }
                        break;
                    case 1:
                        if(order.getPrintCount() >= unitsPerBox){
                            if(SNMACStage == 1){
                                scanVariable = "Scan all Serial Numbers or 3D Barcode";
                            } else {
                                scanVariable = "Scan all MAC Addresses or 3D Barcode";
                            }
                        } else {
                            scanOptionsChoiceBox.setValue("Singles");
                            if(SNMACStage == 1){
                                scanVariable = "Scan Serial Number";
                            } else{
                                scanVariable = "Scan MAC Address";
                            }
                        }
                        break;
                    case 2:
                        if(SNMACStage == 1){
                            scanVariable = "Scan Serial Number";
                        } else{
                            scanVariable = "Scan MAC Address";
                        }
                        break;
                    case 3:
                        scanVariable = "Scan 3D Barcode or Serial Number";
                        break;
                    case 4:
                        if(order.getPrintCount() >= unitsPerBox){
                            if(SNMACStage == 1){
                                scanVariable = "Scan First Serial Number from Box";
                            } else{
                                scanVariable = "Scan First MAC Address from Box";
                            }
                        } else {
                            scanOptionsChoiceBox.setValue("Singles");
                            if(SNMACStage == 1){
                                scanVariable = "Scan Serial Number";
                            } else{
                                scanVariable = "Scan MAC Address";
                            }
                        }
                        break;
                    case 5:
                        scanVariable = "Scan all Serial Numbers or 3D Barcode";
                        break;
                    case 6:
                        scanVariable = "Scan all MAC Addresses or 3D Barcode";
                        break;
                }
                break;
            case 2:
                scanVariable = "Enter Integration ID";
                break;
        }
        if(finish){
            finishOrder();
        }
    }

    private void finishOrder(){
        scanVariable = "Order Finished \n\n Please close this window";
        clearButton.setDisable(true);
        scanOptionsChoiceBox.setDisable(true);
        autoIntIDCheckBox.setDisable(true);
        printingCartCheckBox.setDisable(true);
        autoZyxelTextField.setDisable(true);
        autoZyxelTextField.clear();
        Toolkit.getDefaultToolkit().beep();
    }

    private String getDisplayList() {
        StringBuilder displayList = new StringBuilder();
        switch (stage) {
            case 1:
                for (int i = 0; i < SNList.size(); i++) {
                    displayList.append(i+1);
                    displayList.append(".-  ");
                    displayList.append(SNList.get(i));
                    try{
                        displayList.append("   -   ");
                        displayList.append(MACList.get(i));
                    } catch (IndexOutOfBoundsException ignored){ }
                    displayList.append("\n");
                }
                break;
            case 2:
                Collections.sort(intIDList);
                for (int i = 0; i < intIDList.size(); i++) {
                    displayList.append(i+1);
                    displayList.append(".-  ");
                    displayList.append(intIDList.get(i));
                    displayList.append("\n");
                }
                break;
        }
        return displayList.toString();
    }

    private void refreshView(){
        setScanTitle();
        displayArea.setText(
                "Units: "+ order.getPrintCount() +" of " + order.getOrderQuantity() + "\n" +
                        (printingCartCheckBox.isSelected()? (stage == 1 ? "Boxes in Cart: " + boxesList.size() + "\n" : "Boxes in Cart: " + intIDCounter) + "\n" : "") +
                        "_________________________________________ \n\n" +
                scanVariable + "\n\n" +
                scanError + "\n" +
                        "_________________________________________ \n\n"+
                getDisplayList()
                );
    }

    private String boxIDGenerator(){
        char[] uniqueID = null;
        String boxID;
        for(Box box: order.getBoxesList()){
            uniqueID = box.getBoxID().toCharArray();
        }
        if(uniqueID == null){
            boxID = order.getOrderNumber() + "-BX-001";
        } else {
            for(int i = uniqueID.length-1; i >= 0; i--){
                if(uniqueID[i] == 'Z'){
                    uniqueID[i]= nextChar(uniqueID[i]);
                } else {
                    uniqueID[i]= nextChar(uniqueID[i]);
                    break;
                }
            }
            boxID = String.copyValueOf(uniqueID);
        }
        return boxID;
    }

    private char nextChar(char previousChar){
        char newChar;
        switch (previousChar){
            case '0': newChar = '1'; break;
            case '1': newChar = '2'; break;
            case '2': newChar = '3'; break;
            case '3': newChar = '4'; break;
            case '4': newChar = '5'; break;
            case '5': newChar = '6'; break;
            case '6': newChar = '7'; break;
            case '7': newChar = '8'; break;
            case '8': newChar = '9'; break;
            case '9': newChar = 'A'; break;
            case 'A': newChar = 'B'; break;
            case 'B': newChar = 'C'; break;
            case 'C': newChar = 'D'; break;
            case 'D': newChar = 'E'; break;
            case 'E': newChar = 'F'; break;
            case 'F': newChar = 'G'; break;
            case 'G': newChar = 'H'; break;
            case 'H':
                if (stage == 1){
                    newChar = 'I';
                } else {
                    newChar = 'J';
                }
                 break;
            case 'I': newChar = 'J'; break;
            case 'J': newChar = 'K'; break;
            case 'K': newChar = 'L'; break;
            case 'L': newChar = 'M'; break;
            case 'M': newChar = 'N'; break;
            case 'N':
                if (stage == 1){
                    newChar = 'O';
                } else {
                    newChar = 'P';
                }
                break;
            case 'O': newChar = 'P'; break;
            case 'P': newChar = 'Q'; break;
            case 'Q': newChar = 'R'; break;
            case 'R': newChar = 'S'; break;
            case 'S': newChar = 'T'; break;
            case 'T': newChar = 'U'; break;
            case 'U': newChar = 'V'; break;
            case 'V': newChar = 'W'; break;
            case 'W': newChar = 'X'; break;
            case 'X': newChar = 'Y'; break;
            case 'Y': newChar = 'Z'; break;
            case 'Z': newChar = '0'; break;
            default:
                throw new IllegalStateException("Unexpected value: " + previousChar);
        }
        return newChar;
    }

    private char nextNumber(char previousNumber){
        char newNumber;
        switch (previousNumber){
            case '0': newNumber = '1'; break;
            case '1': newNumber = '2'; break;
            case '2': newNumber = '3'; break;
            case '3': newNumber = '4'; break;
            case '4': newNumber = '5'; break;
            case '5': newNumber = '6'; break;
            case '6': newNumber = '7'; break;
            case '7': newNumber = '8'; break;
            case '8': newNumber = '9'; break;
            case '9': newNumber = '0'; break;
            default:
                throw new IllegalStateException("Unexpected value: " + previousNumber);
        }
        return newNumber;
    }
    private char enm6726NextChar(char previousChar){
        char newChar;
        switch (previousChar){
            case '0': newChar = '1'; break;
            case '1': newChar = '2'; break;
            case '2': newChar = '3'; break;
            case '3': newChar = '4'; break;
            case '4': newChar = '5'; break;
            case '5': newChar = '6'; break;
            case '6': newChar = '7'; break;
            case '7': newChar = '8'; break;
            case '8': newChar = '9'; break;
            case '9': newChar = 'A'; break;
            case 'A': newChar = 'B'; break;
            case 'B': newChar = 'C'; break;
            case 'C': newChar = 'D'; break;
            case 'D': newChar = 'E'; break;
            case 'E': newChar = 'F'; break;
            case 'F': newChar = '0'; break;
            default:
                throw new IllegalStateException("Unexpected value: " + previousChar);
        }
        return newChar;
    }

    private void errorMessage(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Empty text Field");
        alert.setHeaderText(null);
        alert.setContentText("Auto Zyxel field can't be empty.");
        errorBeep();
        alert.showAndWait();
    }

    private void errorBeep(){
        Media hit;
        hit = new Media(new File("error.wav").toURI().toString());
        MediaPlayer player = new MediaPlayer(hit);
        player.play();
    }

    private Optional<ButtonType> askConfirmation(String title, String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}
