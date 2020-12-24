package be.kuleuven.csa.controller;

import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;

import java.io.IOException;
import java.io.InputStream;

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
    private TableView tblWeekelijkseBestellingen;

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

        // TODO verwijderen en "echte data" toevoegen!
        CouchDbClient dbClient = new CouchDbClient();

        JsonObject json =  dbClient.find(JsonObject.class,"062cb2f1582a6a4257bf58b93f002f01");

        System.out.println(" --------------------------------------------------------------------------------");
        System.out.println(" -------------------------------- TEST ------------------------------------------\n");
        System.out.println("\n \n \n");
        System.out.println("JSON:" + json);
        System.out.println("\n \n \n");
        System.out.println(" --------------------------------------------------------------------------------");

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
