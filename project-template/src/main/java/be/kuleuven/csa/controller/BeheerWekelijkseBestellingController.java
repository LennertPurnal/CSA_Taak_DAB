package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.domain.Landbouwbedrijf;
import be.kuleuven.csa.model.domain.Product;
import be.kuleuven.csa.model.domain.WekelijkseBestelling;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

import javax.persistence.Table;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static javafx.application.ConditionalFeature.SWT;

public class BeheerWekelijkseBestellingController {

    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnModify;
    @FXML
    private Button btnClose;
    @FXML
    private TableView<WekelijkseBestelling> tblWeekelijkseBestellingen;
    @FXML
    public TableColumn<WekelijkseBestelling, String> BestelNR = new TableColumn<>("BestelNR");
    @FXML
    public TableColumn<WekelijkseBestelling, Integer> klantID = new TableColumn<>("klantID");
    @FXML
    public TableColumn<WekelijkseBestelling, Product[]> Producten = new TableColumn<>("Producten");
    @FXML
    public TableColumn<WekelijkseBestelling, String> Product = new TableColumn<>("product");
    @FXML
    public TableColumn<WekelijkseBestelling, Integer> Aantal = new TableColumn<>("Aantal");
    @FXML
    public TableColumn<WekelijkseBestelling, Boolean> Afgehaald = new TableColumn<>("Afgehaald");

    public void initialize() {
        initTable();
        btnAdd.setOnAction(e -> addNewRow());
        btnModify.setOnAction(e -> {
            verifyOneRowSelected();
            modifyCurrentRow();
        });
        btnDelete.setOnAction(e -> {
            verifyOneRowSelected();
            deleteCurrentRow();
        });

        btnClose.setOnAction(e -> {
            var stage = (Stage) btnClose.getScene().getWindow();
            stage.close();
        });

    }


    private void initTable() {
        tblWeekelijkseBestellingen.getColumns().clear();
        tblWeekelijkseBestellingen.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblWeekelijkseBestellingen.setEditable(true);


        BestelNR.setCellValueFactory(new PropertyValueFactory<>("bestelNR"));
        klantID.setCellValueFactory((new PropertyValueFactory<>("klantID")));
        //Producten.setCellValueFactory(new PropertyValueFactory<>("producten"));
        Product.setCellValueFactory(new PropertyValueFactory<>("product"));
        Aantal.setCellValueFactory(new PropertyValueFactory<>("aantal"));
        Afgehaald.setCellValueFactory((new PropertyValueFactory<>("afgehaald")));

        // TODO verwijderen en "echte data" toevoegen!
        CouchDbClient dbClient = new CouchDbClient();

        JsonObject json =  dbClient.find(JsonObject.class,"fb17bd06530cd5b0f5730113e401ba4a");

        System.out.println(" --------------------------------------------------------------------------------");
        System.out.println(" -------------------------------- TEST ------------------------------------------\n");
        System.out.println("\n \n \n");
        System.out.println("JSON:" + json);
        System.out.println("\n \n \n");
        System.out.println(" --------------------------------------------------------------------------------");


        Gson gson = new Gson();
        WekelijkseBestelling b = gson.fromJson(json, WekelijkseBestelling.class);

        System.out.println(b);

        tblWeekelijkseBestellingen.getItems().add(b);


        tblWeekelijkseBestellingen.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblWeekelijkseBestellingen.getColumns().addAll(BestelNR,  klantID, Producten ,  Product,  Aantal, Afgehaald);
        // shutdown the client
        dbClient.shutdown();


    }

    private void addNewRow() {
    }

    private void deleteCurrentRow() {
    }

    private void modifyCurrentRow() {

    }

    public void showAlert(String title, String content) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void verifyOneRowSelected() {
        if(tblWeekelijkseBestellingen.getSelectionModel().getSelectedCells().size() == 0) {
            showAlert("Hela!", "Eerst een bestelling selecteren hé.");
        }
    }
}
