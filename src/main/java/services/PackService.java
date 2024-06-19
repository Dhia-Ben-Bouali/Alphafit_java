package services;

import entite.Pack;
import entite.Service;
import util.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackService implements IServicep<Pack>{
    private Connection connection;

    public PackService() {
        this.connection = DataSource.getInstance().getConnection();
        System.out.println(connection);
    }

    @Override
    public void Add(Pack pack) throws SQLException {
        String insertPackSQL = "INSERT INTO Pack (nom, description, prix) VALUES (?, ?, ?)";
        String insertPackServiceSQL = "INSERT INTO pack_service (pack_id, service_id) VALUES (?, ?)";

        try (PreparedStatement packStatement = connection.prepareStatement(insertPackSQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement packServiceStatement = connection.prepareStatement(insertPackServiceSQL)) {

            // Insert pack details
            packStatement.setString(1, pack.getName());
            packStatement.setString(2, pack.getDescription());
            packStatement.setDouble(3, pack.getPrice());
            packStatement.executeUpdate();

            ResultSet generatedKeys = packStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                pack.setId(generatedKeys.getInt(1));

                // Insert pack-service associations
                for (Service service : pack.getServices()) {
                    packServiceStatement.setInt(1, pack.getId());
                    packServiceStatement.setInt(2, service.getId());
                    packServiceStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error adding Pack: " + ex.getMessage());
        }
    }


    @Override
    public void deleteById(int packId) {
        String sql = "DELETE FROM Pack WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, packId);
            statement.executeUpdate();
        }catch (SQLException ex) {
            System.out.println("Error deleting pack: " + ex.getMessage());
        }
    }
    @Override
    public void update(Pack pack) throws SQLException {
        String sql = "UPDATE Pack SET nom = ?, description = ?, prix = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pack.getName());
            statement.setString(2, pack.getDescription());
            statement.setDouble(3, pack.getPrice());
            statement.setInt(4, pack.getId());
            statement.executeUpdate();
        }catch (SQLException ex) {
            System.out.println("Error editing Pack: " + ex.getMessage());
        }
    }
    @Override
    public Pack getById(int packId) throws SQLException {
        List<Service> ls = new ArrayList<>();
        String packSql = "SELECT * FROM Pack WHERE id = ?";
        String packServiceSql = "SELECT s.id, s.nom, s.description FROM Service s " +
                "JOIN pack_service ps ON s.id = ps.service_id " +
                "WHERE ps.pack_id = ?";

        try (PreparedStatement packStatement = connection.prepareStatement(packSql);
             PreparedStatement packServiceStatement = connection.prepareStatement(packServiceSql)) {

            // Retrieve pack details
            packStatement.setInt(1, packId);
            try (ResultSet packResultSet = packStatement.executeQuery()) {
                if (packResultSet.next()) {
                    Pack pack = new Pack();
                    pack.setId(packResultSet.getInt("id"));
                    pack.setName(packResultSet.getString("nom"));
                    pack.setDescription(packResultSet.getString("description"));
                    pack.setPrice(packResultSet.getDouble("prix"));

                    // Retrieve associated services
                    packServiceStatement.setInt(1, packId);
                    try (ResultSet serviceResultSet = packServiceStatement.executeQuery()) {
                        while (serviceResultSet.next()) {
                            Service service = new Service();
                            service.setId(serviceResultSet.getInt("id"));
                            service.setName(serviceResultSet.getString("nom"));
                            service.setDescription(serviceResultSet.getString("description"));

                            // Add service to pack
                            ls.add(service);
                        }
                    }
                    pack.setServices(ls);
                    return pack;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error finding Pack: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<Pack> getAll() throws SQLException {
        List<Pack> packs = new ArrayList<>();
        List<Service> ls = new ArrayList<>();
        String packSql = "SELECT * FROM Pack";
        String packServiceSql = "SELECT s.id, s.nom, s.description FROM Service s " +
                "JOIN pack_service ps ON s.id = ps.service_id " +
                "WHERE ps.pack_id = ?";

        try (Statement packStatement = connection.createStatement();
             PreparedStatement packServiceStatement = connection.prepareStatement(packServiceSql)) {

            ResultSet packResultSet = packStatement.executeQuery(packSql);
            while (packResultSet.next()) {
                Pack pack = new Pack();
                pack.setId(packResultSet.getInt("id"));
                pack.setName(packResultSet.getString("nom"));
                pack.setDescription(packResultSet.getString("description"));
                pack.setPrice(packResultSet.getDouble("prix"));

                // Retrieve associated services
                packServiceStatement.setInt(1, pack.getId());
                try (ResultSet serviceResultSet = packServiceStatement.executeQuery()) {
                    while (serviceResultSet.next()) {
                        Service service = new Service();
                        service.setId(serviceResultSet.getInt("id"));
                        service.setName(serviceResultSet.getString("nom"));
                        service.setDescription(serviceResultSet.getString("description"));

                        // Add service to pack
                        ls.add(service);
                    }
                }
                pack.setServices(ls);
                packs.add(pack);
            }
        } catch (SQLException ex) {
            System.out.println("Error getting Packs: " + ex.getMessage());
        }
        return packs;
    }

    public boolean pack_existant(String name) {
        boolean result = false;
        try {
            for (Pack p : getAll()) {
                if (p.getName().equals(name)) {
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
