import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IRentalService extends Remote {
    // Added 'days' parameter to the rental logic
    String rentVehicle(String customerName, String vehicleType, int days) throws RemoteException;
    List<String> getTransactionHistory() throws RemoteException;
}