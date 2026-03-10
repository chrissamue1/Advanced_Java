import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RentalImpl extends UnicastRemoteObject implements IRentalService {
    private List<String> history;

    protected RentalImpl() throws RemoteException {
        history = new ArrayList<>();
    }

    @Override
    public String rentVehicle(String customer, String type, int days) throws RemoteException {
        int rate = type.equalsIgnoreCase("Truck") ? 100 : 50;
        int totalCost = rate * days;
        
        String transaction = customer + " rented a " + type + " for " + days + " days. Total: $" + totalCost;
        history.add(transaction);

        // This prints the receipt on the SERVER console as requested
        System.out.println("\n--- OFFICIAL RECEIPT ---");
        System.out.println("Customer: " + customer);
        System.out.println("Vehicle:  " + type);
        System.out.println("Duration: " + days + " days");
        System.out.println("Rate:     $" + rate + "/day");
        System.out.println("Total:    $" + totalCost);
        System.out.println("------------------------\n");

        return "Success! Total cost: $" + totalCost;
    }

    @Override
    public List<String> getTransactionHistory() throws RemoteException {
        return history;
    }
}