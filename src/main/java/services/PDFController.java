package services;

/*import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;*/
import entite.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PDFController {


    private List<Service> services;

   /* public void generatePDF() {
        PdfWriter writer;
        try {
            // Create a directory chooser for the user to select the downloads folder
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Downloads Folder");
            File downloadsFolder = directoryChooser.showDialog(new Stage());

            if (downloadsFolder != null) {
                // Construct the file path for the PDF in the downloads folder
                String filePath = downloadsFolder.getAbsolutePath() + File.separator + "services.pdf";
                writer = new PdfWriter(filePath);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Create a table for services
                Table table = new Table(2); // Assuming 2 columns: Name, Description
                table.addCell(new Cell().add(new Paragraph("Name")));
                table.addCell(new Cell().add(new Paragraph("Description")));
                ServiceService ss = new ServiceService();
                List<Service> services = ss.getAll();

                // Add data to the table from the services list
                for (Service service : services) {
                    table.addCell(new Cell().add(new Paragraph(service.getName())));
                    table.addCell(new Cell().add(new Paragraph(service.getDescription())));
                }
                document.add(table);

                // Close the document
                document.close();

                showAlert("Success", "PDF file saved in Downloads folder.");
            }
        } catch (FileNotFoundException e) {
            showAlert("Error", "File not found: " + e.getMessage());
        } catch (IOException e) {
            showAlert("Error", "IO Exception: " + e.getMessage());
        } catch (SQLException e) {
            showAlert("Error", "SQL Exception: " + e.getMessage());
        }
    }*/

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
