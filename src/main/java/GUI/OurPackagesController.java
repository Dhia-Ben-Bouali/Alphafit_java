package GUI;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import entite.Pack;
import entite.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import services.PackService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OurPackagesController implements Initializable {
    @FXML
    private JFXListView<VBox> list;
    private List<Pack> packList;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            packList = new ArrayList<>(packsList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            for(int i=0;i<packList.size();i++){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/packCardUser.fxml"));
                VBox cardBox = fxmlLoader.load();//the error here
                cardBox.setStyle("-fx-background-color: transparent");
                PackCardUserController packCardController = fxmlLoader.getController();
                packCardController.setData(packList.get(i));
                list.getItems().add(cardBox);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private List<Pack> packsList() throws SQLException {
        List<Pack> ls =new ArrayList<>();
        PackService packService= new PackService();
        ls = packService.getAll();
        for (Pack p : ls) {
            System.out.println(p.getName());
        }
        return ls;
    }
}
