package GUI;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import javafx.fxml.FXML;
import java.awt.Desktop;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Token;
import com.stripe.model.checkout.Session;
import com.stripe.param.TokenCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import services.ArticleService;
import services.CartService;

import java.io.IOException;
import java.net.URI;

import java.util.concurrent.CountDownLatch;

public class PaymentController {
    @FXML
    private TextField cardNum;

    @FXML
    private TextField cvcField;

    @FXML
    private TextField expiryDateField;
    @FXML
    private Label errorLabel;
    private CartService cartService = CartService.getInstance();
    @FXML
    private Button payButton;
    @FXML
    private Button cancelButton;

    public void initialize() {
        Stripe.apiKey = "sk_test_51OqkgVGWnxgUQitJJm9uLsNLEaleNivmoJDaKsLfxeeiF3Xwlil5pucqZYkn9gFBwfHwUdUqmpo7owQiPQcVDb0e00hJJu4qlB";

    }


    @FXML
    void HandlePayButton(ActionEvent event) {
        double total = cartService.calculateTotal();
        long totalCents = Math.round(total * 100);
        try {
            PaymentController paymentController = new PaymentController();
            Session session = paymentController.createCheckoutSession(totalCents);
            Desktop.getDesktop().browse(new URI(session.getUrl()));
            System.out.println("Redirect to Stripe Checkout page.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleCancelButton(ActionEvent event) {
        try {
            // Initialisez le service ArticleService
            ArticleService articleService = new ArticleService();

            // Chargez le fichier FXML shop.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shop.fxml"));
            Parent root = loader.load();

            // Obtenir une référence au contrôleur ShopController
            shopController shopController = loader.getController();

            // Vérifier si le contrôleur ShopController n'est pas null
            if (shopController != null) {
                // Définir le service ArticleService pour le contrôleur ShopController
                shopController.setArticleService(articleService);

                // Définir le userData de la racine avec le contrôleur ShopController
                root.setUserData(shopController);
            } else {
                System.out.println("ShopController est null.");
            }

            // Récupérer la scène actuelle
            Scene currentScene = cancelButton.getScene();

            // Changer la scène de la fenêtre actuelle
            currentScene.setRoot(root);

            // Afficher l'alerte uniquement lorsque vous êtes sur la page du magasin
            //shopController.displayLowQuantityAlert();
        } catch (Exception e) {
            System.out.println("Exception lors du chargement du fichier FXML: " + e.getMessage());
            e.printStackTrace();
        }

    }



    public Session createCheckoutSession(long totalAmount) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://example.com/success")
                .setCancelUrl("https://example.com/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(totalAmount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Total Order")
                                                                .build())
                                                .build())
                                .build())
                .build();
        return Session.create(params);
    }

    private boolean validateInput(String cardNum, String[] expiryDateField, String cvcField) {
        if (!isValidCardNumber(cardNum)) {
            errorLabel.setText("Invalid card number.");
            return false;
        }

        if (expiryDateField.length != 2 || !isValidExpDate(expiryDateField[0], expiryDateField[1])) {
            errorLabel.setText("Invalid expiration date.");
            return false;
        }

        if (!isValidCVC(cvcField)) {
            errorLabel.setText("Invalid CVC.");
            return false;
        }

        errorLabel.setText("");
        return true;
    }

    private Token createStripeToken(String cardNumber, String[] expMonthYear, String cvc) throws StripeException {
        TokenCreateParams.Card card = TokenCreateParams.Card.builder()
                .setNumber(cardNumber)
                .setExpMonth(String.valueOf(Long.parseLong(expMonthYear[0].trim())))
                .setExpYear(String.valueOf(Long.parseLong(expMonthYear[1].trim())))
                .setCvc(cvc)
                .build();

        TokenCreateParams params = TokenCreateParams.builder()
                .setCard(card)
                .build();

        return Token.create(params);
    }

    private void processPayment(Token token) {
        System.out.println("Processing payment for token: " + token.getId());

    }

    private boolean isValidCardNumber(String number) {
        return number != null && number.matches("\\d{16}");
    }

    private boolean isValidExpDate(String expMonth, String expYear) {
        return expMonth.matches("0[1-9]|1[0-2]") && expYear.matches("\\d{2}");
    }

    private boolean isValidCVC(String cvc) {
        return cvc.matches("\\d{3,4}");
    }

}