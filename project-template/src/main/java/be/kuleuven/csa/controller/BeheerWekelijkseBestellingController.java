package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import be.kuleuven.csa.model.domain.Aanbieding;
import be.kuleuven.csa.model.domain.Product;
import be.kuleuven.csa.model.domain.WekelijkseBestelling;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.lightcouch.CouchDbClient;

import java.awt.*;
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
    public TableColumn<WekelijkseBestelling, Boolean> Weeknummer = new TableColumn<>("Weeknummer");

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

        // TODO verwijderen en "echte data" toevoegen!
        CouchDbClient dbClient = new CouchDbClient();

        List<JsonObject> wekelijkseBestellingenJSON = dbClient.view("_all_docs")/*.key("bestelNR")*/.includeDocs(true).query(JsonObject.class);

        Gson gson = new Gson();
        for(JsonObject json: wekelijkseBestellingenJSON) {
            System.out.println(" --------------------------------------------------------------------------------");
            System.out.println(" ------------------------------- JSONTEST ---------------------------------------\n");
            System.out.println("\n \n \n");
            System.out.println("JSON:" + json);
            System.out.println("\n \n \n");
            System.out.println(" --------------------------------------------------------------------------------");

            WekelijkseBestelling b = gson.fromJson(json, WekelijkseBestelling.class);
            wekelijkseBestellingen.add(b);
            tblWeekelijkseBestellingen.getItems().add(b);
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

        List<JsonObject> wekelijkseBestellingenJSON = dbClient.view("_all_docs")/*.key("bestelNR")*/.includeDocs(true).query(JsonObject.class);
        Gson gson = new Gson();
        for(JsonObject json: wekelijkseBestellingenJSON) {
            WekelijkseBestelling b = gson.fromJson(json, WekelijkseBestelling.class);
            wekelijkseBestellingen.add(b);
            tblWeekelijkseBestellingen.getItems().add(b);
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

        for(WekelijkseBestelling b : wekelijkseBestellingen){
            String bx = b.getBestelNR();
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

    private Optional<WekelijkseBestelling> showUpdateRowDialog( WekelijkseBestelling selectedBestelling){
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
        klantIDtext.setPromptText(""+selectedBestelling.getKlantID());
        TextField contractIDtext = new TextField();
        contractIDtext.setPromptText(""+selectedBestelling.getContractID());
        TextField weeknummertext = new TextField();
        weeknummertext.setPromptText(""+selectedBestelling.getWeeknummer());
        TextField afgehaaldtext= new TextField();
        afgehaaldtext.setPromptText(""+selectedBestelling.isAfgehaald());
        Button bewerkProductenButton = new Button("wijzig producten");

        grid.add(new Label("BestelNR:"), 0, 0);
        grid.add(bestelNR, 1, 0);
        grid.add(new Label("KlantID*:"), 0, 1);
        grid.add(klantIDtext, 1, 1);
        grid.add(new Label("ContractID*:"), 0, 2);
        grid.add(contractIDtext, 1, 2);
        grid.add(new Label("weeknummer*"), 0, 3);
        grid.add(weeknummertext, 1, 3);
        grid.add(new Label("afgehaald"), 0, 4);
        grid.add(afgehaaldtext, 1, 4);
        grid.add(bewerkProductenButton, 0, 5);

        dialog.getDialogPane().setContent(grid);

        Node voegtoeButton = dialog.getDialogPane().lookupButton(opslaanButtonType);
        voegtoeButton.setDisable(true);

        List<TextField> textfields = new ArrayList<>();
        textfields.add(klantIDtext);
        textfields.add(contractIDtext);
        textfields.add(weeknummertext);

        for (TextField t : textfields){
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                voegtoeButton.setDisable(klantIDtext.getText().isEmpty()  || weeknummertext.getText().isEmpty() || contractIDtext.getText().isEmpty());

            });
        }
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

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 20, 10));


        Map<String,Integer> producten = selectedBestelling.getProducten();;

        List<TextField> textfieldsProd = new ArrayList<>();
        TextField producttext0 = new TextField();
        textfieldsProd.add(producttext0);
        TextField producttext1 = new TextField();
        textfieldsProd.add(producttext1);
        TextField producttext2 = new TextField();
        textfieldsProd.add(producttext2);
        TextField producttext3 = new TextField();
        textfieldsProd.add(producttext3);
        TextField producttext4 = new TextField();
        textfieldsProd.add(producttext4);
        TextField producttext5 = new TextField();
        textfieldsProd.add(producttext5);

        List<TextField> textfieldsAant = new ArrayList<>();
        TextField aantaltext0 = new TextField();;
        textfieldsAant.add(aantaltext0);
        TextField aantaltext1 = new TextField();;
        textfieldsAant.add(aantaltext1);
        TextField aantaltext2 = new TextField();;
        textfieldsAant.add(aantaltext2);
        TextField aantaltext3 = new TextField();;
        textfieldsAant.add(aantaltext3);
        TextField aantaltext4 = new TextField();;
        textfieldsAant.add(aantaltext4);
        TextField aantaltext5 = new TextField();;
        textfieldsAant.add(aantaltext5);

        TextField nieuwProductText = new TextField();
        TextField nieuwAantalText = new TextField();
        Button addProductButton = new Button("add product");
        Label productenToegevoegdText = new Label("product toegevoegd");
        productenToegevoegdText.setVisible(false);
        Label productToegevoegdText = new Label("product");
        productToegevoegdText.setVisible(false);
        Label aantalToegevoegText = new Label("aantal");
        aantalToegevoegText.setVisible(false);
        Label nieuwproductLbl = new Label();
        Label nieuwaantalLbl = new Label();

        int pi = 0;
        int aantal=0;
        if (producten.isEmpty()){
            grid.add(new Label("Er zitten nog geen producten in deze map"), 0, 0);
        }
        else{
            for(Map.Entry p : producten.entrySet()){
                if(aantal <5) {
                    if(aantal == 0) {
                        producttext0.setText("" + p.getKey());
                        aantaltext0.setText("" + p.getValue());
                        grid.add(new Label("product"), 0, pi);
                        grid.add(producttext0, 1, pi);
                        grid.add(new Label("aantal"), 0, pi + 1);
                        grid.add(aantaltext0, 1, pi + 1);
                    }
                    if(aantal == 1) {
                        producttext1.setText("" + p.getKey());
                        aantaltext1.setText("" + p.getValue());
                        grid.add(new Label("product"), 0, pi);
                        grid.add(producttext1, 1, pi);
                        grid.add(new Label("aantal"), 0, pi + 1);
                        grid.add(aantaltext1, 1, pi + 1);
                    }
                    if(aantal == 2) {
                        producttext3.setText("" + p.getKey());
                        aantaltext3.setText("" + p.getValue());
                        grid.add(new Label("product"), 0, pi);
                        grid.add(producttext3, 1, pi);
                        grid.add(new Label("aantal"), 0, pi + 1);
                        grid.add(aantaltext3, 1, pi + 1);
                    }
                    if(aantal == 3) {
                        producttext3.setText("" + p.getKey());
                        aantaltext3.setText("" + p.getValue());
                        grid.add(new Label("product"), 0, pi);
                        grid.add(producttext3, 1, pi);
                        grid.add(new Label("aantal"), 0, pi + 1);
                        grid.add(aantaltext3, 1, pi + 1);
                    }
                    if(aantal == 4) {
                        producttext4.setText("" + p.getKey());
                        aantaltext4.setText("" + p.getValue());
                        grid.add(new Label("product"), 0, pi);
                        grid.add(producttext4, 1, pi);
                        grid.add(new Label("aantal"), 0, pi + 1);
                        grid.add(aantaltext4, 1, pi + 1);
                    }
                    if(aantal == 5) {
                        producttext5.setText("" + p.getKey());
                        aantaltext5.setText("" + p.getValue());
                        grid.add(new Label("product"), 0, pi);
                        grid.add(producttext5, 1, pi);
                        grid.add(new Label("aantal"), 0, pi + 1);
                        grid.add(aantaltext5, 1, pi + 1);
                    }
                    pi= pi + 2;
                    aantal++;
                }
            }

            grid.add(new Label("nieuw product toevoegen"), 0, pi +1);
            grid.add(new Label("product"), 0, pi + 2);
            grid.add(nieuwProductText, 1, pi+2);
            grid.add(new Label("aantal"), 0, pi+3);
            grid.add(nieuwAantalText, 1, pi+3);
            grid.add(addProductButton, 2, pi+3);
            grid.add(productenToegevoegdText, 0, pi +4);
            grid.add(productToegevoegdText, 0, pi + 5);
            grid.add(nieuwproductLbl, 1, pi + 5);
            grid.add(aantalToegevoegText, 0, pi+6);
            grid.add(nieuwaantalLbl, 1, pi + 6);

        }

        dialog.getDialogPane().setContent(grid);

        for (TextField t : textfieldsProd){
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                    Integer aantaltemp = producten.get(oldValue);
                    producten.remove(oldValue);
                    producten.put(newValue, aantaltemp);
            });
        }

        int tf = 0;
        for (TextField t : textfieldsAant){
            TextField textfieldwithkey = textfieldsProd.get(tf);
            String key = textfieldwithkey.getText();
            t.textProperty().addListener((observable, oldValue, newValue) -> {
                if(!newValue.equals("")){
                    producten.replace(key, Integer.parseInt(newValue));
                }
            });
            tf++;
        }


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == opslaanButtonType){
                selectedBestelling.setProducten(producten);
                return selectedBestelling;
            }
            return null;
        });



        addProductButton.setOnAction(e -> {
            productenToegevoegdText.setVisible(true);
            productToegevoegdText.setVisible(true);
            aantalToegevoegText.setVisible(true);
            producten.put(nieuwProductText.getText().trim(),Integer.parseInt(nieuwAantalText.getText().trim()));
            nieuwproductLbl.setText(nieuwProductText.getText().trim());
            nieuwaantalLbl.setText(nieuwAantalText.getText().trim());
            nieuwProductText.clear();
            nieuwAantalText.clear();
        });


        return dialog.showAndWait();
    }

}
