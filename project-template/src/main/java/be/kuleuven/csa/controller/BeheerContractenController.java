package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import be.kuleuven.csa.model.domain.Contract;
import be.kuleuven.csa.model.domain.Klant;
import be.kuleuven.csa.model.domain.Landbouwbedrijf;
import be.kuleuven.csa.model.domain.Pakket;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class BeheerContractenController {

    @FXML
    private Button btnAddContract;
    @FXML
    private Button btnCloseContractScherm;
    @FXML
    private Button btnFilterContracten;
    @FXML
    private TableView<Contract> tblContracten;
    @FXML
    public TableColumn<Contract, Integer> contractIDColumn = new TableColumn<>("Contract ID");
    @FXML
    public TableColumn<Contract, String> pakketNaamColumn = new TableColumn<>("Pakket");
    @FXML
    public TableColumn<Contract, String> klantColumn = new TableColumn<>("klant");
    @FXML
    public TableColumn<Contract, String> landbouwbedrijfColumn = new TableColumn<>("Landbouwbedrijf");
    @FXML
    public TableColumn<Contract, String> beginDatumColumn = new TableColumn<>("begint vanaf");
    @FXML
    public TableColumn<Contract, String> vervalDatumColumn = new TableColumn<>("vervalt op");


    private Contract filterContract;

    public void initialize() {
        initTable();

        btnAddContract.setOnAction(e -> addNewRow());

        btnCloseContractScherm.setOnAction(e -> {
            var stage = (Stage) btnCloseContractScherm.getScene().getWindow();
            stage.close();
        });

        btnFilterContracten.setOnAction(e -> {
            filterContracten();
        });
    }

    private void initTable() {
        tblContracten.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblContracten.getColumns().clear();

        contractIDColumn.setCellValueFactory(new PropertyValueFactory<>("contractID"));

        pakketNaamColumn.setCellValueFactory(contract -> new SimpleStringProperty(contract.getValue().getPakket().getPakketnaam()));

        klantColumn.setCellValueFactory(contract -> new SimpleStringProperty(contract.getValue().getKlant().getNaam()));

        landbouwbedrijfColumn.setCellValueFactory(contract -> new SimpleStringProperty(contract.getValue().getLandbouwbedrijf().getNaam()));

        beginDatumColumn.setCellValueFactory(new PropertyValueFactory<>("begindatum"));

        vervalDatumColumn.setCellValueFactory(new PropertyValueFactory<>("vervaldatum"));

        filterContract = null;
        refreshTable();

        tblContracten.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblContracten.getColumns().addAll(contractIDColumn, pakketNaamColumn, klantColumn, landbouwbedrijfColumn, beginDatumColumn, vervalDatumColumn);
    }


    private void addNewRow() {
        //TODO nieuw contract aanmaken afwerken
        var contractToeTeVoegen = showAddNewRowDialog();
        contractToeTeVoegen.ifPresent(contract -> CsaDatabaseConn.getDatabaseConn().getCsaRepo().persistRecord(contract));
        refreshTable();
    }



    private void refreshTable(){
        tblContracten.getItems().clear();
        for (Contract contract: CsaDatabaseConn.getDatabaseConn().getCsaRepo().getContracten(filterContract)) {
            contract.calculateVervalDatum();
            tblContracten.getItems().add(contract);
        }
    }

    public void showAlert(String title, String content) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void filterContracten(){
        var optionalfilterContract = showChangeFilterDialog();
        filterContract = optionalfilterContract.orElse(null);
        refreshTable();
    }


    private void verifyOneRowSelected() {
        if(tblContracten.getSelectionModel().getSelectedCells().size() == 0) {
            showAlert("Hela!", "Eerst een Contract selecteren hé.");
        }
    }


    private Optional<Contract> showAddNewRowDialog(){
        Dialog<Contract> dialog = new Dialog<>();
        dialog.setTitle("Voeg Contract toe");
        dialog.setHeaderText("Voeg een nieuw Contract toe, velden met een * zijn verplicht");

        ButtonType voegToeButtonType = new ButtonType("Voeg toe", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voegToeButtonType, ButtonType.CANCEL);


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bedrijfText = new TextField();
        bedrijfText.setPromptText("Landbouwbedrijf");
        TextField klantText = new TextField();
        klantText.setPromptText("Klant");
        TextField datumText = new TextField();
        datumText.setPromptText("beginDatum");

        grid.add(new Label("Landbouwbedrijf*:"), 0, 0);
        grid.add(bedrijfText, 1, 0);
        grid.add(new Label("Klant*:"), 0, 1);
        grid.add(klantText, 1, 1);
        grid.add(new Label("vanaf*:"), 0, 2);
        grid.add(datumText, 1, 2);
        dialog.getDialogPane().setContent(grid);

        Node voegtoeButton = dialog.getDialogPane().lookupButton(voegToeButtonType);
        voegtoeButton.setDisable(true);

        List<TextField> textfields = new ArrayList<>();
        textfields.add(bedrijfText);
        textfields.add(klantText);
        textfields.add(datumText);


        for (TextField t : textfields){
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                voegtoeButton.setDisable(bedrijfText.getText().isEmpty() || klantText.getText().isEmpty() || datumText.getText().isEmpty());
            });
        }



        dialog.setResultConverter(dialogButton -> {
            Contract newContract = null;
            if (dialogButton == voegToeButtonType){
                return newContract;

            }
            return null;
        });

        return dialog.showAndWait();
    }



    private Optional<Contract> showChangeFilterDialog() {
        Landbouwbedrijf bedrijffilter = new Landbouwbedrijf();
        Klant klantfilter = new Klant();

        Dialog<Contract> dialog = new Dialog<>();
        dialog.setTitle("Filter Contracten");
        dialog.setHeaderText("voeg filters toe voor het zoeken van contracten.");

        ButtonType voegToeButtonType = new ButtonType("Filter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voegToeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField klantnaam = new TextField();
        klantnaam.setPromptText("klantnaam");
        TextField bedrijfnaam = new TextField();
        bedrijfnaam.setPromptText("landbouwbedrijf");


        if (filterContract != null){
            klantnaam.setText(filterContract.getKlant().getNaam());
            bedrijfnaam.setText(filterContract.getLandbouwbedrijf().getNaam());
        }

        grid.add(new Label("Klant:"), 0, 0);
        grid.add(klantnaam, 1, 0);
        grid.add(new Label("bedrijf:"), 0, 1);
        grid.add(bedrijfnaam, 1, 1);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType) {
                if (filterContract == null){
                    filterContract = new Contract();
                }
                klantfilter.setNaam(klantnaam.getText().trim());
                bedrijffilter.setNaam(bedrijfnaam.getText().trim());

                filterContract.setKlant(klantfilter);
                filterContract.setLandbouwbedrijf(bedrijffilter);
                return filterContract;
            }
            else return filterContract;
        });
        return dialog.showAndWait();
    }

}
