package cz4013.facilitybooking.client;

import java.time.LocalTime;

public class Client {

	public void checkAvailability() {
		System.out.println("Please enter the name of the facility to be checked:");
		String facilityName = UserInputTools.inputString();
		System.out.println("Please enter the days to be checked:");
		//days in lowercase? one day or multiple days?
		String dayToCheck = UserInputTools.inputString().toLowerCase();
		
		System.out.println("Checking Facility: " + facilityName + " on " + dayToCheck);
		
		// insert comms logic here
		// server should handle invalid facility names.
		// verifying of days to be done client-side?
	}

	public void bookFacility() {
		System.out.println("Please enter the name of the facility to be booked:");
		String facilityName = UserInputTools.inputString();
		
		String startTimePrompt = "Please enter the start time in HHMM format: ";
		String endTimePrompt = "Please enter the end time in HHMM format: ";
		
		System.out.println(startTimePrompt);
		LocalTime startTime = UserInputTools.inputTime();
		System.out.println(endTimePrompt);
		LocalTime endTime = UserInputTools.inputTime();
		
		while (!endTime.isAfter(startTime)) {
			System.out.println("End time must be after start time!");
			System.out.println(startTimePrompt);
			startTime = UserInputTools.inputTime();
			System.out.println(endTimePrompt);
			endTime = UserInputTools.inputTime();		
		}
		
		System.out.println("Facility: " + facilityName + ", Start Time: " + startTime.toString() + ", End Time: " + endTime.toString());
	}
	
	public void changeBooking() {
		String advanceOrPostponePrompt = "Select an option from [1-2]:\n" +
		"1. Advance your booking\n" +
		"2. Postpone your booking.\n";
		
		int advancePostponeChoice = 0;
		while (advancePostponeChoice == 0) {
			System.out.print(advanceOrPostponePrompt);
			int optionSelected = UserInputTools.inputInt();
			switch(optionSelected) {
			case 1:
				advancePostponeChoice = -1;
				System.out.println("You have chosen to advance.");
				break;
			case 2:
				advancePostponeChoice = 1;
				System.out.println("You have chosen to postpone.");
				break;
			default:
				System.out.println("Invalid choice! Please try again.");
				break;
			}
		}
		
		System.out.println("By how long? Please enter in minutes.");
		int offset = UserInputTools.inputInt();
		
		System.out.println("Please enter your unique booking ID: ");
		String id = UserInputTools.inputString();
		
		System.out.println("Advance(-1)/postpone(1): " + advancePostponeChoice + ", offset: " + offset + ", booking ID: " + id);
	}

	public void monitorAvailability() {
		//From my understanding, this sends a request to the server
		//for a given monitor interval to send any changes in availability
		//So the client just needs to standby and wait for updates.
		System.out.println("Please enter the name of the facility to be monitored:");
		String facilityName = UserInputTools.inputString();
		System.out.println("For how many minutes would you like to monitor:");
		int monitorInterval = UserInputTools.inputInt();
		
		System.out.println("Monitored Facility: " + facilityName + " , Monitor Interval: " + monitorInterval + " minutes");
		
		//Send callback request to server
		
		System.out.println("Monitoring... Please wait!");
		
		//Print all messages sent from server here
		
		System.out.println("Monitoring ended. Returning back to main menu.");
	}

	public void listAllFacilities() {
		//communicate with server to return all facilities available
		
	}

}
