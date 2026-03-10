import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

public class CarTruckRentalNetworking {

    public static void main(String[] args) {

        try {
            System.out.println("CAR & TRUCK RENTAL SYSTEM");
            System.out.println("========================\n");

            /* InetAddress - Server Connectivity */
            System.out.println("Checking Rental Server Details...");

            InetAddress rentalServer = InetAddress.getByName("example.com");

            System.out.println("Server Name : " + rentalServer.getHostName());
            System.out.println("Server IP   : " + rentalServer.getHostAddress());

            if (rentalServer.isReachable(3000)) {
                System.out.println("Server Status: Reachable\n");
            } else {
                System.out.println("Server Status: Not Reachable\n");
            }

            /* URLConnection - Fetch Rental Data */
            System.out.println("Fetching Available Cars & Trucks...");

            URL rentalURL = new URL("https://example.com");
            URLConnection connection = rentalURL.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            String line;
            System.out.println("\nRental Service Response:\n");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();

        } catch (Exception e) {
            System.out.println("Error occurred in Car & Truck Rental System");
            e.printStackTrace();
        }
    }
}
