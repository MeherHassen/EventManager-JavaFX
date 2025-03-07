package controllers;

import entities.Event;
import entities.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.EventService;
import services.LocationService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

public class UserDashboardController {

    public VBox participationForm;
    @FXML private ListView<Location> locationLV;
    @FXML private ListView<Event> eventLV;
    @FXML private Button switchAccountButton;
    @FXML private VBox mapContainer;
    
    // Search fields
    @FXML private TextField locationNameSearch;
    @FXML private TextField locationCapacitySearch;
    @FXML private TextField eventNameSearch;
    @FXML private DatePicker eventDateSearch;

    private final LocationService locationService = new LocationService();
    private final EventService eventService = new EventService();
    private MapController mapController;
    
    private ObservableList<Location> allLocations;
    private ObservableList<Event> allEvents;
    private FilteredList<Location> filteredLocations;
    private FilteredList<Event> filteredEvents;

    @FXML
    public void initialize() {
        initializeMap();
        setupSearchListeners();
        setupLocationListener();
        try {
            initializeLocations();
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
    }

    private void initializeLocations() throws SQLException {
        allLocations = FXCollections.observableArrayList(locationService.readAll());
        filteredLocations = new FilteredList<>(allLocations);
        locationLV.setItems(filteredLocations);
        
        // Initialize empty events list
        allEvents = FXCollections.observableArrayList();
        filteredEvents = new FilteredList<>(allEvents);
        eventLV.setItems(filteredEvents);
    }

    private void setupSearchListeners() {
        // Location search listeners
        locationNameSearch.textProperty().addListener((obs, oldVal, newVal) -> filterLocations());
        locationCapacitySearch.textProperty().addListener((obs, oldVal, newVal) -> filterLocations());
        
        // Event search listeners
        eventNameSearch.textProperty().addListener((obs, oldVal, newVal) -> filterEvents());
        eventDateSearch.valueProperty().addListener((obs, oldVal, newVal) -> filterEvents());
    }

    private void filterLocations() {
        String nameFilter = locationNameSearch.getText().toLowerCase();
        String capacityStr = locationCapacitySearch.getText().trim();
        
        filteredLocations.setPredicate(location -> {
            boolean matchesName = location.getName().toLowerCase().contains(nameFilter);
            
            if (!capacityStr.isEmpty()) {
                try {
                    int minCapacity = Integer.parseInt(capacityStr);
                    return matchesName && location.getCapacity() >= minCapacity;
                } catch (NumberFormatException e) {
                    showErrorAlert("Please enter a valid number for capacity");
                    locationCapacitySearch.setText("");
                    return matchesName;
                }
            }
            
            return matchesName;
        });
    }

    private void filterEvents() {
        String nameFilter = eventNameSearch.getText().toLowerCase();
        LocalDate dateFilter = eventDateSearch.getValue();
        
        filteredEvents.setPredicate(event -> {
            boolean matchesName = event.getName().toLowerCase().contains(nameFilter);
            boolean matchesDate = dateFilter == null || event.getDate().equals(dateFilter);
            return matchesName && matchesDate;
        });
    }

    private void setupLocationListener() {
        locationLV.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Update map with the selected location's coordinates
                mapController.setLocation(newSelection.getLatitude(), newSelection.getLongitude());
                
                // Update events list
                try {
                    allEvents.setAll(locationService.readAllForLocation(newSelection));
                    filterEvents(); // Apply current event filters
                } catch (SQLException e) {
                    showErrorAlert("Failed to load events: " + e.getMessage());
                }
            }
        });
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
            e.printStackTrace();
            showErrorAlert("Failed to initialize map: " + e.getMessage());
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void switchAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationCrud.fxml"));
            Parent locationCrudPane = loader.load();
            Scene scene = new Scene(locationCrudPane);
            Stage stage = (Stage) switchAccountButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Location Management System");
        } catch (IOException e) {
            showErrorAlert("Failed to switch to Location Management: " + e.getMessage());
        }
    }

    @FXML
    private void showCalendarView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/calendar-view.fxml"));
            Parent calendarViewPane = loader.load();
            Stage stage = (Stage) switchAccountButton.getScene().getWindow();
            Scene scene = new Scene(calendarViewPane);
            stage.setScene(scene);
            stage.setTitle("Event Calendar");
        } catch (IOException e) {
            showErrorAlert("Failed to load calendar view: " + e.getMessage());
        }
    }

    private void handleDatabaseError(SQLException e) {
        e.printStackTrace();
        showErrorAlert("Database error: " + e.getMessage());
    }
}