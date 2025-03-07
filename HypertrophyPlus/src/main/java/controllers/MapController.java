package controllers;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import java.net.URL;
import netscape.javascript.JSObject;

public class MapController {
    @FXML
    private VBox mapContainer;
    @FXML
    private TextField latitudeField;
    @FXML
    private TextField longitudeField;
    
    private WebView webView;
    private WebEngine webEngine;
    private final DoubleProperty latitude = new SimpleDoubleProperty(36.8065);
    private final DoubleProperty longitude = new SimpleDoubleProperty(10.1815);

    @FXML
    public void initialize() {
        webView = new WebView();
        webEngine = webView.getEngine();
        
        webView.setPrefHeight(400);
        webView.setPrefWidth(600);
        
        // Load the OpenStreetMap HTML
        URL mapUrl = getClass().getResource("/html/map.html");
        if (mapUrl == null) {
            System.err.println("Error: map.html not found!");
            return;
        }
        
        System.out.println("Loading map from: " + mapUrl.toExternalForm());
        webEngine.load(mapUrl.toExternalForm());
        
        mapContainer.getChildren().add(webView);
        
        // Initialize coordinate fields
        updateCoordinateFields();
        
        // Add JavaScript bridge
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaController", this);
                System.out.println("JavaScript bridge initialized");
                
                // Initialize the map with default coordinates
                setLocation(latitude.get(), longitude.get());
            }
        });
    }
    
    public void setLocation(double lat, double lng) {
        System.out.println("MapController: Setting location to: " + lat + ", " + lng);
        
        // Update the properties
        latitude.set(lat);
        longitude.set(lng);
        
        // Update the text fields
        updateCoordinateFields();
        
        // Update map marker
        if (webEngine != null) {
            String script = String.format("updateMarker(%f, %f);", lat, lng);
            try {
                webEngine.executeScript(script);
                System.out.println("Map marker updated successfully");
            } catch (Exception e) {
                System.err.println("Error updating map marker: " + e.getMessage());
            }
        }
    }
    
    private void updateCoordinateFields() {
        System.out.println("Updating coordinate fields - Latitude: " + latitude.get() + ", Longitude: " + longitude.get());
        if (latitudeField != null) {
            latitudeField.setText(String.format("%.6f", latitude.get()));
        } else {
            System.err.println("Warning: latitudeField is null");
        }
        if (longitudeField != null) {
            longitudeField.setText(String.format("%.6f", longitude.get()));
        } else {
            System.err.println("Warning: longitudeField is null");
        }
    }
    
    // JavaScript callback method
    public void handleMapClick(double lat, double lng) {
        System.out.println("MapController: Map clicked at: " + lat + ", " + lng);
        
        // Update the properties
        latitude.set(lat);
        longitude.set(lng);
        
        // Update the text fields
        updateCoordinateFields();
        
        // Update map marker
        if (webEngine != null) {
            try {
                String script = String.format("updateMarker(%f, %f);", lat, lng);
                webEngine.executeScript(script);
                System.out.println("Map marker updated successfully");
            } catch (Exception e) {
                System.err.println("Error updating map marker: " + e.getMessage());
            }
        }
    }
    
    public DoubleProperty latitudeProperty() {
        return latitude;
    }
    
    public DoubleProperty longitudeProperty() {
        return longitude;
    }
    
    public double getLatitude() {
        return latitude.get();
    }
    
    public double getLongitude() {
        return longitude.get();
    }
} 