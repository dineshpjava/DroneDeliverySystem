package com.walmart.dds;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class DroneScheduler {

	private ArrayList<CustomerOrder> priorityQueue = new ArrayList<CustomerOrder>();
	private ArrayList<CustomerOrder> ordersFulfilledList = new ArrayList<CustomerOrder>();
	private LinkedList<CustomerOrder> orderQueue;
	private String outputFilePath;
	private static final int START_TIME = 21600; // 6:00:00 am
	private static final int END_TIME = 79200; // 10:00:00 pm

	public DroneScheduler(LinkedList<CustomerOrder> orderQueue, String filePath) {
		this.orderQueue = orderQueue;
		this.outputFilePath = filePath;
	}

	public void scheduleAndDispatchDeliveries() {

		int totalOrders = orderQueue.size();
		int currentTime = START_TIME;
		int lapsedTime = 0;
		int promoters = 0, detractors = 0;

		// keep serving orders until end of day
		while (currentTime <= END_TIME) {

			// check to see if new orders can be added to schedulers queue
			addNewOrdersToSchedulerQueue(currentTime);

			if (priorityQueue.isEmpty() && orderQueue.isEmpty()) {
				break;
			}

			updatePrioritiesOfOrders(currentTime);

			CustomerOrder currentOrder = getNextScheduledOrder();

			dispatchOrder(currentOrder, currentTime);

			// update current time by adding the lapsed time to serve the current order
			lapsedTime = (2 * currentOrder.getDeliveryDuration()) * 60;
			currentTime += lapsedTime;

			// update NPS contributors
			if (currentOrder.getFeedback() == 1) {
				++promoters;
			} else if (currentOrder.getFeedback() == -1) {
				++detractors;
			}
		}

		// these orders can't be served as working hours are between 6 am to 10 pm
		if (!priorityQueue.isEmpty()) {
			detractors += priorityQueue.size();
		}
		// these orders can't be served as working hours are between 6 am to 10 pm
		if (!orderQueue.isEmpty()) {
			detractors += orderQueue.size();
		}

		float NPS = ((promoters - detractors) / (totalOrders * 1f)) * 100;

		saveResultsToFile(outputFilePath, NPS);
	}

	private void addNewOrdersToSchedulerQueue(int currentTime) {

		Iterator<CustomerOrder> iterator = orderQueue.iterator();
		while (iterator.hasNext()) {
			CustomerOrder customerOrder = (CustomerOrder) iterator.next();
			if (customerOrder.hasLapsed(currentTime)) {
				iterator.remove();
				priorityQueue.add(customerOrder);
			} else {
				break;
			}
		}

		// wait for new orders if there are no orders to serve
		if (priorityQueue.isEmpty() && !orderQueue.isEmpty()) {
			CustomerOrder order = orderQueue.removeFirst();
			currentTime = order.getOrderTimeInSecs() > currentTime ? order.getOrderTimeInSecs() : currentTime;
			priorityQueue.add(order);
		}
	}

	private void updatePrioritiesOfOrders(int currentTime) {
		for (int i = 0; i < priorityQueue.size(); i++) {
			CustomerOrder order = priorityQueue.get(i);
			order.setPriority(((currentTime - order.getOrderTimeInSecs()) / 60) + order.getDeliveryDuration());
		}
	}

	private CustomerOrder getNextScheduledOrder() {

		Heap.buildMinHeap(priorityQueue);
		CustomerOrder currentOrder = priorityQueue.get(0);

		priorityQueue.set(0, priorityQueue.get(priorityQueue.size() - 1));
		priorityQueue.remove(priorityQueue.size() - 1);
		return currentOrder;
	}

	private void dispatchOrder(CustomerOrder currentOrder, int currentTime) {
		currentOrder.setOrderFulfillTime(currentTime);
		ordersFulfilledList.add(currentOrder);
	}

	private void saveResultsToFile(String filePath, float NPS) {
		// Get the file reference
		Path path = Paths.get(filePath);

		// Use try-with-resource to get auto-closeable writer instance
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			ordersFulfilledList.forEach(order -> {
				try {
					writer.write(order.getOrderId() + " " + order.getOrderFulfillTime() + "\n");
				} catch (IOException e) {
					System.out.println("Exception while writing single line to file" + e.getMessage());
				}
			});
			writer.write("\nNPS " + NPS + "\n");
		} catch (Exception e) {
			System.out.println("Exception while writing to file" + e.getMessage());
		}
	}
}
