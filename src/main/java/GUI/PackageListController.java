package GUI;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import entite.Pack;
import entite.Service;
import entite.user;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import services.EmailSender;
import services.PackService;
import services.ServiceService;
import services.userService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PackageListController implements Initializable {
    private List<Pack> packList;
    @FXML
    private ListView<HBox> list = new ListView<>();
    @FXML
    private TextField name;
    @FXML
    private JFXTextArea description;
    @FXML
    private Label descriptionError;
    @FXML
    private Label nameError;
    @FXML
    private Label priceError;
    @FXML
    private JFXListView<JFXRadioButton> servicesSelect;
    private List<Service> servicesList;
    @FXML
    private TextField price;
    @FXML
    private AnchorPane contentArea;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            packList = new ArrayList<>(packsList());
            servicesList = new ArrayList<>(servicesList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            for(int i=0;i<packList.size();i++){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/packageCard.fxml"));
                HBox cardBox = fxmlLoader.load();
                PackageCardController packCardController = fxmlLoader.getController();
                packCardController.setData(packList.get(i));
                packCardController.setController(this);
                packCardController.setRoot(contentArea);
                list.getItems().add(cardBox);
            }
            for(int i=0;i<servicesList.size();i++){
                Service service = servicesList.get(i);
                JFXRadioButton radioButton = new JFXRadioButton(service.getName());
                radioButton.setId(String.valueOf(service.getId()));
                servicesSelect.getItems().add(radioButton);
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
    private List<Service> servicesList() throws SQLException {
        List<Service> ls =new ArrayList<>();
        ServiceService serviceService= new ServiceService();
        ls = serviceService.getAll();
        return ls;
    }
    @FXML
    private void handleRegisterButtonClick() {

        clearErrorMessages();

        boolean isValid = validateInputs();
        if (!isValid) {
            return;
        }
        PackService packService= new PackService();
        ServiceService serviceService = new ServiceService();
        String n = name.getText();
        String descr = description.getText();
        String priceText = price.getText();

        if (!isPriceValid(priceText)) {
            showErrorPopup("Price must be a number.");
            return;
        }
        List<Service> selectedServices = new ArrayList<>();
        boolean atLeastOneServiceSelected = false;
        for (Node node : servicesSelect.getItems()) {
            if (node instanceof JFXRadioButton) {
                JFXRadioButton radioButton = (JFXRadioButton) node;
                if (radioButton.isSelected()) {
                    Service selectedService = null;
                    try {
                        selectedService = serviceService.getById(Integer.parseInt(radioButton.getId()));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    selectedServices.add(selectedService);
                    atLeastOneServiceSelected = true;
                }
            }
        }
        if (!atLeastOneServiceSelected) {
            showErrorPopup("Please select at least one service.");
            return;
        }
        if (packService.pack_existant(n)) {
            showErrorPopup("Package already exists");
            return;
        }
        Pack pack = new Pack(n,descr,Integer.parseInt(priceText),selectedServices);
        try {
            packService.Add(pack);
            name.setText("");
            description.setText("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Package has been added!");
            alert.setHeaderText(null);
            alert.setContentText("Added successfully.");
            alert.show();
            refreshPackageList();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        userService us = new userService();
        List<user> userList = new ArrayList<>();
        userList = us.getAll();
        EmailSender emailSender=new EmailSender();
        for(user u: userList){
            String recipientEmail = u.getEmail();
            String subject = "New Package has been added!";
            String body = "Hello " + u.getPrenom() + ",\n\n";
            body += "We are excited to inform you that a new package has been added to our Packages:\n";
            body += "Package Name: " + pack.getName() + "\n";
            body += "Price: " + pack.getPrice() + " TND\n\n";
            body += "Check out our website for more details!\n";
            body += "Best regards,\nAlpha Fit";

            try {
                emailSender.sendEmail(recipientEmail, subject, body);
            } catch (MessagingException e) {
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }

    }
    private void clearErrorMessages() {
        nameError.setText("");
        descriptionError.setText("");
        priceError.setText("");
    }
    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private boolean validateInputs() {
        boolean isValid = true;

        // Validate nom
        if (name.getText().isEmpty()) {
            nameError.setText("Please enter the name");
            nameError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }


        if (description.getText().isEmpty()) {
            descriptionError.setText("Please enter the description");
            descriptionError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }
        if (price.getText().isEmpty()) {
            priceError.setText("Please enter the price");
            priceError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        return isValid;
    }
    private boolean isPriceValid(String priceText) {
        try {
            int price = Integer.parseInt(priceText);
            return true; // Price is a valid integer
        } catch (NumberFormatException e) {
            return false; // Price is not a valid integer
        }
    }

    public void refreshPackageList() {
        list.getItems().clear();

        try {
            packList = new ArrayList<>(packsList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            for (int i = 0; i < packList.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/packageCard.fxml"));
                HBox cardBox = fxmlLoader.load();
                PackageCardController packCardController = fxmlLoader.getController();
                packCardController.setData(packList.get(i));
                list.getItems().add(cardBox);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleRefreshButtonClick(){
        refreshPackageList();
    }
}
