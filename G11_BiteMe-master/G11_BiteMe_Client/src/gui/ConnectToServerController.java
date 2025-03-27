package gui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import client.ClientController;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logic.Message;
import logic.MessageType;
import logic.UserType;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ConnectToServerController implements IControl,Initializable {

	@FXML
	private Button btnExit;

	@FXML
	private Button btnConnect;

	@FXML
	private Label messagelbl;

	@FXML
	private TextField iptxt;

	@FXML
	public void getBackBtn(ActionEvent event) {
		System.exit(0);
	}

	private boolean isValidIPAddress(String ip) {
		String ipPattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
				+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
		return Pattern.compile(ipPattern).matcher(ip).matches();
	}

	@FXML
	void connectToServer(ActionEvent event) throws Exception {
		String ip = iptxt.getText();

		if (ip.trim().isEmpty()) {
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("Enter IP");
			a.showAndWait();
		} else if (!isValidIPAddress(ip)) {
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("Enter a valid IP");
			a.showAndWait();
		} else if (!InetAddress.getByName(ip).isReachable(1000)) {
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("The IP you entered is unreachable");
			a.showAndWait();
		} else {
			ClientUI.chat = new ClientController(ip, 5555);
			((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window

			System.out.println("Connecting to LoginController");

			LoginController lc = new LoginController();
			InetAddress thisIP = InetAddress.getLocalHost();
			ArrayList<Object> inputList = new ArrayList<>();
			inputList.add(thisIP.getHostName());
			inputList.add(thisIP.getHostAddress());
			Message msg = new Message(MessageType.ClientConnected, inputList);
			ClientUI.chat.accept(msg);

			System.out.println("Starting LoginController");
			lc.start(new Stage());
		}

	}

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ConnectToServer.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/ConnectToServer.css").toExternalForm());
		primaryStage.setTitle("Connect to server page");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		iptxt.setText("192.168.31.56");
		
	}
}
