import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.*;

public class RentalApp extends Application {

    
    private static final String URL = "jdbc:mysql://localhost:3306/RentalDB";
    private static final String USER = "root"; 
    private static final String PASSWORD = "chris"; 

    
    TextField txtName, txtVehicle;
    TextArea displayArea;
    Label lblStatus;

    public static void main(String[] args) {
        launch(args);
    } 

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Car & Truck Rental System");

        
        
        Label lblName = new Label("Customer Name:");
        
        txtName = new TextField();
        txtName.setPromptText("Enter Name");

        Label lblVehicle = new Label("Car/Truck Model:");
        
        txtVehicle = new TextField();
        txtVehicle.setPromptText("Enter Vehicle");

        Button btnAdd = new Button("ADD");
        btnAdd.setOnAction(e -> addRecord());

        Button btnEdit = new Button("EDIT");
        btnEdit.setOnAction(e -> editRecord());

        Button btnDelete = new Button("DELETE");
        btnDelete.setOnAction(e -> deleteRecord());

        Button btnDisplay = new Button("DISPLAY");
        btnDisplay.setOnAction(e -> displayRecords());

        displayArea = new TextArea();
        displayArea.setPrefHeight(150);

        lblStatus = new Label("Status: Ready");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(lblName, 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(lblVehicle, 0, 1);
        grid.add(txtVehicle, 1, 1);
        
        grid.add(btnAdd, 0, 2);
        grid.add(btnEdit, 1, 2);
        grid.add(btnDelete, 0, 3);
        grid.add(btnDisplay, 1, 3);
        
        grid.add(displayArea, 0, 4, 2, 1);
        grid.add(lblStatus, 0, 5, 2, 1);

        Scene scene = new Scene(grid, 400, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addRecord() {
        String query = "INSERT INTO Rentals (customer_name, vehicle_model) VALUES (?, ?)";
        executeQuery(query, "Added");
    }

    private void editRecord() {
        String query = "UPDATE Rentals SET vehicle_model = ? WHERE customer_name = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, txtVehicle.getText());
            pstmt.setString(2, txtName.getText());
            pstmt.executeUpdate();
            lblStatus.setText("Status: Record Updated");
            displayRecords(); 
        } catch (SQLException e) {
            lblStatus.setText("Error: " + e.getMessage());
        }
    }

    private void deleteRecord() {
        String query = "DELETE FROM Rentals WHERE customer_name = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, txtName.getText());
            pstmt.executeUpdate();
            lblStatus.setText("Status: Record Deleted");
            displayRecords();
        } catch (SQLException e) {
            lblStatus.setText("Error: " + e.getMessage());
        }
    }

    private void displayRecords() {
        displayArea.clear();
        String query = "SELECT * FROM Rentals";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String entry = "ID: " + rs.getInt("id") + " | Name: " + rs.getString("customer_name") + " | Vehicle: " + rs.getString("vehicle_model") + "\n";
                displayArea.appendText(entry);
            }
        } catch (SQLException e) {
            lblStatus.setText("Error: " + e.getMessage());
        }
    }

    private void executeQuery(String query, String action) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, txtName.getText());
            pstmt.setString(2, txtVehicle.getText());
            pstmt.executeUpdate();
            lblStatus.setText("Status: Record " + action);
        } catch (SQLException e) {
            lblStatus.setText("Error: " + e.getMessage());
        }
    }
}