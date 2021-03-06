package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import be.kuleuven.csa.model.domain.Landbouwbedrijf;
import be.kuleuven.csa.model.domain.Stock;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.lightcouch.CouchDbClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BeheerLandbouwbedrijvenController {

    @FXML
    private Button btnDeleteBedrijf;
    @FXML
    private Button btnAddBedrijf;
    @FXML
    private Button btnCloseBedrijfscherm;
    @FXML
    private Button btnFilterBedrijven;
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

    private Landbouwbedrijf filterBedrijf;


    public void initialize() {
        initTable();

        btnAddBedrijf.setOnAction(e -> addNewRow());

        btnDeleteBedrijf.setOnAction(e -> {
            verifyOneRowSelected();
            deleteCurrentRow();
        });

        btnCloseBedrijfscherm.setOnAction(e -> {
            var stage = (Stage) btnCloseBedrijfscherm.getScene().getWindow();
            stage.close();
        });

        btnFilterBedrijven.setOnAction(e -> {
            filterBedrijven();
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
            modifyCurrentRow();
        });

        bedrijfsGemeente.setCellValueFactory((new PropertyValueFactory<>("gemeente")));
        bedrijfsGemeente.setCellFactory(TextFieldTableCell.forTableColumn());
        bedrijfsGemeente.setOnEditCommit(event -> {
            Landbouwbedrijf selectedBedrijf = event.getRowValue();
            selectedBedrijf.setGemeente(event.getNewValue());
            modifyCurrentRow();
        });

        bedrijfsPostcode.setCellValueFactory(new PropertyValueFactory<>("postcode"));
        bedrijfsPostcode.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        bedrijfsPostcode.setOnEditCommit(event -> {
            Landbouwbedrijf selectedBedrijf = event.getRowValue();
            selectedBedrijf.setPostcode(Integer.valueOf(event.getNewValue()));
            modifyCurrentRow();
        });

        bedrijfsOndernemingsNR.setCellValueFactory(new PropertyValueFactory<>("ondernemingsNR"));
        bedrijfsOndernemingsNR.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        bedrijfsOndernemingsNR.setOnEditCommit(event -> {
            Landbouwbedrijf selectedBedrijf = event.getRowValue();
            selectedBedrijf.setOndernemingsNR(Integer.valueOf(event.getNewValue()));
            modifyCurrentRow();
        });

        bedrijfsLand.setCellValueFactory(new PropertyValueFactory<>("land"));
        bedrijfsLand.setCellFactory(TextFieldTableCell.forTableColumn());
        bedrijfsLand.setOnEditCommit(event -> {
            Landbouwbedrijf selectedBedrijf = event.getRowValue();
            selectedBedrijf.setLand(event.getNewValue());
            modifyCurrentRow();
        });

        filterBedrijf = null;
        refreshTable();

        tblLandbouwbedrijven.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblLandbouwbedrijven.getColumns().addAll(bedrijfsNaam, bedrijfsGemeente, bedrijfsPostcode, bedrijfsOndernemingsNR, bedrijfsLand);
    }


    private void addNewRow() {
        var bedrijfToeTeVoegen = showAddNewRowDialog();
        bedrijfToeTeVoegen.ifPresent(landbouwbedrijf -> CsaDatabaseConn.getDatabaseConn().getCsaRepo().persistRecord(landbouwbedrijf));
        bedrijfToeTeVoegen.ifPresent(landbouwbedrijf  -> dbClientSave(landbouwbedrijf));
        refreshTable();
    }

    private void deleteCurrentRow() {
        Landbouwbedrijf selectedRow = tblLandbouwbedrijven.getSelectionModel().getSelectedItem();
        CsaDatabaseConn.getDatabaseConn().getCsaRepo().deleteRecord(selectedRow);

        refreshTable();
    }

    private void modifyCurrentRow() {
        Landbouwbedrijf selectedBedrijf = tblLandbouwbedrijven.getSelectionModel().getSelectedItem();
        CsaDatabaseConn.getDatabaseConn().getCsaRepo().updateRecord(selectedBedrijf);
    }

    private void refreshTable(){
        tblLandbouwbedrijven.getItems().clear();
        for (Landbouwbedrijf bedrijf: CsaDatabaseConn.getDatabaseConn().getCsaRepo().getLandbouwbedrijven(filterBedrijf)) {
            tblLandbouwbedrijven.getItems().add(bedrijf);
        }
    }

    public void showAlert(String title, String content) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void filterBedrijven(){
        var optionalFilterbedrijf = showChangeFilterDialog();
        filterBedrijf = optionalFilterbedrijf.orElse(null);
        refreshTable();
    }

    private void verifyOneRowSelected() {
        if(tblLandbouwbedrijven.getSelectionModel().getSelectedCells().size() == 0) {
            showAlert("Hela!", "Eerst een bedrijf selecteren hé.");
        }
    }

    private Optional<Landbouwbedrijf> showAddNewRowDialog(){
        Dialog<Landbouwbedrijf> dialog = new Dialog<>();
        dialog.setTitle("Voeg Landbouwbedrijf toe");
        dialog.setHeaderText("Voeg een nieuw Landbouwbedrijf toe, velden met een * zijn verplicht");

        ButtonType voegToeButtonType = new ButtonType("Voeg toe", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voegToeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField naamtext = new TextField();
        naamtext.setPromptText("Naam");
        TextField gemeentetext = new TextField();
        gemeentetext.setPromptText("Gemeente");
        TextField postcodetext = new TextField();
        postcodetext.setPromptText("Postcode");
        TextField ondernemingsNRtext = new TextField();
        ondernemingsNRtext.setPromptText("Ondernemingsnummer");
        TextField landtext = new TextField();
        landtext.setPromptText("Land");

        grid.add(new Label("Naam*:"), 0, 0);
        grid.add(naamtext, 1, 0);
        grid.add(new Label("Gemeente*:"), 0, 1);
        grid.add(gemeentetext, 1, 1);
        grid.add(new Label("Postcode*:"), 0, 2);
        grid.add(postcodetext, 1, 2);
        grid.add(new Label("ondernemingsNR*:"), 0, 3);
        grid.add(ondernemingsNRtext, 1, 3);
        grid.add(new Label("Land"), 0, 4);
        grid.add(landtext, 1, 4);
        dialog.getDialogPane().setContent(grid);

        Node voegtoeButton = dialog.getDialogPane().lookupButton(voegToeButtonType);
        voegtoeButton.setDisable(true);

        List<TextField> textfields = new ArrayList<>();
        textfields.add(naamtext);
        textfields.add(gemeentetext);
        textfields.add(postcodetext);
        textfields.add(ondernemingsNRtext);

        for (TextField t : textfields){
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                voegtoeButton.setDisable(naamtext.getText().isEmpty() || gemeentetext.getText().isEmpty() || ondernemingsNRtext.getText().isEmpty() || postcodetext.getText().isEmpty());
            });
        }
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType){
                Landbouwbedrijf newbedrijf = new Landbouwbedrijf(
                        Integer.parseInt(ondernemingsNRtext.getText().trim()),
                        naamtext.getText().trim(),
                        gemeentetext.getText().trim(),
                        Integer.parseInt(postcodetext.getText().trim()));
                newbedrijf.setLand(landtext.getText().trim());
                return newbedrijf;

            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Optional<Landbouwbedrijf> showChangeFilterDialog() {
        Dialog<Landbouwbedrijf> dialog = new Dialog<>();
        dialog.setTitle("Filter Landbouwbedrijven");
        dialog.setHeaderText("voeg filters toe voor het zoeken van landbouwbedrijven.");

        ButtonType voegToeButtonType = new ButtonType("Filter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voegToeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField naamtext = new TextField();
        naamtext.setPromptText("Naam");
        TextField gemeentetext = new TextField();
        gemeentetext.setPromptText("Gemeente");
        TextField postcodetext = new TextField();
        postcodetext.setPromptText("Postcode");
        TextField ondernemingsNRtext = new TextField();
        ondernemingsNRtext.setPromptText("Ondernemingsnummer");
        TextField landtext = new TextField();
        landtext.setPromptText("Land");

        if (filterBedrijf != null){
            naamtext.setText(filterBedrijf.getNaam());
            gemeentetext.setText(filterBedrijf.getGemeente());
            postcodetext.setText(String.valueOf(filterBedrijf.getPostcode()));
            ondernemingsNRtext.setText(String.valueOf(filterBedrijf.getOndernemingsNR()));
            landtext.setText(filterBedrijf.getLand());
        }

        grid.add(new Label("Naam:"), 0, 0);
        grid.add(naamtext, 1, 0);
        grid.add(new Label("Gemeente:"), 0, 1);
        grid.add(gemeentetext, 1, 1);
        grid.add(new Label("Postcode:"), 0, 2);
        grid.add(postcodetext, 1, 2);
        grid.add(new Label("ondernemingsNR:"), 0, 3);
        grid.add(ondernemingsNRtext, 1, 3);
        grid.add(new Label("Land"), 0, 4);
        grid.add(landtext, 1, 4);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType) {
                if (filterBedrijf == null){
                    filterBedrijf = new Landbouwbedrijf();
                }
                filterBedrijf.setNaam(naamtext.getText().trim());
                filterBedrijf.setGemeente(gemeentetext.getText().trim());
                filterBedrijf.setLand(landtext.getText().trim());
                if (!ondernemingsNRtext.getText().trim().isEmpty()){
                    filterBedrijf.setOndernemingsNR(Integer.valueOf(ondernemingsNRtext.getText().trim()));
                }
                if (!postcodetext.getText().trim().isEmpty()){
                    filterBedrijf.setPostcode(Integer.valueOf(postcodetext.getText().trim()));
                }
                return filterBedrijf;
            }
            else return filterBedrijf;
        });
        return dialog.showAndWait();
    }

    private void dbClientSave(Landbouwbedrijf landbouwbedrijf ){
        //Stock aanmaken op coachDB
        CouchDbClient dbClient = new CouchDbClient();
        Stock newStock = new Stock(landbouwbedrijf.getOndernemingsNR());
        dbClient.save(newStock);

        // shutdown the client
        dbClient.shutdown();
    }

    }