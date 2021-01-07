package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import be.kuleuven.csa.model.domain.Aanbieding;
import be.kuleuven.csa.model.domain.Landbouwbedrijf;
import be.kuleuven.csa.model.domain.Product;
import be.kuleuven.csa.model.domain.WekelijkseBestelling;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import netscape.javascript.JSObject;
import org.lightcouch.CouchDbClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.util.*;
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
    public TableColumn<WekelijkseBestelling, String> _id = new TableColumn<>("_id");
    @FXML
    public TableColumn<WekelijkseBestelling, String> bestelNR = new TableColumn<>("bestelNR");
    @FXML
    public TableColumn<WekelijkseBestelling, Integer> klantID = new TableColumn<>("klantID");
    @FXML
    public TableColumn<WekelijkseBestelling, Boolean> Afgehaald = new TableColumn<>("Afgehaald");
    @FXML
    public TableColumn<WekelijkseBestelling, Integer> Weeknummer = new TableColumn<>("Weeknummer");

    List<WekelijkseBestelling> wekelijkseBestellingen = new ArrayList<WekelijkseBestelling>();

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
        _id.setCellValueFactory((new PropertyValueFactory<>("_id")));
        klantID.setCellValueFactory((new PropertyValueFactory<>("klantID")));
        bestelNR.setCellValueFactory(new PropertyValueFactory<>("bestelNR"));
        Afgehaald.setCellValueFactory((new PropertyValueFactory<>("afgehaald")));
        Weeknummer.setCellValueFactory((new PropertyValueFactory<>("weeknummer")));
        
        CouchDbClient dbClient = new CouchDbClient();

        List<JsonObject> wekelijkseBestellingenJSON = dbClient.view("_all_docs").startKey("B").includeDocs(true).query(JsonObject.class);

        Gson gson = new Gson();
        for(JsonObject json: wekelijkseBestellingenJSON) {
            WekelijkseBestelling b = gson.fromJson(json, WekelijkseBestelling.class);
            if(b.getBestelNR() != null) {
                wekelijkseBestellingen.add(b);
                tblWeekelijkseBestellingen.getItems().add(b);
            }
        }

        tblWeekelijkseBestellingen.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblWeekelijkseBestellingen.getColumns().addAll(_id,bestelNR,  klantID, Afgehaald, Weeknummer);

        // shutdown the client
        dbClient.shutdown();

    }

    private void addNewRow() {
        Optional<WekelijkseBestelling> bestellingToeTeVoegen = showAddNewRowDialog();

        CouchDbClient dbClient = new CouchDbClient();
        bestellingToeTeVoegen.ifPresent(bestelling -> dbClient.save(bestelling));

        // shutdown the client
        dbClient.shutdown();
        refreshTable();
    }

    private void refreshTable(){
        tblWeekelijkseBestellingen.getItems().clear();
        wekelijkseBestellingen.clear();
        CouchDbClient dbClient = new CouchDbClient();

        List<JsonObject> wekelijkseBestellingenJSON = dbClient.view("_all_docs").startKey("B").includeDocs(true).query(JsonObject.class);
        Gson gson = new Gson();
        for(JsonObject json: wekelijkseBestellingenJSON) {
            WekelijkseBestelling b = gson.fromJson(json, WekelijkseBestelling.class);
            if(b.getBestelNR() != null) {
                wekelijkseBestellingen.add(b);
                tblWeekelijkseBestellingen.getItems().add(b);
            }
        }
        // shutdown the client
        dbClient.shutdown();
    }


    private void deleteCurrentRow() {
        WekelijkseBestelling selectedItem = tblWeekelijkseBestellingen.getSelectionModel().getSelectedItem();

        //new client
        CouchDbClient dbClient = new CouchDbClient();
        JsonObject jsonobj= dbClient.find(JsonObject.class,selectedItem.get_id());
        dbClient.remove(jsonobj);
        // shutdown the client
        dbClient.shutdown();

        refreshTable();
    }

    private void modifyCurrentRow() {
        WekelijkseBestelling selectedItem = tblWeekelijkseBestellingen.getSelectionModel().getSelectedItem();
        Optional<WekelijkseBestelling> bestellingAanTePassen = showUpdateRowDialog(selectedItem);
        bestellingAanTePassen.ifPresent(bestelling ->   update(bestelling) );

        refreshTable();
    }

    public void update(WekelijkseBestelling bestellingUpdate){
        CouchDbClient dbClient = new CouchDbClient();

        JsonObject jsonobj= dbClient.find(JsonObject.class,bestellingUpdate.get_id());
        dbClient.remove(jsonobj);
        dbClient.save(bestellingUpdate);

        // shutdown the client
        dbClient.shutdown();
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
            showAlert("Hela!", "Eerst een bestelling selecteren he.");
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
        dialog.setTitle("Voeg bestellling toe");
        dialog.setHeaderText("Voeg een nieuwe bestelling toe, velden met een * zijn verplicht");

        ButtonType voegToeButtonType = new ButtonType("Voeg toe", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(voegToeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 20, 10));

        // Bestelnummer genereren
        int n = 1;
        String bn = "B" + n;

        for(WekelijkseBestelling b1 : wekelijkseBestellingen){
            String bx = b1.getBestelNR();
            System.out.println("bx= " + bx);
            System.out.println("bn= " + bn);
            while(bn.equals(bx)){
                n++;
                bn = "B" + n;
            }
        }

        for(WekelijkseBestelling b2 : wekelijkseBestellingen){
            String bx = b2.getBestelNR();
            System.out.println("bx= " + bx);
            System.out.println("bn= " + bn);
            while(bn.equals(bx)){
                n++;
                bn = "B" + n;
            }
        }

        Label bestelNR = new Label(bn);
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
        Button addProductButton = new Button("add product");
        Label productentext = new Label("producten toegevoegd");
        productentext.setVisible(false);
        Label productToegevoegText = new Label("product");
        productToegevoegText.setVisible(false);
        Label aantalToegevoegText = new Label("aantal");
        aantalToegevoegText.setVisible(false);
        Label nieuwproducttext = new Label();
        Label nieuwaantaltext = new Label();


        grid.add(new Label("BestelNR*:"), 0, 0);
        grid.add(bestelNR, 1, 0);
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

        grid.add(addProductButton, 2, 5);
        grid.add(productentext, 0, 6);
        grid.add(productToegevoegText, 0, 7);
        grid.add(aantalToegevoegText, 0, 8);
        grid.add(nieuwproducttext, 1, 7);
        grid.add(nieuwaantaltext, 1, 8);
        dialog.getDialogPane().setContent(grid);

        Node voegtoeButton = dialog.getDialogPane().lookupButton(voegToeButtonType);
        voegtoeButton.setDisable(true);
        addProductButton.setDisable(true);

        List<TextField> textfields = new ArrayList<>();
        textfields.add(klantIDtext);
        textfields.add(contractIDtext);
        textfields.add(weeknummertext);
        textfields.add(producttext);
        textfields.add(aantaltext);

        Map<String,Integer> producten = new HashMap<>();


        for (TextField t : textfields){
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                voegtoeButton.setDisable(klantIDtext.getText().isEmpty()  || weeknummertext.getText().isEmpty() || contractIDtext.getText().isEmpty());
                addProductButton.setDisable(producttext.getText().isEmpty() || aantaltext.getText().isEmpty());

            });
        }

         dialog.setResultConverter(dialogButton -> {
            if (dialogButton == voegToeButtonType){
                WekelijkseBestelling newWekelijkseBestelling = new WekelijkseBestelling(
                        bestelNR.getText(),
                        Integer.parseInt(klantIDtext.getText().trim()),
                        Integer.parseInt(contractIDtext.getText().trim())
                );

                newWekelijkseBestelling.set_id(bestelNR.getText());
                newWekelijkseBestelling.setBestelNR(bestelNR.getText());
                newWekelijkseBestelling.setAfgehaald(false);
                newWekelijkseBestelling.setProducten(producten);
                newWekelijkseBestelling.setWeeknummer(Integer.parseInt(weeknummertext.getText()));
                wekelijkseBestellingen.add(newWekelijkseBestelling);
                return newWekelijkseBestelling;
            }
            return null;
        });

        addProductButton.setOnAction(e -> {
            productentext.setVisible(true);
            productToegevoegText.setVisible(true);
            aantalToegevoegText.setVisible(true);
            producten.put(producttext.getText().trim(),Integer.parseInt(aantaltext.getText().trim()));
            nieuwproducttext.setText(producttext.getText().trim());
            nieuwaantaltext.setText(aantaltext.getText().trim());
            producttext.clear();
            aantaltext.clear();
        });

        return dialog.showAndWait();
    }

    private Optional<WekelijkseBestelling> showUpdateRowDialog(WekelijkseBestelling selectedBestelling){
        Dialog<WekelijkseBestelling> dialog = new Dialog<>();
        dialog.setTitle("verander de geselcteerde bestelling");

        ButtonType opslaanButtonType = new ButtonType("Opslaan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(opslaanButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 20, 10));

        Label bestelNR = new Label(selectedBestelling.get_id());
        TextField klantIDtext = new TextField();
        klantIDtext.setText(""+selectedBestelling.getKlantID());
        TextField contractIDtext = new TextField();
        contractIDtext.setText(""+selectedBestelling.getContractID());
        TextField weeknummertext = new TextField();
        weeknummertext.setText(""+selectedBestelling.getWeeknummer());
        TextField afgehaaldtext= new TextField();
        afgehaaldtext.setText(""+selectedBestelling.isAfgehaald());
        Button bewerkProductenButton = new Button("Wijzig producten");

        grid.add(new Label("BestelNR:"), 0, 0);
        grid.add(bestelNR, 1, 0);
        grid.add(new Label("KlantID:"), 0, 1);
        grid.add(klantIDtext, 1, 1);
        grid.add(new Label("ContractID:"), 0, 2);
        grid.add(contractIDtext, 1, 2);
        grid.add(new Label("weeknummer"), 0, 3);
        grid.add(weeknummertext, 1, 3);
        grid.add(new Label("afgehaald"), 0, 4);
        grid.add(afgehaaldtext, 1, 4);
        grid.add(bewerkProductenButton, 0, 5);

        dialog.getDialogPane().setContent(grid);

        List<TextField> textfields = new ArrayList<>();
        textfields.add(klantIDtext);
        textfields.add(contractIDtext);
        textfields.add(weeknummertext);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == opslaanButtonType){

                selectedBestelling.setKlantID(Integer.parseInt(klantIDtext.getText()));
                selectedBestelling.setWeeknummer(Integer.parseInt(weeknummertext.getText()));

                Boolean afgehaald = false;
                if(!afgehaaldtext.getText().equals("false")){
                    afgehaald = true;
                }
                selectedBestelling.setAfgehaald(afgehaald);
                return selectedBestelling;
            }
            return null;
        });

        bewerkProductenButton.setOnAction(e -> {
            System.out.println("BEWERK SET ON ACTION");
            showUpdateProductenDialog(selectedBestelling);
        });
        return dialog.showAndWait();
    }

    private Optional<WekelijkseBestelling> showUpdateProductenDialog( WekelijkseBestelling selectedBestelling){
        Dialog<WekelijkseBestelling> dialog = new Dialog<>();
        dialog.setTitle("verander de geselcteerde bestelling");


        ButtonType opslaanButtonType = new ButtonType("Opslaan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(opslaanButtonType, ButtonType.CANCEL);


        TableView<Product> tblproducten = new TableView<Product>();
        TableColumn<Product, String> productNaam = new TableColumn<>("product");
        TableColumn<Product, Integer> aantal = new TableColumn<>("Aantal");

        Map<String,Integer> producten = selectedBestelling.getProducten();

        tblproducten.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblproducten.getColumns().clear();
        tblproducten.setEditable(true);


        productNaam.setCellValueFactory(new PropertyValueFactory<>("naam"));
        productNaam.setCellFactory(TextFieldTableCell.forTableColumn());
        productNaam.setOnEditCommit(event -> {
            Product selectedProduct = event.getRowValue();
            if(selectedProduct.getNaam().equals("Enter new product here")){
                Product toeTeVoegenProd = new Product("Enter new product here", Integer.parseInt("0"));
                tblproducten.getItems().add(toeTeVoegenProd);
            }
            producten.remove(selectedProduct.getNaam());
            selectedProduct.setNaam(event.getNewValue());
            producten.put(selectedProduct.getNaam(), selectedProduct.getAantal());
        });

        aantal.setCellValueFactory(new PropertyValueFactory<>("aantal"));
        aantal.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        aantal.setOnEditCommit(event -> {
           Product selectedProduct = event.getRowValue();
            selectedProduct.setAantal(event.getNewValue());
            producten.replace(selectedProduct.getNaam(), selectedProduct.getAantal());
        });



        for (Map.Entry p : producten.entrySet()) {
            Product toeTeVoegenProd = new Product(""+p.getKey(), Integer.parseInt(p.getValue()+""));
            tblproducten.getItems().add(toeTeVoegenProd);
        }
        Product toeTeVoegenProd = new Product("Enter new product here", Integer.parseInt("0"));
        tblproducten.getItems().add(toeTeVoegenProd);

        tblproducten.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tblproducten.getColumns().addAll(productNaam, aantal);
        Button addProductButton = new Button("add product");
        dialog.getDialogPane().setContent(addProductButton);
        dialog.getDialogPane().setContent(tblproducten);



        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == opslaanButtonType){
                for (Map.Entry p : producten.entrySet()) {
                    if(p.getValue().equals("Enter new product here")){
                    producten.remove("Enter new product here");
                    }
                }
                selectedBestelling.setProducten(producten);
                return selectedBestelling;
            }
            return null;
        });


        return dialog.showAndWait();
    }


}
