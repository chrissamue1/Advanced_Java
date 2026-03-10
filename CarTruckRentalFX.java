import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CarTruckRentalFX extends Application {

    TextArea outputArea = new TextArea();

    @Override
    public void start(Stage stage) {

        
        Button btnCar = new Button("Rent Car");
        Button btnTruck = new Button("Rent Truck");
        Button btnInet = new Button("Show Network Details (InetAddress)");
        Button btnURL = new Button("Fetch Server Data (URLConnection)");

        
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);

        

       
        btnCar.setOnAction(e -> {
            outputArea.setText(
                    "AVAILABLE CARS\n" +
                    "-------------------------\n" +
                    "Maruti Swift  - ₹1800/day\n" +
                    "Hyundai Creta - ₹2500/day\n" +
                    "Honda City    - ₹2700/day\n"
            );
        });

       
        btnTruck.setOnAction(e -> {
            outputArea.setText(
                    "AVAILABLE TRUCKS\n" +
                    "-------------------------\n" +
                    "Tata Ace        - ₹3200/day\n" +
                    "Eicher Pro      - ₹4800/day\n" +
                    "Ashok Leyland   - ₹5500/day\n"
            );
        });

       
        btnInet.setOnAction(e -> {
            try {
                InetAddress server = InetAddress.getByName("www.example.com");

                outputArea.setText(
                        "NETWORK DETAILS (InetAddress)\n" +
                        "--------------------------------\n" +
                        "Host Name : " + server.getHostName() + "\n" +
                        "IP Address: " + server.getHostAddress() + "\n" +
                        "Reachable : " + server.isReachable(3000)
                );

            } catch (Exception ex) {
                outputArea.setText("Error fetching network details");
            }
        });

        
        btnURL.setOnAction(e -> {
            try {
                URL url = new URL("https://www.zoomcar.com/");
                URLConnection conn = url.openConnection();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder data = new StringBuilder();
                String line;
                int count = 0;

                while ((line = br.readLine()) != null && count < 5) {
                    data.append(line).append("\n");
                    count++;
                }

                br.close();

                outputArea.setText(
                        "SERVER DATA (URLConnection)\n" +
                        "--------------------------------\n" +
                        data.toString()
                );

            } catch (Exception ex) {
                outputArea.setText("Error connecting to server");
            }
        });

        
        HBox topButtons = new HBox(10, btnInet, btnURL);
        HBox rentButtons = new HBox(10, btnCar, btnTruck);

        VBox root = new VBox(15,
                new Label("CAR & TRUCK RENTAL SYSTEM"),
                topButtons,
                rentButtons,
                outputArea
        );

        root.setPadding(new javafx.geometry.Insets(15));

        Scene scene = new Scene(root, 550, 420);
        stage.setTitle("Car & Truck Rental System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
