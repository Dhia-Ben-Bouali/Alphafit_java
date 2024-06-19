package GUI;

import entite.Service;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class ShowServiceController {

    @FXML
    private Text description;

    @FXML
    private Label name;
    private AnchorPane page;
    private AnchorPane parentPane;
    private Parent showServiceRoot;
    private Pane darkOverlay;
    private Service service;

    public void setV(AnchorPane page, AnchorPane parentPane, Parent showServiceRoot, Pane darkOverlay) {
        this.page = page;
        this.parentPane = parentPane;
        this.showServiceRoot = showServiceRoot;
        this.darkOverlay = darkOverlay;
    }

    @FXML
    void handleCloseButtonClick() {
        parentPane.getChildren().remove(darkOverlay);
        page.setOpacity(1.0);
        parentPane.getChildren().remove(showServiceRoot);
    }

    public void setData(Service service){
        this.service = service;
        name.setText(service.getName());
        description.setText(service.getDescription());

    }
}
