package GUI;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import services.EmailSender;
import services.userService;

import javax.mail.MessagingException;

public class UserNotificationJob implements Job {

    private userService userService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        int userId = context.getMergedJobDataMap().getInt("userId");

        // Retrieve user's email based on user ID
        userService = new userService();
        String userEmail = userService.getUserEmailById(userId);

        // Compose the email content
        String emailContent = "<html><head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f0f0f0; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #fff; border-radius: 10px; }" +
                ".header { background-color: #007bff; color: #fff; text-align: center; padding: 10px 0; border-radius: 10px 10px 0 0; }" +
                ".content { padding: 20px; }" +
                ".button { display: inline-block; background-color: #007bff; color: #fff; padding: 10px 20px; text-decoration: none; border-radius: 5px; }" +
                "</style></head><body>" +
                "<div class=\"container\">" +
                "<div class=\"header\"><h2>Please activate user</h2></div>" +
                "<div class=\"content\">" +
                "<p>User ID: " + userId + "</p>" +
                "<p>User email: " + userEmail + "</p>" +
                "<a href=\"#\" class=\"button\">Activate User</a>" +
                "</div></div></body></html>";

        // Use the email service to send the HTML email to the administrator
        try {
            EmailSender.sendEmail("ikramsegni28@gmail.com", "Activate User", emailContent);
            // Trigger notification in the application
            notifyAdmin(userId, userEmail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to show notification in the app
    private void notifyAdmin(int userId, String userEmail) {
        // Implement logic to show notification in the app
        // This could involve displaying a popup, updating a notification panel, etc.
        // For example, if using JavaFX:
        showNotification("User Activation", "Please activate user with ID " + userId + ". User email: " + userEmail);
    }

    // Method to display a notification using JavaFX
    private void showNotification(String title, String message) {
        // Implement JavaFX notification logic here
        // This could involve showing an alert, updating a notification panel, etc.
        // For example, if using JavaFX:
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
