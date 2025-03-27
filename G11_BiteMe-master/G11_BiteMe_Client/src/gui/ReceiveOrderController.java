package gui;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logic.Message;
import logic.MessageType;
import logic.Order;

public class ReceiveOrderController implements Initializable, IControl {

	@FXML
	private TableView<Order> orderTableView;

	@FXML
	private TableColumn<Order, Integer> orderIdCol;

	@FXML
	private TableColumn<Order, String> dateOfOrderCol;

	@FXML
	private TableColumn<Order, String> timeOfOrderCol;

	@FXML
	private TableColumn<Order, Integer> restaurantIdCol;

	@FXML
	private TableColumn<Order, String> pickUpTypeCol;

	@FXML
	private TableColumn<Order, String> locationCol;

	@FXML
	private TableColumn<Order, String> statusCol;

	@FXML
	private TableColumn<Order, String> requestedTimeCol;

	@FXML
	private TableColumn<Order, String> requestedDateCol;

	@FXML
	private TableColumn<Order, String> etaCol;

	@FXML
	private TableColumn<Order, String> durationCol;

	@FXML
	private TableColumn<Order, Double> costCol;

	@FXML
	private Button receiveButton;

	@FXML
	void updateStatus(ActionEvent event) {
		Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
		if (selectedOrder != null) {
			if (selectedOrder.getStatus().equals("Received")) {
				showAlert("Order Received", "You already received this order.");
				return;
			}
			Duration duration = Duration.between(selectedOrder.getRequestedDeliveryDateTime(), LocalDateTime.now());
			if (duration.isNegative()) {
				showAlert("Order Requested Time",
						"The current time is earlier than the requested delivery time. Please try again later.");
				return;
			}
			if (!selectedOrder.getStatus().equals("Ready")) {
				showAlert("Order Not Ready", "Order not ready yet.");
				return;
			}
			selectedOrder.calculateDurationFromNow();
			if (selectedOrder.customerEligibleForCoupon()) {
				ChatClient.currUser.setAmountOfCopunts(ChatClient.currUser.getAmountOfCopunts() + 1);
			}
			ArrayList<Object> msgList = new ArrayList<>();
			msgList.add(selectedOrder);
			Message msg = new Message(MessageType.UpdateOrderStatus, msgList);
			ClientUI.chat.accept(msg);
			updateTableView();
		} else {
			showAlert("No Order Selected", "Please select an order.");
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initializeTableColumns();
		init();
	}

	private void initializeTableColumns() {
		try {
			orderIdCol.setCellValueFactory(new PropertyValueFactory<>("OrderID"));
			dateOfOrderCol.setCellValueFactory(new PropertyValueFactory<>("DateOfOrder"));
			timeOfOrderCol.setCellValueFactory(new PropertyValueFactory<>("TimeOfOrder"));
			restaurantIdCol.setCellValueFactory(new PropertyValueFactory<>("ResturantID"));
			pickUpTypeCol.setCellValueFactory(new PropertyValueFactory<>("PickUpType"));
			locationCol.setCellValueFactory(new PropertyValueFactory<>("Location"));
			statusCol.setCellValueFactory(new PropertyValueFactory<>("Status"));
			requestedTimeCol.setCellValueFactory(new PropertyValueFactory<>("RequestedTimeOfDelivery"));
			requestedDateCol.setCellValueFactory(new PropertyValueFactory<>("RequestedDateOfDelivery"));
			etaCol.setCellValueFactory(new PropertyValueFactory<>("ETA"));
			durationCol.setCellValueFactory(new PropertyValueFactory<>("Duration"));
			costCol.setCellValueFactory(new PropertyValueFactory<>("Cost"));
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void init() {
		try {
			ArrayList<Object> msgList = new ArrayList<>();
			msgList.add(ChatClient.currUser.getUserID());

			Message msg = new Message(MessageType.GetCustomerOrders, msgList);
			ClientUI.chat.accept(msg);

			if (ChatClient.userOrders != null) {
				ObservableList<Order> orderList = FXCollections.observableArrayList(ChatClient.userOrders);
				orderTableView.setItems(orderList);
			} else {
				System.out.println("ChatClient.userOrders is null.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateTableView() {
		try {
			ArrayList<Object> msgList = new ArrayList<>();
			msgList.add(ChatClient.currUser.getUserID());
			Message msg = new Message(MessageType.GetCustomerOrders, msgList);
			ClientUI.chat.accept(msg);

			if (ChatClient.userOrders != null) {
				orderTableView.getItems().clear();
				ObservableList<Order> orderList = FXCollections.observableArrayList(ChatClient.userOrders);
				orderTableView.setItems(orderList);
			} else {
				System.out.println("ChatClient.userOrders is null.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ReceiveOrder.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/ReceiveOrder.css").toExternalForm());
		primaryStage.setTitle("Order Page");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void getBackBtn(ActionEvent event) throws Exception {
		ChatClient.userOrders = null;
		((Node) event.getSource()).getScene().getWindow().hide();
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
}
