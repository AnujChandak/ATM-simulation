import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.*;
import java.util.Optional;

public class App extends Application {

    private long ac;

    private void insertData(String name, String email, String pass, long AccNo) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingapp", "root", "")) {
    
            // If email or name does not exist, insert the data
            String sql = "INSERT INTO accdetails (name, email, pass, money, acc_no) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, pass);
                statement.setInt(4, 0);
                statement.setLong(5, AccNo);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Data inserted successfully.");
                } else {
                    System.out.println("Failed to insert data.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private boolean isAccountExists(String name, String email) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingapp", "root", "")) {
            String sql = "SELECT COUNT(*) AS count FROM accdetails WHERE name = ? AND email = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.setString(2, email);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean isAccountNumberExists(long accountNumber) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingapp", "root", "")) {
            String sql = "SELECT COUNT(*) AS count FROM accdetails WHERE acc_no = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setLong(1, accountNumber);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isPasswordCorrect(long accountNumber, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingapp", "root", "")) {
            String sql = "SELECT pass FROM accdetails WHERE acc_no = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setLong(1, accountNumber);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("pass");
                    return storedPassword.equals(password);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private double getAccountBalance(long accountNumber) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingapp", "root", "")) {
            String sql = "SELECT money FROM accdetails WHERE acc_no = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setLong(1, accountNumber);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getDouble("money");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean updateAccountBalance(long accountNumber, double newBalance) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingapp", "root", "")) {
            String sql = "UPDATE accdetails SET money = ? WHERE acc_no = ?";
            try (PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setDouble(1, newBalance);
                statement.setLong(2, accountNumber);
                int rowsUpdated = statement.executeUpdate();
                return rowsUpdated > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean transferFunds(long recipientAccountNumber, double amount) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingapp", "root", "")) {
            if (!isAccountNumberExists(recipientAccountNumber)) {
                System.out.println("Recipient account does not exist");
                return false;
            }

            double senderBalance = getAccountBalance(this.ac);
            if (senderBalance < amount) {
                System.out.println("Insufficient balance");
                return false;
            }

            updateAccountBalance(this.ac, senderBalance - amount);

            double recipientBalance = getAccountBalance(recipientAccountNumber);
            updateAccountBalance(recipientAccountNumber, recipientBalance + amount);

            System.out.println("Funds transferred successfully");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    public static void main(String[] args) {
        launch(args);
    }

    @Override

    public void start(Stage primaryStage) {
    
        primaryStage.setTitle("AAA Bank");

        // Buttons
        Button btn1 = new Button("Register");

        //----------------Page 2:------------------------

        Label l1=new Label("Name: ");
        l1.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField t1= new TextField();

        Label l2=new Label("Set Password: ");
        l2.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        PasswordField t2= new PasswordField();

        Label l3=new Label("Confirm Password: ");
        l3.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        PasswordField t3= new PasswordField();

        Label l4=new Label("Signup Page ");
        l4.setStyle("-fx-font-size: 34px; -fx-font-weight: bold;");

        Label l5=new Label("Email: ");
        l5.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField email= new TextField();

        VBox vb1 = new VBox(10);
        vb1.getChildren().addAll(l1,l5,l2,l3);
        vb1.setStyle("-fx-alignment: right;");

        VBox vb2 = new VBox(15);
        vb2.getChildren().addAll(t1,email,t2,t3);
        vb2.setStyle("-fx-alignment: left; -fx-padding: 15;");

        HBox hb1=new HBox(15);
        hb1.getChildren().addAll(vb1,vb2);
        hb1.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Button back=new Button("Previous");
        back.setStyle("-fx-font-size: 20px;-fx-border-color: black; -fx-border-width: 2px;");

        Button create=new Button("Create");
        create.setStyle("-fx-font-size: 20px;-fx-border-color: black; -fx-border-width: 2px;");

        HBox hb=new HBox(40);
        hb.getChildren().addAll(back,create);
        hb.setStyle("-fx-alignment: center");

        VBox vb3=new VBox();
        vb3.getChildren().addAll(l4,hb1,hb);
        vb3.setStyle("-fx-alignment: center;");

        StackPane page2= new StackPane();
        page2.setStyle("-fx-background-color: cyan;"); // Set background color
        page2.getChildren().addAll(vb3);
        Scene scene2=new Scene(page2,800,600);

        t2.textProperty().addListener((observable, oldValue, newValue) -> {
            String pass1 = t2.getText();
            String  pass2 = t3.getText();
            if (!pass1.equals(pass2)) {
                create.setDisable(true);
            } else {
                create.setDisable(false);
            }
        });
        
        t3.textProperty().addListener((observable, oldValue, newValue) -> {
            String pass1 = t2.getText();
            String pass2 = t3.getText();
            if (!pass1.equals(pass2)) {
                create.setDisable(true);
            } else {
                create.setDisable(false);
            }
        });
                        //-----------------------Page 3---------------------
        long AccNo = (long) (Math.random() * 9000000000L) + 1000000000L;

        Label l=new Label("Your Accounnt has been created successfully"); 
        l.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label l6=new Label("Here is your account Number: "+AccNo);
        l6.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button home= new Button("Home");
        home.setStyle("-fx-font-size: 20px;-fx-border-color: black; -fx-border-width: 2px;");

        Button h= new Button("Home");
        h.setStyle("-fx-font-size: 20px;-fx-border-color: black; -fx-border-width: 2px;");

        VBox vb4=new VBox(15);
        vb4.getChildren().addAll(l,l6,h);
        vb4.setStyle("-fx-alignment: center;");

        StackPane page3= new StackPane();
        page3.setStyle("-fx-background-color: cyan;"); // Set background color
        page3.getChildren().addAll(vb4);
        Scene scene3=new Scene(page3,800,600);

                        //-----------------LOGIN PAGE (scene4)---------------------

        Label accNo=new Label("Account Number: ");
        accNo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            
        TextField acc_no= new TextField();
                
        Label p=new Label("Password: ");
        p.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
                
        PasswordField pwd= new PasswordField();

        HBox h1=new HBox(15);
        h1.getChildren().addAll(accNo,acc_no);
        h1.setStyle("-fx-alignment: center");

        HBox h2=new HBox(15);
        h2.getChildren().addAll(p,pwd);
        h2.setStyle("-fx-alignment: center");


        Button b1=new Button("Login");
        b1.setStyle("-fx-font-size: 20px;-fx-border-color: black; -fx-border-width: 2px;");


        acc_no.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                try {
                    long accountNumber = Long.parseLong(newValue.trim());
                    b1.setDisable(!isAccountNumberExists(accountNumber)); // Disable if account number does not exist
                } catch (NumberFormatException e) {
                    b1.setDisable(true); // Disable if input is not a valid number
                }
            } else {
                b1.setDisable(true); // Disable if text field is empty
            }
        });

        pwd.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                try {
                    long accountNumber = Long.parseLong(acc_no.getText().trim());
                    ac=accountNumber;
                    b1.setDisable(!isPasswordCorrect(accountNumber, newValue.trim())); // Disable if password is incorrect
                } catch (NumberFormatException e) {
                    b1.setDisable(true); // Disable if account number is not a valid number
                }
            } else {
                b1.setDisable(true); // Disable if password field is empty
            }
        });

        HBox hb3=new HBox(15);
        hb3.getChildren().addAll(back,b1);
        hb3.setStyle("-fx-alignment: center");

        VBox v1=new VBox(20);
        v1.getChildren().addAll(h1,h2,hb3);
        v1.setStyle("-fx-alignment: center");

        StackPane page4= new StackPane();
        page4.setStyle("-fx-background-color: cyan;"); // Set background color
        page4.getChildren().addAll(v1);
        Scene scene4=new Scene(page4,800,600);   
        
                        //------------------Scene 5-----------------------

                        Button viewDetailsButton = new Button("View Details");
                        Button depositButton = new Button("Deposit");
                        Button withdrawButton = new Button("Withdraw");
                        Button transferButton = new Button("Transfer");
                        Button changePinButton = new Button("Change PIN");

                        String buttonStyle = "-fx-min-width: 300px; -fx-min-height: 50px; -fx-font-size: 20px";
                        viewDetailsButton.setStyle(buttonStyle);
                        depositButton.setStyle(buttonStyle);
                        withdrawButton.setStyle(buttonStyle);
                        transferButton.setStyle(buttonStyle);
                        changePinButton.setStyle(buttonStyle);
                
                        // Add buttons to a vertical layout
                        VBox vbox = new VBox(10);
                        vbox.getChildren().addAll(viewDetailsButton, depositButton, withdrawButton, transferButton, changePinButton,home);
                
                        // Apply CSS to make buttons span the entire width
                        vbox.setStyle("-fx-alignment: center; -fx-spacing: 10; -fx-padding: 20;");
        
        StackPane page5= new StackPane();
        page5.setStyle("-fx-background-color: cyan;"); // Set background color
        page5.getChildren().addAll(vbox);
        Scene scene5=new Scene(page5,800,600); 

                        //------------------Scene 6---------------------

                        Label nameLabel = new Label();
                        Label emailLabel = new Label();
                        Label balanceLabel = new Label();
                        Label titleL=new Label("Account details: ");

                        // Apply styling to labels
                        nameLabel.setStyle("-fx-font-size: 20px; -fx-alignment: center;");
                        emailLabel.setStyle("-fx-font-size: 20px; -fx-alignment: center;");
                        balanceLabel.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-alignment: center;");
                        titleL.setStyle("-fx-font-size: 44px; -fx-font-weight: bold; -fx-alignment: center;-fx-padding: 20px");

                
                        // Create a layout to hold the labels
                        VBox vbox1 = new VBox(10);
                        vbox1.getChildren().addAll(titleL,balanceLabel, nameLabel, emailLabel,back);
                        vbox1.setStyle("-fx-alignment: center");

        StackPane page6= new StackPane();
        page6.setStyle("-fx-background-color: cyan;"); // Set background color
        page6.getChildren().addAll(vbox1);
        Scene scene6=new Scene(page6,800,600); 

                        //------------------INITIAL PAGE-------------------

        btn1.setOnAction(e -> primaryStage.setScene(scene2));
        btn1.setStyle("-fx-font-size: 20px;-fx-border-color: black; -fx-border-width: 2px;");

        Button btn2 = new Button("Login");
        btn2.setOnAction(e -> System.out.println("Button 2 clicked!"));
        btn2.setStyle("-fx-font-size: 20px; -fx-border-color: black; -fx-border-width: 2px;");

        // Label
        Label label = new Label("AAA Bank");
        label.setStyle("-fx-font-size: 64px; -fx-font-weight: bold;");

        // Layout
        HBox hbox = new HBox(40);
        hbox.getChildren().addAll(btn1, btn2);
        hbox.setStyle("-fx-alignment: center; -fx-padding: 20;");

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(label, hbox);
        vBox.setStyle("-fx-alignment: center; -fx-padding: 20;");

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(vBox);
        stackPane.setStyle("-fx-background-color: cyan;"); // Set background color

        // Scene
        Scene scene = new Scene(stackPane, 800, 600);
        primaryStage.setScene(scene);

        back.setOnAction(e->primaryStage.setScene(scene));

        create.setOnAction(e->{
            String name=t1.getText();
            String Email=email.getText();
            String pass=t2.getText();
            primaryStage.setScene(scene3);
            insertData(name, Email, pass, AccNo);
        });

        home.setOnAction(e->primaryStage.setScene(scene));

        btn2.setOnAction(e->primaryStage.setScene(scene4));

        b1.setOnAction(e->primaryStage.setScene(scene5));

        h.setOnAction(e->primaryStage.setScene(scene));

         // Set event handlers for each button (you need to implement these)
         
            viewDetailsButton.setOnAction(e -> {
                // Fetch account details using the stored account number (ac)
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingapp", "root", "")) {
                    // Prepare statement
                    PreparedStatement statement = conn.prepareStatement("SELECT * FROM accdetails WHERE acc_no = ?");
                    statement.setLong(1, ac);
                    ResultSet resultSet = statement.executeQuery();
            
                    // Check if account exists
                    if (resultSet.next()) {
                        String name = resultSet.getString("name");
                        String email1 = resultSet.getString("email");
                        double balance = resultSet.getDouble("money");
            
                        // Update labels with fetched data
                        nameLabel.setText("Name: " + name);
                        emailLabel.setText("Email: " + email1);
                        balanceLabel.setText("Balance: Rs." + balance);
                    } else {
                        // Handle if account not found
                        nameLabel.setText("Account not found");
                        emailLabel.setText("");
                        balanceLabel.setText("");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    // Handle SQLException
                }
            primaryStage.setScene(scene6);
         });

    depositButton.setOnAction(e -> {
    // Create a new stage for the deposit page
    Stage depositStage = new Stage();
    depositStage.setTitle("Deposit");

    // Create labels and text field for deposit amount
    Label amountLabel = new Label("Enter deposit amount:");
    TextField amountField = new TextField();
    Button confirmButton = new Button("Confirm");

    // Set action for the confirm button
    confirmButton.setOnAction(event -> {
        // Get the deposit amount entered by the user
        double depositAmount = Double.parseDouble(amountField.getText());

        // Update the balance in the database
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingapp", "root", "")) {
            // Prepare statement to update the balance
            PreparedStatement statement = conn.prepareStatement("UPDATE accdetails SET money = money + ? WHERE acc_no = ?");
            statement.setDouble(1, depositAmount);
            statement.setLong(2, ac); // Assuming 'ac' is the account number
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                // Deposit successful
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Deposit Successfull");
                alert.showAndWait();
                // You can update the balance label here if needed
            } else {
                // Deposit failed
                System.out.println("Failed to deposit amount.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle SQLException
        }

        // Close the deposit stage
        depositStage.close();
    });

    // Create layout for the deposit page
    VBox depositLayout = new VBox(10);
    depositLayout.getChildren().addAll(amountLabel, amountField, confirmButton);
    depositLayout.setAlignment(Pos.CENTER);
    depositLayout.setPadding(new Insets(20));

    // Create scene for the deposit page
    Scene depositScene = new Scene(depositLayout, 300, 150);
    depositStage.setScene(depositScene);
    depositStage.show();
});


withdrawButton.setOnAction(e -> {
    // Create a new stage for the withdraw page
    Stage withdrawStage = new Stage();
    withdrawStage.setTitle("Withdraw");

    // Create labels and text field for withdraw amount
    Label amountLabel = new Label("Enter withdraw amount:");
    TextField amountField = new TextField();
    Button confirmButton = new Button("Confirm");

    // Set action for the confirm button
    confirmButton.setOnAction(event -> {
        // Get the withdraw amount entered by the user
        double withdrawAmount = Double.parseDouble(amountField.getText());

        // Check if the withdraw amount is valid
        if (withdrawAmount <= 0) {
            // Display an alert for invalid withdraw amount
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid withdraw amount");
            alert.showAndWait();
            return;
        }

        // Update the balance in the database
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingapp", "root", "")) {
            // Prepare statement to update the balance
            PreparedStatement statement = conn.prepareStatement("UPDATE accdetails SET money = money - ? WHERE acc_no = ? AND money >= ?");
            statement.setDouble(1, withdrawAmount);
            statement.setLong(2, ac); // Assuming 'ac' is the account number
            statement.setDouble(3, withdrawAmount); // Ensure that there are sufficient funds
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                // Withdraw successful
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Withdraw Successfull");
                alert.showAndWait();
                // You can update the balance label here if needed
            } else {
                // Withdraw failed due to insufficient funds or other reasons
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Withdraw Failed");
                alert.setHeaderText(null);
                alert.setContentText("Insufficient Funds");
                alert.showAndWait();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle SQLException
        }

        // Close the withdraw stage
        withdrawStage.close();
    });

    // Create layout for the withdraw page
    VBox withdrawLayout = new VBox(10);
    withdrawLayout.getChildren().addAll(amountLabel, amountField, confirmButton);
    withdrawLayout.setAlignment(Pos.CENTER);
    withdrawLayout.setPadding(new Insets(20));

    // Create scene for the withdraw page
    Scene withdrawScene = new Scene(withdrawLayout, 300, 150);
    withdrawStage.setScene(withdrawScene);
    withdrawStage.show();
});

        transferButton.setOnAction(e -> {
    // Get recipient's account number and transfer amount
    TextInputDialog accountDialog = new TextInputDialog();
    accountDialog.setTitle("Transfer Funds");
    accountDialog.setHeaderText("Enter recipient's account number:");
    Optional<String> accountResult = accountDialog.showAndWait();

    accountResult.ifPresent(accountNumber -> {
        TextInputDialog amountDialog = new TextInputDialog();
        amountDialog.setTitle("Transfer Funds");
        amountDialog.setHeaderText("Enter amount to transfer:");
        Optional<String> amountResult = amountDialog.showAndWait();

        amountResult.ifPresent(amountStr -> {
            try {
                double transferAmount = Double.parseDouble(amountStr);
                long recipientAccountNumber = Long.parseLong(accountNumber.trim());

                // Check if recipient account exists
                boolean recipientExists = isAccountNumberExists(recipientAccountNumber);

                if (recipientExists) {
                    // Perform the transfer operation
                    boolean success = transferFunds(recipientAccountNumber, transferAmount);

                    if (success) {
                            Alert alert = new Alert(AlertType.INFORMATION);
                            alert.setTitle("SUCCESS");
                            alert.setHeaderText(null);
                            alert.setContentText("Tranfer Successfull");
                            alert.setContentText("Transferred: Rs. "+transferAmount+"to account: "+recipientAccountNumber);
                            alert.showAndWait();
                    } else {
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setTitle("Transfer Failed");
                            alert.setHeaderText(null);
                            alert.setContentText("Failed to transfer Funds.");
                            alert.showAndWait();
                    }
                } else {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Recipient Account not found");
                        alert.setHeaderText(null);
                        alert.setContentText("The recipient account number does not exist in the database. Please check and try again.");
                        alert.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Invalid input");
                        alert.setHeaderText(null);
                        alert.setContentText("Please Enter valid amount and account number");
                        alert.showAndWait();
            }
        });
    });
});
        // Assuming you have a button named changePinButton
changePinButton.setOnAction(e -> {
    // Create a new stage for the change PIN page
    Stage changePinStage = new Stage();
    changePinStage.setTitle("Change Password");

    // Create labels and text fields for current and new PIN
    Label currentPinLabel = new Label("Enter current Password:");
    PasswordField currentPinField = new PasswordField();
    Label newPinLabel = new Label("Enter new Password:");
    PasswordField newPinField = new PasswordField();
    Button confirmButton = new Button("Confirm");

    // Set action for the confirm button
    confirmButton.setOnAction(event -> {
        // Get the current and new PIN entered by the user
        String currentPin = currentPinField.getText();
        String newPin = newPinField.getText();

        // Check if the current and new PIN fields are not empty
        if (currentPin.isEmpty() || newPin.isEmpty()) {
            // Display an alert for empty PIN fields
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter both current and new password");
            alert.showAndWait();
            return;
        }

        // Check if the new PIN is different from the current PIN
        if (currentPin.equals(newPin)) {
            // Display an alert for same current and new PIN
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText("New password should be different from the current password");
            alert.showAndWait();
            return;
        }

        // Update the PIN in the database
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankingapp", "root", "")) {
            // Prepare statement to update the PIN
            PreparedStatement statement = conn.prepareStatement("UPDATE accdetails SET pass = ? WHERE acc_no = ? AND pass = ?");
            statement.setString(1, newPin);
            statement.setLong(2, ac); // Assuming 'ac' is the account number
            statement.setString(3, currentPin); // Check if current PIN matches
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                // PIN update successful
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Password changed successfully");
                alert.showAndWait();
            } else {
                // PIN update failed due to incorrect current PIN or other reasons
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to change Password. Please check your current Password.");
                alert.showAndWait();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle SQLException
        }

        // Close the change PIN stage
        changePinStage.close();
    });

    // Create layout for the change PIN page
    VBox changePinLayout = new VBox(10);
    changePinLayout.getChildren().addAll(currentPinLabel, currentPinField, newPinLabel, newPinField, confirmButton);
    changePinLayout.setAlignment(Pos.CENTER);
    changePinLayout.setPadding(new Insets(20));

    // Create scene for the change PIN page
    Scene changePinScene = new Scene(changePinLayout, 300, 200);
    changePinStage.setScene(changePinScene);
    changePinStage.show();
});
        primaryStage.show();
    }
}
