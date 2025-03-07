package services;

import entities.Event;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class EventService implements IService<Event> {
    private Connection cnx;

    public EventService() {
        cnx = MyConnection.getInstance().getConnection();
    }

    @Override
    public void create(Event event) throws SQLException {
        String query = "INSERT INTO event (name, description, location_id, date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getName());
            ps.setString(2, event.getDescription());
            ps.setInt(3, event.getLocationId());
            ps.setDate(4, Date.valueOf(event.getDate()));

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setEventId(generatedKeys.getInt(1));
                }
            }
        }
    }


    @Override
    public void update(Event event) throws SQLException {
        String query = "UPDATE event SET name = ?, description = ?, location_id = ?, date = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, event.getName());
            ps.setString(2, event.getDescription());
            ps.setInt(3, event.getLocationId());
            ps.setDate(4, Date.valueOf(event.getDate()));
            ps.setInt(5, event.getEventId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Event event) throws SQLException {
        String query = "DELETE FROM event WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, event.getEventId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Event> readAll() throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
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
        return events;
    }

    public List<Event> readByLocationId(int id) throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE location_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
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

    @Override
    public Event read(int id) throws SQLException {
        String query = "SELECT * FROM event WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Event(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("location_id"),
                            rs.getDate("date").toLocalDate()
                    );
                }
            }
        }
        return null;
    }

    public List<Event> readAllForDate(LocalDate date) throws SQLException {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE date = ?";
        
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Event event = new Event();
                event.setEventId(rs.getInt("id"));
                event.setName(rs.getString("name"));
                event.setDescription(rs.getString("description"));
                event.setDate(rs.getDate("date").toLocalDate());
                event.setLocationId(rs.getInt("location_id"));
                events.add(event);
            }
        }
        
        return events;
    }
}