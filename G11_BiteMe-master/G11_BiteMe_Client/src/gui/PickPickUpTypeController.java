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
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import logic.Order;
import logic.OrderItemDetail;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import client.ChatClient;

public class PickPickUpTypeController implements Initializable, IControl {

	@FXML
	private Button btnExit;

	@FXML
	private Button btnNext;

	@FXML
	private ToggleButton ToggleBtnTakeAway;

	@FXML
	private ImageView imageViewTakeAway;

	@FXML
	private ToggleGroup photoToggleGroup;

	@FXML
	private ToggleButton ToggleBtnDelivery;

	@FXML
	private ImageView ImageViewDelivery;

	@FXML
	private TextField idTimetxtField;

	@FXML
	private DatePicker idDatePicker;

	@FXML
	private Label lblLocation;

	@FXML
	private TextField idLocation;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		idDatePicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
			@Override
			public DateCell call(DatePicker picker) {
				return new DateCell() {
					@Override
					public void updateItem(LocalDate date, boolean empty) {
						super.updateItem(date, empty);
						LocalDate today = LocalDate.now();
						setDisable(empty || date.compareTo(today) < 0 || date.compareTo(today.plusDays(3)) > 0);
					}
				};
			}
		});
	}

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/PickUpType.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/PickUpType.css").toExternalForm());
		primaryStage.setTitle("PickUpType Page");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@FXML
	public void getBackBtn(ActionEvent event) throws Exception {
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		PickItemsController pic = new PickItemsController();
		pic.start(new Stage());
	}

	@FXML
	public void getNextBtn(ActionEvent event) throws Exception {
		if (!ToggleBtnTakeAway.isSelected() && !ToggleBtnDelivery.isSelected()) {
			showAlert("Validation Error", "Please select a pickup type (TakeAway or Delivery).");
			return;
		}
		if (idTimetxtField.getText().isEmpty() || idDatePicker.getValue() == null
				|| (ToggleBtnDelivery.isSelected() && idLocation.getText().isEmpty())) {
			showAlert("Validation Error", "All fields must be filled.");
			return;
		}

		if (!isValidTime(idTimetxtField.getText(), idDatePicker.getValue())) {
			showAlert("Validation Error", "Invalid time. Ensure the time is in the future and in HH:MM format.");
			return;
		}
		String time = idTimetxtField.getText();
		LocalDate date = idDatePicker.getValue();
		String pType = ToggleBtnDelivery.isSelected() ? "Delivery" : "TakeAway";
		String location = ToggleBtnDelivery.isSelected() ? idLocation.getText() : "";

		Order order = new Order(0, ChatClient.currUser.getUserID(), LocalDate.now(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
				ChatClient.selectedResturant.getResturantID(), pType, location, "Pending", time, date, "", "",
				OrderItemDetail.totalCost(ChatClient.userCart));
		// Apply 10% if early order
		if (!order.isOrderWithinTwoHours()) {
			order.setCost(order.getCost() * (float) 0.9);
		}
		// Apply 50% if customer has copun
		if (ChatClient.currUser.getAmountOfCopunts() > 0) {
			order.setCost(order.getCost() * (float) 0.5);
		}
		ChatClient.newOrder = order;
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		OrderSummaryController osc = new OrderSummaryController();
		osc.start(new Stage());
	}

	@FXML
	private void handleToggleButtonAction(ActionEvent event) {
		if (ToggleBtnDelivery.isSelected()) {
			lblLocation.setVisible(true);
			idLocation.setVisible(true);
		} else {
			lblLocation.setVisible(false);
			idLocation.setVisible(false);
		}
	}

	private boolean isValidTime(String timeStr, LocalDate date) {
		try {
			String[] parts = timeStr.split(":");
			if (parts.length != 2) {
				return false;
			}

			int hour = Integer.parseInt(parts[0]);
			int minute = Integer.parseInt(parts[1]);

			if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
				return false;
			}

			LocalTime time = LocalTime.of(hour, minute);
			LocalDateTime dateTime = LocalDateTime.of(date, time);

			return !dateTime.isBefore(LocalDateTime.now());
		} catch (NumberFormatException e) {
			return false;
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
