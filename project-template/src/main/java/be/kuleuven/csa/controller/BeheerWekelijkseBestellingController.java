package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.domain.Landbouwbedrijf;
import be.kuleuven.csa.model.domain.Product;
import be.kuleuven.csa.model.domain.WekelijkseBestelling;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.lightcouch.CouchDbClient;

import java.util.ArrayList;
import java.util.List;

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

    ArrayList<WekelijkseBestelling> wekelijkseBestellingen = new ArrayList<WekelijkseBestelling>();

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
                    showAddNewRowDialog (clickedRow);
                }
            });
            return row ;
        });


        klantID.setCellValueFactory((new PropertyValueFactory<>("klantID")));
        bestelNR.setCellValueFactory(new PropertyValueFactory<>("bestelNR"));
        Afgehaald.setCellValueFactory((new PropertyValueFactory<>("afgehaald")));

        // TODO verwijderen en "echte data" toevoegen!
        CouchDbClient dbClient = new CouchDbClient();

        //List<JsonObject> bestellingen = dbClient.view("_all_docs").key("_design/wekelijksebestelllingen").query(JsonObject.class);
        //System.out.println("bestellingen" + bestellingen.get(0));

        //wekelijkseBestellingen  = dbClient.view("_all_docs").query(wekelijksebestelllingent.class);
        //JsonObject jsonOfView =  dbClient.find(JsonObject.class,"_design/wekelijksebestelllingen");
        //System.out.println("JSON:" + jsonOfView);
        List<JsonObject> wekelijkseBestellingen = dbClient.view("_all_docs").includeDocs(true).startKey("fb17").query(JsonObject.class);
        List<JsonObject> bestellingen = new ArrayList<>();

        /*
        for (JsonObject wb : wekelijkseBestellingen){
            System.out.println(" wb get :" + wb.get("id"));
            String Id = wb.get("id").toString();
           // JsonObject jsonb1 =  dbClient.find(JsonObject.class,Id);
            // var jsonb1 = dbClient.find(Id);
            //dbClient.find()
            //bestellingen.add(jsonb);
        }


        /*                                                  "fb17bd06530cd5b0f5730113e4004202"
        JsonObject jsonb1 =  dbClient.find(JsonObject.class,"fb17bd06530cd5b0f5730113e4004202");
        bestellingen.add(jsonb1);
        JsonObject jsonb2 =  dbClient.find(JsonObject.class,"fb17bd06530cd5b0f5730113e40090c1");
        bestellingen.add(jsonb2);
        JsonObject jsonb3 =  dbClient.find(JsonObject.class,"fb17bd06530cd5b0f5730113e400c872");
        bestellingen.add(jsonb3);
        JsonObject jsonb4 =  dbClient.find(JsonObject.class,"fb17bd06530cd5b0f5730113e4017b40");
        bestellingen.add(jsonb4);
        JsonObject jsonb5 =  dbClient.find(JsonObject.class,"fb17bd06530cd5b0f5730113e401ba4a");
        bestellingen.add(jsonb5);
        */

        Gson gson = new Gson();
        for(JsonObject json: wekelijkseBestellingen) {
            System.out.println(" --------------------------------------------------------------------------------");
            System.out.println(" -------------------------------- TEST ------------------------------------------\n");
            System.out.println("\n \n \n");
            System.out.println("JSON:" + json);
            System.out.println("\n \n \n");
            System.out.println(" --------------------------------------------------------------------------------");


                WekelijkseBestelling b = gson.fromJson(json, WekelijkseBestelling.class);
                System.out.println(" --------------------------------------------------------------------------------");
                System.out.println(" -------------------------------- JSON HAS ------------------------------------------ \n");
                System.out.println("bestelling:" + b.getBestelNR());
                System.out.println("\n \n \n");
                System.out.println(" --------------------------------------------------------------------------------");
                tblWeekelijkseBestellingen.getItems().add(b);





        }


        tblWeekelijkseBestellingen.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblWeekelijkseBestellingen.getColumns().addAll(bestelNR,  klantID, Afgehaald);

        // shutdown the client
        dbClient.shutdown();

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
        if(tblWeekelijkseBestellingen.getSelectionModel().getSelectedCells().size() == 0) {
            showAlert("Hela!", "Eerst een bestelling selecteren hé.");
        }
    }


    private void showAddNewRowDialog(WekelijkseBestelling bestelling) {

        Dialog<Landbouwbedrijf> dialog = new Dialog<>();
        dialog.setTitle("Producten van de bestelling");
        dialog.setHeaderText("Hier is een overzicht voor alle producten van de geselecteerde bestelling");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        Product[] producten = bestelling.getProducten();
        int i = 0;
        for (Product p : producten) {
            grid.add(new Label("product :"), 0, i);
            grid.add(new Label(p.getNaam()), 1, i);
            grid.add(new Label("Aantal : "), 2, i);
            grid.add(new Label("" +p.getAantal()), 3, i);
            i++;
        }

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }




}
