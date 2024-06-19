package GUI;

import entite.CartItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import services.CartService;



public class CartItemController {
    @FXML private ImageView itemImage;
    @FXML private Label itemNameLabel;
    @FXML private Label itemPriceLabel;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private Button removeButton;

    private CartItem cartItem;
    private CartService cartService;

    private CartController cartController;

    public void setCartController(CartController cartController) {
        this.cartController = cartController;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
        if (cartItem != null && quantitySpinner.getValueFactory() != null) {
            itemNameLabel.setText(cartItem.getArticle().getNom());
            itemPriceLabel.setText(String.format("$%.2f", cartItem.getArticle().getPrix()));
            quantitySpinner.getValueFactory().setValue(cartItem.getQuantite());
            // Set image here using cartItem.getArticle().getImage()
        }
    }


    @FXML
    private void handleRemoveFromCart() {
        cartService.removeFromCart(cartItem.getArticle());
        cartController.updateCartDisplay();
        // Mettre à jour la liste du panier pour supprimer la cellule
    }

    @FXML
    public void initialize() {
        cartController = new CartController();
        cartService = CartService.getInstance();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        quantitySpinner.setValueFactory(valueFactory);
        removeButton.setOnAction(event -> {
            cartService.removeFromCart(cartItem.getArticle());
            // Update the cart view here
            cartController.updateCartDisplay();
        });
        quantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            cartItem.setQuantite(newValue);
            cartController.updateCartDisplay();
            // Update the cart service and cart view here


        });
    }
}
