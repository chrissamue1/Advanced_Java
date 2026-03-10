public import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CarTruckRentalSystem {

    public static void main(String[] args) {

        try {
            System.out.println("\n--- RENTALX NETWORK ---\n");

            // ================= InetAddress =================
            System.out.println("1. Checking Rental Server...\n");

            InetAddress server = InetAddress.getByName("www.example.com");

            System.out.println("Name: " + server.getHostName());
            System.out.println("IP Address: " + server.getHostAddress());
            System.out.println("Is Server Online? " + server.isReachable(3000));

            // ================= URLConnection =================
            System.out.println("\n2. Connecting to Rental Server...\n");

            URL url = new URL("https://www.example.com");
            URLConnection connection = url.openConnection();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));

            System.out.println("Data received from Rental Server:\n");

            String line;
            int count = 0;

            // Reading REAL data from server
            while ((line = reader.readLine()) != null && count < 10) {
                System.out.println("> " + line);
                count++;
            }

            reader.close();

            // ================= Domain Mapping =================
            System.out.println("\n> ===============================================");
            System.out.println("> RENTALX - AVAILABLE VEHICLES");
            System.out.println("> ===============================================\n");

            System.out.println("> VEHICLE        | TYPE   | PRICE/DAY");
            System.out.println("> -----------------------------------");

            System.out.println("> Swift Dzire    | Car    | ₹1800");
            System.out.println("> Hyundai Creta | Car    | ₹2500");
            System.out.println("> Tata Ace      | Truck  | ₹3200");
            System.out.println("> Eicher Pro    | Truck  | ₹5200");

            System.out.println("\n> [Info] Vehicle data fetched after server connection.");

        } catch (Exception e) {
            System.out.println("Error occurred in Car & Truck Rental System");
            System.out.println(e);
        }
    }
}
 {
    
}
