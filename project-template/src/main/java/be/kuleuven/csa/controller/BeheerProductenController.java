package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.domain.Landbouwbedrijf;
import be.kuleuven.csa.model.domain.Product;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

public class BeheerProductenController {

    @FXML
    private TableView<Product> tblproducten;
    @FXML
    public TableColumn<Product, String> productNaam = new TableColumn<>("product");
    @FXML
    public TableColumn<Product, Integer> aantalProduct= new TableColumn<>("aantal");

    public void initialize() {
        initTable();
    }


    private void initTable() {
        tblproducten.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblproducten.getColumns().clear();
        tblproducten.setEditable(true);


        productNaam.setCellValueFactory(new PropertyValueFactory<>("naam"));
        productNaam.setCellFactory(TextFieldTableCell.forTableColumn());
        productNaam.setOnEditCommit(event -> {
            Product selectedProduct = event.getRowValue();
            selectedProduct.setNaam(event.getNewValue());
            modifyCurrentRow();
        });

        aantalProduct.setCellValueFactory(new PropertyValueFactory<>("aantal"));
        aantalProduct.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        aantalProduct.setOnEditCommit(event -> {
            Product selectedProduct = event.getRowValue();
            selectedProduct.setAantal(event.getNewValue());
            modifyCurrentRow();
        });


    }

    private void modifyCurrentRow() {

    }
}
