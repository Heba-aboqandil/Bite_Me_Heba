package logic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class OrderItemDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	private Item item;
	private String notes;
	private int quantity;
	private float cost;

	public OrderItemDetail(Item item, String notes, int quantity, float cost) {
		this.item = item;
		this.notes = notes;
		this.quantity = quantity;
		this.cost = cost;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public static float totalCost(ArrayList<OrderItemDetail> list) {
		float total = 0;
		for (OrderItemDetail oid : list) {
			total += oid.getCost()*oid.getQuantity();
		}
		// Round to 2 decimal places
		BigDecimal bd = new BigDecimal(Float.toString(total));
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		return bd.floatValue();
	}
}