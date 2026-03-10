import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;

public class RentalAdvanced extends Application {

    private static final String URL = "jdbc:mysql://localhost:3306/RentalAdvancedDB";
    private static final String USER = "root";
    private static final String PASS = "chris"; 
    Connection conn;
    Statement stmt;
    ResultSet rs; 

    
    TextField txtId = new TextField();
    TextField txtModel = new TextField();
    ImageView imgView = new ImageView();
    TextArea txtInfo = new TextArea(); 
    Label lblStatus = new Label("Status: Ready");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Car/Truck Rental - Advanced JDBC");

        
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        
        txtId.setEditable(false); 
        grid.add(new Label("Vehicle ID:"), 0, 0);
        grid.add(txtId, 1, 0);
        grid.add(new Label("Model Name:"), 0, 1);
        grid.add(txtModel, 1, 1);

        
        imgView.setFitWidth(150);
        imgView.setFitHeight(100);
        imgView.setPreserveRatio(true);
        grid.add(new Label("Vehicle Photo:"), 0, 2);
        grid.add(imgView, 1, 2);

        
        Button btnFirst = new Button("<< First");
        Button btnPrev = new Button("< Prev");
        Button btnNext = new Button("Next >");
        Button btnLast = new Button("Last >>");

        HBox navBox = new HBox(10, btnFirst, btnPrev, btnNext, btnLast);
        grid.add(navBox, 0, 3, 2, 1);

        
        Button btnUpdate = new Button("Update Name (ResultSet)");
        Button btnUploadImg = new Button("Upload Image (Blob)");
        Button btnMeta = new Button("Show Metadata");
        Button btnJoin = new Button("Show Join (Vehicle+Cat)");

        HBox actionBox = new HBox(10, btnUpdate, btnUploadImg);
        HBox infoBox = new HBox(10, btnMeta, btnJoin);
        
        grid.add(actionBox, 0, 4, 2, 1);
        grid.add(infoBox, 0, 5, 2, 1);
        
        
        txtInfo.setPrefHeight(100);
        grid.add(txtInfo, 0, 6, 2, 1);
        grid.add(lblStatus, 0, 7, 2, 1);

        
        btnFirst.setOnAction(e -> navigate("FIRST"));
        btnPrev.setOnAction(e -> navigate("PREV"));
        btnNext.setOnAction(e -> navigate("NEXT"));
        btnLast.setOnAction(e -> navigate("LAST"));
        btnUpdate.setOnAction(e -> updateRecord());
        btnMeta.setOnAction(e -> showMetadata());
        btnJoin.setOnAction(e -> showJoinOperation());
        btnUploadImg.setOnAction(e -> uploadImage(stage));

        
        connectAndLoad();
        
        Scene scene = new Scene(grid, 550, 600);
        stage.setScene(scene);
        stage.show();
    }

    

    private void connectAndLoad() {
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
            
            
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery("SELECT * FROM Vehicles");

            if (rs.next()) {
                showCurrentRecord();
            }
        } catch (SQLException e) {
            lblStatus.setText("DB Error: " + e.getMessage());
        }
    }

    private void showCurrentRecord() {
        try {
            txtId.setText(String.valueOf(rs.getInt("vid")));
            txtModel.setText(rs.getString("model_name"));

            
            Blob blob = rs.getBlob("photo");
            if (blob != null) {
                InputStream is = blob.getBinaryStream();
                imgView.setImage(new Image(is));
            } else {
                imgView.setImage(null); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    private void navigate(String direction) {
        try {
            boolean moved = false;
            switch (direction) {
                case "FIRST": moved = rs.first(); break;
                case "PREV":  moved = rs.previous(); break;
                case "NEXT":  moved = rs.next(); break;
                case "LAST":  moved = rs.last(); break;
            }
            if (moved) showCurrentRecord();
            else lblStatus.setText("Hit start or end of records.");
        } catch (SQLException e) {
            lblStatus.setText("Nav Error: " + e.getMessage());
        }
    }

    
    private void updateRecord() {
        try {
            rs.updateString("model_name", txtModel.getText());
            rs.updateRow(); // Commit change to DB
            lblStatus.setText("Success: Model Name Updated!");
        } catch (SQLException e) {
            lblStatus.setText("Update Error: " + e.getMessage());
        }
    }

    
    private void showMetadata() {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            StringBuilder sb = new StringBuilder("--- METADATA ---\n");
            sb.append("Total Columns: ").append(colCount).append("\n");
            
            for (int i = 1; i <= colCount; i++) {
                sb.append(meta.getColumnName(i))
                  .append(" (").append(meta.getColumnTypeName(i)).append(")\n");
            }
            txtInfo.setText(sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    private void showJoinOperation() {
        String query = "SELECT v.model_name, c.category_name FROM Vehicles v JOIN Categories c ON v.cat_id = c.cat_id";
        try (Statement joinStmt = conn.createStatement();
             ResultSet joinRs = joinStmt.executeQuery(query)) {
            
            StringBuilder sb = new StringBuilder("--- JOIN RESULT ---\n");
            while (joinRs.next()) {
                sb.append("Vehicle: ").append(joinRs.getString("model_name"))
                  .append(" | Category: ").append(joinRs.getString("category_name")).append("\n");
            }
            txtInfo.setText(sb.toString());
        } catch (SQLException e) {
            lblStatus.setText("Join Error: " + e.getMessage());
        }
    }

    
    private void uploadImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                FileInputStream fis = new FileInputStream(file);
                
                String sql = "UPDATE Vehicles SET photo = ? WHERE vid = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setBinaryStream(1, fis, (int) file.length());
                pstmt.setInt(2, Integer.parseInt(txtId.getText()));
                pstmt.executeUpdate();
                
                lblStatus.setText("Image Uploaded! Refresh/Next to see.");
                
                
                int currentRow = rs.getRow();
                rs.close(); // reload
                stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                rs = stmt.executeQuery("SELECT * FROM Vehicles");
                rs.absolute(currentRow);
                showCurrentRecord();
                
            } catch (Exception e) {
                lblStatus.setText("Upload Error: " + e.getMessage());
            }
        }
    }
}