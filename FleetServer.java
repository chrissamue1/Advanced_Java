import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class FleetServer {
    private static final int TCP_PORT = 5000;
    private static final String MULTICAST_GROUP = "230.0.0.1";
    private static final int MULTICAST_PORT = 4446;
    
    
    private static Set<ClientRecord> activeClients = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Truck Rental Admin Server started...");
        
        
        new Thread(FleetServer::handleRegistrations).start();

        
        try (DatagramSocket udpSocket = new DatagramSocket()) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Type messages to broadcast to the entire fleet:");
            
            while (true) {
                String message = "ADMIN ALERT: " + scanner.nextLine();
                byte[] buffer = message.getBytes();
                InetAddress group = InetAddress.getByName(MULTICAST_GROUP);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, MULTICAST_PORT);
                udpSocket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleRegistrations() {
        try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                String clientIP = socket.getInetAddress().getHostAddress();
                int clientPort = socket.getPort();
                
                activeClients.add(new ClientRecord(clientIP, clientPort));
                System.out.println("New Fleet Member Registered: " + clientIP + ":" + clientPort);
                
                
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println(MULTICAST_GROUP + ":" + MULTICAST_PORT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientRecord {
        String ip;
        int port;
        ClientRecord(String ip, int port) { this.ip = ip; this.port = port; }
    }
}