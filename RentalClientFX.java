import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.rmi.Naming;

public class RentalClientFX extends Application {
    private IRentalService service;

    @Override
    public void start(Stage stage) {
        try {
            service = (IRentalService) Naming.lookup("rmi://localhost/RentalService");
        } catch (Exception e) {
            System.err.println("Connection failed.");
        }

        // Main Container with a light blue background
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f0f4f7;");

        // Header
        Label titleLabel = new Label("VIP Rental Service");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Input Card
        VBox inputCard = new VBox(10);
        inputCard.setPadding(new Insets(15));
        inputCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        TextField nameInput = new TextField();
        nameInput.setPromptText("Enter Customer Name");
        
        ComboBox<String> vehicleBox = new ComboBox<>();
        vehicleBox.getItems().addAll("Car ($50/day)", "Truck ($100/day)");
        vehicleBox.setValue("Car ($50/day)");
        vehicleBox.setMaxWidth(Double.MAX_VALUE);

        Label daysLabel = new Label("Number of Days:");
        Spinner<Integer> daysSpinner = new Spinner<>(1, 30, 1);
        daysSpinner.setMaxWidth(Double.MAX_VALUE);

        Button rentBtn = new Button("Confirm Rental");
        rentBtn.setMaxWidth(Double.MAX_VALUE);
        rentBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        inputCard.getChildren().addAll(new Label("Customer Details"), nameInput, new Label("Select Vehicle"), vehicleBox, daysLabel, daysSpinner, rentBtn);

        // History Section
        ListView<String> historyList = new ListView<>();
        historyList.setPrefHeight(150);
        Button refreshBtn = new Button("Refresh History");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        // Button Logic
        rentBtn.setOnAction(e -> {
            try {
                if (service != null && !nameInput.getText().isEmpty()) {
                    String type = vehicleBox.getValue().split(" ")[0]; // Get "Car" or "Truck"
                    String response = service.rentVehicle(nameInput.getText(), type, daysSpinner.getValue());
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, response);
                    alert.showAndWait();
                    
                    refreshHistory(historyList);
                } else {
                    new Alert(Alert.AlertType.WARNING, "Please enter a name!").show();
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        refreshBtn.setOnAction(e -> refreshHistory(historyList));

        root.getChildren().addAll(titleLabel, inputCard, new Label("Recent Transactions"), historyList, refreshBtn);

        stage.setScene(new Scene(root, 400, 600));
        stage.setTitle("Premium Rental System");
        stage.show();
    }

    private void refreshHistory(ListView<String> list) {
        try {
            if (service != null) {
                list.getItems().setAll(service.getTransactionHistory());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void main(String[] args) { launch(args); }
}