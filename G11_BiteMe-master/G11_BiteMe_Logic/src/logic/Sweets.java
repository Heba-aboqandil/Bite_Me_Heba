package logic;

import java.io.Serializable;
import java.util.ArrayList;

public class Sweets extends Item implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> ingredients;

	public Sweets(int itemID, String itemName, Category categoryName, String description, float cost,
			ArrayList<String> ingredients) {
		super(itemID, itemName, categoryName, description, cost);
		this.ingredients = ingredients;
	}

	public ArrayList<String> getSweetsDescription() {
		return ingredients;
	}

	public void setSweetsDescription(ArrayList<String> ingredients) {
		this.ingredients = ingredients;
	}
}