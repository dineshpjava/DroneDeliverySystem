package com.walmart.dds;

import java.util.ArrayList;

/**
 * MinHeap Algorithm implementation
 * 
 * @author Dinesh
 *
 */
public class Heap {

	public static void buildMinHeap(ArrayList<CustomerOrder> orderList) {

		for (int index = (orderList.size() - 1) / 2; index >= 0; index--) {
			heapify(orderList, index);
		}
	}

	private static void heapify(ArrayList<CustomerOrder> orderList, int rootIndex) {

		int max = rootIndex;
		int leftChildIndex = (2 * rootIndex) + 1;
		int rightChildIndex = (2 * rootIndex) + 2;
		if (leftChildIndex < orderList.size()
				&& orderList.get(rootIndex).getPriority() > orderList.get(leftChildIndex).getPriority()) {
			max = leftChildIndex;
		}
		if (rightChildIndex < orderList.size()
				&& orderList.get(max).getPriority() > orderList.get(rightChildIndex).getPriority()) {
			max = (2 * rootIndex) + 2;
		}
		if (max != rootIndex) {
			CustomerOrder temp = orderList.get(rootIndex);
			orderList.set(rootIndex, orderList.get(max));
			orderList.set(max, temp);
			heapify(orderList, max);
		}
	}
}
