package gui;

import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import client.ChatClient;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Message;
import logic.MessageType;
import logic.UserType;

public class LoginController implements Initializable, IControl {
	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnLogin = null;
	@FXML
	private TextField Usernametxt;
	@FXML
	private TextField Passwordtxt;
	@FXML
	private ComboBox<String> UserTypecb;
	@FXML
	private Label messagelbl;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		UserTypecb.getItems().addAll("Customer", "Manager", "CEO", "Supplier");
		Usernametxt.setText("charlie");
		Passwordtxt.setText("password123");

	}

	public void login(ActionEvent event) throws Exception {
		String username = Usernametxt.getText();
		String password = Passwordtxt.getText();
		UserType ut = UserType.getUserType(UserTypecb.getValue());
		if (username.trim().isEmpty()) {
			showAlert("Validation Error", "All fields must be filled.");
		} else if (password.trim().isEmpty()) {
			showAlert("Validation Error", "All fields must be filled.");
		} else if (ut == null) {
			showAlert("Validation Error", "All fields must be filled.");
		} else {
			ArrayList<Object> inputList = new ArrayList<>();
			inputList.add(username);
			inputList.add(password);
			inputList.add(UserType.getStringType(ut));
			Message msg = new Message(MessageType.UserConnect, inputList);
			ClientUI.chat.accept(msg);
			String retunedMessage = ChatClient.lastAction;
			System.out.println(retunedMessage);
			if (retunedMessage.equals("User not found")) {
				showAlert("Validation Error", "User not found.");
			} else if (retunedMessage.equals("User is already logged in")) {
				showAlert("Validation Error", "User is already logged in.");
			} else if (retunedMessage.equals("User is valid")) {
				openUserHomePage(event);
			}
		}
	}

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/Login.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/Login.css").toExternalForm());
		primaryStage.setTitle("Login Page");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void getBackBtn(ActionEvent event) throws Exception {
		InetAddress ip = InetAddress.getLocalHost();
		ArrayList<Object> inputList = new ArrayList<>();
		inputList.add(ip.getHostName());
		Message msg = new Message(MessageType.ClientDisconnected, inputList);
		ClientUI.chat.accept(msg);
		System.exit(0);
	}

	public void display(String message) {
		System.out.println("message");

	}

	public void openUserHomePage(ActionEvent event) throws Exception {
		UserType type = ChatClient.currUser.getUserType();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		switch (type) {
		case Customer:
			CustomerHomePageController chp = new CustomerHomePageController();
			chp.start(new Stage());
			break;
		case CEO:
			// open CEO homepage
			break;
		case Manager:
			// open manager homepage
			break;
		case Supplier:
			// open supplier homepage
			break;
		default:
			break;
		}

	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
