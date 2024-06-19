package services;

import entite.commande;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import util.DataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandeService implements IServicea<commande> {

    private Connection cnx;

    public CommandeService() {
        cnx = DataSource.getInstance().getConnection();
    }

    @Override
    public void add(commande commande) {
        String query = "INSERT INTO commande (date, totale, first_name, last_name, address, phone_number, payment_type, is_valid) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setDate(1, new Date(commande.getDate().getTime()));
            ps.setDouble(2, commande.getTotale());
            ps.setString(3, commande.getFirstName());
            ps.setString(4, commande.getLastName());
            ps.setString(5, commande.getAddress());
            ps.setString(6, commande.getPhoneNumber());
            ps.setString(7, commande.getPaymentType());
            ps.setBoolean(8, commande.isValid());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM commande WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(commande commande, int id) {
        String query = "UPDATE commande SET date = ?, totale = ?, first_name = ?, last_name = ?, address = ?, phone_number = ?, payment_type = ?, is_valid = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setDate(1, new Date(commande.getDate().getTime()));
            ps.setDouble(2, commande.getTotale());
            ps.setString(3, commande.getFirstName());
            ps.setString(4, commande.getLastName());
            ps.setString(5, commande.getAddress());
            ps.setString(6, commande.getPhoneNumber());
            ps.setString(7, commande.getPaymentType());
            ps.setBoolean(8, commande.isValid());
            ps.setInt(9, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<commande> getAll() {
        List<commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM commande";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                commande commande = new commande(
                        rs.getDate("date"),
                        rs.getDouble("totale"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getString("payment_type"),
                        rs.getBoolean("is_valid")
                );
                commande.setId(rs.getLong("id"));
                commandes.add(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }

    @Override
    public commande getById(int id) {
        String query = "SELECT * FROM commande WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                commande commande = new commande(
                        rs.getDate("date"),
                        rs.getDouble("totale"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getString("payment_type"),
                        rs.getBoolean("is_valid")
                );
                commande.setId(rs.getLong("id"));
                return commande;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public commande getlast() {
        String query = "SELECT * FROM commande WHERE id = (SELECT MAX(id) FROM commande)";
        try (Statement stmt = cnx.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                commande commande = new commande(
                        rs.getDate("date"),
                        rs.getDouble("totale"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("phone_number"),
                        rs.getString("payment_type"),
                        rs.getBoolean("is_valid")
                );
                commande.setId(rs.getLong("id"));
                return commande;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<commande> searchAndSortCommands(String searchQuery, String sortBy) throws SQLException {
        List<commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande WHERE (first_name LIKE ? OR last_name LIKE ?)";

        if (sortBy != null) {
            sql += " ORDER BY " + sortBy;  // Make sure sortBy is controlled to avoid SQL injection
        }

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, "%" + searchQuery + "%");
            ps.setString(2, "%" + searchQuery + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                commandes.add(extractCommandeFromResultSet(rs));
            }
        }

        return commandes;
    }

    private commande extractCommandeFromResultSet(ResultSet rs) throws SQLException {
        commande commande = new commande(
                rs.getDate("date"),
                rs.getDouble("totale"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("address"),
                rs.getString("phone_number"),
                rs.getString("payment_type"),
                rs.getBoolean("is_valid")
        );
        commande.setId(rs.getLong("id"));
        return commande;
    }

    public Map<String, Double> getDailySales() throws SQLException {
        Map<String, Double> salesData = new LinkedHashMap<>();
        String sql = "SELECT DATE(date) as saleDate, SUM(totale) as dailyTotal FROM commande GROUP BY DATE(date) ORDER BY DATE(date)";
        try (PreparedStatement stmt = cnx.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                salesData.put(rs.getDate("saleDate").toString(), rs.getDouble("dailyTotal"));
            }
        }
        return salesData;
    }

    public Map<String, Double> predictSales(Map<String, Double> historicalData) {
        SimpleRegression regression = new SimpleRegression();

        // Convert dates to a numeric value, e.g., epoch days
        long baseDate = LocalDate.parse("2024-05-13").toEpochDay(); // base date for numeric conversion
        historicalData.forEach((date, total) ->
                regression.addData(LocalDate.parse(date).toEpochDay() - baseDate, total));

        // Make predictions
        Map<String, Double> predictions = new LinkedHashMap<>();
        // Predict for the next 3 days
        for (int i = 1; i <= 3; i++) {
            double predictedValue = regression.predict(baseDate + i)/1000;
            predictions.put(LocalDate.ofEpochDay(baseDate + i).toString(), predictedValue);
        }

        return predictions;
    }

}
