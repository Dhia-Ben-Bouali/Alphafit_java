package test;

import GUI.listarticleController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ArticleService;

import java.io.IOException;

public class MainFx extends Application {
    public static final String CURRENCY = "DT"; // Exemple : devise en dollars

    @Override
    public void start(Stage stage) throws IOException {
        try {
            // Initialisez le service ArticleService
            ArticleService articleService = new ArticleService();

            // Chargez le fichier FXML listarticle.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/signin.fxml"));
            loader.setControllerFactory(param -> {
                if (param.equals(listarticleController.class)) {
                    return new listarticleController(articleService); // Instanciez shopController avec le service ArticleService
                } else {
                    try {
                        return param.getConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e); // Gérer les erreurs de création de contrôleur
                    }
                }
            });
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.out.println("Exception loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
