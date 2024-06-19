package services;

import entite.Service;
import util.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceService implements IServicep<Service>{
    private Connection connection;

    public ServiceService() {
        this.connection = DataSource.getInstance().getConnection();
        System.out.println(connection);
    }

    @Override
    public void Add(Service entity) throws SQLException {
        String sql = "INSERT INTO Service (nom, description) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDescription());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt(1));
            }
        }catch (SQLException ex) {
            System.out.println("Error adding service: " + ex.getMessage());
        }
    }

    @Override
    public void deleteById(int serviceId) throws SQLException {
        String sql = "DELETE FROM Service WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, serviceId);
            statement.executeUpdate();
        }catch (SQLException ex) {
            System.out.println("Error deleting service: " + ex.getMessage());
        }
    }
    @Override
    public void update(Service service) throws SQLException {
        String sql = "UPDATE Service SET nom = ?, description = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, service.getName());
            statement.setString(2, service.getDescription());
            statement.setInt(3, service.getId());
            statement.executeUpdate();
        }catch (SQLException ex) {
            System.out.println("Error updating service: " + ex.getMessage());
        }
    }

    public Service getById(int serviceId) throws SQLException {
        String sql = "SELECT * FROM Service WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, serviceId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Service service = new Service();
                    service.setId(resultSet.getInt("id"));
                    service.setName(resultSet.getString("nom"));
                    service.setDescription(resultSet.getString("description"));
                    return service;
                }
            }
        }catch (SQLException ex) {
            System.out.println("Error finding service: " + ex.getMessage());
        }
        return null;
    }

    public List<Service> getAll() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM Service";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Service service = new Service();
                service.setId(resultSet.getInt("id"));
                service.setName(resultSet.getString("nom"));
                service.setDescription(resultSet.getString("description"));
                services.add(service);
            }
        }catch (SQLException ex) {
            System.out.println("Error getting services: " + ex.getMessage());
        }
        return services;
    }
    public boolean service_existant(String name) {
        boolean result = false;
        try {
            for (Service s : getAll()) {
                if (s.getName().equals(name)) {
                    result = true;
                    break;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
