import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class RentalLoginView extends Application {

    @Override
    public void start(Stage stage) {
        TextField nameField = new TextField();
        nameField.setPromptText("Employee or Customer Name");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "Truck Driver", "Rental Customer");
        roleBox.setValue("Rental Customer");

        Button loginBtn = new Button("Join Fleet Network");

        VBox layout = new VBox(15,
                new Label("TruckFlow Rental Admin System"),
                nameField, roleBox, loginBtn
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));

        loginBtn.setOnAction(e -> {
            if (!nameField.getText().isEmpty()) {
                TruckChatView.openChat(nameField.getText(), roleBox.getValue());
                stage.close();
            }
        });

        stage.setScene(new Scene(layout, 350, 250));
        stage.setTitle("TruckFlow Login");
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}