package services;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import entite.Abonnement;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import javafx.collections.ObservableList;
import com.itextpdf.text.pdf.PdfWriter;
import util.DataSource;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import static services.ServiceAbonnement.getAbonnementList;

public class ServiceFA {

    Connection con;

    public ServiceFA() {
        con = DataSource.getInstance().getConnection();
    }
    public static Map<String, Integer> getReclamationsByDay() throws SQLException {
        String query = "SELECT DATE(date) AS jour, COUNT(*) AS nombre FROM reclamation GROUP BY jour";
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        Map<String, Integer> reclamationsParJour = new HashMap<>();

        try {
            con= DataSource.getInstance().getConnection();
            st = con.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                String jour = rs.getString("jour");
                int nombre = rs.getInt("nombre");
                reclamationsParJour.put(jour, nombre);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        }

        return reclamationsParJour;
    }

    public static void genererPDF() {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream("C:/Users/Dell/Downloads/abonnements.pdf"));
            document.open();

            // Ajout de l'en-tête avec arrière-plan bleu
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);
            PdfPCell headerCell = new PdfPCell(new Phrase("AlphaFit", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.WHITE)));
            headerCell.setBackgroundColor(new BaseColor(0, 102, 204)); // Bleu
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(headerCell);
            document.add(headerTable);
            document.add(Chunk.NEWLINE); // Ajouter un espace après l'en-tête

            ObservableList<Abonnement> abonnementList = getAbonnementList();

            // Ajout du titre de la table
            Paragraph title = new Paragraph("Liste des Abonnements", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE); // Ajouter un espace après le titre

            // Création de la police et des styles
            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Font fontData = FontFactory.getFont(FontFactory.HELVETICA);
            fontData.setSize(10);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100); // Pour remplir la largeur de la page

            // En-têtes de colonne avec style
            PdfPCell cell;
            cell = new PdfPCell(new Phrase("ID", fontHeader));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Client", fontHeader));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Coach", fontHeader));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Nutritionniste", fontHeader));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Date d'expiration", fontHeader));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Pack", fontHeader));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            // Ajouter les données des abonnements dans le tableau avec style
            for (Abonnement abonnement : abonnementList) {
                table.addCell(new Phrase(String.valueOf(abonnement.getId()), fontData));
                table.addCell(new Phrase(abonnement.getClient() != null ? abonnement.getClient().getNom() : "", fontData));
                table.addCell(new Phrase(abonnement.getCoach() != null ? abonnement.getCoach().getNom() : "", fontData));
                table.addCell(new Phrase(abonnement.getNutrisionist() != null ? abonnement.getNutrisionist().getNom() : "", fontData));
                table.addCell(new Phrase(String.valueOf(abonnement.getDateExpiration()), fontData));
                table.addCell(new Phrase(abonnement.getPack() != null ? abonnement.getPack().getName() : "", fontData));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Ajout du pied de page avec arrière-plan bleu
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            PdfPCell footerCell = new PdfPCell(new Phrase("©- All rights reserved 2024 - Website: www.alphafit.com", new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.WHITE)));
            PdfPCell footerCell2 = new PdfPCell(new Phrase("Date :" +getCurrentDate()+" ", new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.WHITE)));
            footerCell.setBackgroundColor(new BaseColor(0, 102, 204)); // Bleu
            footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerCell2.setBackgroundColor(new BaseColor(0, 102, 204)); // Bleu
            footerCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            footerTable.addCell(footerCell);
            footerTable.addCell(footerCell2);
            document.add(footerTable);

            document.close();
            System.out.println("Le PDF des abonnements a été généré avec succès.");
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Fonction pour obtenir la date actuelle au format dd/MM/yyyy
    private static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(new Date());
    }


}