package Main;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent preloadParent = FXMLLoader.load(getClass().getResource("/Fxml/Preload.fxml"));
        Scene preloadScene = new Scene(preloadParent);

        Parent reclamationParent = FXMLLoader.load(getClass().getResource("/Fxml/Reclamation.fxml"));
        Scene reclamationScene = new Scene(reclamationParent);
        stage.setTitle("AlphaFit");
        stage.setScene(preloadScene);
        stage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {
            stage.setScene(reclamationScene);
        });
        delay.play();
    }

    public static void main(String[] args) {
        launch();
    }
}