package ViewController;

import OrdersControl.Order;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditOrderController implements Initializable {
    public TextField orderNumberTextField;
    public TextField orderQuantityTextField;
    public TextField unitsPerBoxTextField;
    public TextField optionNumberTextField;

    public Button cancel;
    public Button save;

    private Order order;

    public EditOrderController(Order order){
        this.order = order;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orderNumberTextField.setText(order.getOrderNumber());
        orderQuantityTextField.setText(Integer.toString(order.getOrderQuantity()));
        unitsPerBoxTextField.setText(Integer.toString(order.getUnitsPerBox()));
        optionNumberTextField.setText(order.getOptionNumber());
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
                orderQuantityTextField.textProperty().isEmpty()
                        .or(unitsPerBoxTextField.textProperty().isEmpty())
                        .or(optionNumberTextField.textProperty().isEmpty());
        save.disableProperty().bind(checkSave);
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
        order.setOrderQuantity(Integer.parseInt(orderQuantityTextField.getText()));
        order.setUnitsPerBox(Integer.parseInt(unitsPerBoxTextField.getText()));
        order.setOptionNumber(optionNumberTextField.getText());
        Stage stage = (Stage) save.getScene().getWindow();
        stage.close();
    }

    public void onCancel() {
        Optional<ButtonType> result = askConfirmation();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                Stage stage = (Stage) cancel.getScene().getWindow();
                stage.close();
            }
        }
    }

    private Optional<ButtonType> askConfirmation(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel editing Order");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to cancel? \n All changes will be lost.");
        return alert.showAndWait();
    }
}
