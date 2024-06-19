package GUI;

import entite.CartItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import services.ArticleService;
import services.CartService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CartController {
    public boolean online = false;
    @FXML
    private AnchorPane contentArea;
    public boolean ondelivery = false;
    @FXML
    private ListView<CartItem> cartListView = new ListView<>(); // Liste qui affichera les éléments du panier
    @FXML
    private Label totalPriceLabel = new Label(); // Label pour afficher le prix total

    private CartService cartService = CartService.getInstance(); // Service de panier

    @FXML
    private Button continueShopping;
    private List<CartItem> list = new ArrayList<>();
    @FXML
    private WebView webView;
    @FXML
    private shopController shopController;

    public List<CartItem> getList() {
        return list;
    }

    public void setList(List<CartItem> list) {
        this.list = list;
    }

    public CartController() {
        // Constructeur pour l'initialisation du contrôleur par le FXMLLoader
        this.cartService = CartService.getInstance();
    }
    @FXML
    public void handleCheckOutButton () {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/checkout.fxml"));
        try {
            Parent root = loader.load();
            CheckoutController controller = loader.getController();
            controller.setData(cartService.calculateTotal());
            controller.setCartController(this);
            Stage stage = new Stage();
            stage.setTitle("Checkout");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(ondelivery){
            try {
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/ondelivery.fxml"));
                Pane packageListContent = loader2.load();

                // Replace the content of the second AnchorPane with the loaded content
                AnchorPane.setTopAnchor(packageListContent, 0.0);
                AnchorPane.setBottomAnchor(packageListContent, 0.0);
                AnchorPane.setLeftAnchor(packageListContent, 0.0);
                AnchorPane.setRightAnchor(packageListContent, 0.0);
                contentArea.getChildren().removeAll();
                contentArea.getChildren().setAll(packageListContent); // Assuming the second child is the AnchorPane to replace
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }
        if (online) {
            try {
                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/online.fxml"));
                Pane packageListContent = loader2.load();

                // Replace the content of the second AnchorPane with the loaded content
                AnchorPane.setTopAnchor(packageListContent, 0.0);
                AnchorPane.setBottomAnchor(packageListContent, 0.0);
                AnchorPane.setLeftAnchor(packageListContent, 0.0);
                AnchorPane.setRightAnchor(packageListContent, 0.0);
                contentArea.getChildren().removeAll();
                contentArea.getChildren().setAll(packageListContent); // Assuming the second child is the AnchorPane to replace
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }


        }


    }


    @FXML
    public void initialize() {
        CartController cartController = this;
        cartListView.setCellFactory(param -> new ListCell<CartItem>() {
            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/cart_item.fxml"));
                        Node node = loader.load();
                        CartItemController itemController = loader.getController();
                        itemController.setCartItem(item);
                        itemController.setCartController(cartController); // Notez l'utilisation de CartController.this pour éviter la confusion avec this dans le contexte de la lambda
                        setGraphic(node);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        updateCartDisplay(); // Mettre à jour l'affichage dès l'initialisation
    }

    @FXML
    private void handleContinueShopping() {
        try {
            // Load the FXML file shop.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shop.fxml"));
            AnchorPane shopContent = loader.load();

            // Retrieve a reference to the ShopController
            shopController = loader.getController();
            shopContent.getProperties().put("controller", shopController);

            // Replace the content of contentArea with the loaded content
            contentArea.getChildren().clear();
            contentArea.getChildren().add(shopContent);

            // Initialize the ArticleService
            ArticleService articleService = new ArticleService();

            // Set the ArticleService for the ShopController
            shopController.setArticleService(articleService);

        } catch (IOException e) {
            System.out.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour mettre à jour l'affichage du panier
    public void updateCartDisplay() {
        cartListView.setItems(FXCollections.observableArrayList(cartService.getCartItems()));
        list.clear();
        list.addAll(cartService.getCartItems());
        totalPriceLabel.setText("Total: $" + cartService.calculateTotal()); // Mettre à jour le prix total
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }



}
