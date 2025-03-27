package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.ChatClient;
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
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import logic.Message;
import logic.MessageType;

public class CustomerHomePageController implements IControl,Initializable {
	@FXML
	private ImageView imageUserimg;

	@FXML
	private Label lblUsername;

	@FXML
	private Button btnLogout;

	@FXML
	private Button btnOrderFood;

	@FXML
	private Button btnRecueveOrder;

	public void getOrderFoodBtn(ActionEvent event) throws Exception {
		PickRestuarntController ofc = new PickRestuarntController();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		ofc.start(new Stage());
	}

	public void getRecieveOrderBtn(ActionEvent event) throws Exception {
		ReceiveOrderController roc = new ReceiveOrderController();
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		roc.start(new Stage());
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/CustomerHomePage.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/CustomerHomePage.css").toExternalForm());
		primaryStage.setTitle("Customer Home Page");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void getBackBtn(ActionEvent event) throws Exception {
		ArrayList<Object> msgObjectList = new ArrayList<>();
		msgObjectList.add(ChatClient.currUser);
		msgObjectList.add(0);
		Message msg = new Message(MessageType.UserDisconnect, msgObjectList);
		ClientUI.chat.accept(msg);
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		LoginController lc = new LoginController();
		lc.start(new Stage());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lblUsername.setText("Hello "+ChatClient.currUser.getFirstName());
		ChatClient.menuItems=null;
		ChatClient.newOrder=null;
		ChatClient.selectedResturant=null;
		ChatClient.userCart=null;
		ChatClient.userOrders=null;
		ChatClient.userResturants=null;
	}

}
