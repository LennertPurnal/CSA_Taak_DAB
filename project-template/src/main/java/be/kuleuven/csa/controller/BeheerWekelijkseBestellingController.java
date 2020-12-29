package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import be.kuleuven.csa.model.domain.WekelijkseBestelling;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.lightcouch.CouchDbClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BeheerWekelijkseBestellingController {

    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnModify;
    @FXML
    private Button btnClose;
    @FXML
    private TableView<WekelijkseBestelling> tblWeekelijkseBestellingen;
    @FXML
    public TableColumn<WekelijkseBestelling, String> bestelNR = new TableColumn<>("bestelNR");
    @FXML
    public TableColumn<WekelijkseBestelling, Integer> klantID = new TableColumn<>("klantID");
    @FXML
    public TableColumn<WekelijkseBestelling, Boolean> Afgehaald = new TableColumn<>("Afgehaald");
    @FXML
    public TableColumn<WekelijkseBestelling, Boolean> Weeknummer = new TableColumn<>("Weeknummer");


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
        tblWeekelijkseBestellingen.getColumns().clear();
        tblWeekelijkseBestellingen.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblWeekelijkseBestellingen.setEditable(true);

        tblWeekelijkseBestellingen.setRowFactory(tv -> {
            TableRow<WekelijkseBestelling> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton()== MouseButton.PRIMARY
                        && event.getClickCount() == 2) {

                    WekelijkseBestelling clickedRow = row.getItem();
                    showProductenDialog (clickedRow);
                }
            });
            return row ;
        });

        klantID.setCellValueFactory((new PropertyValueFactory<>("klantID")));
        bestelNR.setCellValueFactory(new PropertyValueFactory<>("bestelNR"));
        Afgehaald.setCellValueFactory((new PropertyValueFactory<>("afgehaald")));
        Weeknummer.setCellValueFactory((new PropertyValueFactory<>("weeknummer")));

        // TODO verwijderen en "echte data" toevoegen!
        CouchDbClient dbClient = new CouchDbClient();

        List<JsonObject> wekelijkseBestellingen = dbClient.view("_all_docs").includeDocs(true).startKey("B").query(JsonObject.class);

        Gson gson = new Gson();
        for(JsonObject json: wekelijkseBestellingen) {
            System.out.println(" --------------------------------------------------------------------------------");
            System.out.println(" -------------------------------- JSONTEST ------------------------------------------\n");
            System.out.println("\n \n \n");
            System.out.println("JSON:" + json);
            System.out.println("\n \n \n");
            System.out.println(" --------------------------------------------------------------------------------");

            WekelijkseBestelling b = gson.fromJson(json, WekelijkseBestelling.class);


            tblWeekelijkseBestellingen.getItems().add(b);
        }

        tblWeekelijkseBestellingen.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblWeekelijkseBestellingen.getColumns().addAll(bestelNR,  klantID, Afgehaald, Weeknummer);

        // shutdown the client
        dbClient.shutdown();

    }

    private void addNewRow() {
        Optional<WekelijkseBestelling> bestellingToeTeVoegen= showAddNewRowDialog();
        //Gson gson = new Gson();
        //JsonObject jsonobj = new JsonObject();
        CouchDbClient dbClient = new CouchDbClient();
        bestellingToeTeVoegen.ifPresent(bestelling -> dbClient.save(bestelling));
        System.out.println(" --------------------------------------------------------------------------------");
        System.out.println(" ---------------------------- before dialog closes ------------------------------\n");
        System.out.println("\n \n \n");
        System.out.println("bedrijf" + bestellingToeTeVoegen);
        System.out.println("\n \n \n");
        System.out.println("\n \n \n");
        System.out.println(" --------------------------------------------------------------------------------");
        // shutdown the client
        dbClient.shutdown();
        //refreshTable();
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
        if(tblWeekelijkseBestellingen.getSelectionModel().getSelectedCells().size() == 0) {
            showAlert("Hela!", "Eerst een bestelling selecteren hé.");
        }
    }


    private void showProductenDialog(WekelijkseBestelling bestelling) {

        Dialog<WekelijkseBestelling> dialog = new Dialog<>();
        dialog.setTitle("Producten van de bestelling");
        dialog.setHeaderText("Hier is een overzicht voor alle producten van de geselecteerde bestelling");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        Map<String, Integer> producten = bestelling.getProducten();
        int i = 0;
        for (Map.Entry<String,Integer> p : producten.entrySet()) {

            grid.add(new Label("product :"), 0, i);
            grid.add(new Label(p.getKey()), 1, i);
            grid.add(new Label("Aantal : "), 2, i);
            grid.add(new Label("" +p.getValue()), 3, i);
            i++;
        }

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }

    private Optional<WekelijkseBestelling> showAddNewRowDialog(){
        Dialog<WekelijkseBestelling> dialog = new Dialog<>();
        dialog.setTitle("Voeg Landbouwbedrijf toe");
        dialog.setHeaderText("Voeg een nieuw Landbouwbedrijf toe, velden met een * zijn verplicht");

        ButtonType voegToeButtonType = new ButtonType("Voeg toe", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voegToeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField bestelNRtext = new TextField();
        bestelNRtext.setPromptText("bestelNR");
        TextField klantIDtext = new TextField();
        klantIDtext.setPromptText("klantID");
        TextField contractIDtext = new TextField();
        contractIDtext.setPromptText("ContractID");
        TextField weeknummertext = new TextField();
        weeknummertext.setPromptText("weeknummer");
        TextField producttext = new TextField();
        producttext.setPromptText("product");
        TextField aantaltext = new TextField();
        aantaltext.setPromptText("aantal");

        grid.add(new Label("BestelNR*:"), 0, 0);
        grid.add(bestelNRtext, 1, 0);
        grid.add(new Label("KlantID*:"), 0, 1);
        grid.add(klantIDtext, 1, 1);
        grid.add(new Label("ContractID*:"), 0, 2);
        grid.add(contractIDtext, 1, 2);
        grid.add(new Label("weeknummer"), 0, 3);
        grid.add(weeknummertext, 1, 3);
        grid.add(new Label("product"), 0, 4);
        grid.add(producttext, 1, 4);
        grid.add(new Label("aantal"), 0, 5);
        grid.add(aantaltext, 1, 5);
        dialog.getDialogPane().setContent(grid);

        Node voegtoeButton = dialog.getDialogPane().lookupButton(voegToeButtonType);
        voegtoeButton.setDisable(true);

        List<TextField> textfields = new ArrayList<>();
        textfields.add(klantIDtext);
        textfields.add(bestelNRtext);
        textfields.add(contractIDtext);
        textfields.add(weeknummertext);
        textfields.add(aantaltext);

        for (TextField t : textfields){
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                voegtoeButton.setDisable(klantIDtext.getText().isEmpty() || bestelNRtext.getText().isEmpty() || weeknummertext.getText().isEmpty() || contractIDtext.getText().isEmpty());
            });
        }
         dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType){
                WekelijkseBestelling newWekelijkseBestelling = new WekelijkseBestelling(
                        bestelNRtext.getText().trim(),
                        Integer.parseInt(klantIDtext.getText().trim()),
                        Integer.parseInt(contractIDtext.getText().trim())
                );

                newWekelijkseBestelling.setAfgehaald(false);
                return newWekelijkseBestelling;
            }
            return null;
        });

        return dialog.showAndWait();
    }

}
