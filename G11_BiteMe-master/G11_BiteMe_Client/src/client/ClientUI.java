package client;

import javafx.application.Application;

import javafx.stage.Stage;

import java.util.Vector;

import gui.ConnectToServerController;
import gui.LoginController;
import client.ClientController;

public class ClientUI extends Application {
	public static ClientController chat; // only one instance

	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub

		ConnectToServerController aFrame = new ConnectToServerController(); // create StudentFrame

		aFrame.start(primaryStage);
	}

	// public static void connectToServer(String host, int port) {
	//
	// }
}
