package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.domain.Landbouwbedrijf;
import be.kuleuven.csa.model.domain.Product;
import be.kuleuven.csa.model.domain.Stock;
import be.kuleuven.csa.model.domain.WekelijkseBestelling;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.lightcouch.CouchDbClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BeheerProductenController {


    @FXML
    private Button btnDelete;
    @FXML
    private Button btnClose;
    @FXML
    private ChoiceBox<String> SelectedONR;
    @FXML
    private TableView<Product> tblproducten;
    @FXML
    public TableColumn<Product, String> productNaam = new TableColumn<>("product");
    @FXML
    public TableColumn<Product, Integer> aantalProduct= new TableColumn<>("aantal");

    Map<String,Integer> stockSelectedLb;

    public void initialize() {
        initTable();

        btnDelete.setOnAction(e -> {
            verifyOneRowSelected();
            deleteCurrentRow();
        });

        btnClose.setOnAction(e -> {
            var stage = (Stage) btnClose.getScene().getWindow();
            stage.close();
        });

        SelectedONR.setOnAction(e-> {
            if(SelectedONR.isShowing()){  refreshTable();}
        });
    }

    private void initTable() {
        tblproducten.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblproducten.getColumns().clear();
        tblproducten.setEditable(true);

        productNaam.setCellValueFactory(new PropertyValueFactory<>("naam"));
        productNaam.setCellFactory(TextFieldTableCell.forTableColumn());
        productNaam.setOnEditCommit(event -> {
            Product selectedProduct = event.getRowValue();
            stockSelectedLb.remove(selectedProduct.getNaam());
            selectedProduct.setNaam(event.getNewValue());
            stockSelectedLb.put(selectedProduct.getNaam(), selectedProduct.getAantal());
            modifyCurrentRow();
        });

        aantalProduct.setCellValueFactory(new PropertyValueFactory<>("aantal"));
        aantalProduct.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        aantalProduct.setOnEditCommit(event -> {
            Product selectedProduct = event.getRowValue();
            selectedProduct.setAantal(event.getNewValue());
            stockSelectedLb.replace(selectedProduct.getNaam(), selectedProduct.getAantal());
            modifyCurrentRow();
        });

        CouchDbClient dbClient = new CouchDbClient();
        List<JsonObject> LandbouwbedrijvenStockJSON = dbClient.view("_all_docs").startKey("ONR").includeDocs(true).query(JsonObject.class);

        Gson gson = new Gson();
        for(JsonObject json: LandbouwbedrijvenStockJSON) {

            Stock s = gson.fromJson(json, Stock.class);
            if(s.getOndernemingsNR() != 0) {
                SelectedONR.getItems().add(s.getOndernemingsNR() + "");
            }
            stockSelectedLb = s.getStock();

            if (SelectedONR.isShowing() && SelectedONR.getValue().equals(s.getOndernemingsNR()+"")) {
                for (Map.Entry<String, Integer> p : stockSelectedLb.entrySet()) {
                    Product pr = new Product(p.getKey(), Integer.parseInt(p.getValue() + ""));
                    tblproducten.getItems().add(pr);
                }
                Product pr = new Product("New product", 0);
                tblproducten.getItems().add(pr);
            }
        }
        tblproducten.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblproducten.getColumns().addAll(productNaam,aantalProduct);

        // shutdown the client
        dbClient.shutdown();
    }

    private void refreshTable(){
        tblproducten.getItems().clear();
        CouchDbClient dbClient = new CouchDbClient();

        List<JsonObject> LandbouwbedrijvenStockJSON = dbClient.view("_all_docs").startKey("ONR").includeDocs(true).query(JsonObject.class);
        Gson gson = new Gson();

        for(JsonObject json: LandbouwbedrijvenStockJSON) {
            Stock s = gson.fromJson(json, Stock.class);
            Map<String,Integer> stockSelectedLb = s.getStock();
            if(SelectedONR.getValue().equals(s.getOndernemingsNR()+"")) {
                for (Map.Entry<String, Integer> p : stockSelectedLb.entrySet()) {
                    Product pr = new Product(p.getKey(), Integer.parseInt(p.getValue() + ""));
                    tblproducten.getItems().add(pr);
                }
                Product pr = new Product("New product", 0);
                tblproducten.getItems().add(pr);
            }
        }

        // shutdown the client
        dbClient.shutdown();
    }


    private void modifyCurrentRow() {
        update();
        refreshTable();
    }

    public void update(){
        CouchDbClient dbClient = new CouchDbClient();

        JsonObject jsonobj= dbClient.find(JsonObject.class,"ONR"+SelectedONR.getValue());
        dbClient.remove(jsonobj);
        Stock stockUpdate = new Stock(Integer.parseInt(SelectedONR.getValue()));
        stockUpdate.setStock(stockSelectedLb);
        stockUpdate.setOndernemingsNR(Integer.parseInt(SelectedONR.getValue()));
        dbClient.save(stockUpdate);

        // shutdown the client
        dbClient.shutdown();
    }


    private void deleteCurrentRow() {
        Product selectedProduct = tblproducten.getSelectionModel().getSelectedItem();
        stockSelectedLb.remove(selectedProduct.getNaam());
        update();
        refreshTable();
    }


    private void verifyOneRowSelected() {
        if(tblproducten.getSelectionModel().getSelectedCells().size() == 0) {
            showAlert("Hela!", "Eerst een bestelling selecteren he.");
        }
    }

    public void showAlert(String title, String content) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
