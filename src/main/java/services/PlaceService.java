package services;


import entite.Place;
import util.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlaceService implements IServiceG<Place> {
    Connection cnx;

    public PlaceService() {
        cnx = DataSource.getInstance().getConnection();
    }


    @Override
    public void Add(Place place) {
        if (verifiedPlace(place)) {
            try {
                String query = "INSERT INTO place (place) VALUES (?)";
                PreparedStatement stm = cnx.prepareStatement(query);
                stm.setString(1, place.getPlace());

                int rowsAffected = stm.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Place successfully added.");
                } else {
                    System.out.println("Error adding place.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error adding place: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid place information.");
        }
    }

    @Override
    public List<Place> getAll() throws SQLException {
        List<Place> places = new ArrayList<>();
        String query = "SELECT * FROM place";
        try (PreparedStatement stm = cnx.prepareStatement(query);
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                Place place = new Place(rs.getInt("id"),rs.getString("place"));
                places.add(place);
            }
        }
        return places;
    }
    public void deleteByName(String placeName) {
        try {
            String query = "DELETE FROM place WHERE place = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setString(1, placeName);
            int rowsAffected = stm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Place '" + placeName + "' deleted successfully.");
            } else {
                System.out.println("Place '" + placeName + "' not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting place: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteById(int id) {
        try {
            String query = "DELETE FROM place WHERE id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, id);
            int rowsAffected = stm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Place deleted successfully.");
                return true;
            } else {
                System.out.println("Place with id " + id + " not found.");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting place: " + e.getMessage());
        }
    }

    @Override
    public Place getById(int id) {
        try {
            String query = "SELECT * FROM place WHERE id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return new Place(rs.getString("place"));
                } else {
                    System.out.println("Place with id " + id + " not found.");
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting place: " + e.getMessage());
        }
    }
    public Place getByPlace(String placeName) {
        try {
            String query = "SELECT * FROM place WHERE place = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setString(1, placeName);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return new Place(rs.getString("place"));
                } else {
                    System.out.println("Place with name " + placeName + " not found.");
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting place: " + e.getMessage());
        }
    }


    @Override
    public void update(Place updatedPlace) {
        try {
            String query = "UPDATE place SET place = ? WHERE id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setString(1, updatedPlace.getPlace());
            stm.setLong(2, updatedPlace.getId());

            int rowsAffected = stm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Place updated successfully.");
            } else {
                System.out.println("Place with id " + updatedPlace.getId() + " not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating place: " + e.getMessage());
        }
    }
    public void deleteAll() {
        try {
            String query = "DELETE FROM place";
            PreparedStatement stm = cnx.prepareStatement(query);
            int rowsAffected = stm.executeUpdate();
            System.out.println(rowsAffected + " places deleted successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error clearing all places: " + e.getMessage());
        }
    }

    private boolean verifiedPlace(Place place) {
        if (place == null) {
            System.out.println("Place cannot be null.");
            return false;
        }
        if (place.getPlace() == null || place.getPlace().isEmpty()) {
            System.out.println("Place name cannot be empty.");
            return false;
        }
        if (!isAlphabetic(place.getPlace())) {
            System.out.println("Place name must contain only alphabetic characters.");
            return false;
        }
        return true;
    }

    private boolean isAlphabetic(String str) {
        if (str.matches("[a-zA-Z]+")) {
            return true;
        } else {
            System.out.println("Place name must contain only alphabetic characters.");
            return false;
        }
    }

}
