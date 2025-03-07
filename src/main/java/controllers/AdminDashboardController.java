package controllers;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Label;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminDashboardController {

    @FXML private BorderPane mainContentPane;
    @FXML private Label dashboardLabel;
    @FXML private Label eventLabel;
    @FXML private Label locationLabel;
    @FXML private Label calendarLabel;
    @FXML private Label timeLabel;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");

    @FXML
    public void initialize() {
        setupEventHandlers();
        initializeClock();
        loadDefaultView();
    }

    private void loadDefaultView() {
        loadInterface("/Admin-dashboard.fxml");
    }

    private void initializeClock() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateTime();
            }
        };
        timer.start();
    }

    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        timeLabel.setText(now.format(formatter));
    }

    private void setupEventHandlers() {
        dashboardLabel.setOnMouseClicked(e -> loadInterface("/Admin-dashboard.fxml"));
        eventLabel.setOnMouseClicked(e -> loadInterface("/EventCrud.fxml"));
        locationLabel.setOnMouseClicked(e -> loadInterface("/LocationCrud.fxml"));
        calendarLabel.setOnMouseClicked(e -> loadInterface("/Calendar.fxml"));
    }

    private void loadInterface(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newContent = loader.load();
            mainContentPane.setCenter(newContent);
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading interface: " + fxmlPath);
            e.printStackTrace();
        }
    }
}