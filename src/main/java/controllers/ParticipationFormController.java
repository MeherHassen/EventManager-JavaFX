package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class ParticipationFormController {

    @FXML
    private ImageView eventImage;

    @FXML
    private Text eventDescription;

    @FXML
    private TextField fullNameTF;

    @FXML
    private TextField emailTF;

    @FXML
    private TextField phoneTF;

    @FXML
    private TextField locationTF;

    @FXML
    private void submitParticipation() {
        // Handle participation submission
        String fullName = fullNameTF.getText();
        String email = emailTF.getText();
        String phone = phoneTF.getText();
        String location = locationTF.getText();

        // Validate and process the data
        System.out.println("Participation submitted: " + fullName + ", " + email + ", " + phone + ", " + location);
    }

    public void setEventDetails(String description, String imageUrl) {
        eventDescription.setText(description);
        eventImage.setImage(new javafx.scene.image.Image(imageUrl));
    }
}