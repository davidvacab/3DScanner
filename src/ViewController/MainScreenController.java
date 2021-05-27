package ViewController;

import OrdersControl.Inventory;
import OrdersControl.Order;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {
    public TextField searchBar;

    public Button addOrder;
    public Button deleteOrder;
    public Button scanOrder;
    public Button viewOrder;
    public Button editOrder;
    public Button printBoxID;
    public Button exitButton;

    public TableView<Order> ordersTable;
    public TableColumn<Order, String> orderNumberCol;
    public TableColumn<Order, Integer> quantityCol;
    public TableColumn<Order, Integer> printCountCol;
    public TableColumn<Order, Integer> unitsPerBoxCol;
    public TableColumn<Order, String> optionNumberCol;

    private Order selectedOrder;
    private Inventory inv = new Inventory();

    public MainScreenController(){
        readData();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orderNumberCol.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("orderQuantity"));
        printCountCol.setCellValueFactory(new PropertyValueFactory<>("printCount"));
        unitsPerBoxCol.setCellValueFactory(new PropertyValueFactory<>("unitsPerBox"));
        optionNumberCol.setCellValueFactory(new PropertyValueFactory<>("optionNumber"));
        ordersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedOrder = newSelection;
                scanOrder.setDisable(false);
                deleteOrder.setDisable(false);
                editOrder.setDisable(false);
                viewOrder.setDisable(false);
            } else {
                scanOrder.setDisable(true);
                deleteOrder.setDisable(true);
                editOrder.setDisable(true);
                viewOrder.setDisable(true);
            }
        });
        searchOrders();

    }

    private void searchOrders(){
        FilteredList<Order> filteredOrders = new FilteredList<>(FXCollections.observableArrayList(inv.getOrdersList()), p -> true);
        searchBar.textProperty().addListener((observable, oldValue, newValue )-> filteredOrders.setPredicate(order -> {
            if (newValue == null || newValue.isEmpty()){
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            if (order.getOrderNumber().toLowerCase().contains(lowerCaseFilter)){
                return true;
            }
            if (Integer.toString(order.getOrderQuantity()).contains(newValue)) {
                return true;
            }
            if (Double.toString(order.getPrintCount()).contains(newValue)){
                return true;
            }
            return Integer.toString(order.getUnitsPerBox()).contains(newValue);
        }));
        SortedList<Order> sortedOrders = new SortedList<>(filteredOrders);
        sortedOrders.comparatorProperty().bind(ordersTable.comparatorProperty());
        ordersTable.setItems(sortedOrders);
        ordersTable.refresh();
    }

    public void onPrintBoxID(){
        Stage parentStage = (Stage) searchBar.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewController/PrintBoxID.fxml"));
        try {
            PrintBoxIDController controller = new PrintBoxIDController();
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage childrenStage = new Stage();
            childrenStage.initOwner(parentStage);
            childrenStage.initModality(Modality.APPLICATION_MODAL);
            childrenStage.setTitle("Print Box ID");
            childrenStage.setScene(scene);
            childrenStage.setMinHeight(600);
            childrenStage.setMinWidth(500);
            childrenStage.setOnCloseRequest(event -> childrenStage.close());
            parentStage.hide();
            childrenStage.showAndWait();
            parentStage.show();
            writeData();
            searchOrders();
        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }

    public void onAddOrder() {
        Stage parentStage = (Stage) searchBar.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewController/AddOrder.fxml"));
        try {
            AddOrderController controller = new AddOrderController(inv);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage childrenStage = new Stage();
            childrenStage.initOwner(parentStage);
            childrenStage.initModality(Modality.APPLICATION_MODAL);
            childrenStage.setTitle("Add New Order");
            childrenStage.setScene(scene);
            childrenStage.setResizable(false);
            childrenStage.setOnCloseRequest(events -> childrenStage.close());
            parentStage.hide();
            childrenStage.showAndWait();
            parentStage.show();
            writeData();
            searchOrders();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onEditOrder() {
        Stage parentStage = (Stage) searchBar.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewController/EditOrder.fxml"));
        try {
            EditOrderController controller = new EditOrderController(selectedOrder);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage childrenStage = new Stage();
            childrenStage.initOwner(parentStage);
            childrenStage.initModality(Modality.APPLICATION_MODAL);
            childrenStage.setTitle("Edit Order");
            childrenStage.setScene(scene);
            childrenStage.setResizable(false);
            childrenStage.setOnCloseRequest(events -> childrenStage.close());
            parentStage.hide();
            childrenStage.showAndWait();
            parentStage.show();
            writeData();
            searchOrders();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDeleteOrder() {
        ObservableList<Order> selectedOrders = ordersTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> result = askConfirmation();
        if(result.isPresent()){
            if (result.get() == ButtonType.OK) {
                for (Order order: selectedOrders) {
                    inv.deleteOrder(order);
                }
                writeData();
                searchOrders();
            }
        }
    }

    public void onScanOrder() {
        if(selectedOrder.getPrintCount() == 0 ) {
            errorMessage();
        } else {
            Stage parentStage = (Stage) searchBar.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewController/ScanOrder.fxml"));
            try {
                ScanOrderController controller = new ScanOrderController(selectedOrder);
                loader.setController(controller);
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage childrenStage = new Stage();
                childrenStage.initOwner(parentStage);
                childrenStage.initModality(Modality.APPLICATION_MODAL);
                childrenStage.setTitle("Scan Order");
                childrenStage.setScene(scene);
                childrenStage.setMinHeight(820);
                childrenStage.setMinWidth(500);
                childrenStage.setOnCloseRequest(event -> childrenStage.close());
                parentStage.hide();
                childrenStage.showAndWait();
                parentStage.show();
                writeData();
                searchOrders();
            } catch (IOException | AWTException e) {
                e.printStackTrace();
            }
        }
    }

    public void onViewOrder(){
        Stage parentStage = (Stage) searchBar.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewController/ViewOrder.fxml"));
        try {
            ViewOrderController controller = new ViewOrderController(selectedOrder);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage childStage = new Stage();
            childStage.initOwner(parentStage);
            childStage.initModality(Modality.APPLICATION_MODAL);
            childStage.setTitle("View Order");
            childStage.setScene(scene);
            childStage.setMinHeight(600);
            childStage.setMinWidth(800);
            childStage.setOnCloseRequest(event -> childStage.close());
            parentStage.hide();
            childStage.showAndWait();
            parentStage.show();
            writeData();
            searchOrders();
        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }

    private void errorMessage(){
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Finished");
        alert.setHeaderText(null);
        alert.setContentText("Order is already finished");
        alert.showAndWait();
    }

    private Optional<ButtonType> askConfirmation(){
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Order");
        alert.setHeaderText(null);
        alert.setContentText("Do you want to delete the selected Order?");
        return alert.showAndWait();
    }

    private void readData(){
        try {
            FileInputStream fileIn = new FileInputStream("Database.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            inv = (Inventory) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
        }
    }

    private void writeData(){
        try {
            FileOutputStream fileOut = new FileOutputStream("Database.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(inv);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public void onExitButton(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void onExitButton(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER){
            Platform.exit();
        }
    }
}
