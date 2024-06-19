package GUI;

import entite.Article;
import entite.lignecommande;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import services.ArticleService;

public class CommandDetailsCardController {
    @FXML
    private Label S_qtt;

    @FXML
    private Label art;

    @FXML
    private Label id;

    @FXML
    private Label qtt;
    CommandListController commandListController;



    public void setData(lignecommande lc){


        id.setText(String.valueOf(lc.getArticleId()));
        ArticleService articleService = new ArticleService();
        Article article = articleService.getById(Math.toIntExact(lc.getArticleId()));
        art.setText(article.getNom());
        S_qtt.setText(String.valueOf(article.getQuantite()));
        qtt.setText(String.valueOf(lc.getQuantite()));


    }

    public void setController(CommandListController commandListController) {
        this.commandListController = commandListController;
    }

}
