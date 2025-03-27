package gui;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.URL;
import java.util.ResourceBundle;

import Server.EchoServer;
import Server.ServerUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logic.ClientInfo;

public class ServerPortFrameController implements Initializable {

	@FXML
	private TextArea txtareaserver;

	@FXML
	private TableView<ClientInfo> tblViewConnection;

	@FXML
	private TableColumn<ClientInfo, String> hostnameCol;

	@FXML
	private TableColumn<ClientInfo, String> ipCol;

	@FXML
	private TableColumn<ClientInfo, String> statusCol;

	@FXML
	private ToggleButton tglBtnClient;

	@FXML
	private ToggleButton tglBtnCustomer;

	@FXML
	private ToggleButton tglBtnSupplier;

	@FXML
	private ToggleButton tglBtnManager;

	@FXML
	private ToggleButton tglBtnCeo;

	@FXML
	private Button btnconnect;

	@FXML
	private Button btnDisconnect;

	@FXML
	private Button btnExit;

	@FXML
	private Text porttxt;

	@FXML
	private Text iptxt;

	@FXML
	private TextField dbnametxtfield;

	@FXML
	private TextField mysqlusertxtfield;

	@FXML
	private TextField mysqlpasstxtfield;

	private ObservableList<ClientInfo> clientInfoList = FXCollections.observableArrayList();

	private ToggleGroup group1 = new ToggleGroup();

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ServerPort.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/ServerPort.css").toExternalForm());
		primaryStage.setTitle("Server");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	public void getDisconnectBtn(ActionEvent event) {
		ServerUI.sv.closeDB();
		btnDisconnect.setDisable(true);
		btnconnect.setDisable(false);
	}
	public void getConnectBtn(ActionEvent event) throws Exception {
		ServerUI.sv.startDB(dbnametxtfield.getText(), mysqlusertxtfield.getText(), mysqlpasstxtfield.getText());
		ServerUI.sv.setController(this);
		btnconnect.setDisable(true);
		btnDisconnect.setDisable(false);
	}

	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("exiting server");
		System.exit(0);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnDisconnect.setDisable(true);
		String ip;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}
		porttxt.setText(Integer.toString(ServerUI.DEFAULT_PORT));
		iptxt.setText(ip);
		PrintStream printStream = new PrintStream(new TextAreaOutputStream(txtareaserver));
		System.setOut(printStream);
		mysqlpasstxtfield.setText("Qwer19102000@");
		mysqlusertxtfield.setText("root");
		dbnametxtfield.setText("jdbc:mysql://localhost/biteme");

		initializeTableColumns();
		addToggleButtonsListeners();
	}

	private void initializeTableColumns() {
		hostnameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		ipCol.setCellValueFactory(new PropertyValueFactory<>("address"));
		statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
		tblViewConnection.setItems(clientInfoList);
	}

	private void addToggleButtonsListeners() {
		tglBtnClient.setToggleGroup(group1);
		tglBtnCustomer.setToggleGroup(group1);
		tglBtnSupplier.setToggleGroup(group1);
		tglBtnManager.setToggleGroup(group1);
		tglBtnCeo.setToggleGroup(group1);

		group1.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) {
				oldValue.setSelected(true);
			} else {
				updateTableView();
			}
		});

		tglBtnClient.setOnAction(event -> updateTableView());
		tglBtnCustomer.setOnAction(event -> updateTableView());
		tglBtnSupplier.setOnAction(event -> updateTableView());
		tglBtnManager.setOnAction(event -> updateTableView());
		tglBtnCeo.setOnAction(event -> updateTableView());
	}

	public void updateTableView() {
		ToggleButton selectedToggleButton = (ToggleButton) group1.getSelectedToggle();
		if (selectedToggleButton == tglBtnClient) {
			clientInfoList.setAll(EchoServer.ConnectedClientsList);
		} else if (selectedToggleButton == tglBtnCustomer) {
			clientInfoList.setAll(EchoServer.ConnectedCustomerList);
		} else if (selectedToggleButton == tglBtnSupplier) {
			clientInfoList.setAll(EchoServer.ConnectedSupplierList);
		} else if (selectedToggleButton == tglBtnManager) {
			clientInfoList.setAll(EchoServer.ConnectedManagerList);
		} else if (selectedToggleButton == tglBtnCeo) {
			clientInfoList.setAll(EchoServer.ConnectedCeoList);
		}
	}

	// Custom OutputStream to write to TextArea
	private static class TextAreaOutputStream extends OutputStream {
		private final TextArea textArea;

		public TextAreaOutputStream(TextArea textArea) {
			this.textArea = textArea;
		}

		@Override
		public void write(int b) throws IOException {
			textArea.appendText(String.valueOf((char) b));
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			textArea.appendText(new String(b, off, len));
		}
	}
}
