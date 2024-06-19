package services;



import entite.Suivi;
import entite.user;
import util.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SuiviService  implements IServiceG<Suivi>{
    Connection cnx;
    public SuiviService()
    {
        cnx= DataSource.getInstance().getConnection();
    }
    @Override
    public void Add(Suivi suivi) {
        System.out.println("++++++++++++++++++++++++++++++++++++++++"+suivi);


        try {
            String query = "INSERT INTO suivi (plan_coach, plan_nutritionniste, title, start, end, description, all_day, background_colar, border_color, text_color, user_id,place_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setString(1, suivi.getPlan_coach());
            stm.setString(2, suivi.getPlan_nutritionniste());
            stm.setString(3, suivi.getTitle());
            stm.setTimestamp(4, new Timestamp(suivi.getStart().getTime())); // Convertir en Timestamp pour DATETIME
            stm.setTimestamp(5, new Timestamp(suivi.getEnd().getTime()));
            stm.setString(6, suivi.getDescription());
            stm.setBoolean(7, suivi.isAll_day());
            stm.setString(8, suivi.getBackground_color());
            stm.setString(9, suivi.getBorder_color());
            stm.setString(10, suivi.getText_color());
            stm.setInt(11, suivi.getAdherent().getId());
            stm.setInt(12, suivi.getPlace().getId());


            int rowsAffected = stm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Workout Successfully added  for the user ID: " );
            } else {
                System.out.println("Error adding Workout: ") ;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding Workout: "  + e.getMessage());
        }



    }




    @Override
    public List<Suivi> getAll() throws SQLException {
        List<Suivi> suiviList = new ArrayList<>();
        userService usereervice =new userService();
        PlaceService placeservice=new PlaceService();

        String query = "SELECT * FROM suivi";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(query)) {

            while (rs.next()) {
                Suivi suivi = new Suivi(
                        rs.getString("plan_coach"),
                        rs.getString("plan_nutritionniste"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("start"),
                        rs.getTimestamp("end"),
                        rs.getBoolean("all_day"),
                        rs.getString("background_colar"),
                        rs.getString("border_color"),
                        rs.getString("text_color"),
                        usereervice.getById(rs.getInt("user_id")),
                        placeservice.getById(rs.getInt("place_id"))
                );

                suiviList.add(suivi);
            }
        }
        return suiviList;
    }
    public List<Suivi> getAllByIDC(int userID) throws SQLException {
        List<Suivi> suiviList = new ArrayList<>();
        userService usereervice = new userService();
        PlaceService placeservice = new PlaceService();

        String query = "SELECT * FROM suivi WHERE user_id = ?  AND plan_coach = 'oui' ";
        try (PreparedStatement stm = cnx.prepareStatement(query)) {
            stm.setInt(1, userID);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Suivi suivi = new Suivi(rs.getInt("id"),
                            rs.getString("plan_coach"),
                            rs.getString("plan_nutritionniste"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getTimestamp("start"),
                            rs.getTimestamp("end"),
                            rs.getBoolean("all_day"),
                            rs.getString("background_colar"),
                            rs.getString("border_color"),
                            rs.getString("text_color"),
                            usereervice.getById(rs.getInt("user_id")),
                            placeservice.getById(rs.getInt("place_id"))
                    );
                    suiviList.add(suivi);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error getting Suivi by user ID: " + e.getMessage());
        }
        return suiviList;
    }



    @Override
    public boolean deleteById(int id) {
        try {
            String query = "DELETE FROM suivi WHERE id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Suivi deleted successfully.");
            } else {
                System.out.println("Suivi with id " + id + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting Suivi: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Suivi getById(int id) {

        Suivi suivi = null;
        userService usereervice =new userService();
        PlaceService placeservice=new PlaceService();
        try {
            String query = "SELECT * FROM suivi WHERE id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    suivi = new Suivi(
                            rs.getString("plan_coach"),
                            rs.getString("plan_nutritionniste"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getTimestamp("start"),
                            rs.getTimestamp("end"),
                            rs.getBoolean("all_day"),
                            rs.getString("background_colar"),
                            rs.getString("border_color"),
                            rs.getString("text_color"),
                            usereervice.getById(rs.getInt("user_id")),
                            placeservice.getByPlace(rs.getString("place"))
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting Suivi: " + e.getMessage());
        }
        return suivi;
    }
    public boolean getByDate(Date date,int id) {
        try {
            String query = "SELECT * FROM suivi WHERE DATE(start) = ? AND user_id = ? ";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setDate(1, date);
            stm.setInt(2, id);
            // Exécuter la requête
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting Suivi by date: " + e.getMessage());
        }
        return false;
    }


    @Override
    public void update(Suivi suivi) {
        try {


            if (suivi != null) {
                System.out.println(suivi);

                System.out.println("wseltttttttttttttttnnnnnnnnnnnnnnnnnnnnnnn");
                System.out.println(suivi);
                System.out.println(suivi.getPlace());
                System.out.println(suivi.getPlace().getId());
                String query = "UPDATE suivi SET plan_coach=?, plan_nutritionniste=?, title=?, start=?, end=?, description=?, all_day=?, background_colar=?, border_color=?, text_color=?, user_id=? ,place_id=? WHERE id=?";
                PreparedStatement stm = cnx.prepareStatement(query);
                stm.setString(1, suivi.getPlan_coach());
                stm.setString(2, suivi.getPlan_nutritionniste());
                stm.setString(3, suivi.getTitle());
                System.out.println("houni 111111111111.");

                stm.setTimestamp(4,suivi.getStart());
                stm.setTimestamp(5,  suivi.getEnd());
                stm.setString(6, suivi.getDescription());
                stm.setBoolean(7, suivi.isAll_day());
                System.out.println("houni222222222222222222222.");
                stm.setString(8, suivi.getBackground_color());
                stm.setString(9, suivi.getBorder_color());
                stm.setString(10, suivi.getText_color());
                System.out.println("houni333333333333.");

                stm.setInt(11, suivi.getAdherent().getId());
                stm.setInt(12, suivi.getPlace().getId());
                stm.setInt(13, suivi.getId());

                System.out.println("houni4444444444444444444.");


                int rowsAffected = stm.executeUpdate();
                System.out.println("houni55555555555.");
                System.out.println(rowsAffected);

                if (rowsAffected > 0) {
                    System.out.println("Suivi updated successfully.");
                } else {
                    System.out.println("Error updating Suivi.");
                }
            } else {
                System.out.println("Suivi with id " + suivi.getId() + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating Suivi: " + e.getMessage());
        }
    }



    public int getNombreWorkoutMoisCourant(user user) {
        int userId = user.getId();
        int nombreSeances = 0;

        Calendar cal = Calendar.getInstance();
        int moisCourant = cal.get(Calendar.MONTH) + 1; // Ajouter 1 car les mois commencent à 0 dans Calendar
        int anneeCourante = cal.get(Calendar.YEAR);

        try {
            String query = "SELECT COUNT(*) AS nombre_seances FROM suivi WHERE MONTH(start) = ? AND YEAR(start) = ? AND user_id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, moisCourant);
            stm.setInt(2, anneeCourante);
            stm.setInt(3, userId);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                nombreSeances = rs.getInt("nombre_seances");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des séances de workout : " + e.getMessage());
        }

        return nombreSeances;
    }


}
