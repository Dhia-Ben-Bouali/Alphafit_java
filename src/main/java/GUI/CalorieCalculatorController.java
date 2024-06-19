package GUI;

import com.jfoenix.controls.JFXRadioButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CalorieCalculatorController {
    @FXML
    private JFXRadioButton female;
    @FXML
    private JFXRadioButton male;
    @FXML
    private TextField age;

    @FXML
    private Label ageError;

    @FXML
    private TextField height;

    @FXML
    private Label heightError;

    @FXML
    private Label result;

    @FXML
    private TextField weight;

    @FXML
    private Label weightError;

    @FXML
    void handleCalculButtonClick() {
        String ageText = age.getText();
        String heightText = height.getText();
        String weightText = weight.getText();

        // Validate input
        if (ageText.isEmpty() || heightText.isEmpty() || weightText.isEmpty()) {
            // Show error message if any field is empty
            ageError.setText("Please enter age");
            heightError.setText("Please enter height");
            weightError.setText("Please enter weight");
            return;
        }

        try {
            int ageValue = Integer.parseInt(ageText);
            double heightValue = Double.parseDouble(heightText);
            double weightValue = Double.parseDouble(weightText);

            // Calculate total calories based on gender (assuming some formula)
            double totalCalories = calculateTotalCalories(ageValue, heightValue, weightValue, female.isSelected());

            // Display the result
            result.setText(totalCalories+" Calories");
        } catch (NumberFormatException e) {
            // Show error message if input is not a valid number
            ageError.setText("Invalid age");
            heightError.setText("Invalid height");
            weightError.setText("Invalid weight");
        }
    }
    private double calculateTotalCalories(int age, double height, double weight, boolean isFemale) {
        double bmr;
        double totalCalories;

        if (isFemale) {
            bmr = 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age);
        } else {
            bmr = 66 + (13.7 * weight) + (5 * height) - (6.8 * age);
        }
        totalCalories = bmr * 1.2;

        return totalCalories;
    }


}
