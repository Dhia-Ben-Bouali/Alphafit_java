package GUI;



import entite.Article;
import entite.commande;
import entite.lignecommande;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ArticleService;
import services.CommandeService;
import services.LigneCommandeService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandDetailsController {
    @FXML
    private ListView<HBox> articleList;
    private commande com;
    private List<lignecommande> lignecommandeList = new ArrayList<>();
    @FXML
    void handleCanxelButton() {
        ((Stage) articleList.getScene().getWindow()).close();
    }

    @FXML
    void handleValidateButton() {
        if(!com.isValid()){
            CommandeService commandeService = new CommandeService();
            com.setValid(true);
            commandeService.update(com, Math.toIntExact(com.getId()));
            ArticleService articleService = new ArticleService();
            for(int i=0;i<lignecommandeList.size();i++){
                Article article = articleService.getById(Math.toIntExact(lignecommandeList.get(i).getArticleId()));
                article.setQuantite(article.getQuantite()-lignecommandeList.get(i).getQuantite());
                articleService.update(article,article.getId());
            }
        }

        ((Stage) articleList.getScene().getWindow()).close();
    }

    public void setCommandDetails(){
        try {
            lignecommandeList = new ArrayList<>(lignecommandsList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            for(int i=0;i<lignecommandeList.size();i++){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/commandDetailsCard.fxml"));
                HBox cardBox = null;
                cardBox = fxmlLoader.load();
                CommandDetailsCardController packCardController = fxmlLoader.getController();
                packCardController.setData(lignecommandeList.get(i));
                //packCardController.setController(this);
                articleList.getItems().add(cardBox);
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





    private List<lignecommande> lignecommandsList() throws SQLException {
        List<lignecommande> ls = new ArrayList<>();
        LigneCommandeService lignecommandeService= new LigneCommandeService();
        ls = lignecommandeService.getByCommandId(com.getId());
        for (lignecommande p : ls) {
            System.out.println(p.getId());
        }
        return ls;
    }

    public void setData(commande com) {
        this.com = com;
    }
}
