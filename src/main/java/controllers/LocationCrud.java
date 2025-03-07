package controllers;

import entities.Location;
import entities.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.LocationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert;
import javafx.collections.transformation.FilteredList;

public class LocationCrud {

    @FXML
    private TextArea adressTF;

    @FXML
    private TextField capacityTF;

    @FXML
    private ListView<Location> locationLV;

    @FXML
    private TextField nameTF;

    @FXML
    private Button switchAccountButton;

    @FXML
    private Button AdminAccountButton;

    @FXML
    private Button mdifyButton;

    @FXML
    private VBox mapContainer;

    @FXML
    private TextField latitudeField;

    @FXML
    private TextField longitudeField;

    @FXML
    private TextField searchNameField;

    @FXML
    private TextField searchCapacityField;

    private Location location;
    private final LocationService locationService = new LocationService();
    private MapController mapController;
    private double latitude = 0;
    private double longitude = 0;
    private ObservableList<Location> allLocations = FXCollections.observableArrayList();
    private FilteredList<Location> filteredLocations;

    @FXML
    public void initialize() {
        try {
            initializeMap();
            loadLocations();
            setupListView();
            setupSearchListeners();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error initializing: " + e.getMessage());
        }
    }

    private void initializeMap() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MapView.fxml"));
        try {
            VBox mapView = loader.load();
            MapController mapController = loader.getController();
            
            // Add the map view to the container
            mapContainer.getChildren().add(mapView);
            
            // Store the map controller reference
            this.mapController = mapController;
            
            // Add listeners for coordinate changes from the map
            mapController.latitudeProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    this.latitude = newVal.doubleValue();
                    latitudeField.setText(String.format("%.6f", this.latitude));
                    System.out.println("LocationCrud: Latitude updated to " + this.latitude);
                }
            });
            
            mapController.longitudeProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    this.longitude = newVal.doubleValue();
                    longitudeField.setText(String.format("%.6f", this.longitude));
                    System.out.println("LocationCrud: Longitude updated to " + this.longitude);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Failed to initialize map: " + e.getMessage());
        }
    }

    private void setupListView() {
        locationLV.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameTF.setText(newSelection.getName());
                adressTF.setText(newSelection.getAddress());
                capacityTF.setText(String.valueOf(newSelection.getCapacity()));
                latitudeField.setText(String.valueOf(newSelection.getLatitude()));
                longitudeField.setText(String.valueOf(newSelection.getLongitude()));
                
                // Update map with selected location
                mapController.setLocation(newSelection.getLatitude(), newSelection.getLongitude());
            }
        });
    }

    private void setupSearchListeners() {
        // Add listeners to search fields
        searchNameField.textProperty().addListener((obs, oldVal, newVal) -> filterLocations());
        searchCapacityField.textProperty().addListener((obs, oldVal, newVal) -> filterLocations());
    }

    private void filterLocations() {
        String nameFilter = searchNameField.getText().toLowerCase();
        String capacityStr = searchCapacityField.getText().trim();

        filteredLocations.setPredicate(location -> {
            if (location == null) return false;
            
            boolean matchesName = true;
            if (nameFilter != null && !nameFilter.isEmpty()) {
                matchesName = location.getName().toLowerCase().contains(nameFilter);
            }
            
            if (!capacityStr.isEmpty()) {
                try {
                    int minCapacity = Integer.parseInt(capacityStr);
                    return matchesName && location.getCapacity() >= minCapacity;
                } catch (NumberFormatException e) {
                    showAlert("Please enter a valid number for capacity", Alert.AlertType.WARNING);
                    searchCapacityField.setText("");
                    return matchesName;
                }
            }
            
            return matchesName;
        });
    }

    private void loadLocations() throws SQLException {
        try {
            List<Location> locations = locationService.readAll();
            allLocations.setAll(locations);
            // Initialize filtered list after loading data
            filteredLocations = new FilteredList<>(allLocations, p -> true);
            locationLV.setItems(filteredLocations);
        } catch (SQLException e) {
            showErrorAlert("Failed to load locations: " + e.getMessage());
            throw e;
        }
    }

    // Method to populate fields with location data
    private void populateFields(Location location) {
        nameTF.setText(location.getName());
        adressTF.setText(location.getAddress());
        capacityTF.setText(String.valueOf(location.getCapacity()));
        latitudeField.setText(String.format("%.6f", location.getLatitude()));
        longitudeField.setText(String.format("%.6f", location.getLongitude()));
    }

    // Method to set the location
    public void setLocation(Location location) {
        this.location = location;
        if (location != null) {
            populateFields(location);
        }
    }

    @FXML
    private void switchAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDashboard.fxml"));
            Parent userDashboardPane = loader.load();
            Stage stage = (Stage) switchAccountButton.getScene().getWindow();
            Scene scene = new Scene(userDashboardPane);
            stage.setScene(scene);
            stage.setTitle("User Dashboard");
        } catch (IOException e) {
            showErrorAlert("Failed to switch to User Dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void adminAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin-dashboard.fxml"));
            Parent adminDashboardPane = loader.load();
            Stage stage = (Stage) AdminAccountButton.getScene().getWindow();
            Scene scene = new Scene(adminDashboardPane);
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard");
        } catch (IOException e) {
            showErrorAlert("Failed to switch to Admin Dashboard: " + e.getMessage());
        }
    }

    @FXML
    void showevents(ActionEvent event) {
        Location selectedLocation = locationLV.getSelectionModel().getSelectedItem();
        if (selectedLocation != null) {
            try {
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventCrud.fxml"));
                AnchorPane root = loader.load();

                EventCrud controller = loader.getController();
                controller.setLocationId(selectedLocation.getId());
                ObservableList<Event> obs = FXCollections.observableArrayList(this.locationService.readAllForLocation(selectedLocation));
                controller.setEvents(obs);

                Stage stage = new Stage();
                stage.setTitle("Events for " + selectedLocation.getName());
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                showErrorAlert("Failed to load events: " + e.getMessage());
            }
        } else {
            showErrorAlert("Please select a location to view its events.");
        }
    }

    @FXML
    private void addlocation() {
        System.out.println("Add location clicked. Current coordinates: lat=" + latitude + ", lng=" + longitude);
        
        if (validateForm()) {
            try {
                Location newLocation = new Location(
                    nameTF.getText().trim(),
                    adressTF.getText().trim(),
                    Integer.parseInt(capacityTF.getText().trim()),
                    latitude,
                    longitude
                );
                
                System.out.println("Creating new location with coordinates: " + latitude + ", " + longitude);
                locationService.create(newLocation);
                showSuccessMessage("Location '" + newLocation.getName() + "' added successfully!");
                refreshLocationListView();
                clearFields();
            } catch (SQLException e) {
                showErrorAlert("Database error: " + e.getMessage());
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid number format: " + e.getMessage());
            } catch (Exception e) {
                showErrorAlert("Unexpected error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void deletelocation() {
        Location selectedLocation = locationLV.getSelectionModel().getSelectedItem();
        if (selectedLocation != null) {
            try {
                locationService.delete(selectedLocation);
                showSuccessMessage("Location '" + selectedLocation.getName() + "' deleted successfully!");
                refreshLocationListView();
                clearFields();
                if (mapController != null) {
                    mapController.setLocation(36.8065, 10.1815); // Reset to default location
                }
            } catch (SQLException e) {
                showErrorAlert("Failed to delete location: " + e.getMessage());
            }
        } else {
            showErrorAlert("Please select a location to delete.");
        }
    }

    @FXML
    private void modifylocation() {
        Location selectedLocation = locationLV.getSelectionModel().getSelectedItem();
        if (selectedLocation != null && validateForm()) {
            try {
                selectedLocation.setName(nameTF.getText());
                selectedLocation.setAddress(adressTF.getText());
                selectedLocation.setCapacity(Integer.parseInt(capacityTF.getText()));
                selectedLocation.setLatitude(latitude);
                selectedLocation.setLongitude(longitude);
                
                locationService.update(selectedLocation);
                showSuccessMessage("Location '" + selectedLocation.getName() + "' updated successfully!");
                refreshLocationListView();
            } catch (SQLException e) {
                showErrorAlert("Failed to update location: " + e.getMessage());
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid capacity value. Please enter a valid number.");
            }
        } else if (selectedLocation == null) {
            showErrorAlert("Please select a location to modify.");
        }
    }

    private boolean validateForm() {
        StringBuilder errorMessage = new StringBuilder();

        if (nameTF.getText().trim().isEmpty()) {
            errorMessage.append("Name is required.\n");
        }

        if (adressTF.getText().trim().isEmpty()) {
            errorMessage.append("Address is required.\n");
        }

        String capacityText = capacityTF.getText().trim();
        if (capacityText.isEmpty()) {
            errorMessage.append("Capacity is required.\n");
        } else {
            try {
                int capacity = Integer.parseInt(capacityText);
                if (capacity <= 0) {
                    errorMessage.append("Capacity must be greater than 0.\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Capacity must be a valid number.\n");
            }
        }

        // Get the current coordinates from the map controller
        if (mapController != null) {
            this.latitude = mapController.getLatitude();
            this.longitude = mapController.getLongitude();
        }

        System.out.println("Validating coordinates: lat=" + latitude + ", lng=" + longitude);
        
        // Check if coordinates are valid
        if (latitude == 36.8065 && longitude == 10.1815) {
            // These are the default coordinates (Tunis)
            errorMessage.append("Please click on the map to select a specific location.\n");
        }

        if (errorMessage.length() > 0) {
            showErrorAlert(errorMessage.toString());
            return false;
        }

        return true;
    }

    private void refreshLocationListView() {
        try {
            List<Location> locations = locationService.readAll();
            allLocations.setAll(locations);
            // No need to create new FilteredList, existing one will update automatically
        } catch (SQLException e) {
            showErrorAlert("Failed to refresh location list: " + e.getMessage());
        }
    }

    private void clearFields() {
        nameTF.clear();
        adressTF.clear();
        capacityTF.clear();
        latitudeField.clear();
        longitudeField.clear();
        latitude = 0;
        longitude = 0;
        if (mapController != null) {
            mapController.setLocation(36.8065, 10.1815); // Reset to default location (Tunis)
        }
    }

    private void showSuccessMessage(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
