import java.io.Serializable;

public class CarRentalBean implements Serializable {
    private String customerName;
    private String carModel;
    private String category; 
    private int rentalDays;
    private double totalCost;

   
    public CarRentalBean() {}

   
    public void calculateTotal() {
        double baseRate = switch (category) {
            case "Luxury" -> 150.0;
            case "SUV" -> 100.0;
            default -> 50.0;
        };
        
        
        double discount = (rentalDays > 7) ? 0.9 : 1.0;
        this.totalCost = (baseRate * rentalDays) * discount;
    }

    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getRentalDays() { return rentalDays; }
    public void setRentalDays(int rentalDays) { this.rentalDays = rentalDays; }

    public double getTotalCost() { return totalCost; }
}