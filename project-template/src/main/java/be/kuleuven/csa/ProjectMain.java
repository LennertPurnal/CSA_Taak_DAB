package be.kuleuven.csa;

import be.kuleuven.csa.model.domain.CsaDatabaseRepo;
import be.kuleuven.csa.model.domain.Klant;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.persistence.Persistence;

public class ProjectMain extends Application {

    private static Stage rootStage;

    public static Stage getRootStage() {
        return rootStage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        rootStage = stage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("csamain.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("CSA Administratie hoofdscherm");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
       launch();
        var sessionfactory = Persistence.createEntityManagerFactory("be.kuleuven.csa.model.domain");
        var entitymanager = sessionfactory.createEntityManager();

        var repo = new CsaDatabaseRepo(entitymanager);
        repo.saveNewKlant(new Klant("Jozef", "Tongeren", 3700, "Kleinstraat", 10, "België"));
        entitymanager.close();


    }
}
