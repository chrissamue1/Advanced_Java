import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

public class lab5 {

    public static void main(String[] args) {

        try {
            System.out.println("\n--- CAR & TRUCK RENTAL SYSTEM ---\n");

            
            System.out.println("1. Checking Rental Server...\n");

            InetAddress server = InetAddress.getByName("www.example.com");

            System.out.println("Server Name      : " + server.getHostName());
            System.out.println("Server IP Address: " + server.getHostAddress());
            System.out.println("Server Reachable : " + server.isReachable(3000));

            
            System.out.println("\n2. Connecting to Rental Server...\n");

            URL url = new URL("https://www.example.com");
            URLConnection connection = url.openConnection();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));

            System.out.println("Rental Server Response:\n");

            String line;
            int linesRead = 0;

            
            while ((line = reader.readLine()) != null && linesRead < 5) {
                System.out.println("> " + line);
                linesRead++;
            }

            reader.close();

            
            System.out.println("\n===============================================");
            System.out.println(" RENTAL VEHICLE AVAILABILITY ");
            System.out.println("===============================================\n");

            System.out.println("Vehicle Name        Type      Price/Day (INR)");
            System.out.println("-----------------------------------------------");

            System.out.println("Maruti Swift        Car       1800");
            System.out.println("Hyundai Creta      Car       2500");
            System.out.println("Tata Ace           Truck     3200");
            System.out.println("Ashok Leyland      Truck     5500");

            System.out.println("\n[Info] Vehicle data displayed after successful server connection.");

        } catch (Exception e) {
            System.out.println("Error occurred in Car & Truck Rental System");
            System.out.println(e);
        }
    }
}
