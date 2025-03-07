package controllers;

import entities.Event;
import entities.Location;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.EventService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EventCrud implements Initializable {

    @FXML
    private DatePicker dateDP;

    @FXML
    private TextArea descriptionTF;

    @FXML
    private ListView<Event> eventLV;

    @FXML
    private TextField nameTF;

    private Location location;

    public void setLocation(Location location) {
        this.location = location;
        refreshEventListView();
    }

    @FXML
    void addevent(ActionEvent event) {
        if (validateForm()) {
            try {
                Event newEvent = new Event(
                    nameTF.getText().trim(),
                    descriptionTF.getText().trim(),
                    location.getId(),
                    dateDP.getValue()
                );
                
                EventService eventService = new EventService();
                eventService.create(newEvent);
                showSuccessMessage("Event '" + newEvent.getName() + "' created successfully!");
                refreshEventListView();
                clearFields();
            } catch (SQLException e) {
                showErrorAlert("Failed to create event: " + e.getMessage());
            } catch (Exception e) {
                showErrorAlert("Unexpected error while creating event: " + e.getMessage());
            }
        }
    }

    @FXML
    void deleteevent(ActionEvent event) {
        Event selectedEvent = eventLV.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            try {
                EventService eventService = new EventService();
                eventService.delete(selectedEvent);
                showSuccessMessage("Event '" + selectedEvent.getName() + "' deleted successfully!");
                refreshEventListView();
                clearFields();
            } catch (SQLException e) {
                showErrorAlert("Failed to delete event: " + e.getMessage());
            }
        } else {
            showErrorAlert("Please select an event to delete.");
        }
    }

    @FXML
    void modifyevent(ActionEvent event) {
        Event selectedEvent = eventLV.getSelectionModel().getSelectedItem();
        if (selectedEvent != null && validateForm()) {
            try {
                selectedEvent.setName(nameTF.getText().trim());
                selectedEvent.setDescription(descriptionTF.getText().trim());
                selectedEvent.setDate(dateDP.getValue());

                EventService eventService = new EventService();
                eventService.update(selectedEvent);
                showSuccessMessage("Event '" + selectedEvent.getName() + "' updated successfully!");
                refreshEventListView();
                clearFields();
            } catch (SQLException e) {
                showErrorAlert("Failed to update event: " + e.getMessage());
            }
        } else if (selectedEvent == null) {
            showErrorAlert("Please select an event to modify.");
        }
    }

    @FXML
    void showlocation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationCrud.fxml"));
            Parent root = loader.load();
            LocationCrud controller = loader.getController();
            controller.setLocation(location);
            Stage stage = (Stage) eventLV.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showErrorAlert("Failed to load location view: " + e.getMessage());
        }
    }

    private void refreshEventListView() {
        try {
            ObservableList<Event> events = FXCollections.observableArrayList(
                new EventService().readByLocationId(location.getId())
            );
            eventLV.setItems(events);
        } catch (SQLException e) {
            showErrorAlert("Failed to load events: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        StringBuilder errorMessage = new StringBuilder();

        String name = nameTF.getText().trim();
        String description = descriptionTF.getText().trim();
        LocalDate date = dateDP.getValue();

        if (name.isEmpty()) {
            errorMessage.append("Event name is required.\n");
        }
        if (description.isEmpty()) {
            errorMessage.append("Event description is required.\n");
        }
        if (date == null) {
            errorMessage.append("Event date is required.\n");
        } else if (date.isBefore(LocalDate.now())) {
            errorMessage.append("Event date cannot be in the past.\n");
        }

        if (errorMessage.length() > 0) {
            showErrorAlert(errorMessage.toString());
            return false;
        }
        return true;
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setEvents(ObservableList<Event> obs) {
        eventLV.setItems(obs);
    }

    public void setLocationId(int id) {
        location = new Location();
        location.setId(id);
        refreshEventListView();
    }

    private void populateFields(Event event) {
        if (event != null) {
            nameTF.setText(event.getName());
            descriptionTF.setText(event.getDescription());
            dateDP.setValue(event.getDate());
        }
    }

    private void selectEvent(Event selectedEvent) {
        if (selectedEvent != null) {
            populateFields(selectedEvent);
        } else {
            clearFields();
        }
    }

    private void clearFields() {
        nameTF.clear();
        descriptionTF.clear();
        dateDP.setValue(null);
    }

    @FXML
    private void switchAccount(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("User Dashboard");
            stage.show();
        } catch (IOException e) {
            showErrorAlert("Failed to switch to User Dashboard: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (eventLV != null) {
            eventLV.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                selectEvent(newSelection);
            });
        }
    }
}


