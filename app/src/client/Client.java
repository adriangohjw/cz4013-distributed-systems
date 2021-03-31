package src.client;

import java.net.SocketAddress;
import java.time.LocalTime;

public class Client {
	private final SendRecv sendRecv;
	private final SocketAddress serverAddress;
	
	/**
	 * Constructor for the Client class
	 * 
	 * @param sendRecv An instance of SendRecv
	 * @param serverAddress A SocketAddress object containing the IP address and port of the server
	 */
	public Client(SendRecv sendRecv, SocketAddress serverAddress ) {
		this.sendRecv = sendRecv;
		this.serverAddress = serverAddress;
	}
	
	/**
	 * Checks the availability of a facility and day specified by the user
	 */
	public void checkAvailability() {
		System.out.println("Please enter the name of the facility to be checked:");
		String facilityName = UserInputTools.inputString();
		String dayOfWeekChoice = UserInputTools.inputDayOfWeek();
		
		String requestContent = dayOfWeekChoice;
		
		System.out.println("Checking Facility: " + facilityName + " on " + dayOfWeekChoice);
		sendRecv.sendRequest(serverAddress, "Availability", facilityName, requestContent);
	}

	/**
	 * Makes a booking request to the server for a user-specified facility, day and time period
	 */
	public void bookFacility() {
		System.out.println("Please enter the name of the facility to be booked:");
		String facilityName = UserInputTools.inputString();
		
		String dayOfWeekChoice = UserInputTools.inputDayOfWeek();
		
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
		
		String requestContent = dayOfWeekChoice + "," + 
				Integer.toString(startTime.getHour()) + "," +
				Integer.toString(startTime.getMinute()) + "," + 
				Integer.toString(startTime.getSecond()) + "," +
				Integer.toString(endTime.getHour()) + "," +
				Integer.toString(endTime.getMinute()) + "," + 
				Integer.toString(endTime.getSecond());
		System.out.println("Request content: " + requestContent);
		System.out.println("Facility: " + facilityName + ", Day: " + dayOfWeekChoice + ", Start Time: " + startTime.toString() + ", End Time: " + endTime.toString());
		sendRecv.sendRequest(serverAddress, "Book", facilityName, requestContent);
	}
	
	/**
	 * Advances or postpones an existing booking as specified by the user
	 */
	public void changeBooking() {
		System.out.println("Please enter the name of the facility where you made your booking:");
		String facilityName = UserInputTools.inputString();
		
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
		
		String requestContent = id + "," +
				Integer.toString(advancePostponeChoice) + "," +
				Integer.toString(offset);
		System.out.println("Request content: " + requestContent);
		System.out.println("Facility: " + facilityName + ", Advance(-1)/postpone(1): " + advancePostponeChoice + ", offset: " + offset + ", booking ID: " + id);
		sendRecv.sendRequest(serverAddress, "Change", facilityName, requestContent);
	}

	/**
	 * Sends a callback request to the server and monitors booking changes of a facility for a specified monitor interval
	 */
	public void monitorAvailability() {
		//From my understanding, this sends a request to the server
		//for a given monitor interval to send any changes in availability
		//So the client just needs to standby and wait for updates.
		System.out.println("Please enter the name of the facility to be monitored:");
		String facilityName = UserInputTools.inputString();
		System.out.println("For how many minutes would you like to monitor:");
		int monitorInterval = UserInputTools.inputInt();
		
		System.out.println("Monitored Facility: " + facilityName + " , Monitor Interval: " + monitorInterval + " minutes");

		sendRecv.sendRequest(serverAddress, "Monitor", facilityName, Integer.toString(monitorInterval));
		System.out.println("Monitoring ended. Returning back to main menu.");
	}

	/**
	 * Lists all facilities available for booking to the user
	 */
	public void listAllFacilities() {
		//communicate with server to return all facilities available
		
	}
	
	/**
	 * Test function to send a message to the server
	 */
	public void testMessageToServer() {
		System.out.println("Please enter the message:");
		String message = UserInputTools.inputString();
		sendRecv.sendMessage(serverAddress, message);
	}

}
