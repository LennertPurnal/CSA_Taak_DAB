package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import be.kuleuven.csa.model.domain.Landbouwbedrijf;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class BeheerLandbouwbedrijvenController {

    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnModify;
    @FXML
    private Button btnClose;
    @FXML
    private TableView<Landbouwbedrijf> tblLandbouwbedrijven;
    @FXML
    public TableColumn<Landbouwbedrijf, String> bedrijfsNaam = new TableColumn<>("Bedrijfsnaam");
    @FXML
    public TableColumn<Landbouwbedrijf, String> bedrijfsGemeente= new TableColumn<>("Gemeente");
    @FXML
    public TableColumn<Landbouwbedrijf, Integer> bedrijfsPostcode = new TableColumn<>("Postcode");
    @FXML
    public TableColumn<Landbouwbedrijf, String> bedrijfsOndernemingsNR = new TableColumn<>("OndernemingsNR");
    @FXML
    public TableColumn<Landbouwbedrijf, String> bedrijfsLand = new TableColumn<>("Land");

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
        tblLandbouwbedrijven.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblLandbouwbedrijven.getColumns().clear();
        tblLandbouwbedrijven.setEditable(true);


        bedrijfsNaam.setCellValueFactory(new PropertyValueFactory<>("naam"));
        bedrijfsGemeente.setCellValueFactory((new PropertyValueFactory<>("Gemeente")));
        bedrijfsPostcode.setCellValueFactory(new PropertyValueFactory<>("postcode"));
        bedrijfsOndernemingsNR.setCellValueFactory(new PropertyValueFactory<>("ondernemingsNR"));
        bedrijfsLand.setCellValueFactory(new PropertyValueFactory<>("land"));



        /*
        // TODO verwijderen en "echte data" toevoegen!
        int colIndex = 0;
        for(var colName : new String[]{"Naam", "gemeente", "postcode", "ondernemingsnummer", "land"}) {
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(colName);
            final int finalColIndex = colIndex;
            col.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().get(finalColIndex)));
            tblLandbouwbedrijven.getColumns().add(col);
            col.setCellFactory(TextFieldTableCell.forTableColumn());
            colIndex++;
        }
        */

        for (Landbouwbedrijf bedrijf: CsaDatabaseConn.getDatabaseConn().getCsaRepo().getLandbouwbedrijven()) {
            tblLandbouwbedrijven.getItems().add(bedrijf);
        }

        tblLandbouwbedrijven.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblLandbouwbedrijven.getColumns().addAll(bedrijfsNaam, bedrijfsGemeente, bedrijfsPostcode, bedrijfsOndernemingsNR, bedrijfsLand);
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
        if(tblLandbouwbedrijven.getSelectionModel().getSelectedCells().size() == 0) {
            showAlert("Hela!", "Eerst een bedrijf selecteren hé.");
        }
    }
}