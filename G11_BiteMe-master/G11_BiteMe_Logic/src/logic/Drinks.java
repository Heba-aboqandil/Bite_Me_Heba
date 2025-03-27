package logic;

import java.io.Serializable;

public class Drinks extends Item implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Size size;

    public Drinks(int itemID, String itemName, Category categoryName, String description, float cost, Size size) {
        super(itemID, itemName, categoryName, description, cost);
        this.size = size;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }
}