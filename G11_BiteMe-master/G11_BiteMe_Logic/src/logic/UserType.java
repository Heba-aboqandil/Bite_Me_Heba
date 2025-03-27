package logic;

public enum UserType {
	Customer, Manager, CEO, Supplier;

	public static UserType getUserType(String type) {
		if (type == null)
			return null;
		switch (type) {
		case "Customer":
			return UserType.Customer;
		case "Manager":
			return UserType.Manager;
		case "CEO":
			return UserType.CEO;
		case "Supplier":
			return UserType.Supplier;
		default:
			return null;
		}
	}

	public static String getStringType(UserType type) {
		if (type.equals(Customer))
			return "Customer";
		if (type.equals(Manager))
			return "Manager";
		if(type.equals(Supplier))
			return "Supplier";
		return "CEO";
	}
}
