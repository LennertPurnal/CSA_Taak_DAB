package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import be.kuleuven.csa.model.domain.Aanbieding;
import be.kuleuven.csa.model.domain.Klant;
import be.kuleuven.csa.model.domain.Landbouwbedrijf;
import be.kuleuven.csa.model.domain.Pakket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BeheerAanbiedingenController {

    @FXML
    private Button btnDeleteAanbieding;
    @FXML
    private Button btnAddAanbieding;
    @FXML
    private Button btnCloseAanbiedingenscherm;
    @FXML
    private Button btnFilterAanbiedingen;
    @FXML
    private TableView<Aanbieding> tblAanbiedingen;
    @FXML
    public TableColumn<Aanbieding, String> pakketnaamColumn = new TableColumn<>("Pakket");
    @FXML
    public TableColumn<Aanbieding, String> bedrijfnaamColumn = new TableColumn<>("aangeboden door");
    @FXML
    public TableColumn<Aanbieding, Integer> prijsColumn = new TableColumn<>("Prijs in Euro");

    private Aanbieding filterAanbieding;
    private int minPrijsFilter = 0;
    private int maxPrijsFilter= 10000;

    public void initialize() {
        initTable();

        btnAddAanbieding.setOnAction(e -> addNewRow());

        btnDeleteAanbieding.setOnAction(e -> {
            verifyOneRowSelected();
            deleteCurrentRow();
        });

        btnCloseAanbiedingenscherm.setOnAction(e -> {
            var stage = (Stage) btnCloseAanbiedingenscherm.getScene().getWindow();
            stage.close();
        });

        btnFilterAanbiedingen.setOnAction(e -> {
            filterAanbiedingen();
        });
    }

    private void initTable() {
        tblAanbiedingen.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblAanbiedingen.getColumns().clear();
        tblAanbiedingen.setEditable(true);

        pakketnaamColumn.setCellValueFactory(aanbieding -> new SimpleStringProperty(aanbieding.getValue().getPakket().getPakketnaam()));

        bedrijfnaamColumn.setCellValueFactory(aanbieding -> new SimpleStringProperty(aanbieding.getValue().getLandbouwbedrijf().getNaam()));

        prijsColumn.setCellValueFactory(new PropertyValueFactory<>("prijs"));
        prijsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        prijsColumn.setOnEditCommit(event -> {
            Aanbieding selectedAanbieding = event.getRowValue();
            selectedAanbieding.setPrijs(Integer.valueOf(event.getNewValue()));
            modifyCurrentRow();
        });



        filterAanbieding = null;
        minPrijsFilter = 0;
        maxPrijsFilter = 10000;
        refreshTable();

        tblAanbiedingen.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblAanbiedingen.getColumns().addAll(pakketnaamColumn, bedrijfnaamColumn, prijsColumn);
    }


    private void addNewRow() {
        var aanbiedingToeTeVoegen = showAddNewRowDialog();
        aanbiedingToeTeVoegen.ifPresent(aanbieding -> {
            aanbieding.getLandbouwbedrijf().voegAanbiedingToe(aanbieding);
            aanbieding.getPakket().voegAanbiedingToe(aanbieding);
            CsaDatabaseConn.getDatabaseConn().getCsaRepo().persistRecord(aanbieding);
        });
        refreshTable();
    }

    private void deleteCurrentRow() {
        Aanbieding selectedRow = tblAanbiedingen.getSelectionModel().getSelectedItem();
        CsaDatabaseConn.getDatabaseConn().getCsaRepo().deleteRecord(selectedRow);

        refreshTable();
    }

    private void modifyCurrentRow() {
        Aanbieding selectedRow = tblAanbiedingen.getSelectionModel().getSelectedItem();
        CsaDatabaseConn.getDatabaseConn().getCsaRepo().updateRecord(selectedRow);
    }

    private void refreshTable(){
        tblAanbiedingen.getItems().clear();
        for (Aanbieding aanbieding: CsaDatabaseConn.getDatabaseConn().getCsaRepo().getAanbiedingen(filterAanbieding, minPrijsFilter, maxPrijsFilter)) {
            tblAanbiedingen.getItems().add(aanbieding);
        }
    }

    public void showAlert(String title, String content) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void filterAanbiedingen(){
        var optionalFilterKlant = showChangeFilterDialog();
        filterAanbieding = optionalFilterKlant.orElse(null);
        refreshTable();
    }

    private void verifyOneRowSelected() {
        if(tblAanbiedingen.getSelectionModel().getSelectedCells().size() == 0) {
            showAlert("Hela!", "Eerst een Aanbieding selecteren hé.");
        }
    }


    private Optional<Aanbieding> showAddNewRowDialog(){
        Dialog<Aanbieding> dialog = new Dialog<>();
        dialog.setTitle("Voeg aanbieding toe");
        dialog.setHeaderText("Voeg een nieuwe Aanbieding toe, velden met een * zijn verplicht\n"+
                            "zoek pakket en bedrijf op naam en voeg toe");

        Aanbieding newAanbieding = new Aanbieding();

        ButtonType voegToeButtonType = new ButtonType("Voeg toe", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voegToeButtonType, ButtonType.CANCEL);

        Button btnZoekBedrijf = new Button("zoek");
        Button btnZoekPakket = new Button("zoek");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField pakketText = new TextField();
        pakketText.setPromptText("Pakket");
        TextField landbouwbedrijfText = new TextField();
        landbouwbedrijfText.setPromptText("Landbouwbedrijf");
        TextField prijsText = new TextField();
        prijsText.setPromptText("Prijs");

        grid.add(new Label("Pakket*:"), 0, 0);
        grid.add(pakketText, 1, 0);
        grid.add(btnZoekPakket, 2, 0);
        grid.add(new Label("Aangeboden door*:"), 0, 1);
        grid.add(landbouwbedrijfText, 1, 1);
        grid.add(btnZoekBedrijf, 2, 1);
        grid.add(new Label("Prijs*:"), 0, 2);
        grid.add(prijsText, 1, 2);
        dialog.getDialogPane().setContent(grid);

        btnZoekBedrijf.setOnAction(e -> {
            var optionalLandbouwbedrijf = showZoekBedrijfDialog(landbouwbedrijfText.getText().trim());
            if (optionalLandbouwbedrijf.isPresent()){
                newAanbieding.setLandbouwbedrijf(optionalLandbouwbedrijf.get());
                landbouwbedrijfText.setText(newAanbieding.getLandbouwbedrijf().getNaam());
            }
        });

        btnZoekPakket.setOnAction(e -> {
            var optionalPakket = showZoekPakketDialog(pakketText.getText().trim());
            if (optionalPakket.isPresent()){
                newAanbieding.setPakket(optionalPakket.get());
                pakketText.setText(newAanbieding.getPakket().getPakketnaam());
            }
        });

        Node voegtoeButton = dialog.getDialogPane().lookupButton(voegToeButtonType);
        voegtoeButton.setDisable(true);

        List<TextField> textfields = new ArrayList<>();
        textfields.add(pakketText);
        textfields.add(landbouwbedrijfText);
        textfields.add(prijsText);

        for (TextField t : textfields){
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                voegtoeButton.setDisable(
                                 prijsText.getText().isEmpty()
                                || (newAanbieding.getPakket()==null)
                                || (newAanbieding.getLandbouwbedrijf()==null));
            });
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType){
                newAanbieding.setPrijs(Integer.valueOf(prijsText.getText().trim()));
                return newAanbieding;

            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Optional<Aanbieding> showChangeFilterDialog() {
        Dialog<Aanbieding> dialog = new Dialog<>();
        dialog.setTitle("Filter Aanbiedingen");
        dialog.setHeaderText("voeg filters toe voor het zoeken van Aanbiedingen.");

        Pakket pakketFilter = new Pakket();
        Landbouwbedrijf landbouwbedrijfFilter = new Landbouwbedrijf();

        ButtonType voegToeButtonType = new ButtonType("Filter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voegToeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField pakketText = new TextField();
        pakketText.setPromptText("Pakket");
        TextField bedrijfText = new TextField();
        bedrijfText.setPromptText("Bedrijf");
        TextField minPrijsText = new TextField();
        minPrijsText.setPromptText("Minimum Prijs");
        TextField maxPrijsText = new TextField();
        maxPrijsText.setPromptText("Maximum ");


        if (filterAanbieding != null){
            pakketText.setText(filterAanbieding.getPakket().getPakketnaam());
            bedrijfText.setText(filterAanbieding.getLandbouwbedrijf().getNaam());
        }
        minPrijsText.setText(String.valueOf(minPrijsFilter));
        maxPrijsText.setText(String.valueOf(maxPrijsFilter));

        grid.add(new Label("Pakketnaam:"), 0, 0);
        grid.add(pakketText, 1, 0);
        grid.add(new Label("bedrijfsnaam:"), 0, 1);
        grid.add(bedrijfText, 1, 1);
        grid.add(new Label("Prijs minimum:"), 0, 2);
        grid.add(minPrijsText, 1, 2);
        grid.add(new Label("tot maximum:"), 2, 2);
        grid.add(maxPrijsText, 3, 2);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType) {
                if (filterAanbieding == null){
                    filterAanbieding = new Aanbieding();
                }
                pakketFilter.setPakketnaam(pakketText.getText().trim());
                landbouwbedrijfFilter.setNaam(bedrijfText.getText().trim());
                filterAanbieding.setPakket(pakketFilter);
                filterAanbieding.setLandbouwbedrijf(landbouwbedrijfFilter);
                minPrijsFilter = Integer.valueOf(minPrijsText.getText().trim());
                maxPrijsFilter = Integer.valueOf(maxPrijsText.getText().trim());
                return filterAanbieding;
            }
            else return filterAanbieding;
        });
        return dialog.showAndWait();
    }

    private Optional<Landbouwbedrijf> showZoekBedrijfDialog(String bedrijfnaam){
        Dialog<Landbouwbedrijf> dialog = new Dialog<>();
        dialog.setHeaderText("selecteer een Landbouwbedrijf");

        ButtonType voegToeButtonType = new ButtonType("selecteer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voegToeButtonType, ButtonType.CANCEL);

        TableView<Landbouwbedrijf> tblBedrijven = new TableView<>();
        TableColumn<Landbouwbedrijf, String> naamColumn = new TableColumn<>("naam");
        TableColumn<Landbouwbedrijf, Integer> ondernemingsNRColumn = new TableColumn<>("ondernemingsnummer");

        naamColumn.setCellValueFactory(new PropertyValueFactory<>("naam"));
        ondernemingsNRColumn.setCellValueFactory(new PropertyValueFactory<>("ondernemingsNR"));

        tblBedrijven.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblBedrijven.getColumns().addAll(ondernemingsNRColumn, naamColumn);

        dialog.getDialogPane().setPrefWidth(350);
        dialog.getDialogPane().setContent(tblBedrijven);

        tblBedrijven.setFixedCellSize(35);
        tblBedrijven.prefHeightProperty().bind(Bindings.size(tblBedrijven.getItems()).multiply(tblBedrijven.getFixedCellSize()).add(45));

        Landbouwbedrijf filterbedrijf = new Landbouwbedrijf(0,bedrijfnaam,"",0);
        filterbedrijf.setLand("");
        System.out.println(filterbedrijf.getGemeente());
        for (Landbouwbedrijf bedrijf : CsaDatabaseConn.getDatabaseConn().getCsaRepo().getLandbouwbedrijven(filterbedrijf)){
            tblBedrijven.getItems().add(bedrijf);
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType) {
                return tblBedrijven.getSelectionModel().getSelectedItem();
            }
            else return null;
        });

        return dialog.showAndWait();
    }

    private Optional<Pakket> showZoekPakketDialog(String pakketnaam) {
        Dialog<Pakket> dialog = new Dialog<>();
        dialog.setHeaderText("selecteer een Pakket");

        ButtonType voegToeButtonType = new ButtonType("selecteer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voegToeButtonType, ButtonType.CANCEL);

        TableView<Pakket> tblPakketen = new TableView<>();
        TableColumn<Pakket, String> naamColumn = new TableColumn<>("pakketnaam");
        TableColumn<Pakket, String> beschrijvingColumn = new TableColumn<>("beschrijving");

        naamColumn.setCellValueFactory(new PropertyValueFactory<>("pakketnaam"));
        beschrijvingColumn.setCellValueFactory(new PropertyValueFactory<>("beschrijving"));

        tblPakketen.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblPakketen.getColumns().addAll(naamColumn, beschrijvingColumn);

        dialog.getDialogPane().setPrefWidth(350);
        dialog.getDialogPane().setContent(tblPakketen);

        tblPakketen.setFixedCellSize(35);
        tblPakketen.prefHeightProperty().bind(Bindings.size(tblPakketen.getItems()).multiply(tblPakketen.getFixedCellSize()).add(45));

        Pakket filterpakket = new Pakket(pakketnaam, 0, 0, "");
        for (Pakket pakket : CsaDatabaseConn.getDatabaseConn().getCsaRepo().getPakketen(filterpakket)) {
            tblPakketen.getItems().add(pakket);
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType) {
                return tblPakketen.getSelectionModel().getSelectedItem();
            } else return null;
        });
        return dialog.showAndWait();
    }
}
