package GUI;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import entite.Pack;
import entite.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ShowPackController {

    @FXML
    private Text description;

    @FXML
    private JFXListView<HBox> list;

    @FXML
    private Label name;

    @FXML
    private Label price;
    private AnchorPane listroot;
    private AnchorPane parentPane;
    private Parent showServiceRoot;
    private Pane darkOverlay;
    private Pack pack;

    public void setV(AnchorPane listroot, AnchorPane parentPane, Parent showServiceRoot, Pane darkOverlay) {
        this.listroot = listroot;
        this.parentPane = parentPane;
        this.showServiceRoot = showServiceRoot;
        this.darkOverlay = darkOverlay;
    }

    @FXML
    void handleCloseButtonClick() {
        parentPane.getChildren().remove(darkOverlay);
        listroot.setOpacity(1.0);
        parentPane.getChildren().remove(showServiceRoot);
    }

    public void setData(Pack pack){
        this.pack = pack;
        name.setText(pack.getName());
        description.setText(pack.getDescription());
        price.setText(String.valueOf(pack.getPrice()));
        list.getItems().clear();

        for (Service service : pack.getServices()) {
            HBox hbox = new HBox();
            hbox.setSpacing(10);
            ImageView icon = new ImageView(new Image("/imagess/plus.png"));
            icon.setFitWidth(20); // Set the width of the icon
            icon.setFitHeight(20);
            Label nameLabel = new Label(service.getName());
            nameLabel.setFont(Font.font("Ebrima", FontWeight.BOLD, 16));
            hbox.getChildren().addAll(icon, nameLabel);
            hbox.setPadding(new Insets(5, 0, 5, 0));
            list.getItems().add(hbox);
        }
    }
}
