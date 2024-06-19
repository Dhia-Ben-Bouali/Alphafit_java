
package GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import entite.Avis;
import entite.Place;
import entite.Suivi;
import entite.user;
import services.*;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class CalendarControllerCoach implements Initializable {

    @FXML
    private ImageView image;

    @FXML
    private Pane addworkout;
    @FXML
    private DatePicker Date;
    @FXML
    private ColorPicker backgound, border,text;

    @FXML
    private TextField descriptionTF,tilteTF,placeTF,rate;

    @FXML
    private Spinner<Integer> end_H,end_M,endS,startH,startM, startS;

    @FXML
    private ComboBox<Place> place;
    @FXML
    private CheckBox star1,star2,star3,star4,star5;

    @FXML
    private HBox star_1,star_2,star_3,star_4,star_5,star_0;


    @FXML
    private StackPane details,manageplaces,managecoachingrate,addcoachingrate;
    @FXML
    private Button update,update2,add,delete,update31,delete31,update1,update11;
    private List<Place> places;
    public Suivi suivi_temp;

    @FXML
    private FlowPane calendar;

    @FXML
    private Text monthYearText;

    private LocalDate currentDate;
    private SuiviService suiviService;

    @FXML
    private Label title, description, placee, end, start,subscriber,date,commentaire1,commentaire;


    @FXML
    private ListView<Place> placeListView;
public int idadherent;
    private final PlaceService placeService = new PlaceService();
    public void setId(int idadherent) {
        this.idadherent = idadherent;

    }

    public CalendarControllerCoach()
    {

    }
    public CalendarControllerCoach(int idadherent) {
        this.idadherent = idadherent;

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

image.setVisible(false);
        user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();

        String role = loggedInUser.getRoles().toString();




        if (role.contains("ROLE_USER")) {
            addworkout.setVisible(false);
            image.setVisible(true);
            update11.setVisible(false);

        }

        if (role.contains("ROLE_COACH")) {
            update1.setVisible(false);

        }
        loadPlaces(); // Chargez les lieux au démarrage de l'application
        System.out.println("iddddddddddddddddddddddddd"+idadherent);
        details.setVisible(false);
        managecoachingrate.setVisible(false);
        addcoachingrate.setVisible(false);
        update2.setVisible(false);
        manageplaces.setVisible(false);
        star_5.setVisible(false);
        star_4.setVisible(false);
        star_3.setVisible(false);
        star_2.setVisible(false);
        star_1.setVisible(false);
        Date.setValue(LocalDate.now());
        initializeSpinner(startH, 0, 23, 0);
        initializeSpinner(startM, 0, 59, 0);
        initializeSpinner(startS, 0, 59, 0);
        initializeSpinner(end_H, 0, 23, 0);
        initializeSpinner(end_M, 0, 59, 0);
        initializeSpinner(endS, 0, 59, 0);

        PlaceService placeService = new PlaceService();
        try {
            places = placeService.getAll();
            System.out.println(places);
            place.getItems().addAll(places);
        } catch (SQLException e) {
            e.printStackTrace();
        }




        currentDate = LocalDate.now();
        suiviService = new SuiviService();
        updateCalendar();
    }

    @FXML
    private void prevMonth() {
        currentDate = currentDate.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void nextMonth() {
        currentDate = currentDate.plusMonths(1);
        updateCalendar();
    }


    private void updateCalendar() {
        // Clear previous calendar
        calendar.getChildren().clear();

        // Update month and year text
        monthYearText.setText(currentDate.getMonth().toString() + " " + currentDate.getYear());

        int daysInMonth = currentDate.lengthOfMonth();
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate dayDate = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), i);
            List<Suivi> suiviList;
            try {

                user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();

                String role = loggedInUser.getRoles().toString();




                if (role.contains("COACH")) {

                    suiviList = suiviService.getAllByIDC(this.idadherent); // Appel de la méthode getAllByID avec l'ID de l'utilisateur

                }
                else {
                    suiviList = suiviService.getAllByIDC(loggedInUser.getId()); // Appel de la méthode getAllByID avec l'ID de l'utilisateur

                }

                String appointmentTitle = ""; // Titre par défaut
                String backgroundColor = "lightgray"; // Couleur de fond par défaut
                String textColor = "black"; // Couleur du texte par défaut
                String borderColor = "black"; // Couleur de la bordure par défaut
                int ID = -1;
                for (Suivi suivi : suiviList) {
                    LocalDate suiviStartDate = suivi.getStart().toLocalDateTime().toLocalDate();
                    LocalDate suiviEndDate = suivi.getEnd().toLocalDateTime().toLocalDate();
                    if (!dayDate.isBefore(suiviStartDate) && !dayDate.isAfter(suiviEndDate) && suivi.getPlan_coach().equalsIgnoreCase("oui")) {
                        appointmentTitle = suivi.getTitle() + "\n" +
                                suivi.getStart().toLocalDateTime().toLocalTime().toString() + " - " +
                                suivi.getEnd().toLocalDateTime().toLocalTime().toString();
                        backgroundColor = suivi.getBackground_color();
                        textColor = suivi.getText_color();
                        borderColor = suivi.getBorder_color();
                        this.suivi_temp = suivi;


                        break;
                    }
                }

                VBox dayPane = createDayPane(i, appointmentTitle, backgroundColor, textColor, borderColor); // Création du jour avec les données du suivi
                calendar.getChildren().add(dayPane);
                dayPane.setOnMouseClicked(event -> {
                    this.suivi_temp = (Suivi) dayPane.getUserData();

                    details.setVisible(true);
                    System.out.println("-------------------------------------------------------");
                    System.out.println(this.suivi_temp);
                    System.out.println("-------------------------------------------------------");

                    LocalDateTime localDateTime1 = this.suivi_temp.getStart().toLocalDateTime();
                    LocalDateTime localDateTime2 = this.suivi_temp.getEnd().toLocalDateTime();
                    String formattedDateTime1 = localDateTime1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String formattedDateTime2 = localDateTime2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    System.out.println(formattedDateTime1);
                    System.out.println(formattedDateTime2);
                    description.setText(suivi_temp.getDescription());
                    title.setText(suivi_temp.getTitle());
                    placee.setText(suivi_temp.getPlace().getPlace());
                    start.setText(formattedDateTime1);
                    end.setText(formattedDateTime2);
                    subscriber.setText("ghassen");

// Formater le LocalDateTime en une chaîne de caractères avec le format souhaité

                    System.out.println("temppppppppppp" + this.suivi_temp);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




    }

    private VBox createDayPane(int dayNumber, String appointmentTitle, String backgroundColor, String textColor, String borderColor) {
        VBox dayPane = new VBox();
        dayPane.setPrefSize(100, 100);

        Text titleText = new Text(appointmentTitle);
        Text dayText = new Text(String.valueOf(dayNumber));

        dayPane.getChildren().addAll(dayText, titleText);
        dayPane.setUserData(this.suivi_temp);

        if (!appointmentTitle.isEmpty()) {
            dayPane.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: " + textColor + "; -fx-border-color: " + borderColor + ";"); // Définition du style avec les couleurs et la bordure
            titleText.setFill(Color.web(textColor)); // Définition de la couleur du texte

        } else {
            dayPane.setStyle("-fx-background-color: " + "white");

        }
        return dayPane;
    }





    @FXML
    void addtoplan(ActionEvent event) {
        String errorMessage = "";
        userService serviceuser=new userService();
        user user = serviceuser.getById(idadherent);

        if (tilteTF.getText().isEmpty()) {
            errorMessage += "- The title field is empty.\n";
        }
        if (descriptionTF.getText().isEmpty()) {
            errorMessage += "- The description field is empty.\n";
        }
        if (Date.getValue().isBefore(LocalDate.now())) {
            errorMessage += "- The selected date must be today or later.\n";
        }
        if (place.getValue() == null) {
            errorMessage += "- No place has been selected.\n";
        }

        if (end_H.getValue() < startH.getValue() ||
                (end_H.getValue() == startH.getValue() && end_M.getValue() < startM.getValue()) ||
                (end_H.getValue() == startH.getValue() && end_M.getValue() == startM.getValue() && endS.getValue() <= startS.getValue())) {
            errorMessage += "- The end time must be after the start time.\n";
        }
        SuiviService service=new SuiviService();
        if (service.getByDate(java.sql.Date.valueOf(Date.getValue()),user.getId())) {
            errorMessage += "- A workout is already scheduled for this date. Please select a different date.\n";

        }
        if (!errorMessage.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add Errors");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return;
        }

        String title = tilteTF.getText();
        String description = descriptionTF.getText();
        String isCoach = "oui";
        String isNutri = "non";


        LocalDate date = Date.getValue();
        int startHour = (int) startH.getValue();
        int startMinute = (int) startM.getValue();
        int startSecond = (int) startS.getValue();
        int endHour = (int) end_H.getValue();
        int endMinute = (int) end_M.getValue();
        int endSecond = (int) endS.getValue();
        Color backgroundColor = backgound.getValue();
        Color textColor = text.getValue();
        Color borderColor = border.getValue();
        Place selectedPlace = place.getValue();

        LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.of(startHour, startMinute, startSecond));
        LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.of(endHour, endMinute, endSecond));
        SuiviService suiviService = new SuiviService();
        System.out.println(textColor+"      "+borderColor+"    "+borderColor);
        if (!details.isVisible()) {

            Suivi suivi = new Suivi(
                    isCoach,
                    isNutri,
                    title,
                    description,
                    Timestamp.valueOf(startDateTime),
                    Timestamp.valueOf(endDateTime),
                    false,
                    toRgbString(backgroundColor),
                    toRgbString(borderColor),
                    toRgbString(textColor),
                    user,
                    selectedPlace
            );

            System.out.println("----------------------------------------"+suivi);

            suiviService.Add(suivi);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Workout Added ");
            alert.setHeaderText(null);
            alert.setContentText("Workout added sucssefulyy");
            alert.showAndWait();
            updateCalendar();
            try {
                 int nb= suiviService.getNombreWorkoutMoisCourant(user);
                 String s;
                s = "Workout numbers for this week for " + user.getNom() + " " + user.getPrenom() + " are " + nb;
                NotificationUtil.showNotification("All workout ",s);
            } catch (AWTException | MalformedURLException ex) {
                ex.printStackTrace();
            }
        } else if (details.isVisible()) {
            System.out.println("wselttttttttttttttt");
            Suivi suivi = new Suivi(suivi_temp.getId(),
                    isCoach,
                    isNutri,
                    title,
                    description,
                    Timestamp.valueOf(startDateTime),
                    Timestamp.valueOf(endDateTime),
                    false, // Assumer que tous les suivis ne sont pas des événements de toute la journée
                    toRgbString(backgroundColor),
                    toRgbString(borderColor),
                    toRgbString(textColor),
                    null, // Vous devrez remplir cette partie avec l'utilisateur associé
                    selectedPlace
            );
            System.out.println("wselttttttttttttttt2222222222222");

            suiviService.update(suivi);
            System.out.println("wselttttttttttttttt33333");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Workout ");
            alert.setHeaderText(null);
            alert.setContentText("Workout updated sucssefulyy");
            alert.showAndWait();
            updateCalendar();
        }
        tilteTF.clear();
        descriptionTF.clear();
        place.setPromptText("Choose a place");
        end_H.getValueFactory().setValue(0);
        end_M.getValueFactory().setValue(0);
        endS.getValueFactory().setValue(0);
        startH.getValueFactory().setValue(0);
        startM.getValueFactory().setValue(0);
        startS.getValueFactory().setValue(0);
        backgound.setValue(Color.WHITE);
        border.setValue(Color.WHITE);
        text.setValue(Color.WHITE);
        Date.setValue(LocalDate.now());



    }


    private String toRgbString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }


    public void handleExitButtonClick(ActionEvent event) throws SQLException {

        details.setVisible(false);
        manageplaces.setVisible(false);
        places = placeService.getAll(); // Récupérer la liste des places depuis le service
        place.getItems().addAll(places);
        managecoachingrate.setVisible(false);
        addcoachingrate.setVisible(false);
    }


    public void delete(ActionEvent event) {

        SuiviService suiviService = new SuiviService();
        suiviService.deleteById(this.suivi_temp.getId());

        updateCalendar();
        details.setVisible(false);

    }

    @FXML
    public void update(ActionEvent event) {
        details.setVisible(false);
        add.setVisible(false);
        update2.setVisible(true);


        String currentTitle = suivi_temp.getTitle();

        String currentDescription = suivi_temp.getDescription();
        String currentIsCoach = suivi_temp.getPlan_coach();
        Timestamp currentStart = suivi_temp.getStart();
        Timestamp currentEnd = suivi_temp.getEnd();
        String currentBackgroundColor = suivi_temp.getBackground_color();
        String currentTextColor = suivi_temp.getText_color();
        String currentBorderColor = suivi_temp.getBorder_color();
        Place currentSelectedPlace = suivi_temp.getPlace();


        LocalDateTime startDateTime = suivi_temp.getStart().toLocalDateTime();
        LocalDateTime endDateTime = suivi_temp.getEnd().toLocalDateTime();
        LocalDate date = startDateTime.toLocalDate();
        int startHour = startDateTime.getHour();
        int startMinute = startDateTime.getMinute();
        int startSecond = startDateTime.getSecond();
        int endHour = endDateTime.getHour();
        int endMinute = endDateTime.getMinute();
        int endSecond = endDateTime.getSecond();

        tilteTF.setText(currentTitle);
        descriptionTF.setText(currentDescription);
        Date.setValue(date);
        backgound.setValue(Color.web(currentBackgroundColor));
        border.setValue(Color.web(currentBorderColor));
        text.setValue(Color.web(currentTextColor));
        place.setValue(currentSelectedPlace);


        startH.getValueFactory().setValue(startHour);
        startM.getValueFactory().setValue(startMinute);
        startS.getValueFactory().setValue(startSecond);
        end_H.getValueFactory().setValue(endHour);
        end_M.getValueFactory().setValue(endMinute);

        endS.getValueFactory().setValue(endSecond);




    }


    public void update2(ActionEvent event) {
        String errorMessage = "";
        user user = new user(1, "ghassen", "sakka");

        if (tilteTF.getText().isEmpty()) {
            errorMessage += "- The title field is empty.\n";
        }
        if (descriptionTF.getText().isEmpty()) {
            errorMessage += "- The description field is empty.\n";
        }
        if (Date.getValue().isBefore(LocalDate.now())) {
            errorMessage += "- The selected date must be today or later.\n";
        }
        if (place.getValue() == null) {
            errorMessage += "- No place has been selected.\n";
        }

        if (end_H.getValue() < startH.getValue() ||
                (end_H.getValue() == startH.getValue() && end_M.getValue() < startM.getValue()) ||
                (end_H.getValue() == startH.getValue() && end_M.getValue() == startM.getValue() && endS.getValue() <= startS.getValue())) {
            errorMessage += "- The end time must be after the start time.\n";
        }


        // Afficher une alerte s'il y a des erreurs de saisie
        if (!errorMessage.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errors");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return; // Arrêter le traitement car des erreurs ont été trouvées
        }

        LocalDate date = Date.getValue();
        int startHour = (int) startH.getValue();
        int startMinute = (int) startM.getValue();
        int startSecond = (int) startS.getValue();
        int endHour = (int) end_H.getValue();
        int endMinute = (int) end_M.getValue();
        int endSecond = (int) endS.getValue();

        // Créer les objets Timestamp pour la date et l'heure de début et de fin
        LocalDateTime startDateTime = LocalDateTime.of(date, LocalTime.of(startHour, startMinute, startSecond));
        LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.of(endHour, endMinute, endSecond));

        Suivi suivi= new Suivi(suivi_temp.getId(), "oui","non",tilteTF.getText(),descriptionTF.getText(),Timestamp.valueOf(startDateTime),Timestamp.valueOf(endDateTime),false, toRgbString(backgound.getValue()),toRgbString(border.getValue()),toRgbString(text.getValue()),suivi_temp.getAdherent(),place.getValue());
        System.out.println("*****************************************"+suivi);


        SuiviService s = new SuiviService();
        s.update(suivi);
        updateCalendar();
        add.setVisible(true);
        update2.setVisible(false);

        tilteTF.clear();
        descriptionTF.clear();
        place.setPromptText("Choose a place");
        end_H.getValueFactory().setValue(0);
        end_M.getValueFactory().setValue(0);
        endS.getValueFactory().setValue(0);
        startH.getValueFactory().setValue(0);
        startM.getValueFactory().setValue(0);
        startS.getValueFactory().setValue(0);
        backgound.setValue(Color.WHITE);
        border.setValue(Color.WHITE);
        text.setValue(Color.WHITE);
        Date.setValue(LocalDate.now());

        place.getSelectionModel().clearSelection();

    }

    public void managesplaces(ActionEvent event) {
        System.out.println("jittttttttttttttttt");
        manageplaces.setVisible(true);

        System.out.println("jittttttttttttttttt22222222222");

    }


    private void loadPlaces() {
        try {
            ObservableList<Place> places = FXCollections.observableArrayList(placeService.getAll());
            placeListView.setItems(places);
        } catch (SQLException e) {
            e.printStackTrace(); // Gérez l'exception de manière appropriée, par exemple affichez un message d'erreur à l'utilisateur
        }
    }

    @FXML
    void deleteall(ActionEvent event) {

        placeService.deleteAll();
        loadPlaces();
    }

    @FXML
    void deleteplace(ActionEvent event) throws SQLException {
        Place selectedPlace = placeListView.getSelectionModel().getSelectedItem();
        if (selectedPlace != null) {
            int id = selectedPlace.getId();
            if (placeService.deleteById(id)) {
                loadPlaces();
            }
        }
        places = placeService.getAll();
        place.getItems().addAll(places);
    }

    @FXML
    void updateplace(ActionEvent event) throws SQLException {
        Place selectedPlace = placeListView.getSelectionModel().getSelectedItem();
        String newName = placeTF.getText().trim();
        if (selectedPlace != null && !newName.isEmpty()) {
            Place updatedPlace = new Place(selectedPlace.getId(), newName);
            placeService.update(updatedPlace);
            loadPlaces();
        }

        places = placeService.getAll();
        place.getItems().addAll(places);
    }

    @FXML
    void handleListViewClick(MouseEvent event) {
        Place selectedItem = placeListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            placeTF.setText(selectedItem.getPlace());
        }
    }

    @FXML
    void addplace(ActionEvent event) throws SQLException {
        String placeName = placeTF.getText();
        Place newPlace = new Place(placeName);
        placeService.Add(newPlace);
        loadPlaces();
        PlaceService placeService = new PlaceService();
        try {
            places = placeService.getAll();
            System.out.println(places);
            place.getItems().addAll(places);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



            places = placeService.getAll();
            place.getItems().addAll(places);
    }


    private void initializeSpinner(Spinner<Integer> spinner, int minValue, int maxValue, int initialValue) {
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, maxValue, initialValue));
    }

    @FXML
    void reset(ActionEvent event) {
        descriptionTF.setText("");
        tilteTF.setText("");
        place.getSelectionModel().clearSelection();
    }


    @FXML
    public void OnExport(ActionEvent actionEvent) throws SQLException {

        SuiviService suiviService = new SuiviService();
        List<Suivi> todos = suiviService.getAllByIDC(1); // Exemple : récupérer la liste des tâches depuis un service

        try {
            String firstName = "John"; // Exemple
            String lastName = "Doe"; // Exemple
            String startDate = "2024-04-01"; // Exemple
            String endDate = "2024-04-07"; // Exemple

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText("To Do list pour cette semaine");
                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 670);
                    contentStream.showText("Subscriber First Name : " + firstName);
                    contentStream.newLine();
                    contentStream.showText("Subscriber Last Name : " + lastName);
                    contentStream.newLine();
                    contentStream.showText("To DO list pour la Semaine : " + startDate + " à " + endDate);
                    contentStream.endText();

                    // Draw table headers
                    drawTableHeader(contentStream);

                    // Draw table content
                    drawTableContent(contentStream, todos);

                    // Draw creation date
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 50);
                    contentStream.showText("Généré le " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    contentStream.endText();
                }

                // Save document
                try (OutputStream outputStream = Files.newOutputStream(Paths.get("todolisttt.pdf"))) {
                    document.save(outputStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void drawTableHeader(PDPageContentStream contentStream) throws IOException {
        contentStream.setLineWidth(1);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.setNonStrokingColor(0, 0, 1); // Set non-stroking color to blue
        contentStream.moveTo(50, 600);
        contentStream.lineTo(550, 600);
        contentStream.moveTo(50, 575);
        contentStream.lineTo(550, 575);
        contentStream.stroke();

        contentStream.beginText();
        contentStream.newLineAtOffset(55, 585);
        contentStream.showText("Done");
        contentStream.newLineAtOffset(60, 0);
        contentStream.showText("Not Done");
        contentStream.newLineAtOffset(60, 0);
        contentStream.showText("Day");
        contentStream.newLineAtOffset(70, 0);
        contentStream.showText("To Do");
        contentStream.newLineAtOffset(60, 0);
        contentStream.showText("Description");
        contentStream.newLineAtOffset(60, 0);
        contentStream.showText("Start Time");
        contentStream.newLineAtOffset(60, 0);
        contentStream.showText("End Time");
        contentStream.endText();
    }

    private static void drawTableContent(PDPageContentStream contentStream, List<Suivi> todos) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.setNonStrokingColor(0, 0, 1); // Set non-stroking color to blue

        int y = 525; // Initial y position
        for (Suivi todo : todos) {
            contentStream.moveTo(50, y);
            contentStream.lineTo(550, y);
            contentStream.stroke();

            contentStream.beginText();
            contentStream.newLineAtOffset(55, y - 10); // Offset for "Done" column
            contentStream.showText("");
            contentStream.newLineAtOffset(60, 0); // Offset for "Not Done" column
            contentStream.showText("");
            contentStream.newLineAtOffset(60, 0); // Offset for "Day" column
            contentStream.showText(todo.getStart().toLocalDateTime().format(DateTimeFormatter.ofPattern("EEEE")));
            contentStream.newLineAtOffset(70, 0); // Offset for "To Do" column
            contentStream.showText(todo.getTitle());
            contentStream.newLineAtOffset(140, 0); // Offset for "Description" column
            contentStream.showText(todo.getDescription());
            contentStream.newLineAtOffset(150, 0); // Offset for "Start Time" column
            contentStream.showText(todo.getStart().toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            contentStream.newLineAtOffset(80, 0); // Offset for "End Time" column
            contentStream.showText(todo.getEnd().toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            contentStream.endText();

            y -= 25; // Move to next row
        }
    }

    public void ManageRating(ActionEvent event) {
        user user = new user(1, "ghassen", "sakka");
        AvisService avisservice=new AvisService();
       Avis avis=avisservice.getById(1);
        if (avis==null)
            {
                addcoachingrate.setVisible(true);

            }
else{  update31.setVisible(true);
            delete31.setVisible(true);
    managecoachingrate.setVisible(true);
    commentaire1.setText(avis.getCommentaire());
            if  (avisservice.countStarsForAvis(avis.getId())==0)
            {   star_0.setVisible(true);
                star_1.setVisible(false);
                star_2.setVisible(false);
                star_3.setVisible(false);
                star_4.setVisible(false);
                star_5.setVisible(false);




            }


            if  (avisservice.countStarsForAvis(avis.getId())==1)
    {   star_0.setVisible(false);
        star_1.setVisible(true);
        star_2.setVisible(false);
        star_3.setVisible(false);
        star_4.setVisible(false);
        star_5.setVisible(false);




    }

            if  (avisservice.countStarsForAvis(avis.getId())==2)
            {
                star_0.setVisible(false);
                star_1.setVisible(false);
                star_2.setVisible(true);
                star_3.setVisible(false);
                star_4.setVisible(false);
                star_5.setVisible(false);
            }
            if  (avisservice.countStarsForAvis(avis.getId())==3)
            {   star_0.setVisible(false);
                star_1.setVisible(false);
                star_2.setVisible(false);
                star_3.setVisible(true);
                star_4.setVisible(false);
                star_5.setVisible(false);


            }
            if  (avisservice.countStarsForAvis(avis.getId())==4)
            {   star_0.setVisible(false);
                star_1.setVisible(false);
                star_2.setVisible(false);
                star_3.setVisible(false);
                star_4.setVisible(true);
                star_5.setVisible(false);

            }
            if  (avisservice.countStarsForAvis(avis.getId())==5)
            {   star_0.setVisible(false);
                star_1.setVisible(false);
                star_2.setVisible(false);
                star_3.setVisible(false);
                star_4.setVisible(false);
                star_5.setVisible(true);

            }
}
    }

    public void subscriberrate(ActionEvent event) {
        user user = new user(1, "ghassen", "sakka");
        AvisService avisservice=new AvisService();
        Avis avis=avisservice.getById(1);

            managecoachingrate.setVisible(true);
        update31.setVisible(false);
        delete31.setVisible(false);
            commentaire1.setText(avis.getCommentaire());
            if  (avisservice.countStarsForAvis(avis.getId())==0)
            {   star_0.setVisible(true);
                star_1.setVisible(false);
                star_2.setVisible(false);
                star_3.setVisible(false);
                star_4.setVisible(false);
                star_5.setVisible(false);




            }


            if  (avisservice.countStarsForAvis(avis.getId())==1)
            {   star_0.setVisible(false);
                star_1.setVisible(true);
                star_2.setVisible(false);
                star_3.setVisible(false);
                star_4.setVisible(false);
                star_5.setVisible(false);




            }

            if  (avisservice.countStarsForAvis(avis.getId())==2)
            {
                star_0.setVisible(false);
                star_1.setVisible(false);
                star_2.setVisible(true);
                star_3.setVisible(false);
                star_4.setVisible(false);
                star_5.setVisible(false);
            }
            if  (avisservice.countStarsForAvis(avis.getId())==3)
            {   star_0.setVisible(false);
                star_1.setVisible(false);
                star_2.setVisible(false);
                star_3.setVisible(true);
                star_4.setVisible(false);
                star_5.setVisible(false);


            }
            if  (avisservice.countStarsForAvis(avis.getId())==4)
            {   star_0.setVisible(false);
                star_1.setVisible(false);
                star_2.setVisible(false);
                star_3.setVisible(false);
                star_4.setVisible(true);
                star_5.setVisible(false);

            }
            if  (avisservice.countStarsForAvis(avis.getId())==5)
            {   star_0.setVisible(false);
                star_1.setVisible(false);
                star_2.setVisible(false);
                star_3.setVisible(false);
                star_4.setVisible(false);
                star_5.setVisible(true);

            }
        }


    public void confirmRate(ActionEvent event) throws Exception {
        user user = new user(1, "ghassen", "sakka");

        Avis avis=new Avis(star1.isSelected(),star2.isSelected(),star3.isSelected(),star4.isSelected(),star5.isSelected(),rate.getText(),user);
        AvisService avisservice=new AvisService();
        avisservice.Add(avis);
       addcoachingrate.setVisible(false);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Planning Rate ");
        alert.setHeaderText(null);
        alert.setContentText("Your rate added sucessfully ! you can check it");
        alert.showAndWait();

        star1.setSelected(false);
        star2.setSelected(false);
        star3.setSelected(false);
        star4.setSelected(false);
        star5.setSelected(false);
        rate.clear();
    }

    public void updateRate(ActionEvent event) {
    }

    public void deleteRate(ActionEvent event) {
        AvisService avisservice=new AvisService();
        Avis avis=avisservice.getById(1);
        avisservice.deleteById(avis.getId());
        managecoachingrate.setVisible(false);


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Planning Rate ");
        alert.setHeaderText(null);
        alert.setContentText("Your rate was deleted ! you can Add a new rate");
        alert.showAndWait();

    }
}