package ViewController;

import OrdersControl.Box;
import OrdersControl.Unit;
import Printing.LabelPrinter;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
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

public class PrintBoxIDController implements Initializable {
    public Button cancel;
    public Button rePrint;
    public Button clear;
    public Label displayArea;
    public TextField inputField;
    public ScrollPane scrollDisplay;
    public TextField unitsPerBoxField;
    public TextField scanBeforePrintField;
    public CheckBox autoGenerateIntIDCheckBox;

    private final LabelPrinter printer;

    private ArrayList<String> intIDList = new ArrayList<>();
    private ArrayList<Box> boxesList =  new ArrayList<>();

    private int unitsPerBox;
    private int scanBeforePrintNumber = 1;

    private String scanError = "";



    public PrintBoxIDController() throws AWTException {
        unitsPerBox = 0;
        printer = new LabelPrinter();
    }
    public PrintBoxIDController(int unitsPerBox) throws AWTException {
        this.unitsPerBox = unitsPerBox;
        printer = new LabelPrinter();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scanBeforePrintField.setText(Integer.toString(scanBeforePrintNumber));
        if(unitsPerBox != 0){
            unitsPerBoxField.setText(Integer.toString(unitsPerBox));
        }
        validateData();
        checkSave();
        focusedFields();
        setScanTitle();
        Platform.runLater(()->inputField.requestFocus());
    }

    private void validateData(){
        unitsPerBoxField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("([1-9][0-9]*)?")) ? change : null));
        scanBeforePrintField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("([1-9][0-9]*)?")) ? change : null));
    }

    private void checkSave(){
        BooleanBinding checkSave = unitsPerBoxField.textProperty().isEmpty()
                .or(scanBeforePrintField.textProperty().isEmpty());
        inputField.disableProperty().bind(checkSave);
    }

    private void focusedFields(){
        unitsPerBoxField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(!newVal) {
                if (unitsPerBoxField.getText() == null || unitsPerBoxField.getText().trim().isEmpty()) {
                    errorMessage("Units per Box field can't be empty.");
                    unitsPerBoxField.requestFocus();
                } else {
                    unitsPerBox = Integer.parseInt(unitsPerBoxField.getText());
                    refreshView();
                    scanBeforePrintField.requestFocus();
                }
            }
        });

        scanBeforePrintField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(!newVal){
                if(scanBeforePrintField.getText() == null || scanBeforePrintField.getText().trim().isEmpty()){
                    errorMessage("Scan before Print field can't be empty.");
                    scanBeforePrintField.requestFocus();
                } else {
                    scanBeforePrintNumber = Integer.parseInt(scanBeforePrintField.getText());
                    refreshView();
                    inputField.requestFocus();
                }
            }
        });
    }

    public void onUnitsPerBoxField() {
        scanBeforePrintField.requestFocus();
    }

    public void onScanBeforePrintField(){
        inputField.requestFocus();
    }

    public void onAutoGenerateIntIDCheckBox(){
        refreshView();
        inputField.requestFocus();
    }

    public void onCancel() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
        inputField.requestFocus();
    }

    public void onRePrint(){
        printer.printBoxID(boxesList);
        inputField.requestFocus();
    }

    public void onClear(){
        intIDList.clear();
        refreshView();
        inputField.requestFocus();
    }

    public void onInputField()  {
        scanError = "";
        String input = inputField.getText().toUpperCase();
        intIDBarcodeHandler(input);
        inputField.clear();
        refreshView();
        scrollDisplay.vvalueProperty().bind(displayArea.heightProperty());
        scrollDisplay.vvalueProperty().bind(displayArea.widthProperty());
    }



    private void intIDBarcodeHandler(String input)  {
        if(input.startsWith("WA") && !intIDList.contains(input) && input.length() == 9){
            if (autoGenerateIntIDCheckBox.isSelected()){
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
            Toolkit.getDefaultToolkit().beep();
        }
        if(intIDList.size() == unitsPerBox){
            if (autoGenerateIntIDCheckBox.isSelected()){
                String content = "Is this correct? \n" + "First: " + intIDList.get(0) + "\n" + "Last: " + intIDList.get(unitsPerBox-1);
                Optional<ButtonType> result = askConfirmation(content);
                if(result.isPresent()) {
                    if (result.get() == ButtonType.OK) {
                        printBoxID();
                    } else {
                        intIDList.clear();
                        inputField.requestFocus();
                    }
                }
            } else {
                printBoxID();
            }
        }
        refreshView();
    }

    private void printBoxID() {
        rePrint.setDisable(false);
        Collections.sort(intIDList);
        Box box = new Box();
        for (String s : intIDList) {
            Unit unit = new Unit();
            unit.setIntID(s);
            box.addUnit(unit);
        }
        boxesList.add(box);
        if(boxesList.size() == scanBeforePrintNumber) {
            printer.printBoxID(boxesList);
            boxesList = new ArrayList<>();
        }
        intIDList.clear();
        setScanTitle();
    }

    private void setScanTitle(){
        scanError = "";
        refreshView();
        Toolkit.getDefaultToolkit().beep();
    }

    private String getDisplayList() {
        StringBuilder displayList = new StringBuilder();
        Collections.sort(intIDList);
        for (int i = 0; i < intIDList.size(); i++) {
            displayList.append(i+1);
            displayList.append(".-  ");
            displayList.append(intIDList.get(i));
            displayList.append("\n");
        }
        return displayList.toString();
    }

    private void refreshView(){
        displayArea.setText(
                "Boxes left: " + (scanBeforePrintNumber - boxesList.size()) + "\n\n" +
                "Enter Integration ID" + "\n\n" +
                scanError + "\n" +
                        "_________________________________________ \n\n"+
                getDisplayList()
                );
    }

    public void onClick() {
        inputField.requestFocus();
    }

    private String autoIntegrationIDGenerator(String input){
        char[] integrationIDArray = input.toCharArray();
        String newIntegrationID;
        for(int i = integrationIDArray.length-1; i >= 0; i--){
            if(integrationIDArray[i] == 'Z'){
                integrationIDArray[i]= nextChar(integrationIDArray[i]);
            } else {
                integrationIDArray[i]= nextChar(integrationIDArray[i]);
                break;
            }
        }
        newIntegrationID = String.copyValueOf(integrationIDArray);
        return newIntegrationID;
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
            case 'H': newChar = 'J'; break;
            case 'I': newChar = 'J'; break;
            case 'J': newChar = 'K'; break;
            case 'K': newChar = 'L'; break;
            case 'L': newChar = 'M'; break;
            case 'M': newChar = 'N'; break;
            case 'N': newChar = 'P'; break;
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

    private Optional<ButtonType> askConfirmation(String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation required before printing.");
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }

    private void errorMessage(String content){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Empty text Field");
        alert.setHeaderText(null);
        alert.setContentText(content);
        errorBeep();
        alert.showAndWait();
    }

    private void errorBeep(){
        Media hit;
        hit = new Media(new File("error.wav").toURI().toString());
        MediaPlayer player = new MediaPlayer(hit);
        player.play();
    }
}
