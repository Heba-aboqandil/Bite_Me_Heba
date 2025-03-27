package gui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import logic.*;
import logic.MenuItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.ChatClient;

public class PickItemsController implements Initializable, IControl {

	@FXML
	private Button btnExit;

	@FXML
	private TableView<Item> MenuitemsTable;

	@FXML
	private TableColumn<Item, String> ItemNameCol;

	@FXML
	private TableColumn<Item, String> ItemDescriptionCol;

	@FXML
	private TableColumn<Item, Float> ItemCostCol;

	@FXML
	private Button btnFinishOrder;

	@FXML
	private ComboBox<Size> SizeCB;

	@FXML
	private ComboBox<Doneness> DonenessCB;

	@FXML
	private TableView<IngredientEntry> IngredientItemsTable;

	@FXML
	private TableColumn<IngredientEntry, String> IngredientCol;

	@FXML
	private TableColumn<IngredientEntry, Boolean> IngredientCheck;

	@FXML
	private TableView<OrderItemEntry> tableviewCart;

	@FXML
	private TableColumn<OrderItemEntry, String> cartItemNameCol;

	@FXML
	private TableColumn<OrderItemEntry, String> cartItemNotesCol;

	@FXML
	private TableColumn<OrderItemEntry, Integer> CartItemQuantityCol;

	@FXML
	private Text totalCosttxt;

	@FXML
	private Button btnAddToCart;

	@FXML
	private Button btnRemoveFromCart;

	private MenuItem menuItem;
	private ObservableList<OrderItemEntry> cartItems = FXCollections.observableArrayList();

	@FXML
	public void getBtnRemoveFromCart(ActionEvent event) {
		OrderItemEntry selectedItem = tableviewCart.getSelectionModel().getSelectedItem();
		if (selectedItem == null) {
			showAlert("No Item Selected", "Please select an item to remove from the cart.");
			return;
		}

		if (selectedItem.getQuantity() > 1) {
			// Decrease the quantity by 1
			selectedItem.setQuantity(selectedItem.getQuantity() - 1);

			// Update ChatClient.userCart accordingly
			for (OrderItemDetail orderItemDetail : ChatClient.userCart) {
				if (orderItemDetail.getItem().getItemName().equals(selectedItem.getName())
						&& orderItemDetail.getNotes().equals(selectedItem.getNotes())) {
					orderItemDetail.setQuantity(orderItemDetail.getQuantity() - 1);
					break;
				}
			}
		} else {
			// Remove from cartItems
			cartItems.remove(selectedItem);

			// Remove from ChatClient.userCart
			if (ChatClient.userCart != null) {
				ChatClient.userCart.removeIf(
						orderItemDetail -> orderItemDetail.getItem().getItemName().equals(selectedItem.getName())
								&& orderItemDetail.getNotes().equals(selectedItem.getNotes()));
			}
		}

		// Update the TableView and total cost
		tableviewCart.setItems(cartItems);
		updateTotalCost();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Assuming ChatClient.menuItems is populated with data
		menuItem = ChatClient.menuItems;
		new OrderItem(new Order(0, 0, null, null, 0, null, null, null, null, null, null, null, 0));

		// Setup MenuitemsTable
		ItemNameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
		ItemDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
		ItemCostCol.setCellValueFactory(new PropertyValueFactory<>("cost"));

		ObservableList<Item> items = FXCollections.observableArrayList(menuItem.getMenuItems());
		MenuitemsTable.setItems(items);

		// Setup IngredientItemsTable
		IngredientCol.setCellValueFactory(cellData -> cellData.getValue().ingredientProperty());
		IngredientCheck.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
		IngredientCheck.setCellFactory(column -> {
			CheckBoxTableCell<IngredientEntry, Boolean> cell = new CheckBoxTableCell<>();
			cell.setEditable(true);
			return cell;
		});

		// Enable editing for the table
		IngredientItemsTable.setEditable(true);

		// Add listener to update ingredients and other fields based on selection
		MenuitemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				updateDetailFields(newSelection);
			} else {
				clearDetailFields();
			}
		});

		// Setup Cart TableView
		cartItemNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		cartItemNotesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));
		CartItemQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		// Initialize cartItems with ChatClient.userCart if it's not null
		if (ChatClient.userCart != null) {
			for (OrderItemDetail orderItemDetail : ChatClient.userCart) {
				cartItems.add(new OrderItemEntry(orderItemDetail.getItem().getItemName(), orderItemDetail.getNotes(),
						orderItemDetail.getQuantity(), orderItemDetail.getCost()));
			}
		}

		tableviewCart.setItems(cartItems);
		updateTotalCost();
	}

	@FXML
	private void getAddToCartBtn(ActionEvent event) {
		Item selectedItem = MenuitemsTable.getSelectionModel().getSelectedItem();
		if (selectedItem == null) {
			showAlert("No Item Selected", "Please select an item to add to the cart.");
			return;
		}

		if (selectedItem instanceof Salad || selectedItem instanceof Sweets) {
			if (!validateIngredients()) {
				showAlert("No Ingredients Selected", "Please select at least one ingredient.");
				return;
			}
		}

		String notes = generateNotes(selectedItem);
		int quantity = 1; // Default quantity

		boolean itemExists = false;
		for (OrderItemEntry entry : cartItems) {
			if (entry.getName().equals(selectedItem.getItemName()) && entry.getNotes().equals(notes)) {
				entry.setQuantity(entry.getQuantity() + quantity);
				itemExists = true;
				break;
			}

		}

		if (!itemExists) {
			cartItems.add(new OrderItemEntry(selectedItem.getItemName(), notes, quantity, selectedItem.getCost()));
		}

		// Save item to ChatClient.userCart
		if (ChatClient.userCart == null) {
			ChatClient.userCart = new ArrayList<>();
		}

		// Check if the item already exists in the userCart
		boolean userCartItemExists = false;
		for (OrderItemDetail orderItemDetail : ChatClient.userCart) {
			if (orderItemDetail.getItem().getItemName().equals(selectedItem.getItemName())
					&& orderItemDetail.getNotes().equals(notes)) {
				orderItemDetail.setQuantity(orderItemDetail.getQuantity() + quantity);
				userCartItemExists = true;
				break;
			}
		}

		if (!userCartItemExists) {
			ChatClient.userCart.add(new OrderItemDetail(selectedItem, notes, quantity, selectedItem.getCost()));
		}
		updateTotalCost();
	}

	private boolean validateIngredients() {
		for (IngredientEntry entry : IngredientItemsTable.getItems()) {
			if (entry.isSelected()) {
				return true;
			}
		}
		return false;
	}

	private String generateNotes(Item item) {
		StringBuilder notes = new StringBuilder();

		if (SizeCB.getValue() != null) {
			notes.append("Size: ").append(SizeCB.getValue().name()).append(", ");
		}

		if (DonenessCB.getValue() != null) {
			notes.append("Doneness: ").append(DonenessCB.getValue().name()).append(", ");
		}

		IngredientItemsTable.getItems().forEach(ingredientEntry -> {
			if (!ingredientEntry.isSelected()) {
				notes.append("No ").append(ingredientEntry.getIngredient()).append(", ");
			}
		});

		if (notes.length() > 0) {
			notes.setLength(notes.length() - 2); // Remove trailing comma and space
		}

		return notes.toString();
	}

	private void updateTotalCost() {
		float totalCost = 0;
		for (OrderItemEntry entry : cartItems) {
			totalCost += entry.getQuantity() * entry.getItemCost();
		}
		totalCosttxt.setText(String.format(" %.2f$", totalCost));
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void updateDetailFields(Item item) {
		// Clear previous selections
		SizeCB.getSelectionModel().clearSelection();
		DonenessCB.getSelectionModel().clearSelection();
		IngredientItemsTable.setItems(FXCollections.observableArrayList());

		// Clear size and doneness combo boxes if item doesn't have these attributes
		SizeCB.setItems(FXCollections.observableArrayList());
		DonenessCB.setItems(FXCollections.observableArrayList());

		// Update fields based on item type
		if (item instanceof Salad) {
			Salad salad = (Salad) item;
			SizeCB.setItems(FXCollections.observableArrayList(Size.values()));
			SizeCB.getSelectionModel().select(Size.valueOf(salad.getSize()));
			ObservableList<IngredientEntry> ingredients = FXCollections.observableArrayList();
			for (String ingredient : salad.getIngredients()) {
				ingredients.add(new IngredientEntry(ingredient, true));
			}
			IngredientItemsTable.setItems(ingredients);
		} else if (item instanceof Drinks) {
			Drinks drink = (Drinks) item;
			SizeCB.setItems(FXCollections.observableArrayList(Size.values()));
			SizeCB.getSelectionModel().select(drink.getSize());
		} else if (item instanceof MainMeal) {
			MainMeal mainMeal = (MainMeal) item;
			DonenessCB.setItems(FXCollections.observableArrayList(Doneness.values()));
			DonenessCB.getSelectionModel().select(mainMeal.getDoneness());
		} else if (item instanceof Sweets) {
			Sweets sweets = (Sweets) item;
			ObservableList<IngredientEntry> ingredients = FXCollections.observableArrayList();
			for (String ingredient : sweets.getSweetsDescription()) {
				ingredients.add(new IngredientEntry(ingredient, true));
			}
			IngredientItemsTable.setItems(ingredients);
		}
	}

	private void clearDetailFields() {
		SizeCB.getSelectionModel().clearSelection();
		SizeCB.setItems(FXCollections.observableArrayList());
		DonenessCB.getSelectionModel().clearSelection();
		DonenessCB.setItems(FXCollections.observableArrayList());
		IngredientItemsTable.setItems(FXCollections.observableArrayList());
	}

	@Override
	public void getBackBtn(ActionEvent event) throws Exception {
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		PickRestuarntController prc = new PickRestuarntController();
		prc.start(new Stage());
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/PickItems.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/PickItems.css").toExternalForm());
		primaryStage.setTitle("OrderFood Page");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// Inner class to represent an ingredient with a checkbox
	public static class IngredientEntry {
		private final SimpleStringProperty ingredient;
		private final SimpleBooleanProperty selected;

		public IngredientEntry(String ingredient, boolean selected) {
			this.ingredient = new SimpleStringProperty(ingredient);
			this.selected = new SimpleBooleanProperty(selected);
		}

		public String getIngredient() {
			return ingredient.get();
		}

		public SimpleStringProperty ingredientProperty() {
			return ingredient;
		}

		public boolean isSelected() {
			return selected.get();
		}

		public SimpleBooleanProperty selectedProperty() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected.set(selected);
		}
	}

	// Inner class to represent an order item in the cart
	public static class OrderItemEntry {
		private final SimpleStringProperty name;
		private final SimpleStringProperty notes;
		private final SimpleIntegerProperty quantity;
		private final float itemCost;

		public OrderItemEntry(String name, String notes, int quantity, float itemCost) {
			this.name = new SimpleStringProperty(name);
			this.notes = new SimpleStringProperty(notes);
			this.quantity = new SimpleIntegerProperty(quantity);
			this.itemCost = itemCost;
		}

		public String getName() {
			return name.get();
		}

		public SimpleStringProperty nameProperty() {
			return name;
		}

		public String getNotes() {
			return notes.get();
		}

		public SimpleStringProperty notesProperty() {
			return notes;
		}

		public int getQuantity() {
			return quantity.get();
		}

		public SimpleIntegerProperty quantityProperty() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity.set(quantity);
		}

		public float getItemCost() {
			return itemCost;
		}
	}

	@FXML
	void getFinishOrderBtn(ActionEvent event) throws Exception {
		if (ChatClient.userCart == null || ChatClient.userCart.size() == 0) {
			showAlert("No Items In Cart", "Please add at least one item to cart.");
			return;
		}
		((Node) event.getSource()).getScene().getWindow().hide(); // hiding primary window
		PickPickUpTypeController pputc = new PickPickUpTypeController();
		pputc.start(new Stage());
	}
}
