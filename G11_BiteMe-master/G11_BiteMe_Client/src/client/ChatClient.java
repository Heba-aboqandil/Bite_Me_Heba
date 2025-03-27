// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import client.*;
import common.ChatIF;
import logic.MenuItem;
import logic.Message;
import logic.Order;
import logic.OrderItemDetail;
import logic.Resturant;
import logic.User;
import logic.UserType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {

	public static User currUser = null;
	public static String lastAction = "";
	public static ArrayList<Order> userOrders;
	public static ArrayList<Resturant> userResturants;
	public static Resturant selectedResturant;
	public static MenuItem menuItems;
	public static ArrayList<OrderItemDetail> userCart;
	public static Order newOrder;
	// Instance variables **********************************************

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ChatIF clientUI;
	public static boolean awaitResponse = false;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param clientUI The interface type variable.
	 */

	public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		this.clientUI = clientUI;
		// openConnection();
	}

	// Instance methods ************************************************

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) {
		awaitResponse = false;
		System.out.println("--> handleMessageFromServer");
		Message message = (Message) msg;
		ArrayList<Object> arr = (ArrayList<Object>) message.getObject();
		switch (message.getType()) {
		case UserConnect:
			if (((String) arr.get(0)).equals("User not found")) {
				lastAction = "User not found";
			} else if (((String) arr.get(0)).equals("User is already logged in")) {
				lastAction = "User is already logged in";
			} else if (((String) arr.get(0)).equals("User is valid")) {
				lastAction = "User is valid";
				currUser = (User) (arr.get(1));
				currUser.setIsLogged(1);
			}
			break;
		case UserDisconnect:
			currUser = null;
			lastAction = "Disconnected";
			break;
		case GetCustomerOrders:
			userOrders = new ArrayList<>();
			lastAction = "Order is found";
			for (Object obj : arr) {
				if (obj instanceof Order) {
					userOrders.add((Order) obj);
				}
			}
			break;
		case UpdateOrderStatus:
			lastAction = "Order Status has been changed";
			break;
		case GetResturants:
			lastAction = "Got Resturants";
			userResturants = new ArrayList<>();
			for (Object obj : arr) {
				if (obj instanceof Resturant) {
					userResturants.add((Resturant) obj);
				}
			}
			break;
		case GetResturantMenuItems:
			menuItems = (MenuItem) arr.get(0);
			lastAction = "Returned menu items";
		case AddOrderToDB:
			if (((boolean) arr.get(0)) == true)
				lastAction = "Order Confirmed\nOrder number is: " + (int) arr.get(1)+".";
			else
				lastAction = "Order was unsuccessful";
		default:
			break;
		}

	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param msg The message from the UI.
	 */

	public void handleMessageFromClientUI(Object msg) {
		try {
			openConnection();// in order to send more than one message
			awaitResponse = true;
			sendToServer(msg);
			// wait for response
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			clientUI.display("Could not send message to server: Terminating client." + e);
			quit();
		}
	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
	}
}
//End of ChatClient class
