package entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Event {
    private int eventId;
    private String name;
    private String description;
    private int locationId;
    private LocalDate date;

    // Default constructor
    public Event() {
    }

    // Constructor without eventId (for creating new events)
    public Event(String name, String description, int locationId, LocalDate date) {
        this.name = name;
        this.description = description;
        this.locationId = locationId;
        this.date = date;
    }

    // Constructor with eventId (for retrieving existing events)
    public Event(int eventId, String name, String description, int locationId, LocalDate date) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.locationId = locationId;
        this.date = date;
    }

    // Getters and Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // toString method
    @Override
    public String toString() {
        return String.format(
                "Name: %-20.20s%nDescription:%n%s%nDate: %-20s",
                name.replaceAll("(?<=\\G.{" + 20 + "})", "\n"),
                description.replaceAll("(?<=\\G.{" + 100 + "})", "\n"),
                date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        );
    }

    public int getId() {
        return eventId;
    }
}
