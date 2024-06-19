package GUI;

import com.jfoenix.controls.JFXListView;
import entite.Pack;
import entite.Service;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class PackCardUserController {
    @FXML
    private Text description;

    @FXML
    private Label name;

    @FXML
    private Label price;

    @FXML
    private JFXListView<HBox> services;
    private Pack pack;

    public void setData(Pack pack){
        this.pack = pack;
        name.setText(pack.getName());
        description.setText(pack.getDescription());
        price.setText(String.valueOf((int)pack.getPrice()));
        services.getItems().clear();
        services.setStyle("-fx-background-color: Transparent");

        for (Service service : pack.getServices()) {
            HBox hbox = new HBox();
            hbox.setSpacing(10);
            ImageView icon = new ImageView(new Image("/imagess/tick.png"));
            icon.setFitWidth(20); // Set the width of the icon
            icon.setFitHeight(20);
            Label nameLabel = new Label(service.getName());
            nameLabel.setStyle("-fx-background-color: Transparent");
            nameLabel.setFont(Font.font("Ebrima", FontWeight.BOLD, 16));
            hbox.getChildren().addAll(icon, nameLabel);
            hbox.setPadding(new Insets(5, 0, 5, 0));
            services.getItems().add(hbox);
        }
    }
}
