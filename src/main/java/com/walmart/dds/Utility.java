package com.walmart.dds;

public class Utility {

	public static int getDeliveryDurationInSeconds(String customerAddress) {
		String address;
		String[] durationArray;
		if (customerAddress.contains("N")) {
			address = customerAddress.replace("N", "");
		} else {
			address = customerAddress.replace("S", "");
		}
		if (address.contains("E")) {
			durationArray = address.split("E");
		} else {
			durationArray = address.split("W");
		}
		// duration to reach this address in minutes
		int duration = (Integer.parseInt(durationArray[0]) + Integer.parseInt(durationArray[1]));
		return duration;
	}

	public static int getTimeInSecs(String time) {
		String[] orderedTimeArray = time.split(":");
		int timeInSecs = Integer.MAX_VALUE;
		if (orderedTimeArray.length == 3) {
			int hour = Integer.parseInt(orderedTimeArray[0]);
			int minute = Integer.parseInt(orderedTimeArray[1]);
			int second = Integer.parseInt(orderedTimeArray[2]);
			timeInSecs = (60 * 60 * hour) + (60 * minute) + second;
		}
		return timeInSecs;
	}

	public static String formatToTime(int timeInSeconds) {
		int hours = (int) (timeInSeconds / 3600);
		int secondsLeft = (int) (timeInSeconds - hours * 3600);
		int minutes = secondsLeft / 60;
		int seconds = secondsLeft - minutes * 60;

		String formattedTime = "";
		if (hours < 10)
			formattedTime += "0";
		formattedTime += hours + ":";

		if (minutes < 10)
			formattedTime += "0";
		formattedTime += minutes + ":";

		if (seconds < 10)
			formattedTime += "0";
		formattedTime += seconds;

		return formattedTime;
	}
}
