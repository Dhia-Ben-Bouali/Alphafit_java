package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import util.SentimentAnalyzer;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {
    @FXML
    private TextArea answer;


    @FXML
    private TextArea question;

    @FXML
    private StackPane chatbot;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chatbot.setVisible(false);

    }
    @FXML
    private void update() {
        // Récupérer les réponses du formulaire
        List<String> formAnswerStrings = List.of(question.getText());

        String sentimentResult = SentimentAnalyzer.analyzeAnswersSentiment(formAnswerStrings);
        answer.setText(sentimentResult);

    }



    public void handlechatbot(ActionEvent event) {
        chatbot.setVisible(true);

    }
    public void handleExitButtonClick(ActionEvent event) {

        chatbot.setVisible(false);

    }
}
