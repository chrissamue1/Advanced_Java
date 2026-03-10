import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CarRentalController {
    @FXML private TextField nameField, modelField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private Slider daysSlider;
    @FXML private Label resultLabel;

    @FXML
    public void initialize() {
        categoryBox.getItems().addAll("Economy", "SUV", "Luxury");
        categoryBox.setValue("Economy");
    }

    @FXML
    private void handleBooking() {
        
        CarRentalBean booking = new CarRentalBean();

        
        booking.setCustomerName(nameField.getText());
        booking.setCarModel(modelField.getText());
        booking.setCategory(categoryBox.getValue());
        booking.setRentalDays((int) daysSlider.getValue());

        
        booking.calculateTotal();

        
        resultLabel.setText(String.format("Confirmed: %s\nTotal for %d days: $%.2f", 
                            booking.getCarModel(), booking.getRentalDays(), booking.getTotalCost()));
    }
}