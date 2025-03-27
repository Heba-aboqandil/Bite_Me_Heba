package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import logic.Message;
import logic.MessageType;
import logic.Order;
import logic.OrderItem;
import logic.OrderItemDetail;
import logic.Resturant;
import client.ChatClient;
import client.ClientController;
import client.ClientUI;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class OrderSummaryController implements Initializable, IControl {

	@FXML
	private Button btnExit;

	@FXML
	private Button btnConfirm;

	@FXML
	private ListView<String> listViewOrderDetails;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ArrayList<String> orderDetails = new ArrayList<>();
		String pType;
		// Add order details
		Order order = ChatClient.newOrder;
		if (order != null) {
			orderDetails.add("User ID: " + order.getUserID());
			orderDetails
					.add("Date of Order: " + order.getDateOfOrder().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			orderDetails.add("Time of Order: " + order.getTimeOfOrder());
			orderDetails.add("Restaurant: " + ChatClient.selectedResturant.getResturantName());
			orderDetails.add("Pick Up Type: " + order.getPickUpType());
			if (order.getPickUpType().equals("Delivery")) {
				orderDetails.add("Location: " + order.getLocation());
				pType = "Delivery";
			} else
				pType = "TakeAway";
			orderDetails.add("Requested Time of " + pType + ": " + order.getRequestedTimeOfDelivery());
			orderDetails.add("Requested Date of " + pType + ": "
					+ order.getRequestedDateOfDelivery().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			orderDetails.add("Total Cost: $" + String.format("%.2f", order.getCost()));
		}

		// Add order item details
		ArrayList<OrderItemDetail> cart = ChatClient.userCart;
		if (cart != null && !cart.isEmpty()) {
			orderDetails.add("Order Items:");
			for (OrderItemDetail itemDetail : cart) {
				String itemString = String.format("- %s: %d x $%.2f = $%.2f (Notes: %s)",
						itemDetail.getItem().getItemName(), itemDetail.getQuantity(), itemDetail.getCost(),
						itemDetail.getQuantity() * itemDetail.getCost(), itemDetail.getNotes());
				orderDetails.add(itemString);
			}
		}

		// Populate the ListView
		listViewOrderDetails.getItems().addAll(orderDetails);
	}

	@FXML
	public void getBackBtn(ActionEvent event) throws Exception {
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		CustomerHomePageController chpc = new CustomerHomePageController();
		chpc.start(new Stage());
	}

	@FXML
	public void getConfirmBtn(ActionEvent event) throws Exception {
		ArrayList<Object> msgObjList = new ArrayList<>();
		OrderItem om = new OrderItem(ChatClient.newOrder);
		om.setOrderItemDetails(ChatClient.userCart);
		msgObjList.add(om);
		Message msg = new Message(MessageType.AddOrderToDB, msgObjList);
		ClientUI.chat.accept(msg);
		if (ChatClient.lastAction.equals("Order was unsuccessful")) {
			showAlert("Order Unsuccessful", "Your order was not confirmed");
		} else {
			if (ChatClient.currUser.getAmountOfCopunts() > 1)
				ChatClient.currUser.setAmountOfCopunts(ChatClient.currUser.getAmountOfCopunts() - 1);
			showAlert("Order Confirmed", ChatClient.lastAction);
		}

		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		CustomerHomePageController chpc = new CustomerHomePageController();
		chpc.start(new Stage());
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/OrderSummary.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/OrderSummary.css").toExternalForm());
		primaryStage.setTitle("Login Page");
		primaryStage.setScene(scene);
		primaryStage.show();

	}
}
