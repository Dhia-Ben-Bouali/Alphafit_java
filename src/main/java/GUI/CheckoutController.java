package GUI;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import entite.CartItem;
import entite.commande;
import entite.lignecommande;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import services.CommandeService;
import services.LigneCommandeService;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


public class CheckoutController implements Initializable {
    @FXML
    private TextField address;

    @FXML
    private Label adressError;

    @FXML
    private TextField firstName;

    @FXML
    private Label firstNameError;

    @FXML
    private TextField lastName;

    @FXML
    private Label lastNameError;

    @FXML
    private TextField num_tel;

    @FXML
    private Label num_telError;

    @FXML
    private ComboBox<String> paymentType;

    @FXML
    private Label paymentTypeError;
    @FXML
    private WebView webView;
    private double total;
    private CartController cartController;
    private List<CartItem> cartItems;


    @FXML
    public void handleSubmitButton() {
        if (!validateInputs()) {
            // Arrêter le traitement si les champs ne sont pas valides
            return;
        }

        // Si les champs sont valides, continuer avec l'ajout de l'article
        String fname = firstName.getText();
        String lname = lastName.getText();
        String ad = address.getText();
        String num = num_tel.getText();
        String pt = paymentType.getValue();
        int n;
        try {
            n = Integer.parseInt(num);
        } catch (NumberFormatException e) {
            System.out.println("Le num doit être un nombre valide.");
            return;
        }
        LocalDate localDate = LocalDate.now();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        commande com = new commande(date,total,fname,lname,ad,num,pt,false);
        CommandeService commandeService = new CommandeService();
        commandeService.add(com);
        commande com2 = commandeService.getlast();
        cartItems = cartController.getList();
        LigneCommandeService ligneCommandeService = new LigneCommandeService();

        for (CartItem item : cartItems) {
            lignecommande line = new lignecommande(com2, item.getQuantite(), (long) item.getArticle().getId());
            ligneCommandeService.add(line);
            System.out.println("heeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }
        if(pt.equals("On Delivery")) {
            cartController.ondelivery = true;
            SmsService.sendSms("+21625495840", "+12315184289", "Thank you for your order. It will be delivered soon.");
            ((Stage) firstName.getScene().getWindow()).close();
        }else {

        cartController.online = true;
        ((Stage) firstName.getScene().getWindow()).close();
        }

    }


    public class SmsService {
        public static final String ACCOUNT_SID = "AC3bff1ae4ce1095be5b82ef90fe951754";
        public static final String AUTH_TOKEN = "216c080ea69e270a77eb998f85e415c4";

        public static void sendSms(String to, String from, String message) {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            Message sms = Message.creator(
                    new PhoneNumber(to),  // To number
                    new PhoneNumber(from),  // From number
                    message                // SMS body
            ).create();
            System.out.println("SMS sent successfully: " + sms.getSid());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paymentType.getItems().addAll("On Delivery","Online");
        paymentType.setOnAction(event -> {
            String selectedLabel = paymentType.getSelectionModel().getSelectedItem();
        });
    }
    private boolean validateInputs() {
        boolean isValid = true;

        if (firstName.getText().isEmpty()) {
            firstNameError.setText("Please enter something.");
            isValid = false;
        }
        if (lastName.getText().isEmpty()) {
            lastNameError.setText("Please enter something.");
            isValid = false;
        }
        if (address.getText().isEmpty()) {
            adressError.setText("Please enter something.");
            isValid = false;
        }
        if (num_tel.getText().isEmpty()) {
            num_tel.setText("Please enter something.");
            isValid = false;
        }

        return isValid;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setData(double v) {
        this.total=v;
    }

    public void setCartController(CartController cartController) {
        this.cartController = cartController;
    }
}
