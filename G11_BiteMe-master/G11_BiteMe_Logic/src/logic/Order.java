package logic;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Order implements Serializable {

	private static final long serialVersionUID = 1L;
	private static int nextOrderID = 2;
	private int OrderID;
	private int UserID;
	private LocalDate DateOfOrder;
	private String TimeOfOrder;
	private int ResturantID;
	private String PickUpType;
	private String Location;
	private String Status;
	private String RequestedTimeOfDelivery;
	private LocalDate RequestedDateOfDelivery;
	private String ETA;
	private String duration;
	private float Cost;

	public Order(int OrderID, int UserID, LocalDate DateOfOrder, String TimeOfOrder, int ResturantID, String PickUpType,
			String Location, String Status, String RequestedTimeOfDelivery, LocalDate RequestedDateOfDelivery,
			String ETA, String duration, float Cost) {
		this.OrderID = OrderID;
		this.UserID = UserID;
		this.DateOfOrder = DateOfOrder;
		this.TimeOfOrder = TimeOfOrder;
		this.ResturantID = ResturantID;
		this.PickUpType = PickUpType;
		this.Location = Location;
		this.Status = Status;
		this.RequestedTimeOfDelivery = RequestedTimeOfDelivery;
		this.RequestedDateOfDelivery = RequestedDateOfDelivery;
		this.ETA = ETA;
		this.duration = duration;
		this.Cost = Cost;
	}

	public int getOrderID() {
		return OrderID;
	}

	public void setOrderID(int orderID) {
		OrderID = orderID;
	}

	public int getUserID() {
		return UserID;
	}

	public void setUserID(int userID) {
		UserID = userID;
	}

	public LocalDate getDateOfOrder() {
		return DateOfOrder;
	}

	public void setDateOfOrder(LocalDate dateOfOrder) {
		DateOfOrder = dateOfOrder;
	}

	public String getTimeOfOrder() {
		return TimeOfOrder;
	}

	public void setTimeOfOrder(String timeOfOrder) {
		TimeOfOrder = timeOfOrder;
	}

	public int getResturantID() {
		return ResturantID;
	}

	public void setResturantID(int resturantID) {
		ResturantID = resturantID;
	}

	public String getPickUpType() {
		return PickUpType;
	}

	public void setPickUpType(String pickUpType) {
		PickUpType = pickUpType;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getRequestedTimeOfDelivery() {
		return RequestedTimeOfDelivery;
	}

	public void setRequestedTimeOfDelivery(String requestedTimeOfDelivery) {
		RequestedTimeOfDelivery = requestedTimeOfDelivery;
	}

	public LocalDate getRequestedDateOfDelivery() {
		return RequestedDateOfDelivery;
	}

	public void setRequestedDateOfDelivery(LocalDate requestedDateOfDelivery) {
		RequestedDateOfDelivery = requestedDateOfDelivery;
	}

	public String getETA() {
		return ETA;
	}

	public void setETA(String eTA) {
		ETA = eTA;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public float getCost() {
		return Cost;
	}

	public void setCost(float cost) {
		Cost = cost;
	}

	public static synchronized int getNextOrderID() {
		return nextOrderID++;
	}

	public LocalDateTime getOrderDateTime() {
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime time = LocalTime.parse(TimeOfOrder, timeFormatter);
		return LocalDateTime.of(DateOfOrder, time);
	}

	public LocalDateTime getRequestedDeliveryDateTime() {
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime time = LocalTime.parse(RequestedTimeOfDelivery, timeFormatter);
		return LocalDateTime.of(RequestedDateOfDelivery, time);
	}

	public boolean isOrderWithinTwoHours() {
		LocalDateTime orderDateTime = getOrderDateTime();
		LocalDateTime requestedDeliveryDateTime = getRequestedDeliveryDateTime();
		Duration duration = Duration.between(orderDateTime, requestedDeliveryDateTime);
		return duration.toHours() <= 2;
	}

	public void calculateDurationFromNow() {
		LocalDateTime requestedDateTime = getRequestedDeliveryDateTime();
		LocalDateTime currentDateTime = LocalDateTime.now();
		// Calculate the duration between current time and requested time
		Duration duration = Duration.between(requestedDateTime, currentDateTime);
		long diffMinutes = duration.toMinutes() % 60;
		long diffHours = duration.toHours() % 24;
		long diffDays = duration.toDays();

		// Format the duration as a string
		String durationString = String.format("%d:%02d:%02d", diffDays, diffHours, diffMinutes);

		// Set the Duration field
		this.duration = durationString;
	}

	public boolean customerEligibleForCoupon() {
		LocalDateTime requestedDateTime = getRequestedDeliveryDateTime();
		LocalDateTime currentDateTime = LocalDateTime.now();

		// Calculate the duration between current time and requested time
		Duration duration = Duration.between(requestedDateTime, currentDateTime);
		long diffMinutes = duration.toMinutes();
		long diffHours = duration.toHours();
		// Check if order is within 2 hours
		if (isOrderWithinTwoHours()) {
			// Order is within 2 hours, check if duration is longer than an hour
			return diffHours > 1;
		} else {
			// Order is more than 2 hours away, check if duration is more than 20 minutes
			return diffMinutes > 20;
		}
	}
}
