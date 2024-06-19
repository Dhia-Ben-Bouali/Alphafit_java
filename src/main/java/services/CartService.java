package services;

import entite.Article;
import entite.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartService {
    private static CartService instance;
    private List<CartItem> cartItems = new ArrayList<>();


    public static CartService getInstance() {
        if (instance == null) {
            synchronized (CartService.class) {
                if (instance == null) {
                    instance = new CartService();
                }
            }
        }
        return instance;
    }

    public CartService() {
        // Private constructor to prevent instantiation from outside this class.
    }

    public void addToCart(Article article, int quantity) {
        CartItem existingItem = cartItems.stream()
                .filter(item -> item.getArticle().getId() == article.getId())
                .findFirst()
                .orElse(null);
        if (existingItem != null) {
            existingItem.setQuantite(existingItem.getQuantite() + quantity);
        } else {
            cartItems.add(new CartItem(article, quantity));
        }
    }


    public void removeFromCart(Article article) {
        cartItems.removeIf(item -> item.getArticle().getId() == article.getId());
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public double calculateTotal() {
        return cartItems.stream().mapToDouble(item -> item.getQuantite() * item.getArticle().getPrix()).sum();
    }

    public void clearCart() {
        cartItems.clear();
    }

    public void updateCart(CartItem updatedItem) {
        for (CartItem cartItem : cartItems) {
            if (cartItem.getArticle().getId() == updatedItem.getArticle().getId()) {
                cartItem.setQuantite(updatedItem.getQuantite());
                break;
            }
        }
    }

}
