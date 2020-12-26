package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import be.kuleuven.csa.model.domain.Landbouwbedrijf;
import com.sun.javafx.scene.control.IntegerField;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

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
    public TableColumn<Landbouwbedrijf, Integer> bedrijfsOndernemingsNR = new TableColumn<>("OndernemingsNR");
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
        bedrijfsNaam.setCellFactory(TextFieldTableCell.forTableColumn());
        bedrijfsNaam.setOnEditCommit(event -> {
            Landbouwbedrijf selectedBedrijf = event.getRowValue();
            selectedBedrijf.setNaam(event.getNewValue());
        });

        bedrijfsGemeente.setCellValueFactory((new PropertyValueFactory<>("Gemeente")));
        bedrijfsGemeente.setCellFactory(TextFieldTableCell.forTableColumn());
        bedrijfsGemeente.setOnEditCommit(event -> {
            Landbouwbedrijf selectedBedrijf = event.getRowValue();
            selectedBedrijf.setGemeente(event.getNewValue());
        });

        bedrijfsPostcode.setCellValueFactory(new PropertyValueFactory<>("postcode"));
        bedrijfsPostcode.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        bedrijfsPostcode.setOnEditCommit(event -> {
            Landbouwbedrijf selectedBedrijf = event.getRowValue();
            selectedBedrijf.setPostcode(Integer.valueOf(event.getNewValue()));
        });

        bedrijfsOndernemingsNR.setCellValueFactory(new PropertyValueFactory<>("ondernemingsNR"));
        bedrijfsOndernemingsNR.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        bedrijfsOndernemingsNR.setOnEditCommit(event -> {
            Landbouwbedrijf selectedBedrijf = event.getRowValue();
            selectedBedrijf.setOndernemingsNR(Integer.valueOf(event.getNewValue()));
        });

        bedrijfsLand.setCellValueFactory(new PropertyValueFactory<>("land"));
        bedrijfsLand.setCellFactory(TextFieldTableCell.forTableColumn());
        bedrijfsLand.setOnEditCommit(event -> {
            Landbouwbedrijf selectedBedrijf = event.getRowValue();
            selectedBedrijf.setLand(event.getNewValue());
        });

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
        Landbouwbedrijf selectedBedrijf = tblLandbouwbedrijven.getSelectionModel().getSelectedItem();
        CsaDatabaseConn.getDatabaseConn().getCsaRepo().persistRecord(selectedBedrijf);
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