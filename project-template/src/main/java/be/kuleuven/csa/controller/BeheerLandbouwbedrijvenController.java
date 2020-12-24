package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import be.kuleuven.csa.model.domain.Landbouwbedrijf;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
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
    private TableView tblLandbouwbedrijven;

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

        for (Landbouwbedrijf bedrijf: CsaDatabaseConn.getDatabaseConn().getCsaRepo().getLandbouwbedrijven()) {
            tblLandbouwbedrijven.getItems().add(FXCollections.observableArrayList(
                    bedrijf.getNaam(),
                    bedrijf.getGemeente(),
                    String.valueOf(bedrijf.getPostcode()),
                    String.valueOf(bedrijf.getOndernemingsNR()),
                    bedrijf.getLand()));
        }
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
