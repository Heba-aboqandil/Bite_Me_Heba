package logic;

import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;

public class Resturant implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int ResturantID;
	private String ResturantName;
	private Branch branch;
	private int MenuID;

	public Resturant(int resturantID, String resturantName, int branch, int menuID) {
		ResturantID = resturantID;
		ResturantName = resturantName;
		this.branch = Branch.fromValue(branch);
		MenuID = menuID;
	}

	public int getResturantID() {
		return ResturantID;
	}

	public void setResturantID(int resturantID) {
		ResturantID = resturantID;
	}

	public String getResturantName() {
		return ResturantName;
	}

	public void setResturantName(String resturantName) {
		ResturantName = resturantName;
	}

	public Branch getBranchID() {
		return branch;
	}

	public void setBranchID(Branch branch) {
		this.branch = branch;
	}

	public int getMenuID() {
		return MenuID;
	}

	public void setMenuID(int menuID) {
		MenuID = menuID;
	}

	public SimpleStringProperty branchProperty() {
		return new SimpleStringProperty(branch.name());
	}
}
