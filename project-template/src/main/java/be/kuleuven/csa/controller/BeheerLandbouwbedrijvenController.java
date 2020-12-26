package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import be.kuleuven.csa.model.domain.Landbouwbedrijf;
import com.sun.javafx.scene.control.IntegerField;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.w3c.dom.Text;

import java.util.Optional;

public class BeheerLandbouwbedrijvenController {

    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAdd;
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
            modifyCurrentRow();
        });

        bedrijfsGemeente.setCellValueFactory((new PropertyValueFactory<>("Gemeente")));
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

        for (Landbouwbedrijf bedrijf: CsaDatabaseConn.getDatabaseConn().getCsaRepo().getLandbouwbedrijven()) {
            tblLandbouwbedrijven.getItems().add(bedrijf);
        }

        tblLandbouwbedrijven.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblLandbouwbedrijven.getColumns().addAll(bedrijfsNaam, bedrijfsGemeente, bedrijfsPostcode, bedrijfsOndernemingsNR, bedrijfsLand);
    }

    private void addNewRow() {
        var bedrijfToeTeVoegen = showAddNewRowDialog();
        if (bedrijfToeTeVoegen.isPresent()){
            CsaDatabaseConn.getDatabaseConn().getCsaRepo().persistRecord(bedrijfToeTeVoegen.get());
        }
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

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType){
                Landbouwbedrijf newbedrijf = new Landbouwbedrijf(
                        Integer.parseInt(ondernemingsNRtext.getText()),
                        naamtext.getText(),
                        gemeentetext.getText(),
                        Integer.parseInt(postcodetext.getText()));
                newbedrijf.setLand(landtext.getText());
                return newbedrijf;

            }
            return null;
        });



        Optional<Landbouwbedrijf> bedrijfToeTeVoegen = dialog.showAndWait();
        return bedrijfToeTeVoegen;
    }

}