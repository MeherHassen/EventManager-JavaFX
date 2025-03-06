package services;

import entities.Event;
import entities.Location;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationService implements IService<Location> {
    private Connection cnx;

    public LocationService() {
        cnx = MyConnection.getInstance().getConnection();
    }

    @Override
    public void create(Location location) throws SQLException {
        String query = "INSERT INTO location (name, address, capacity, latitude, longitude) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, location.getName());
            ps.setString(2, location.getAddress());
            ps.setInt(3, location.getCapacity());
            ps.setDouble(4, location.getLatitude());
            ps.setDouble(5, location.getLongitude());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    location.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Location location) throws SQLException {
        String query = "UPDATE location SET name = ?, address = ?, capacity = ?, latitude = ?, longitude = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, location.getName());
            ps.setString(2, location.getAddress());
            ps.setInt(3, location.getCapacity());
            ps.setDouble(4, location.getLatitude());
            ps.setDouble(5, location.getLongitude());
            ps.setInt(6, location.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Location location) throws SQLException {
        String query = "DELETE FROM location WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, location.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Location> readAll() throws SQLException {
        List<Location> locations = new ArrayList<>();
        String query = "SELECT * FROM location";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Location location = new Location(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getInt("capacity"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude")
                );
                locations.add(location);
            }
        }
        return locations;
    }

    @Override
    public Location read(int id) throws SQLException {
        String query = "SELECT * FROM location WHERE id = ?";
        
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Location location = new Location();
                location.setId(rs.getInt("id"));
                location.setName(rs.getString("name"));
                location.setCapacity(rs.getInt("capacity"));
                location.setLatitude(rs.getDouble("latitude"));
                location.setLongitude(rs.getDouble("longitude"));
                return location;
            }
        }
        
        return null;
    }

    public List<Location> readByName(String keyword) throws SQLException {
        List<Location> locations = new ArrayList<>();
        String query = "SELECT * FROM location WHERE name LIKE ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Location location = new Location(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getInt("capacity"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude")
                    );
                    locations.add(location);
                }
            }
        }
        return locations;
    }

    public List<Event> readAllForLocation(Location location) throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE location_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, location.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Event event = new Event(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("location_id"),
                            rs.getDate("date").toLocalDate()
                    );
                    events.add(event);
                }
            }
        }
        return events;
    }
}