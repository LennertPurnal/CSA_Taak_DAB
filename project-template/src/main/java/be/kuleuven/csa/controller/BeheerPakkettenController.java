package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import be.kuleuven.csa.model.domain.Klant;
import be.kuleuven.csa.model.domain.Pakket;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import org.hibernate.cfg.PkDrivenByDefaultMapsIdSecondPass;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BeheerPakkettenController {

    @FXML
    private Button btnDeletePakket;
    @FXML
    private Button btnAddPakket;
    @FXML
    private Button btnClosePakkettenscherm;
    @FXML
    private TableView<Pakket> tblPakketten;
    @FXML
    public TableColumn<Pakket, String> pakketnaamColumn = new TableColumn<>("Pakketnaam");
    @FXML
    public TableColumn<Pakket, String> beschrijvingColumn = new TableColumn<>("Beschrijving");
    @FXML
    public TableColumn<Pakket, Integer> volwassenenColumn = new TableColumn<>("aantal volwassenen");
    @FXML
    public  TableColumn<Pakket, Integer> kinderenColumn = new TableColumn<>("aatal kinderen");

    private Pakket filterPakket;

    public void initialize() {
        initTable();

        btnAddPakket.setOnAction(e -> addNewRow());

        btnDeletePakket.setOnAction(e -> {
            verifyOneRowSelected();
            deleteCurrentRow();
        });

        btnClosePakkettenscherm.setOnAction(e -> {
            var stage = (Stage) btnClosePakkettenscherm.getScene().getWindow();
            stage.close();
        });

    }

    private void initTable() {
        tblPakketten.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblPakketten.getColumns().clear();
        tblPakketten.setEditable(true);

        pakketnaamColumn.setCellValueFactory(new PropertyValueFactory<>("pakketnaam"));
        pakketnaamColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        pakketnaamColumn.setOnEditCommit(event -> {
            Pakket selectedRow = event.getRowValue();
            selectedRow.setPakketnaam(event.getNewValue());
            modifyCurrentRow();
        });

        beschrijvingColumn.setCellValueFactory((new PropertyValueFactory<>("beschrijving")));
        beschrijvingColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        beschrijvingColumn.setOnEditCommit(event -> {
            Pakket selectedRow = event.getRowValue();
            selectedRow.setBeschrijving(event.getNewValue());
            modifyCurrentRow();
        });

        volwassenenColumn.setCellValueFactory(new PropertyValueFactory<>("aantal_volwassenen"));
        volwassenenColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        volwassenenColumn.setOnEditCommit(event -> {
            Pakket selectedRow = event.getRowValue();
            selectedRow.setAantal_volwassenen(event.getNewValue());
            modifyCurrentRow();
        });

        kinderenColumn.setCellValueFactory(new PropertyValueFactory<>("aantal_kinderen"));
        kinderenColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        kinderenColumn.setOnEditCommit(event -> {
            Pakket selectedRow = event.getRowValue();
            selectedRow.setAantal_kinderen(event.getNewValue());
            modifyCurrentRow();
        });

        filterPakket = null;
        refreshTable();

        tblPakketten.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblPakketten.getColumns().addAll(pakketnaamColumn, volwassenenColumn, kinderenColumn, beschrijvingColumn);
    }


    private void addNewRow() {
        var pakketToeTeVoegen = showAddNewRowDialog();
        pakketToeTeVoegen.ifPresent(pakket -> CsaDatabaseConn.getDatabaseConn().getCsaRepo().persistRecord(pakket));
        refreshTable();
    }

    private void deleteCurrentRow() {
        Pakket selectedRow = tblPakketten.getSelectionModel().getSelectedItem();
        CsaDatabaseConn.getDatabaseConn().getCsaRepo().deleteRecord(selectedRow);

        refreshTable();
    }

    private void modifyCurrentRow() {
        Pakket selectedRow = tblPakketten.getSelectionModel().getSelectedItem();
        CsaDatabaseConn.getDatabaseConn().getCsaRepo().updateRecord(selectedRow);
    }

    private void refreshTable(){
        tblPakketten.getItems().clear();
        for (Pakket pakket: CsaDatabaseConn.getDatabaseConn().getCsaRepo().getPakketen(filterPakket)) {
            tblPakketten.getItems().add(pakket);
        }
    }

    public void showAlert(String title, String content) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void verifyOneRowSelected() {
        if(tblPakketten.getSelectionModel().getSelectedCells().size() == 0) {
            showAlert("Hela!", "Eerst een pakket selecteren hé.");
        }
    }

    private Optional<Pakket> showAddNewRowDialog(){
        Dialog<Pakket> dialog = new Dialog<>();
        dialog.setTitle("Voeg Pakket toe");
        dialog.setHeaderText("Voeg een nieuwe Pakket toe, velden met een * zijn verplicht\n" +
                "geeft ook een korte beschrijving van maximum 300 tekens");

        ButtonType voegToeButtonType = new ButtonType("Voeg toe", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voegToeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField naamtext = new TextField();
        naamtext.setPromptText("Pakketnaam");
        TextArea beschrijvingtext = new TextArea();
        beschrijvingtext.setPromptText("");
        beschrijvingtext.setPrefSize(300, 80);
        beschrijvingtext.setWrapText(true);
        TextField volwassenentext = new TextField();
        volwassenentext.setPromptText("#volwassenen");
        TextField kinderentext = new TextField();
        kinderentext.setPromptText("#kinderen");

        grid.add(new Label("Pakketnaam*:"), 0, 0);
        grid.add(naamtext, 1, 0);
        grid.add(new Label("aantal volwassenen*:"), 0, 1);
        grid.add(volwassenentext, 1, 1);
        grid.add(new Label("aantal kinderen*:"), 0, 2);
        grid.add(kinderentext, 1, 2);
        grid.add(new Label("Beschijving:"), 0, 3);
        grid.add(beschrijvingtext, 1, 3);
        dialog.getDialogPane().setContent(grid);

        Node voegtoeButton = dialog.getDialogPane().lookupButton(voegToeButtonType);
        voegtoeButton.setDisable(true);

        List<TextInputControl> textfields = new ArrayList<>();
        textfields.add(naamtext);
        textfields.add(volwassenentext);
        textfields.add(kinderentext);
        textfields.add(beschrijvingtext);

        for (TextInputControl t : textfields){
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                voegtoeButton.setDisable(
                                naamtext.getText().isEmpty()
                                || volwassenentext.getText().isEmpty()
                                || kinderentext.getText().isEmpty()
                                || beschrijvingtext.getText().isEmpty());
            });
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType){
                return new Pakket(naamtext.getText().trim()
                        , Integer.parseInt(volwassenentext.getText().trim())
                        , Integer.parseInt(kinderentext.getText().trim())
                        , beschrijvingtext.getText().trim());

            }
            return null;
        });

        return dialog.showAndWait();
    }
}
