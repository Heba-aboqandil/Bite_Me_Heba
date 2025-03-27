package logic;

import java.io.Serializable;

public class MainMeal extends Item implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String cookMethod;
	private Doneness doneness;

	public MainMeal(int itemID, String itemName, Category categoryName, String description, float cost,
			String cookMethod, Doneness doneness) {
		super(itemID, itemName, categoryName, description, cost);
		this.cookMethod = cookMethod;
		this.doneness = doneness;
	}

	public String getCookMethod() {
		return cookMethod;
	}

	public void setCookMethod(String cookMethod) {
		this.cookMethod = cookMethod;
	}

	public Doneness getDoneness() {
		return doneness;
	}

	public void setDoneness(Doneness doneness) {
		this.doneness = doneness;
	}
}