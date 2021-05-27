package ViewController;

import OrdersControl.Inventory;
import OrdersControl.Order;
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

public class AddOrderController implements Initializable {
    public TextField orderNumberTextField;
    public TextField orderQuantityTextField;
    public TextField unitsPerBoxTextField;
    public TextField optionNumberTextField;

    public Button cancel;
    public Button save;

    private Inventory inventory;

    public AddOrderController(Inventory inventory){
        this.inventory = inventory;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validateInput();
        checkSave();
    }

    private void validateInput(){
        orderQuantityTextField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("([1-9][0-9]*)?")) ? change : null));
        unitsPerBoxTextField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("([1-9][0-9]*)?")) ? change : null));
        optionNumberTextField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("([1-9][0-9]*)?")) ? change : null));
    }

    private void checkSave (){
        BooleanBinding checkSave =
                orderNumberTextField.textProperty().isEmpty()
                        .or(orderQuantityTextField.textProperty().isEmpty())
                        .or(unitsPerBoxTextField.textProperty().isEmpty())
                        .or(optionNumberTextField.textProperty().isEmpty());
        save.disableProperty().bind(checkSave);
    }

    public void onOrderNumber() {
        orderQuantityTextField.requestFocus();
    }

    public void onOrderQuantity() {
        unitsPerBoxTextField.requestFocus();
    }

    public void onUnitsPerBox() {
        optionNumberTextField.requestFocus();
    }

    public void onOptionNumber() {
        onSave();
    }

    public void onSave() {
        String orderNumber  = orderNumberTextField.getText().toUpperCase();
        int orderQuantity = Integer.parseInt(orderQuantityTextField.getText());
        int unitsPerBox = Integer.parseInt(unitsPerBoxTextField.getText());
        String optionNumber = optionNumberTextField.getText();
        if((orderNumber.startsWith("NC") || orderNumber.startsWith("MFG")) && !checkDuplicatedOrder(orderNumber)){
            Order order = new Order(orderNumber, orderQuantity, unitsPerBox, optionNumber);
            inventory.addOrder(order);
            Stage stage = (Stage) save.getScene().getWindow();
            stage.close();
        } else{
            if(!orderNumber.startsWith("NC") && !orderNumber.startsWith("MFG")) {
                orderNumberTextField.clear();
                errorMessage("Invalid Order Number", "Order Number must start with 'NC' or 'MFG' ");
            } else{
                orderNumberTextField.clear();
                errorMessage("Duplicated Order", "Order Number already on the List");
            }
        }
    }

    private boolean checkDuplicatedOrder(String orderNumber){
        boolean duplicatedOrder = false;
        for(Order order: inventory.getOrdersList()){
            if(order.getOrderNumber().equals(orderNumber)){
                duplicatedOrder = true;
                break;
            }
        }
        return duplicatedOrder;
    }

    public void onCancel() {
        Optional<ButtonType> result = askConfirmation();
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                Stage stage = (Stage) cancel.getScene().getWindow();
                stage.close();
            }
        }
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
        alert.setTitle("Cancel adding new Order");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to cancel? \n All information will be lost.");
        return alert.showAndWait();
    }
}
