package logic;

import java.io.Serializable;

public abstract class Item implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int ItemID;
	private String ItemName;
	private Category CategoryName;
	private String Description;
	private float Cost;

	public Item(int itemID, String itemName, Category categoryName, String description, float cost) {
		ItemID = itemID;
		ItemName = itemName;
		CategoryName = categoryName;
		Description = description;
		Cost = cost;
	}

	public int getItemID() {
		return ItemID;
	}

	public void setItemID(int itemID) {
		ItemID = itemID;
	}

	public String getItemName() {
		return ItemName;
	}

	public void setItemName(String itemName) {
		ItemName = itemName;
	}

	public Category getCategoryName() {
		return CategoryName;
	}

	public void setCategoryName(Category categoryName) {
		CategoryName = categoryName;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public float getCost() {
		return Cost;
	}

	public void setCost(float cost) {
		Cost = cost;
	}
}
