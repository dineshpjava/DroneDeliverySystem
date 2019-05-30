package com.walmart.dds;

public class CustomerOrder {

	private String orderId;
	private String customerAddress;
	private String orderPlacedTime;
	private String orderFulfillTime;
	private int orderTimeInSecs;
	private int deliveryDuration;
	private int priority;
	private int feedback;

	public CustomerOrder(String orderId, String customerAddress, String orderPlacedTime) {
		super();
		this.orderId = orderId;
		this.customerAddress = customerAddress;
		this.orderPlacedTime = orderPlacedTime;
		this.orderTimeInSecs = Utility.getTimeInSecs(orderPlacedTime);
		this.deliveryDuration = Utility.getDeliveryDurationInSeconds(customerAddress);
	}

	public boolean hasLapsed(int currentTime) {
		int currentTimeInSecs = currentTime;
		if (orderTimeInSecs < currentTimeInSecs) {
			return true;
		}
		return false;
	}

	public void setOrderFulfillTime(int currentTime) {
		String fulfilledTime = Utility.formatToTime(currentTime);
		orderFulfillTime = fulfilledTime;
		if (currentTime - orderTimeInSecs <= 3600) {
			feedback = 1;
		} else if (currentTime - orderTimeInSecs <= 10800) {
			feedback = 0;
		} else {
			feedback = -1;
		}
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getOrderPlacedTime() {
		return orderPlacedTime;
	}

	public void setOrderPlacedTime(String orderPlacedTime) {
		this.orderPlacedTime = orderPlacedTime;
	}

	public String getOrderFulfillTime() {
		return orderFulfillTime;
	}

	public void setOrderFulfillTime(String orderFulfillTime) {
		this.orderFulfillTime = orderFulfillTime;
	}

	public int getOrderTimeInSecs() {
		return orderTimeInSecs;
	}

	public void setOrderTimeInSecs(int orderTimeInSecs) {
		this.orderTimeInSecs = orderTimeInSecs;
	}

	public int getDeliveryDuration() {
		return deliveryDuration;
	}

	public void setDeliveryDuration(int deliveryDuration) {
		this.deliveryDuration = deliveryDuration;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getFeedback() {
		return feedback;
	}

	public void setFeedback(int feedback) {
		this.feedback = feedback;
	}
}
