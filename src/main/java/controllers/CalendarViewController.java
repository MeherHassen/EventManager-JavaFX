package controllers;

import entities.Event;
import entities.Location;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import services.EventService;
import services.LocationService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CalendarViewController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private Button prevMonthBtn, nextMonthBtn;
    @FXML private ListView<Event> eventList;
    @FXML private Label selectedDateLabel;
    @FXML private VBox mapContainer;
    @FXML private VBox eventDetailsPane;
    @FXML private Label eventNameLabel;
    @FXML private Label eventDateLabel;
    @FXML private Label eventLocationLabel;
    @FXML private TextArea eventDescriptionArea;

    private final EventService eventService = new EventService();
    private final LocationService locationService = new LocationService();
    private MapController mapController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMap();
        setupDatePicker();
        setupEventList();
        loadEventsForDate(LocalDate.now());
    }

    private void setupDatePicker() {
        datePicker.setValue(LocalDate.now());
        datePicker.setOnAction(e -> loadEventsForDate(datePicker.getValue()));
        
        prevMonthBtn.setOnAction(e -> navigateDate(-1));
        nextMonthBtn.setOnAction(e -> navigateDate(1));
    }

    private void navigateDate(int days) {
        LocalDate newDate = datePicker.getValue().plusDays(days);
        datePicker.setValue(newDate);
        loadEventsForDate(newDate);
    }

    private void setupEventList() {
        eventList.setCellFactory(lv -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                } else {
                    setText(event.getName());
                }
            }
        });

        eventList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showEventDetails(newVal);
            }
        });
    }

    private void showEventDetails(Event event) {
        eventDetailsPane.setVisible(true);
        mapContainer.setVisible(true);
        
        eventNameLabel.setText(event.getName());
        eventDateLabel.setText("Date: " + event.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        eventDescriptionArea.setText(event.getDescription() != null ? event.getDescription() : "No description available");

        try {
            Location location = locationService.read(event.getLocationId());
            if (location != null) {
                eventLocationLabel.setText("Location: " + location.getName());
                mapController.setLocation(location.getLatitude(), location.getLongitude());
            } else {
                eventLocationLabel.setText("Location: Not available");
                mapController.setLocation(36.8065, 10.1815); // Default to Tunis
            }
        } catch (SQLException e) {
            showAlert("Error loading location details: " + e.getMessage(), Alert.AlertType.ERROR);
            eventLocationLabel.setText("Location: Error loading location");
        }
    }

    private void loadEventsForDate(LocalDate date) {
        try {
            selectedDateLabel.setText(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
            List<Event> events = eventService.readAllForDate(date);
            eventList.getItems().setAll(events);
            
            if (events.isEmpty()) {
                eventDetailsPane.setVisible(false);
                mapContainer.setVisible(false);
                eventList.setPlaceholder(new Label("No events scheduled for this date"));
            } else {
                // Select the first event by default
                eventList.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            showAlert("Error loading events: " + e.getMessage(), Alert.AlertType.ERROR);
            eventList.setPlaceholder(new Label("Error loading events"));
        }
    }

    private void initializeMap() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MapView.fxml"));
            VBox mapView = loader.load();
            mapController = loader.getController();
            mapContainer.getChildren().add(mapView);
            
            // Set default location (e.g., Tunis)
            mapController.setLocation(36.8065, 10.1815);
        } catch (IOException e) {
            showAlert("Failed to initialize map: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goToToday() {
        datePicker.setValue(LocalDate.now());
        loadEventsForDate(LocalDate.now());
    }

    @FXML
    private void backToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDashboard.fxml"));
            Parent dashboardView = loader.load();
            Scene scene = new Scene(dashboardView);
            Stage stage = (Stage) datePicker.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("User Dashboard");
        } catch (IOException e) {
            showAlert("Failed to return to dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}