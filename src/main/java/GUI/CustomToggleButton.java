package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class CustomToggleButton extends ToggleButton {

    public CustomToggleButton() {
        super();
        setSkin(new CustomToggleButtonSkin(this));
    }

    private class CustomToggleButtonSkin extends SkinBase<CustomToggleButton> implements Skin<CustomToggleButton> {

        private final Rectangle background;
        private final Circle indicator;

        public CustomToggleButtonSkin(CustomToggleButton control) {
            super(control);

            // Create background
            background = new Rectangle(50, 30);
            background.setFill(Color.LIGHTGRAY);
            background.setArcWidth(15);
            background.setArcHeight(15);

            // Create indicator
            indicator = new Circle(15);
            indicator.setFill(Color.LIGHTGRAY);

            // Handle toggle events
            control.selectedProperty().addListener((observable, oldValue, newValue) -> {
                updateIndicator();
            });

            // Set initial state based on toggle state
            updateIndicator();

            // Add indicator and background to button
            StackPane stackPane = new StackPane(background, indicator);
            stackPane.setAlignment(Pos.CENTER_LEFT);
            stackPane.setPadding(new Insets(0, 0, 0, 5));
            getChildren().add(stackPane);
        }

        private void updateIndicator() {
            if (getSkinnable().isSelected()) {
                indicator.setFill(Color.GREEN); // Set color for activated state
                indicator.setTranslateX(20); // Move the indicator to the right for activated state
            } else {
                indicator.setFill(Color.RED); // Set color for deactivated state
                indicator.setTranslateX(0); // Move the indicator to the left for deactivated state
            }
        }
    }
}
