package GUI;

import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.util.Duration;
import javafx.scene.image.ImageView;

public class PreloadControler {


    @FXML
    private ImageView imgeReload;

    public void initialize() {

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), imgeReload);

        rotateTransition.setByAngle(360);

        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);

        rotateTransition.play();
    }
}
