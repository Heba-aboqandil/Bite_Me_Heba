package gui;

import java.net.URL;
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
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logic.Message;
import logic.MessageType;
import logic.Resturant;
import javafx.scene.control.TableView;

public class PickRestuarntController implements Initializable, IControl {
	@FXML
	private Button btnExit;

	@FXML
	private TableView<Resturant> idResturantTableView;

	@FXML
	private TableColumn<Resturant, String> resturantNameCol;

	@FXML
	private TableColumn<Resturant, String> resturantBranchCol;

	@FXML
	private Button btnNext;

	private Resturant selectedResturant;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Message msg = new Message(MessageType.GetResturants, null);
		ClientUI.chat.accept(msg);
		resturantNameCol.setCellValueFactory(new PropertyValueFactory<>("resturantName"));
		resturantBranchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));

		// Populate the table with data from ChatClient.userResturants
		ObservableList<Resturant> resturants = FXCollections.observableArrayList(ChatClient.userResturants);
		idResturantTableView.setItems(resturants);

		// Add listener to table selection
		idResturantTableView.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSelection, newSelection) -> {
					if (newSelection != null) {
						selectedResturant = newSelection;
					}
				});
	}

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/PickResturant.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/PickResturant.css").toExternalForm());
		primaryStage.setTitle("OrderFood Page");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void getBackBtn(ActionEvent event) throws Exception {
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		CustomerHomePageController chpc = new CustomerHomePageController();
		chpc.start(new Stage());
	}

	public void getNextBtn(ActionEvent event) throws Exception {
		if (selectedResturant != null) {
			ArrayList<Object> msgObjList = new ArrayList<>();
			msgObjList.add(selectedResturant.getMenuID());
			Message msg = new Message(MessageType.GetResturantMenuItems, msgObjList);
			ClientUI.chat.accept(msg);
			ChatClient.selectedResturant = this.selectedResturant;
			((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
			PickItemsController pic = new PickItemsController();
			pic.start(new Stage());

		} else {
			showAlert("No Resturant Selecetd", "Please select a resturant.");
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
