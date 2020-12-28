package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import be.kuleuven.csa.model.domain.Klant;
import be.kuleuven.csa.model.domain.Landbouwbedrijf;
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

public class BeheerKlantenController {

    @FXML
    private Button btnDeleteKlant;
    @FXML
    private Button btnAddKlant;
    @FXML
    private Button btnCloseKlantenscherm;
    @FXML
    private Button btnFilterKlanten;
    @FXML
    private TableView<Klant> tblKlanten;
    @FXML
    public TableColumn<Klant, String> klantnaam = new TableColumn<>("Naam");
    @FXML
    public TableColumn<Klant, String> klantGemeente= new TableColumn<>("Gemeente");
    @FXML
    public TableColumn<Klant, Integer> klantPostcode = new TableColumn<>("Postcode");
    @FXML
    public  TableColumn<Klant, String> klantStraat = new TableColumn<>("Straatnaam");
    @FXML
    public TableColumn<Klant, Integer> klantHuisNR = new TableColumn<>("Huisnummer");
    @FXML
    public TableColumn<Klant, String> klantLand = new TableColumn<>("Land");

    private Klant filterKlant;

    public void initialize() {
        initTable();

        btnAddKlant.setOnAction(e -> addNewRow());

        btnDeleteKlant.setOnAction(e -> {
            verifyOneRowSelected();
            deleteCurrentRow();
        });

        btnCloseKlantenscherm.setOnAction(e -> {
            var stage = (Stage) btnCloseKlantenscherm.getScene().getWindow();
            stage.close();
        });

        btnFilterKlanten.setOnAction(e -> {
            filterKlanten();
        });
    }

    private void initTable() {
        tblKlanten.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblKlanten.getColumns().clear();
        tblKlanten.setEditable(true);

        klantnaam.setCellValueFactory(new PropertyValueFactory<>("naam"));
        klantnaam.setCellFactory(TextFieldTableCell.forTableColumn());
        klantnaam.setOnEditCommit(event -> {
            Klant selectedKlant = event.getRowValue();
            selectedKlant.setNaam(event.getNewValue());
            modifyCurrentRow();
        });

        klantGemeente.setCellValueFactory((new PropertyValueFactory<>("gemeente")));
        klantGemeente.setCellFactory(TextFieldTableCell.forTableColumn());
        klantGemeente.setOnEditCommit(event -> {
            Klant selectedKlant = event.getRowValue();
            selectedKlant.setGemeente(event.getNewValue());
            modifyCurrentRow();
        });

        klantPostcode.setCellValueFactory(new PropertyValueFactory<>("postcode"));
        klantPostcode.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        klantPostcode.setOnEditCommit(event -> {
            Klant selectedKlant = event.getRowValue();
            selectedKlant.setPostcode(Integer.valueOf(event.getNewValue()));
            modifyCurrentRow();
        });

        klantStraat.setCellValueFactory(new PropertyValueFactory<>("straat"));
        klantStraat.setCellFactory(TextFieldTableCell.forTableColumn());
        klantStraat.setOnEditCommit(event -> {
            Klant selectedKlant = event.getRowValue();
            selectedKlant.setStraat(event.getNewValue());
            modifyCurrentRow();
        });

        klantHuisNR.setCellValueFactory(new PropertyValueFactory<>("huisnummer"));
        klantHuisNR.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        klantHuisNR.setOnEditCommit(event -> {
            Klant selectedKlant = event.getRowValue();
            selectedKlant.setHuisnummer(Integer.valueOf(event.getNewValue()));
            modifyCurrentRow();
        });

        klantLand.setCellValueFactory(new PropertyValueFactory<>("land"));
        klantLand.setCellFactory(TextFieldTableCell.forTableColumn());
        klantLand.setOnEditCommit(event -> {
            Klant selectedKlant = event.getRowValue();
            selectedKlant.setLand(event.getNewValue());
            modifyCurrentRow();
        });

        filterKlant = null;
        refreshTable();

        tblKlanten.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblKlanten.getColumns().addAll(klantnaam, klantGemeente, klantPostcode, klantStraat, klantHuisNR, klantLand);
    }


    private void addNewRow() {
        var bedrijfToeTeVoegen = showAddNewRowDialog();
        bedrijfToeTeVoegen.ifPresent(landbouwbedrijf -> CsaDatabaseConn.getDatabaseConn().getCsaRepo().persistRecord(landbouwbedrijf));
        refreshTable();
    }

    private void deleteCurrentRow() {
        Klant selectedRow = tblKlanten.getSelectionModel().getSelectedItem();
        var entitymanager = CsaDatabaseConn.getDatabaseConn().getEntityManager();
        entitymanager.getTransaction().begin();
        entitymanager.remove(selectedRow);
        CsaDatabaseConn.getDatabaseConn().getCsaRepo().flushAndClear();
        entitymanager.getTransaction().commit();

        refreshTable();
    }

    private void modifyCurrentRow() {
        Klant selectedRow = tblKlanten.getSelectionModel().getSelectedItem();
        CsaDatabaseConn.getDatabaseConn().getCsaRepo().updateRecord(selectedRow);
    }

    private void refreshTable(){
        tblKlanten.getItems().clear();
        for (Klant klant: CsaDatabaseConn.getDatabaseConn().getCsaRepo().getKlanten(filterKlant)) {
            tblKlanten.getItems().add(klant);
        }
    }

    public void showAlert(String title, String content) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void filterKlanten(){
        var optionalFilterKlant = showChangeFilterDialog();
        filterKlant = optionalFilterKlant.orElse(null);
        refreshTable();
    }

    private void verifyOneRowSelected() {
        if(tblKlanten.getSelectionModel().getSelectedCells().size() == 0) {
            showAlert("Hela!", "Eerst een klant selecteren hé.");
        }
    }

    private Optional<Klant> showAddNewRowDialog(){
        Dialog<Klant> dialog = new Dialog<>();
        dialog.setTitle("Voeg Klant toe");
        dialog.setHeaderText("Voeg een nieuwe Klant toe, velden met een * zijn verplicht");

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
        TextField straattext = new TextField();
        straattext.setPromptText("Straat");
        TextField huisNRtext = new TextField();
        huisNRtext.setPromptText("Huisnummer");
        TextField landtext = new TextField();
        landtext.setPromptText("Land");

        grid.add(new Label("Naam*:"), 0, 0);
        grid.add(naamtext, 1, 0);
        grid.add(new Label("Gemeente*:"), 0, 1);
        grid.add(gemeentetext, 1, 1);
        grid.add(new Label("Postcode*:"), 0, 2);
        grid.add(postcodetext, 1, 2);
        grid.add(new Label("Straat*:"), 0, 3);
        grid.add(straattext, 1, 3);
        grid.add(new Label("Huisnummer*:"), 0, 4);
        grid.add(huisNRtext, 1, 4);
        grid.add(new Label("Land*"), 0, 5);
        grid.add(landtext, 1, 5);
        dialog.getDialogPane().setContent(grid);

        Node voegtoeButton = dialog.getDialogPane().lookupButton(voegToeButtonType);
        voegtoeButton.setDisable(true);

        List<TextField> textfields = new ArrayList<>();
        textfields.add(naamtext);
        textfields.add(gemeentetext);
        textfields.add(postcodetext);
        textfields.add(huisNRtext);
        textfields.add(straattext);
        textfields.add(landtext);

        for (TextField t : textfields){
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                voegtoeButton.setDisable(
                        naamtext.getText().isEmpty()
                        || gemeentetext.getText().isEmpty()
                        || huisNRtext.getText().isEmpty()
                        || postcodetext.getText().isEmpty()
                        || straattext.getText().isEmpty()
                        || landtext.getText().isEmpty());
            });
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType){
                Klant newKlant = new Klant(
                        naamtext.getText().trim(),
                        gemeentetext.getText().trim(),
                        Integer.parseInt(postcodetext.getText().trim()),
                        straattext.getText().trim(),
                        Integer.parseInt(huisNRtext.getText().trim()),
                        landtext.getText().trim());
                return newKlant;

            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Optional<Klant> showChangeFilterDialog() {
        Dialog<Klant> dialog = new Dialog<>();
        dialog.setTitle("Filter klanten");
        dialog.setHeaderText("voeg filters toe voor het zoeken van klanten.");

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
        TextField straattext = new TextField();
        straattext.setPromptText("Straat");
        TextField huisNRtext = new TextField();
        huisNRtext.setPromptText("Huisnummer");
        TextField landtext = new TextField();
        landtext.setPromptText("Land");

        grid.add(new Label("Naam:"), 0, 0);
        grid.add(naamtext, 1, 0);
        grid.add(new Label("Gemeente:"), 0, 1);
        grid.add(gemeentetext, 1, 1);
        grid.add(new Label("Postcode:"), 0, 2);
        grid.add(postcodetext, 1, 2);
        grid.add(new Label("Straat:"), 0, 3);
        grid.add(straattext, 1, 3);
        grid.add(new Label("Huisnummer:"), 0, 4);
        grid.add(huisNRtext, 1, 4);
        grid.add(new Label("Land"), 0, 5);
        grid.add(landtext, 1, 5);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType) {
                if (filterKlant == null){
                    filterKlant = new Klant();
                }
                filterKlant.setNaam(naamtext.getText().trim());
                filterKlant.setGemeente(gemeentetext.getText().trim());
                filterKlant.setLand(landtext.getText().trim());
                filterKlant.setStraat(straattext.getText().trim());
                if (!huisNRtext.getText().trim().isEmpty()){
                    filterKlant.setHuisnummer(Integer.valueOf(huisNRtext.getText().trim()));
                }
                if (!postcodetext.getText().trim().isEmpty()){
                    filterKlant.setPostcode(Integer.valueOf(postcodetext.getText().trim()));
                }
                return filterKlant;
            }
            else return filterKlant;
        });
        return dialog.showAndWait();
    }
}
