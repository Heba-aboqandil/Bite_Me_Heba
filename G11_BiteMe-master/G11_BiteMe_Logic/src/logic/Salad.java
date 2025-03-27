package logic;

import java.io.Serializable;
import java.util.ArrayList;

public class Salad extends Item implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<String> ingredients;
    private String size;

    public Salad(int itemID, String itemName, Category categoryName, String description, float cost, ArrayList<String> ingredients, String size) {
        super(itemID, itemName, categoryName, description, cost);
        this.ingredients = ingredients;
        this.size = size;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}