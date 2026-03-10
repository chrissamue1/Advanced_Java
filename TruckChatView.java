import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;

public class TruckChatView {

    public static void openChat(String name, String role) {
        Stage stage = new Stage();
        TextArea alertArea = new TextArea();
        alertArea.setEditable(false);
        alertArea.setPromptText("Waiting for Admin broadcasts...");

        VBox root = new VBox(10, new Label("Fleet Notifications (" + role + ")"), alertArea);
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("TruckFlow - " + name);
        stage.show();

        new Thread(() -> {
            try {
                
                Socket socket = new Socket("localhost", 5000);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String config = in.readLine(); // Receives "230.0.0.1:4446"
                
                String[] parts = config.split(":");
                InetAddress group = InetAddress.getByName(parts[0]);
                int port = Integer.parseInt(parts[1]);

                
                MulticastSocket mSocket = new MulticastSocket(port);
                NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
                mSocket.joinGroup(new InetSocketAddress(group, port), netIf);

                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    mSocket.receive(packet);
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    
                    Platform.runLater(() -> alertArea.appendText(msg + "\n"));
                }
            } catch (IOException e) {
                Platform.runLater(() -> alertArea.appendText("Connection Error.\n"));
            }
        }).start();
        
        stage.setOnCloseRequest(e -> System.exit(0));
    }
}