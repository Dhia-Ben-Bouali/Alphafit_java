package GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import services.ArticleService;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardprodController implements Initializable {

    @FXML
    private PieChart pieChart;

    private ArticleService articleService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser le service ArticleService
        articleService = new ArticleService();

        // Récupérer les données d'articles par catégorie
        Map<String, Integer> articleCountsByCategory = articleService.getArticleCountsByCategory();

        // Créer une liste d'objets PieChart.Data pour chaque catégorie
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : articleCountsByCategory.entrySet()) {
            PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
            pieChartData.add(slice);
        }

        // Ajouter les données au PieChart
        pieChart.setData(pieChartData);
    }
}
