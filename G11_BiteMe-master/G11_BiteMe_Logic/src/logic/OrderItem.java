package logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Order order;
    private List<OrderItemDetail> orderItemDetails = new ArrayList<>();

    public OrderItem(Order order) {
        this.order = order;
    }

    public void setOrderItemDetails(List<OrderItemDetail> orderItemDetails) {
		this.orderItemDetails = orderItemDetails;
	}

	public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItemDetail> getOrderItemDetails() {
        return orderItemDetails;
    }
    
    public void addItem(Item item, String notes, int quantity, float cost) {
        orderItemDetails.add(new OrderItemDetail(item, notes, quantity, cost));
    }
}
