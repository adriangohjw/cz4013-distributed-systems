package cz4013.facilitybooking.client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.time.LocalTime;
import java.util.UUID;

import server.serialization;
import server.deserialization;

public class Client {
	private final DatagramSocket clientSocket;
	private final SocketAddress serverAddress;
	private byte[] sendBuf = new byte[1024];
	private byte[] recvBuf = new byte[1024];
	
	public Client(DatagramSocket clientSocket, SocketAddress serverAddress ) {
		this.clientSocket = clientSocket;
		this.serverAddress = serverAddress;
	}
	
	public void checkAvailability() {
		System.out.println("Please enter the name of the facility to be checked:");
		String facilityName = UserInputTools.inputString();
		
		String dayOfWeekPrompt = "Please select the day to be checked.\n" +
		"Select an option from [1-7]:\n" +
		"1. Monday\n" +
		"2. Tuesday\n" +
		"3. Wednesday\n" +
		"4. Thursday\n" +
		"5. Friday\n" +
		"6. Saturday\n" +
		"7. Sunday\n";
		
		System.out.print(dayOfWeekPrompt);
		int dayOfWeekChoice = UserInputTools.inputInt();
		while (dayOfWeekChoice < 1 | dayOfWeekChoice > 7) {
			System.out.println("Invalid input - please enter an integer from 1-7!");
			dayOfWeekChoice = UserInputTools.inputInt();
		}
		
		System.out.println("Checking Facility: " + facilityName + " on " + dayOfWeekChoice);
		
		//put this in another class later
		try {
		
			byte[] request = null;
		
			String requestId = UUID.randomUUID().toString().substring(0,8); //not truly random! beware
			String requestType = "Availability";
			String requestFacility = facilityName;
			String requestContent = Integer.toString(dayOfWeekChoice);
			String requestString = requestId + "/" + requestType + "/" + requestFacility + "/" + requestContent;
			System.out.println("Request: " + requestString);
			request = serialization.serialize(requestString);
		
			sendBuf = request;
			DatagramPacket requestPacket = new DatagramPacket(sendBuf, sendBuf.length, serverAddress);
			clientSocket.send(requestPacket);
			System.out.println("Request sent to: " + serverAddress.toString());
			DatagramPacket responsePacket = new DatagramPacket(recvBuf, recvBuf.length);
			clientSocket.receive(responsePacket);
		
			//THIS ASSUMES DATA RECEIVED IS STRING!!!
			//WHICH IS NOT THE CASE, PLEASE MODIFY ACCORDINGLY
			String recvData = deserialization.deserialize(responsePacket.getData()).toString();
			System.out.println("Received data: " + recvData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//if no response, send again with same UUID
		//server should handle invalid facility names.
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
