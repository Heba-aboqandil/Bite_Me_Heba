package Server;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.UserType;
import logic.User;
import logic.Category;
import logic.Doneness;
import logic.Drinks;
import logic.Item;
import logic.MainMeal;
import logic.MenuItem;
import logic.Message;
import logic.MessageType;
import logic.Order;
import logic.OrderItem;
import logic.OrderItemDetail;
import logic.Resturant;
import logic.Salad;
import logic.Size;
import logic.Sweets;
import logic.User;

public class DBControl {
	public Connection conn;

	public void connectToDB(String dbName, String user, String pass) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
		}

		try {
			conn = DriverManager.getConnection(dbName + "?serverTimezone=IST", user, pass);
			System.out.println("SQL connection succeed");
		} catch (SQLException ex) {/* handle any errors */
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
	}

	public void disconnectFromDB() {
		if (conn != null) {
			try {
				conn.close();
				System.out.println("SQL connection closed successfully");
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		} else {
			System.out.println("Connection is already closed or was never opened");
		}
	}

	public static void updateOrder(Connection con1, List<String> inputList) {
		try {
			PreparedStatement ps = con1
					.prepareStatement("UPDATE `Order` SET Total_price = ?, Order_address = ? WHERE Order_number = ?");
			ps.setString(1, inputList.get(1));
			ps.setString(2, inputList.get(2));
			ps.setString(3, inputList.get(0));
			ps.executeUpdate();
			System.out.println("Order was updated in DB\n");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static ArrayList<String> getOrders(Connection con1) {
		ArrayList<String> resultList = new ArrayList<>();
		try {
			Statement st = con1.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM `Order`");
			while (rs.next()) {
				resultList.add(rs.getString("Restaurant"));
				resultList.add(rs.getString("Order_number"));
				resultList.add(rs.getString("Total_price"));
				resultList.add(rs.getString("Order_list_number"));
				resultList.add(rs.getString("Order_address"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultList;
	}

	public Object userValidation(ArrayList<Object> msgObjectList) {
		String username = (String) msgObjectList.get(0);
		String password = (String) msgObjectList.get(1);
		String userType = (String) msgObjectList.get(2);
		String query = "SELECT * FROM user WHERE Username = ? AND Password = ? AND UserType = ?";
		ArrayList<Object> returnList = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setString(3, userType);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
							rs.getString(5), logic.UserType.getUserType(rs.getString(6)), rs.getInt(7), rs.getInt(8),
							rs.getString(9), rs.getString(10), rs.getInt(11), rs.getInt(12));

					if (user.getIsLogged() == 1) {
						returnList.add("User is already logged in");
						return new Message(MessageType.UserConnect, returnList);
					}
					ArrayList<Object> userIsLoggedList = new ArrayList<>();
					userIsLoggedList.add(user);
					userIsLoggedList.add(1);
					updateUserLogged(userIsLoggedList);
					returnList.add("User is valid");
					returnList.add(user);
					return new Message(MessageType.UserConnect, returnList);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		returnList.add("User not found");
		return new Message(MessageType.UserConnect, returnList);
	}

	public void updateUserLogged(ArrayList<Object> msgObjectList) {
		String query = "UPDATE user SET isLogged = ? WHERE UserID = ?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, (Integer) msgObjectList.get(1));
			ps.setInt(2, ((User) msgObjectList.get(0)).getUserID());
			ps.executeUpdate();
			System.out.println("User's logged status updated in DB");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Object getOrderHistory(ArrayList<Object> msgObjectList) {
		ArrayList<Object> resultList = new ArrayList<>();
		int userid = (int) msgObjectList.get(0);
		String str = "SELECT * FROM `Order` WHERE UserID=?";
		try {
			PreparedStatement ps = conn.prepareStatement(str);
			ps.setInt(1, userid);
			try {
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					// Fetch dates correctly from the ResultSet
					Order order = new Order(rs.getInt("OrderID"), rs.getInt("UserID"),
							rs.getDate("DateOfOrder").toLocalDate().plusDays(1), rs.getString("TimeOfOrder"),
							rs.getInt("RestaurantID"), rs.getString("PickUpType"), rs.getString("Location"),
							rs.getString("Status"), rs.getString("RequestedTimeOfDelivery"),
							rs.getDate("RequestedDateOfDelivery").toLocalDate().plusDays(1), rs.getString("ETA"),
							rs.getString("Duration"), rs.getFloat("Cost"));
					resultList.add(order);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultList;
	}

	public Object getResturants(ArrayList<Object> msgObjectList) {
		String query = "SELECT * FROM restaurant";
		ArrayList<Object> resultList = new ArrayList<>();
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Resturant res = new Resturant(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4));
					resultList.add(res);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return new Message(MessageType.GetResturants, resultList);
	}

	public void updateOrderStatusToReceived(ArrayList<Object> inputList) {
		// Retrieve the order ID from the input list
		int orderID = ((Order) inputList.get(0)).getOrderID();
		String duration = ((Order) inputList.get(0)).getDuration();

		// SQL query to update the status of the order
		String updateQuery = "UPDATE `Order` SET Status = 'Received', Duration = ? WHERE OrderID = ?";

		try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
			ps.setString(1, duration);
			ps.setInt(2, orderID);
			ps.executeUpdate();
			System.out.println("Order status updated to 'Received' and duration set in DB\n");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		boolean eligilbeForCoupon = ((Order) inputList.get(0)).customerEligibleForCoupon();
		if (eligilbeForCoupon)
			updateCustomerCoupons(((Order) inputList.get(0)).getUserID());
	}

	public void updateCustomerCoupons(int customerID) {
		// SQL query to update the customer's coupons
		String updateCouponsQuery = "UPDATE `user` SET amountOfCopuns = amountOfCopuns + 1 WHERE UserID = ?";

		try (PreparedStatement ps = conn.prepareStatement(updateCouponsQuery)) {
			ps.setInt(1, customerID);
			ps.executeUpdate();
			System.out.println("Customer's amount of coupons updated in DB\n");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Object getResturantMenuItems(int menuID) {
		String query = "SELECT i.ItemID, i.ItemName, i.Description, i.Cost, i.CategoryName, "
				+ "s.Ingredients AS saladIngredients, s.Size AS saladSize, "
				+ "m.CookMethod AS mainMealCookMethod, m.Doneness AS mainMealDoneness, "
				+ "sw.Ingredients AS sweetsIngredients, d.Size AS drinkSize " + "FROM item i "
				+ "JOIN itemmenu im ON i.ItemID = im.ItemID " + "LEFT JOIN salad s ON i.ItemID = s.ItemID "
				+ "LEFT JOIN mainmeal m ON i.ItemID = m.ItemID " + "LEFT JOIN sweets sw ON i.ItemID = sw.ItemID "
				+ "LEFT JOIN drinks d ON i.ItemID = d.ItemID " + "WHERE im.MenuID = ?";

		ArrayList<Item> menuItems = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, menuID);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int itemID = rs.getInt("ItemID");
					String itemName = rs.getString("ItemName");
					String itemDescription = rs.getString("Description");
					float itemCost = rs.getFloat("Cost");
					Category category = Category.valueOf(rs.getString("CategoryName"));

					Item item = null;

					switch (category) {
					case Salad:
						ArrayList<String> saladIngredientsList = new ArrayList<>();
						String[] saladIngredientsArray = rs.getString("saladIngredients").split(", ");
						for (String ingredient : saladIngredientsArray) {
							saladIngredientsList.add(ingredient);
						}
						String saladSize = rs.getString("saladSize");
						item = new Salad(itemID, itemName, category, itemDescription, itemCost, saladIngredientsList,
								saladSize);
						break;
					case MainMeal:
						String mainMealCookMethod = rs.getString("mainMealCookMethod");
						Doneness mainMealDoneness = Doneness.valueOf(rs.getString("mainMealDoneness"));
						item = new MainMeal(itemID, itemName, category, itemDescription, itemCost, mainMealCookMethod,
								mainMealDoneness);
						break;
					case Sweets:
						ArrayList<String> sweetsIngredientsList = new ArrayList<>();
						String[] sweetsIngredientsArray = rs.getString("sweetsIngredients").split(", ");
						for (String ingredient : sweetsIngredientsArray) {
							sweetsIngredientsList.add(ingredient);
						}
						item = new Sweets(itemID, itemName, category, itemDescription, itemCost, sweetsIngredientsList);
						break;
					case Drinks:
						Size drinkSize = Size.valueOf(rs.getString("drinkSize"));
						item = new Drinks(itemID, itemName, category, itemDescription, itemCost, drinkSize);
						break;
					}

					if (item != null) {
						menuItems.add(item);
						System.out.println("Added item: " + itemName);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		MenuItem menuItem = new MenuItem(menuID, menuItems);
		ArrayList<Object> resultList = new ArrayList<>();
		resultList.add(menuItem);
		return new Message(MessageType.GetResturantMenuItems, resultList);
	}

	public Object AddOrderToDb(ArrayList<Object> inputList) {
		ArrayList<Object> resultList = new ArrayList<>();
		// Assuming inputList contains an OrderItem object
		if (inputList == null || inputList.isEmpty() || !(inputList.get(0) instanceof OrderItem)) {
			resultList.add(false);
			return new Message(MessageType.AddOrderToDB, resultList);
		}

		OrderItem orderItem = (OrderItem) inputList.get(0);
		Order order = orderItem.getOrder();
		ArrayList<OrderItemDetail> orderItemDetails = (ArrayList<OrderItemDetail>) orderItem.getOrderItemDetails();

		String orderQuery = "INSERT INTO `order` (UserID, DateOfOrder, TimeOfOrder, RestaurantID, PickUpType, Location, Status, RequestedTimeOfDelivery, RequestedDateOfDelivery, ETA, Duration, Cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
			orderStmt.setInt(1, order.getUserID());
			orderStmt.setDate(2, java.sql.Date.valueOf(order.getDateOfOrder()));
			orderStmt.setString(3, order.getTimeOfOrder());
			orderStmt.setInt(4, order.getResturantID());
			orderStmt.setString(5, order.getPickUpType());
			orderStmt.setString(6, order.getLocation());
			orderStmt.setString(7, order.getStatus());
			orderStmt.setString(8, order.getRequestedTimeOfDelivery());
			orderStmt.setDate(9, java.sql.Date.valueOf(order.getRequestedDateOfDelivery()));
			System.out.println(order.getRequestedDateOfDelivery());
			if (order.getETA() != null && !order.getETA().isEmpty()) {
				orderStmt.setString(10, order.getETA());
			} else {
				orderStmt.setNull(10, java.sql.Types.VARCHAR);
			}

			if (order.getDuration() != null && !order.getDuration().isEmpty()) {
				orderStmt.setString(11, order.getDuration());
			} else {
				orderStmt.setNull(11, java.sql.Types.VARCHAR);
			}

			orderStmt.setFloat(12, order.getCost());

			orderStmt.executeUpdate();

			// Retrieve the generated OrderID
			try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					order.setOrderID(generatedKeys.getInt(1));
				} else {
					resultList.add(false);
					return new Message(MessageType.AddOrderToDB, resultList);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			resultList.add(false);
			return new Message(MessageType.AddOrderToDB, resultList);
		}

		// Insert order items into orderitem table
		String orderItemQuery = "INSERT INTO orderitem (OrderID, ItemID, Quantity, Notes) VALUES (?, ?, ?, ?)";

		try (PreparedStatement orderItemStmt = conn.prepareStatement(orderItemQuery)) {
			for (OrderItemDetail oid : orderItemDetails) {
				orderItemStmt.setInt(1, order.getOrderID());
				orderItemStmt.setInt(2, oid.getItem().getItemID());
				orderItemStmt.setInt(3, oid.getQuantity());
				orderItemStmt.setString(4, oid.getNotes());

				orderItemStmt.addBatch();
			}
			orderItemStmt.executeBatch();

		} catch (SQLException e) {
			e.printStackTrace();
			resultList.add(false);
			return new Message(MessageType.AddOrderToDB, resultList);
		}

		// Check and update the user's coupons if they have more than 1
		String checkCouponQuery = "SELECT amountOfCopuns FROM `user` WHERE UserID = ?";
		String updateCouponQuery = "UPDATE `user` SET amountOfCopuns = amountOfCopuns - 1 WHERE UserID = ?";

		try (PreparedStatement checkCouponStmt = conn.prepareStatement(checkCouponQuery)) {
			checkCouponStmt.setInt(1, order.getUserID());
			try (ResultSet rs = checkCouponStmt.executeQuery()) {
				if (rs.next()) {
					int amountOfCoupons = rs.getInt("amountOfCopuns");
					System.out.println("Current coupons for UserID " + order.getUserID() + ": " + amountOfCoupons);
					if (amountOfCoupons > 0) {
						try (PreparedStatement updateCouponStmt = conn.prepareStatement(updateCouponQuery)) {
							updateCouponStmt.setInt(1, order.getUserID());
							updateCouponStmt.executeUpdate();
							System.out.println("Coupon count decreased by 1 for UserID: " + order.getUserID());
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			resultList.add(false);
			return new Message(MessageType.AddOrderToDB, resultList);
		}

		resultList.add(true);
		resultList.add(order.getOrderID());
		return new Message(MessageType.AddOrderToDB, resultList);
	}

}
