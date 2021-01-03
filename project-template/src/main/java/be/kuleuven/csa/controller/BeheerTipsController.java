package be.kuleuven.csa.controller;

import be.kuleuven.csa.model.domain.Product;
import be.kuleuven.csa.model.domain.Stock;
import be.kuleuven.csa.model.domain.Tip;
import be.kuleuven.csa.model.domain.Tips;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import org.lightcouch.CouchDbClient;
import java.util.List;
import java.util.Map;

public class BeheerTipsController {

    @FXML
    private Button btnDelete;
    @FXML
    private Button btnClose;
    @FXML
    private TableView<Tip> tblTips;
    @FXML
    public TableColumn<Tip, String> tipBeschrijving = new TableColumn<>("Tip beschrijving");
    @FXML
    public TableColumn<Tip, String> uitleg = new TableColumn<>("Uitleg");

    Map<String,String> tipsLijst;

    public void initialize() {
        initTable();

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
        tblTips.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tblTips.getColumns().clear();
        tblTips.setEditable(true);


        tipBeschrijving.setCellValueFactory((new PropertyValueFactory<>("tipBeschrijving")));
        tipBeschrijving.setCellFactory(TextFieldTableCell.forTableColumn());
        tipBeschrijving.setOnEditCommit(event -> {
            Tip selectedTip = event.getRowValue();
            tipsLijst.remove(selectedTip.getTipBeschrijving());
            selectedTip.setTipBeschrijving(event.getNewValue());
            tipsLijst.put(selectedTip.getTipBeschrijving(), selectedTip.getUitleg());
            modifyCurrentRow();
        });


        uitleg.setCellValueFactory(new PropertyValueFactory<>("uitleg"));
        uitleg.setCellFactory(TextFieldTableCell.forTableColumn());
        uitleg.setOnEditCommit(event -> {
            Tip selectedTip = event.getRowValue();
            selectedTip.setUitleg(event.getNewValue());
            tipsLijst.replace(selectedTip.getTipBeschrijving(), selectedTip.getUitleg());
            modifyCurrentRow();
        });



        CouchDbClient dbClient = new CouchDbClient();

        List<JsonObject> wekelijkseBestellingenJSON = dbClient.view("_all_docs").key("TIPS").includeDocs(true).query(JsonObject.class);

        Gson gson = new Gson();
        for(JsonObject json: wekelijkseBestellingenJSON) {
            Tips t = gson.fromJson(json, Tips.class);
            if(t.getTips() != null) {
                tipsLijst = t.getTips();
                for (Map.Entry<String, String> tipVolgende : tipsLijst.entrySet()) {
                   Tip tip = new Tip(tipVolgende.getKey(), tipVolgende.getValue());
                   tblTips.getItems().add(tip);
                }
                Tip tip = new Tip("Enter new tip"," ...");
                tblTips.getItems().add(tip);
            }
        }

        // shutdown the client
        dbClient.shutdown();

       tblTips.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
       tblTips.getColumns().addAll(tipBeschrijving,uitleg);
    }

    private void modifyCurrentRow() {
        update();
        refreshTable();
    }

    public void update(){
        CouchDbClient dbClient = new CouchDbClient();

        JsonObject jsonobj= dbClient.find(JsonObject.class,"TIPS");
        dbClient.remove(jsonobj);
        Tips tipsUpdate = new Tips();
        tipsUpdate.setTips(tipsLijst);
        dbClient.save(tipsUpdate);

        // shutdown the client
        dbClient.shutdown();
    }

    private void refreshTable(){
        tblTips.getItems().clear();
        CouchDbClient dbClient = new CouchDbClient();

        List<JsonObject> wekelijkseBestellingenJSON = dbClient.view("_all_docs").key("TIPS").includeDocs(true).query(JsonObject.class);

        Gson gson = new Gson();
        for(JsonObject json: wekelijkseBestellingenJSON) {
            Tips t = gson.fromJson(json, Tips.class);
            if(t.getTips() != null) {
                tipsLijst = t.getTips();
                for (Map.Entry<String, String> tipVolgende : tipsLijst.entrySet()) {
                    Tip tip = new Tip(tipVolgende.getKey(), tipVolgende.getValue());
                    tblTips.getItems().add(tip);
                }
                Tip tip = new Tip("Enter new tip"," ...");
                tblTips.getItems().add(tip);
            }
        }

        // shutdown the client
        dbClient.shutdown();
    }

    private void deleteCurrentRow() {
       Tip selectedTip = tblTips.getSelectionModel().getSelectedItem();
        tipsLijst.remove(selectedTip.getTipBeschrijving());
        update();
        refreshTable();
    }

    public void showAlert(String title, String content) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void verifyOneRowSelected() {
        if(tblTips.getSelectionModel().getSelectedCells().size() == 0) {
            showAlert("Hela!", "Eerst een bestelling selecteren he.");
        }
    }

}
