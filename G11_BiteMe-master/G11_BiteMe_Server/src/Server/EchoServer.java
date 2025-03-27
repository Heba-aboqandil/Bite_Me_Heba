package Server;

import java.io.IOException;
import java.util.ArrayList;
import gui.ServerPortFrameController;
import logic.ClientInfo;
import logic.Message;
import logic.MessageType;
import logic.User;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class EchoServer extends AbstractServer {
	DBControl db;
	public static ArrayList<ClientInfo> ConnectedClientsList = new ArrayList<>();
	public static ArrayList<ClientInfo> ConnectedCustomerList = new ArrayList<>();
	public static ArrayList<ClientInfo> ConnectedSupplierList = new ArrayList<>();
	public static ArrayList<ClientInfo> ConnectedCeoList = new ArrayList<>();
	public static ArrayList<ClientInfo> ConnectedManagerList = new ArrayList<>();

	private ServerPortFrameController controller;

	public EchoServer(int port) {
		super(port);
	}

	public void setController(ServerPortFrameController controller) {
		this.controller = controller;
	}

	public ServerPortFrameController getController() {
		return this.controller;
	}

	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Message message = (Message) msg;
		ArrayList<Object> msgObjectList = (ArrayList<Object>) message.getObject();

		switch (message.getType()) {
		case ClientConnected:
			String name = (String) msgObjectList.get(0);
			String address = (String) msgObjectList.get(1);
			ConnectedClientsList.add(new ClientInfo(name, address, "Connected"));
			try {
				client.sendToClient(new Message(null, null));
			} catch (IOException e) {
				e.printStackTrace();
			}
			notifyClientListUpdate();
			break;
		case ClientDisconnected:
			String name2 = (String) msgObjectList.get(0);
			ConnectedClientsList.removeIf(ci -> ci.getName().equals(name2));
			try {
				client.sendToClient(null);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			notifyClientListUpdate();
			break;
		case UserConnect:
			Message resultMsg = (Message) db.userValidation(msgObjectList);
			ArrayList<Object> resultList = (ArrayList<Object>) resultMsg.getObject();
			String firstElement = (String) resultList.get(0);
			if (firstElement.equals("User is valid")) {
				User user = (User) resultList.get(1);
				switch (user.getUserType()) {
				case Customer:
					ConnectedCustomerList.add(
							new ClientInfo(user.getUsername(), client.getInetAddress().getHostAddress(), "Connected"));
					break;
				case CEO:
					ConnectedCeoList.add(
							new ClientInfo(user.getUsername(), client.getInetAddress().getHostAddress(), "Connected"));
					break;
				case Manager:
					ConnectedManagerList.add(
							new ClientInfo(user.getUsername(), client.getInetAddress().getHostAddress(), "Connected"));
					break;
				case Supplier:
					ConnectedSupplierList.add(
							new ClientInfo(user.getUsername(), client.getInetAddress().getHostAddress(), "Connected"));
					break;
				default:
					break;
				}
				notifyClientListUpdate();
			}
			try {
				client.sendToClient(resultMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case UserDisconnect:
			db.updateUserLogged(msgObjectList);
			User user = (User) msgObjectList.get(0);
			switch (user.getUserType()) {
			case Customer:
				ConnectedCustomerList.removeIf(ci -> ci.getName().equals(user.getUsername()));
				break;
			case CEO:
				ConnectedCeoList.removeIf(ci -> ci.getName().equals(user.getUsername()));
				break;
			case Manager:
				ConnectedManagerList.removeIf(ci -> ci.getName().equals(user.getUsername()));
				break;
			case Supplier:
				ConnectedSupplierList.removeIf(ci -> ci.getName().equals(user.getUsername()));
				break;
			default:
				break;
			}
			notifyClientListUpdate();
			try {
				client.sendToClient(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case GetCustomerOrders:
			Message msgOrder = new Message(MessageType.GetCustomerOrders,
					(ArrayList<Object>) db.getOrderHistory(msgObjectList));
			try {
				client.sendToClient(msgOrder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case GetResturants:
			try {
				client.sendToClient(db.getResturants(msgObjectList));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case UpdateOrderStatus:
			db.updateOrderStatusToReceived(msgObjectList);
			Message msgUpdateOrder = new Message(MessageType.UpdateOrderStatus, null);
			try {
				client.sendToClient(msgUpdateOrder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case GetResturantMenuItems:
			try {
				client.sendToClient(db.getResturantMenuItems((int) msgObjectList.get(0)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case AddOrderToDB:
			try {
				client.sendToClient(db.AddOrderToDb(msgObjectList));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	private void notifyClientListUpdate() {
		if (controller != null) {
			controller.updateTableView();
		}
	}

	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}

	public void closeDB() {
		if (db != null) {
			db.disconnectFromDB();
		} else {
			System.out.println("DB is not connected");
		}
	}

	public void startDB(String dbName, String user, String pass) {
		db = new DBControl();
		db.connectToDB(dbName, user, pass);
	}

	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

	synchronized protected void clientDisconnected(String[] messageArr) {
		System.out.println("Client " + messageArr[1] + " Disconnected from IP: " + messageArr[2]);
	}
}
