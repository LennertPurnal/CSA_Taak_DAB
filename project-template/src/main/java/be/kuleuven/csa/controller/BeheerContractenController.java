package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import be.kuleuven.csa.model.domain.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

        Contract newContract = new Contract();
        Aanbieding aanbieding = new Aanbieding();
        Klant klant = new Klant();

        Button btnZoekAanbieding = new Button("zoek");
        Button btnZoekKlant = new Button("zoek");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField aanbiedingtext = new TextField();
        aanbiedingtext.setPromptText("pakketnaam");
        TextField klantText = new TextField();
        klantText.setPromptText("Klant");

        Node voegtoeButton = dialog.getDialogPane().lookupButton(voegToeButtonType);
        voegtoeButton.setDisable(true);

        final DatePicker datePicker = new DatePicker();
        datePicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                LocalDate localDate = datePicker.getValue();
                Date date = java.sql.Date.valueOf(localDate);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String datestring = dateFormat.format(date);
                voegtoeButton.setDisable(newContract.getPakket() == null || newContract.getKlant() == null || datePicker.getValue() == null);
                newContract.setBegindatum(datestring);
            }
        });

        grid.add(new Label("Aanbieding*:"), 0, 0);
        grid.add(aanbiedingtext, 1, 0);
        grid.add(btnZoekAanbieding, 2, 0);
        grid.add(new Label("Klant*:"), 0, 1);
        grid.add(klantText, 1, 1);
        grid.add(btnZoekKlant, 2, 1);
        grid.add(new Label("vanaf*:"), 0, 2);
        grid.add(datePicker, 1, 2);
        dialog.getDialogPane().setContent(grid);



        List<TextField> textfields = new ArrayList<>();
        textfields.add(aanbiedingtext);
        textfields.add(klantText);

        for (TextField t : textfields){
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                voegtoeButton.setDisable(newContract.getPakket() == null || newContract.getKlant() == null || datePicker.getValue() == null);
            });
        }

        btnZoekAanbieding.setOnAction(e -> {
            var optionalAanbieding = showZoekAanbiedingDialog(aanbiedingtext.getText().trim());
            if (optionalAanbieding.isPresent()){
                newContract.setPakket(optionalAanbieding.get().getPakket());
                newContract.setLandbouwbedrijf(optionalAanbieding.get().getLandbouwbedrijf());
                aanbiedingtext.setText(newContract.getPakket().getPakketnaam());
            }
        });

        btnZoekKlant.setOnAction(e -> {
            var optionalKlant = showZoekKlantDialog(klantText.getText().trim());
            if (optionalKlant.isPresent()){
                newContract.setKlant(optionalKlant.get());
                klantText.setText(newContract.getKlant().getNaam());
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType){
                return newContract;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Optional<Aanbieding> showZoekAanbiedingDialog(String pakketnaam){
        Dialog<Aanbieding> dialog = new Dialog<>();
        dialog.setHeaderText("selecteer een aanbieding");

        ButtonType voegToeButtonType = new ButtonType("selecteer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voegToeButtonType, ButtonType.CANCEL);

        TableView<Aanbieding> tblAanbiedingen = new TableView<>();
        TableColumn<Aanbieding, String> pakketnaamColumn = new TableColumn<>("pakketnaam");
        TableColumn<Aanbieding, String> bedrijfColumn = new TableColumn<>("landbouwbedrijf");
        TableColumn<Aanbieding, Integer> prijsColumn = new TableColumn<>("prijs");

        pakketnaamColumn.setCellValueFactory(aanbieding -> new SimpleStringProperty(aanbieding.getValue().getPakket().getPakketnaam()));
        bedrijfColumn.setCellValueFactory(aanbieding -> new SimpleStringProperty(aanbieding.getValue().getLandbouwbedrijf().getNaam()));
        prijsColumn.setCellValueFactory(aanbieding -> new SimpleObjectProperty<>(aanbieding.getValue().getPrijs()));

        tblAanbiedingen.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblAanbiedingen.getColumns().addAll(bedrijfColumn, pakketnaamColumn, prijsColumn);

        dialog.getDialogPane().setPrefWidth(350);
        dialog.getDialogPane().setContent(tblAanbiedingen);

        tblAanbiedingen.setFixedCellSize(35);
        tblAanbiedingen.prefHeightProperty().bind(Bindings.size(tblAanbiedingen.getItems()).multiply(tblAanbiedingen.getFixedCellSize()).add(45));

        Aanbieding filteraanbieding = new Aanbieding(new Pakket() ,new Landbouwbedrijf(), 0);
        filteraanbieding.getPakket().setPakketnaam(pakketnaam);
        filteraanbieding.getLandbouwbedrijf().setNaam("");

        for (Aanbieding aanbieding : CsaDatabaseConn.getDatabaseConn().getCsaRepo().getAanbiedingen(filteraanbieding, 0 , 100000)){
            tblAanbiedingen.getItems().add(aanbieding);
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType) {
                return tblAanbiedingen.getSelectionModel().getSelectedItem();
            }
            else return null;
        });

        return dialog.showAndWait();
    }

    private Optional<Klant> showZoekKlantDialog(String klantnaam){
        Dialog<Klant> dialog = new Dialog<>();
        dialog.setHeaderText("selecteer een klant");

        ButtonType voegToeButtonType = new ButtonType("selecteer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voegToeButtonType, ButtonType.CANCEL);

        TableView<Klant> tblKlanten = new TableView<>();
        TableColumn<Klant, String> klantnaamColumn = new TableColumn<>("klant");
        TableColumn<Klant, Integer> klantNRColumn = new TableColumn<>("klantNR");

        klantnaamColumn.setCellValueFactory(klant -> new SimpleStringProperty(klant.getValue().getNaam()));
        klantNRColumn.setCellValueFactory(klant -> new SimpleObjectProperty<>(klant.getValue().getKlantID()));

        tblKlanten.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblKlanten.getColumns().addAll(klantNRColumn, klantnaamColumn);

        dialog.getDialogPane().setPrefWidth(350);
        dialog.getDialogPane().setContent(tblKlanten);

        tblKlanten.setFixedCellSize(35);
        tblKlanten.prefHeightProperty().bind(Bindings.size(tblKlanten.getItems()).multiply(tblKlanten.getFixedCellSize()).add(45));

        Klant filterklant = new Klant();
        filterklant.setNaam(klantnaam);
        filterklant.setGemeente("");
        filterklant.setStraat("");
        filterklant.setLand("");

        for (Klant klant : CsaDatabaseConn.getDatabaseConn().getCsaRepo().getKlanten(filterklant)){
            tblKlanten.getItems().add(klant);
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType) {
                return tblKlanten.getSelectionModel().getSelectedItem();
            }
            else return null;
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
