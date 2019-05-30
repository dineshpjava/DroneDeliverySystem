package com.walmart.dds;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.stream.Stream;

public class Main {

	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("\n*************** Please provide input and out file path ***************\n");
			return;
		}

		String inputFilePath = args[args.length - 2];
		String outputFilePath = args[args.length - 1];
		// String inputFilePath = "/Users/dini/Downloads/input.txt";
		// String outputFilePath = "/Users/dini/Downloads/output.txt";

		if (!isValidFilePath(inputFilePath, outputFilePath)) {
			return;
		}

		LinkedList<CustomerOrder> orderQueue = readFile(inputFilePath);

		DroneScheduler droneScheduler = new DroneScheduler(orderQueue, outputFilePath);
		droneScheduler.scheduleAndDispatchDeliveries();

	}

	/**
	 * 
	 * Method to validate the existence of input and output files
	 * 
	 * @param inputFilePath
	 * @param outputFilePath
	 * @return
	 */
	private static boolean isValidFilePath(String inputFilePath, String outputFilePath) {
		File inFile = new File(inputFilePath);
		if (!inFile.exists() || inFile.isDirectory()) {
			System.out.println("input file doesn't exist");
			return false;
		}

		File outFile = new File(outputFilePath);
		if (!outFile.exists() || outFile.isDirectory()) {
			System.out.println("output file doesn't exist");
			return false;
		}
		return true;
	}

	/**
	 * 
	 * Method to read the input file, parse the content and create a list of
	 * customer orders to be served.
	 * 
	 * @param filePath : input file path
	 * @return list of customer orders which are sorted as per the order placed time
	 *         stamp
	 */
	private static LinkedList<CustomerOrder> readFile(String filePath) {

		LinkedList<CustomerOrder> orderQueue = new LinkedList<CustomerOrder>();

		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {

			stream.forEach((inputString) -> {
				String[] inArray = inputString.split(" ");
				orderQueue.addLast(new CustomerOrder(inArray[0], inArray[1], inArray[2]));
			});

		} catch (IOException e) {
			System.out.println("Exception while reading file" + e.getMessage());
		}

		return orderQueue;
	}
}
