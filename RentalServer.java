import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class RentalServer {
    public static void main(String[] args) {
        try {
            
            LocateRegistry.createRegistry(1099);
            
            // 2. Create the implementation object
            RentalImpl rentalService = new RentalImpl();
            
            // 3. Bind the object to a specific name in the registry
            Naming.rebind("RentalService", rentalService);
            
            System.out.println("========================================");
            System.out.println("Car & Truck Rental Server is running...");
            System.out.println("Waiting for client transactions...");
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("Server Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}