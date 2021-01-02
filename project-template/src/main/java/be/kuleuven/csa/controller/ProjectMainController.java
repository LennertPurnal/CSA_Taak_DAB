package be.kuleuven.csa.controller;

import be.kuleuven.csa.ProjectMain;
import be.kuleuven.csa.model.databaseConn.CsaDatabaseConn;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProjectMainController {

    @FXML
    private Button btnLandbouwbedrijven;
    @FXML
    private Button btnTips;
    @FXML
    private Button btnKlanten;
    @FXML
    private Button btnContracten;
    @FXML
    private Button btnProducten;
    @FXML
    private Button btnWeekelijkseBest;
    @FXML
    private Button btnAanbiedingen;
    @FXML
    private Button btnPakketten;

    public void initialize() {
        btnLandbouwbedrijven.setOnAction(e -> showBeheerScherm("landbouwbedrijven"));
        btnTips.setOnAction(e -> showBeheerScherm("tips"));
        btnKlanten.setOnAction(e -> showBeheerScherm("klanten"));
        btnContracten.setOnAction(e -> showBeheerScherm("contracten"));
        btnProducten.setOnAction(e -> showBeheerScherm("producten"));
        btnWeekelijkseBest.setOnAction(e -> showBeheerScherm("weekelijksebestellingen"));
        btnAanbiedingen.setOnAction(e -> showBeheerScherm("aanbiedingen"));
        btnPakketten.setOnAction(e -> showBeheerScherm("pakketten"));

        CsaDatabaseConn.getDatabaseConn().setUp();
    }



    private void showBeheerScherm(String id) {
        var resourceName = "beheerschermen/beheer" + id + ".fxml";
        try {
            var stage = new Stage();
            var root = (AnchorPane) FXMLLoader.load(getClass().getClassLoader().getResource(resourceName));
            var scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Beheer van " + id);
            stage.initOwner(ProjectMain.getRootStage());
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

        } catch (Exception e) {
            throw new RuntimeException("Kan beheerscherm " + resourceName + " niet vinden", e);
        }
    }
}
