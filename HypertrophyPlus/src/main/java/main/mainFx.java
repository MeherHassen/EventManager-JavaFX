package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mainFx extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        showMainDashboard();
    }

    public void showMainDashboard() {
        try {
            // Get the class loader
            ClassLoader classLoader = mainFx.class.getClassLoader();
            
            // Load the FXML file using the class loader
            FXMLLoader loader = new FXMLLoader(classLoader.getResource("UserDashboard.fxml"));
            Parent root = loader.load();

            // Set up main window
            Scene scene = new Scene(root);
            scene.getStylesheets().add(classLoader.getResource("style/main.css").toExternalForm());

            primaryStage.setTitle("QuickMove - Carpool System");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error loading FXML: ");
            e.printStackTrace();
        }
    }

    public void showLocationManagement() {
        try {
            ClassLoader classLoader = mainFx.class.getClassLoader();
            FXMLLoader loader = new FXMLLoader(classLoader.getResource("LocationCrud.fxml"));
            Parent root = loader.load();
            showScene(root, "Location Management");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCalendarView() {
        try {
            ClassLoader classLoader = mainFx.class.getClassLoader();
            FXMLLoader loader = new FXMLLoader(classLoader.getResource("calendar-view.fxml"));
            Parent root = loader.load();
            showScene(root, "Calendar View");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showScene(Parent root, String title) {
        Scene scene = new Scene(root);
        ClassLoader classLoader = mainFx.class.getClassLoader();
        scene.getStylesheets().add(classLoader.getResource("style/main.css").toExternalForm());

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}