package ViewController;

import OrdersControl.Box;
import OrdersControl.Order;
import OrdersControl.Unit;
import Printing.LabelPrinter;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ViewOrderController implements Initializable {

    public TextField searchBar;

    public TableView<Box> boxesTable;
    public TableColumn<Box, String> boxIDColumn;
    public TableColumn<Box, Integer> unitsPerBoxColumn;

    public Button addBox;
    public Button deleteBox;
    public Button printBox;
    public Button exportButton;
    public Button reprintButton;

    public TableView<Unit> unitsTable;
    public TableColumn<Unit, String> serialNumberColumn;
    public TableColumn<Unit, String> macAddressColumn;
    public TableColumn<Unit, String> integrationIDColumn;

    public Button addUnit;
    public Button deleteUnit;
    public Button cancel;

    private final Order order;
    private Box selectedBox;
    private final LabelPrinter printer;

    public ViewOrderController(Order order) throws AWTException {
        this.order = order;
        printer = new LabelPrinter(order);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boxIDColumn.setCellValueFactory(new PropertyValueFactory<>("boxID"));
        unitsPerBoxColumn.setCellValueFactory(new PropertyValueFactory<>("unitsInBox"));

        serialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        macAddressColumn.setCellValueFactory(new PropertyValueFactory<>("macAddress"));
        integrationIDColumn.setCellValueFactory(new PropertyValueFactory<>("intID"));

        searchBoxes();
        boxesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        unitsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        boxesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedBox = newSelection;
                addUnit.setDisable(false);
                addBox.setDisable(false);
                deleteBox.setDisable(false);
                printBox.setDisable(false);
                reprintButton.setDisable(false);
                exportButton.setDisable(false);
                unitsTable.getSelectionModel().clearSelection();
                unitsTable.setItems(FXCollections.observableArrayList(selectedBox.getUnitsList()));
            } else{
                addUnit.setDisable(true);
                addBox.setDisable(true);
                deleteBox.setDisable(true);
                printBox.setDisable(true);
                reprintButton.setDisable(true);
                exportButton.setDisable(true);
            }
        });
        unitsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                deleteUnit.setDisable(false);
            } else{
                deleteUnit.setDisable(true);
            }
        });
    }

    private void searchBoxes(){
        FilteredList<Box> filteredOrders = new FilteredList<>(FXCollections.observableArrayList(order.getBoxesList()), p -> true);
        searchBar.textProperty().addListener((observable, oldValue, newValue )-> filteredOrders.setPredicate(box -> {
            if (newValue == null || newValue.isEmpty()){
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            return box.getBoxID().toLowerCase().contains(lowerCaseFilter);
        }));
        SortedList<Box> sortedOrders = new SortedList<>(filteredOrders);
        sortedOrders.comparatorProperty().bind(boxesTable.comparatorProperty());
        boxesTable.setItems(sortedOrders);
        boxesTable.refresh();
    }

    public void onExportButton(){
        ObservableList<Box> selectedBoxes = boxesTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> result = askConfirmation("Export Boxes", "Do you want to export the selected Boxes?");
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet spreadsheet = workbook.createSheet(order.getOrderNumber());
                HSSFRow row;
                row = spreadsheet.createRow(0);
                row.createCell(0).setCellValue("Serial Number");
                row.createCell(1).setCellValue("MAC Address");
                row.createCell(2).setCellValue("Integration ID");
                int i = 0;
                for (Box box: selectedBoxes){
                    for (Unit unit: box.getUnitsList()){
                        row = spreadsheet.createRow(i + 1);
                        row.createCell(0).setCellValue(unit.getSerialNumber());
                        row.createCell(1).setCellValue(unit.getMacAddress());
                        row.createCell(2).setCellValue(unit.getIntID());
                        i++;
                    }
                }
                try {
                    FileOutputStream fileOut = new FileOutputStream(order.getOrderNumber()+".xls");
                    workbook.write(fileOut);
                    fileOut.close();
                    errorMessage("Success!", "Excel file exported successfully.");
                } catch (IOException e){
                    errorMessage("Error exporting", Arrays.toString(e.getStackTrace()));
                }
            }
        }
    }

    public void onAddBox() {
        if(order.getPrintCount() == 0 ) {
            errorMessage("Order Finished", "Order is already finished");
        } else {
            Stage parentStage = (Stage) searchBar.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewController/ScanOrder.fxml"));
            try {
                ScanOrderController controller = new ScanOrderController(order);
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
                childrenStage.showAndWait();
                parentStage.show();
                searchBoxes();
            } catch (IOException | AWTException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDeleteBox() {
        ObservableList<Box> selectedBoxes = boxesTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> result = askConfirmation("Delete Boxes", "Do you want to delete the selected Boxes?");
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                Optional<ButtonType> result2 = askConfirmation("Delete from HighJump", "Do you want to also delete from HighJump?");
                if(result2.isPresent()) {
                    if (result2.get() == ButtonType.OK) {
                        printer.deleteLabels(new ArrayList<>(selectedBoxes));
                    }
                }
                for(Box box: selectedBoxes){
                    order.deleteBox(box);
                }
                unitsTable.setItems(null);
                searchBoxes();
            }
        }
    }

    public void onPrintBox() {
        ObservableList<Box> selectedBoxes = boxesTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> result = askConfirmation("Print Boxes", "Do you want to print the selected Boxes?");
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                printer.printLabels(new ArrayList<>(selectedBoxes), false);
            }
        }
    }

    public void onReprintButton(){
        ObservableList<Box> selectedBoxes = boxesTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> result = askConfirmation("Reprint Boxes", "Do you want to reprint the selected Boxes?");
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                printer.reprintLabels(new ArrayList<>(selectedBoxes));
            }
        }
    }

    public void onAddUnit() {
        if(order.getPrintCount() == 0 ) {
            errorMessage("Order Finished", "Order is already finished");
        } else if(selectedBox.getUnitsList().size() >= order.getUnitsPerBox()){
            errorMessage("Box Limit", "Box already has maximum amount of units");
        }
        else {
            Stage parentStage = (Stage) searchBar.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewController/AddUnit.fxml"));
            try {
                AddUnitController controller = new AddUnitController(order, selectedBox);
                loader.setController(controller);
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage childrenStage = new Stage();
                childrenStage.initOwner(parentStage);
                childrenStage.initModality(Modality.APPLICATION_MODAL);
                childrenStage.setTitle("Add Unit");
                childrenStage.setScene(scene);
                childrenStage.setResizable(false);
                childrenStage.setOnCloseRequest(event -> childrenStage.close());
                childrenStage.showAndWait();
                parentStage.show();
                searchBoxes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDeleteUnit(ActionEvent actionEvent) {
        ObservableList<Unit> selectedUnits = unitsTable.getSelectionModel().getSelectedItems();
        Optional<ButtonType> result = askConfirmation("Delete Units", "Do you want to delete the selected Units?");
        if(result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                for(Unit unit: selectedUnits){
                    selectedBox.deleteUnit(unit);
                }
                if (selectedBox.getUnitsList().isEmpty()) {
                    order.deleteBox(selectedBox);
                    unitsTable.setItems(null);
                } else {
                    unitsTable.setItems(FXCollections.observableArrayList(selectedBox.getUnitsList()));
                    unitsTable.refresh();
                }
                searchBoxes();
            }
        }
    }

    public void onCancel() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    private void errorMessage(String title, String content){
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(content);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Optional<ButtonType> askConfirmation(String title, String content){
        Toolkit.getDefaultToolkit().beep();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}
