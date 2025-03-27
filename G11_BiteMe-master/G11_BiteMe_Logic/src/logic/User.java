package logic;

import java.io.Serializable;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	private String firstName;
	private String lastName;
	private int UserID;
	private String Username;
	private String Password;
	private UserType userType;
	private int branchID;
	private int phone;
	private String email;
	private String creditCard;
	private int isLogged;
	private int amountOfCopunts;
	public User(int userID,String firstName, String lastName, String username, String password, UserType userType,
			int branchID, int phone, String email, String creditCard, int isLogged,int amountOfCopunts) {
		this.firstName = firstName;
		this.lastName = lastName;
		UserID = userID;
		Username = username;
		Password = password;
		this.userType = userType;
		this.branchID = branchID;
		this.phone = phone;
		this.email = email;
		this.creditCard = creditCard;
		this.isLogged=isLogged;
		this.amountOfCopunts=amountOfCopunts;
	}

	public int getAmountOfCopunts() {
		return amountOfCopunts;
	}

	public void setAmountOfCopunts(int amountOfCopunts) {
		this.amountOfCopunts = amountOfCopunts;
	}

	public int getIsLogged() {
		return isLogged;
	}

	public void setIsLogged(int isLogged) {
		this.isLogged = isLogged;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getUserID() {
		return UserID;
	}

	public void setUserID(int userID) {
		UserID = userID;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public int getBranchID() {
		return branchID;
	}

	public void setBranchID(int branchID) {
		this.branchID = branchID;
	}

	public int getPhone() {
		return phone;
	}

	public void setPhone(int phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}

}
