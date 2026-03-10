import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class CarTruckRentalApp extends Application {

    // 1) CHANGE THESE TO YOUR DETAILS
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/rentaldb?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "chris";

    private ComboBox<String> cbType;
    private TextField tfMake;
    private TextField tfModel;
    private TextField tfYear;
    private TextField tfDailyRate;

    private Button btnAdd;
    private Button btnDisplay;
    private Button btnEdit;
    private Button btnDelete;
    private Button btnClear;

    private TableView<VehicleRow> table;
    private ObservableList<VehicleRow> data = FXCollections.observableArrayList();

    public static void main(String[] args) {
        createTableIfNotExists();
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Simple Car & Truck Rental");

        cbType = new ComboBox<>();
        cbType.getItems().addAll("Car", "Truck");
        cbType.setPromptText("Type");

        tfMake = new TextField();
        tfMake.setPromptText("Make");

        tfModel = new TextField();
        tfModel.setPromptText("Model");

        tfYear = new TextField();
        tfYear.setPromptText("Year");

        tfDailyRate = new TextField();
        tfDailyRate.setPromptText("Daily Rate");

        btnAdd = new Button("ADD");
        btnDisplay = new Button("DISPLAY");
        btnEdit = new Button("EDIT");
        btnDelete = new Button("DELETE");
        btnClear = new Button("CLEAR");

        HBox buttons = new HBox(10, btnAdd, btnDisplay, btnEdit, btnDelete, btnClear);

        table = new TableView<>();
        table.setItems(data);

        TableColumn<VehicleRow, Integer> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<VehicleRow, String> cType = new TableColumn<>("Type");
        cType.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<VehicleRow, String> cMake = new TableColumn<>("Make");
        cMake.setCellValueFactory(new PropertyValueFactory<>("make"));

        TableColumn<VehicleRow, String> cModel = new TableColumn<>("Model");
        cModel.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<VehicleRow, Integer> cYear = new TableColumn<>("Year");
        cYear.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<VehicleRow, Double> cRate = new TableColumn<>("Rate");
        cRate.setCellValueFactory(new PropertyValueFactory<>("dailyRate"));

        table.getColumns().addAll(cId, cType, cMake, cModel, cYear, cRate);

        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(10);
        form.setVgap(8);

        form.add(new Label("Type:"), 0, 0);
        form.add(cbType, 1, 0);

        form.add(new Label("Make:"), 0, 1);
        form.add(tfMake, 1, 1);

        form.add(new Label("Model:"), 0, 2);
        form.add(tfModel, 1, 2);

        form.add(new Label("Year:"), 0, 3);
        form.add(tfYear, 1, 3);

        form.add(new Label("Rate:"), 0, 4);
        form.add(tfDailyRate, 1, 4);

        VBox root = new VBox(10, form, buttons, table);
        root.setPadding(new Insets(10));

        btnAdd.setOnAction(e -> addVehicle());
        btnDisplay.setOnAction(e -> loadVehicles());
        btnEdit.setOnAction(e -> editVehicle());
        btnDelete.setOnAction(e -> deleteVehicle());
        btnClear.setOnAction(e -> clearForm());

        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> loadFromTable(newV)
        );

        stage.setScene(new Scene(root, 800, 500));
        stage.show();

        loadVehicles();
    }

    // DB setup
    private static void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS rental_vehicle (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "type VARCHAR(20) NOT NULL," +
                "make VARCHAR(50)," +
                "model VARCHAR(50)," +
                "year INT," +
                "daily_rate DOUBLE" +
                ")";
        try (Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement s = c.createStatement()) {
            s.execute(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // ADD
    private void addVehicle() {
        if (!validate()) return;

        String sql = "INSERT INTO rental_vehicle(type, make, model, year, daily_rate) " +
                     "VALUES(?, ?, ?, ?, ?)";
        try (Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, cbType.getValue());
            ps.setString(2, tfMake.getText().trim());
            ps.setString(3, tfModel.getText().trim());
            ps.setInt(4, Integer.parseInt(tfYear.getText().trim()));
            ps.setDouble(5, Double.parseDouble(tfDailyRate.getText().trim()));

            ps.executeUpdate();
            showInfo("Vehicle added.");
            loadVehicles();
            clearForm();
        } catch (SQLException ex) {
            showError("Add failed: " + ex.getMessage());
        }
    }

    // DISPLAY
    private void loadVehicles() {
        data.clear();
        String sql = "SELECT * FROM rental_vehicle";
        try (Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                data.add(new VehicleRow(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getDouble("daily_rate")
                ));
            }
        } catch (SQLException ex) {
            showError("Load failed: " + ex.getMessage());
        }
    }

    // EDIT
    private void editVehicle() {
        VehicleRow v = table.getSelectionModel().getSelectedItem();
        if (v == null) {
            showError("Select a row first.");
            return;
        }
        if (!validate()) return;

        String sql = "UPDATE rental_vehicle SET type=?, make=?, model=?, year=?, daily_rate=? " +
                     "WHERE id=?";
        try (Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, cbType.getValue());
            ps.setString(2, tfMake.getText().trim());
            ps.setString(3, tfModel.getText().trim());
            ps.setInt(4, Integer.parseInt(tfYear.getText().trim()));
            ps.setDouble(5, Double.parseDouble(tfDailyRate.getText().trim()));
            ps.setInt(6, v.getId());

            ps.executeUpdate();
            showInfo("Vehicle updated.");
            loadVehicles();
            clearForm();
        } catch (SQLException ex) {
            showError("Edit failed: " + ex.getMessage());
        }
    }

    // DELETE
    private void deleteVehicle() {
        VehicleRow v = table.getSelectionModel().getSelectedItem();
        if (v == null) {
            showError("Select a row first.");
            return;
        }
        String sql = "DELETE FROM rental_vehicle WHERE id=?";
        try (Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, v.getId());
            ps.executeUpdate();
            showInfo("Vehicle deleted.");
            loadVehicles();
            clearForm();
        } catch (SQLException ex) {
            showError("Delete failed: " + ex.getMessage());
        }
    }

    // Helpers
    private boolean validate() {
        if (cbType.getValue() == null ||
            tfMake.getText().trim().isEmpty() ||
            tfModel.getText().trim().isEmpty() ||
            tfYear.getText().trim().isEmpty() ||
            tfDailyRate.getText().trim().isEmpty()) {
            showError("Fill Type, Make, Model, Year, Rate.");
            return false;
        }
        try { Integer.parseInt(tfYear.getText().trim()); }
        catch (NumberFormatException e) { showError("Year must be number."); return false; }
        try { Double.parseDouble(tfDailyRate.getText().trim()); }
        catch (NumberFormatException e) { showError("Rate must be number."); return false; }
        return true;
    }

    private void clearForm() {
        cbType.setValue(null);
        tfMake.clear();
        tfModel.clear();
        tfYear.clear();
        tfDailyRate.clear();
        table.getSelectionModel().clearSelection();
    }

    private void loadFromTable(VehicleRow v) {
        if (v == null) return;
        cbType.setValue(v.getType());
        tfMake.setText(v.getMake());
        tfModel.setText(v.getModel());
        tfYear.setText(String.valueOf(v.getYear()));
        tfDailyRate.setText(String.valueOf(v.getDailyRate()));
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText("Error");
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText("Info");
        a.showAndWait();
    }

    // Table row class
    public static class VehicleRow {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty type;
        private final SimpleStringProperty make;
        private final SimpleStringProperty model;
        private final SimpleIntegerProperty year;
        private final SimpleDoubleProperty dailyRate;

        public VehicleRow(int id, String type, String make, String model,
                          int year, double dailyRate) {
            this.id = new SimpleIntegerProperty(id);
            this.type = new SimpleStringProperty(type);
            this.make = new SimpleStringProperty(make);
            this.model = new SimpleStringProperty(model);
            this.year = new SimpleIntegerProperty(year);
            this.dailyRate = new SimpleDoubleProperty(dailyRate);
        }

        public int getId() { return id.get(); }
        public String getType() { return type.get(); }
        public String getMake() { return make.get(); }
        public String getModel() { return model.get(); }
        public int getYear() { return year.get(); }
        public double getDailyRate() { return dailyRate.get(); }
    }
}
