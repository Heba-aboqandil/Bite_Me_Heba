package logic;

import java.io.Serializable;
import java.util.ArrayList;

public class MenuItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int MenuID;
	private ArrayList<Item> menuItems=new ArrayList<>();

	public MenuItem(int menuID, ArrayList<Item> menuItems) {
		MenuID = menuID;
		this.menuItems = menuItems;
	}

	public int getMenuID() {
		return MenuID;
	}

	public void setMenuID(int menuID) {
		MenuID = menuID;
	}

	public ArrayList<Item> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(ArrayList<Item> menuItems) {
		this.menuItems = menuItems;
	}

	public void addItem(Item item) {
		menuItems.add(item);
	}
}
