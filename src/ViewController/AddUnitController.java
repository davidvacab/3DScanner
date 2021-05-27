package ViewController;

import OrdersControl.Box;
import OrdersControl.Order;
import OrdersControl.Unit;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddUnitController implements Initializable {
    public TextField serialNumberTextField;
    public TextField mACAddressTextField;
    public TextField integrationIDTextField;

    public Button cancelButton;
    public Button saveButton;

    private Order order;
    private Box box;
    private Unit unit = new Unit();

    public AddUnitController(Order order, Box box){
        this.order = order;
        this.box = box;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validateInput();
        checkSave();
    }

    private void validateInput(){
        serialNumberTextField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("^[a-zA-Z0-9_]{0,20}$")) ? change : null));
        mACAddressTextField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("^[0-9a-fA-F]{0,12}$")) ? change : null));
        integrationIDTextField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("^[a-zA-Z0-9_]{0,9}$")) ? change : null));
    }

    private void checkSave (){
        BooleanBinding checkSave;
        if(box.getUnitsList().get(0).getMacAddress() != null){
            checkSave = serialNumberTextField.textProperty().isEmpty().or(mACAddressTextField.textProperty().isEmpty());
        } else {
            checkSave = serialNumberTextField.textProperty().isEmpty();
        }
        saveButton.disableProperty().bind(checkSave);
    }

    public void onSerialNumberTextField() {
        mACAddressTextField.requestFocus();
    }

    public void onMACAddressTextField() {
        integrationIDTextField.requestFocus();
    }

    public void onIntegrationIDTextField() {
        onSaveButton();
    }

    public void onSaveButton() {
        String serialNumber = serialNumberTextField.getText().toUpperCase();
        String macAddress = mACAddressTextField.getText().toUpperCase();
        String integrationID = integrationIDTextField.getText().toUpperCase();
        if(checkSerialNumber(serialNumber) && checkMACAddress(macAddress) && checkIntegrationID(integrationID)){
            box.addUnit(unit);
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        }
    }

    private boolean checkSerialNumber(String serialNumber){
        boolean isValid = false;
        if(serialNumber == null || serialNumber.trim().isEmpty()){
            errorMessage("Empty Field", "Serial Number Text Field can not be empty");
            serialNumberTextField.requestFocus();
        } else {
            if (serialNumber.length() > 10 && checkDuplicatedSN(serialNumber)) {
                unit.setSerialNumber(serialNumber);
                isValid = true;
            } else {
                if(serialNumber.length() <= 10){
                    errorMessage("Invalid Input:", "Too short for a Serial Number");
                } else {
                    errorMessage("Duplicated","Duplicated Serial Number");
                }
                serialNumberTextField.clear();
                serialNumberTextField.requestFocus();
            }
        }
        return isValid;
    }

    private boolean checkMACAddress(String macAddress){
        boolean isValid = false;
        if(box.getUnitsList().get(0).getMacAddress() != null){
            if(macAddress == null || macAddress.trim().isEmpty()){
                errorMessage("Empty Field", "MAC Address Text Field can not be empty");
                mACAddressTextField.requestFocus();
            } else {
                if (macAddress.length() == 12 && !checkDuplicatedMAC(macAddress) && macAddress.matches("-?[0-9a-fA-F]+")) {
                    unit.setMacAddress(macAddress);
                    isValid = true;
                }
                else {
                    if (macAddress.length() != 12) {
                        errorMessage("Invalid Input","MAC Address must be 12 digits long");
                    } else {
                        if(macAddress.matches("-?[0-9a-fA-F]+")){
                            errorMessage("Duplicated", "Duplicated MAC Address");
                        } else {
                            errorMessage("Invalid Input","MAC Address must be in hexadecimal format");
                        }
                    }
                    mACAddressTextField.clear();
                    mACAddressTextField.requestFocus();
                }
            }
        } else {
            isValid = true;
        }
        return isValid;
    }

    private boolean checkIntegrationID(String integrationID){
        boolean isValid = false;
        if (integrationID != null && !integrationID.trim().isEmpty()) {
            if(integrationID.startsWith("WA") && !checkDuplicatedIntID(integrationID) && integrationID.length() == 9){
                unit.setIntID(integrationID.toUpperCase());
                isValid = true;
            } else{
                if(!integrationID.startsWith("WA")){
                    errorMessage("Invalid Input" ,"Integration ID must start with 'WA'.");
                } else{
                    if(integrationID.length() != 9){
                        errorMessage("Invalid length", "Integration ID must be 9 characters long");
                    } else {
                        errorMessage("Duplicated", "Duplicated Integration ID");
                    }
                }
                integrationIDTextField.clear();
                integrationIDTextField.requestFocus();
            }
        } else {
            isValid = true;
        }
        return isValid;
    }

    public void onCancelButton() {
        Optional<ButtonType> result = askConfirmation();
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.close();
            }
        }
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
        return !duplicatedSN;
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
        return duplicatedIntID;
    }

    private void errorMessage(String title, String content){
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Optional<ButtonType> askConfirmation(){
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel adding new Unit");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to cancel? \n All information will be lost.");
        return alert.showAndWait();
    }
}
