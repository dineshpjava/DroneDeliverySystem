package com.walmart.dds;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class DroneScheduler {

	private static final int START_TIME = 21600; // 6:00:00 am
	private static final int END_TIME = 79200; // 10:00:00 pm

	private ArrayList<CustomerOrder> priorityQueue;
	private ArrayList<CustomerOrder> ordersFulfilledList;
	private LinkedList<CustomerOrder> orderQueue;
	private String outputFilePath;

	public DroneScheduler(LinkedList<CustomerOrder> orderQueue, String filePath) {
		priorityQueue = new ArrayList<CustomerOrder>();
		ordersFulfilledList = new ArrayList<CustomerOrder>();
		this.orderQueue = orderQueue;
		this.outputFilePath = filePath;
	}

	/**
	 * 
	 * This method is to prioritize the orders and dispatch the orders as per the
	 * priority. Updates the NPS contributers for each order and calculates the NPS
	 * at the end of the day's work
	 * 
	 */
	public void scheduleAndDispatchDeliveries() {

		int totalOrders = orderQueue.size();
		int currentTime = START_TIME;
		int lapsedTime = 0;
		int promoters = 0, detractors = 0;

		// keep serving orders until end of day or until there are no more orders to be
		// served
		while (currentTime <= END_TIME) {

			currentTime = addNewOrdersToSchedulerQueue(currentTime);

			// exit if done serving all the orders
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
		
		System.out.println("\n*************** End of Day's work.... Output printed in file ***************\n");
	}

	/**
	 * 
	 * check to see if new orders are placed by peeking at the start of Order Queue
	 * and add those orders to schedulers queue. If no orders are available to serve
	 * rest until next order is available.
	 * 
	 * @param currentTime : current time in seconds, current time starts counting
	 *                    from 12am per second
	 */
	private int addNewOrdersToSchedulerQueue(int currentTime) {

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

		// wait for new orders if there are no orders to serve at that point in time
		if (priorityQueue.isEmpty() && !orderQueue.isEmpty()) {
			CustomerOrder order = orderQueue.removeFirst();
			currentTime = order.getOrderTimeInSecs() > currentTime ? order.getOrderTimeInSecs() : currentTime;
			priorityQueue.add(order);
		}

		return currentTime;
	}

	/**
	 * 
	 * This method calculates the priority of each order at that point in time.
	 * Priority order is the ascending order of sum of an order's waiting time and
	 * the order's delivery duration. The priorityQueue is updated with new
	 * priorities at the start of every delivery, to pick the shortest job first.
	 * 
	 * @param currentTime : current time in seconds, current time starts counting
	 *                    from 12am per second
	 */
	private void updatePrioritiesOfOrders(int currentTime) {
		for (int i = 0; i < priorityQueue.size(); i++) {
			CustomerOrder order = priorityQueue.get(i);
			order.setPriority(((currentTime - order.getOrderTimeInSecs()) / 60) + order.getDeliveryDuration());
		}
	}

	/**
	 * 
	 * This method returns the next order to be served as per priority. Priority is
	 * based on the shortest job first. Priority is a sum of an order's waiting time
	 * and the order's delivery duration.
	 * 
	 * @return next order to be served
	 */
	private CustomerOrder getNextScheduledOrder() {

		Heap.buildMinHeap(priorityQueue);
		CustomerOrder currentOrder = priorityQueue.get(0);

		priorityQueue.set(0, priorityQueue.get(priorityQueue.size() - 1));
		priorityQueue.remove(priorityQueue.size() - 1);
		return currentOrder;
	}

	/**
	 * 
	 * This method is responsible for serving orders and updating the order object
	 * with fulfillment details
	 * 
	 * @param currentOrder : order to be dispatched
	 * @param currentTime  : current time in seconds, current time starts counting
	 *                     from 12am per second
	 */

	private void dispatchOrder(CustomerOrder currentOrder, int currentTime) {
		currentOrder.setOrderFulfillTime(currentTime);
		ordersFulfilledList.add(currentOrder);
	}

	/**
	 * 
	 * Method to print out results to an output file
	 * 
	 * @param filePath : Path to output file
	 * @param NPS      : NPS of the orders that are served
	 */

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
