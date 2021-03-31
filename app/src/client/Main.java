package src.client;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.InetSocketAddress;

public class Main {

	
	/** 
	 * @param args
	 * @throws SocketException
	 */
	public static void main(String[] args) throws SocketException {
		String serverIpAddr = "127.0.0.1";
		int clientPort = 65535;
		int serverPort = 50001;
		
		// menu
		String MENU = 
		"------------------------------------\n" +
		"Distributed Facility Booking System\n" +
		"------------------------------------\n" +
		"Select an option from [1-6]:\n" +
		"1. Check Availability\n" +
		"2. Book Facility\n" +
		"3. Change Booking Timing\n" +
		"4. Monitor Availability\n" +
		"5. List All Facilities\n" +
		"6. Exit\n" +
		"------------------------------------\n";
		
		DatagramSocket socket = new DatagramSocket(clientPort);
		Client client = new Client(new SendRecv(socket), new InetSocketAddress(serverIpAddr, serverPort));
		
		boolean exitProgram = false; 
		while (!exitProgram) {
			System.out.print(MENU);
			int optionSelected = UserInputTools.inputInt();
			switch(optionSelected) {
			case 1:
				client.checkAvailability();
				break;
			case 2:
				client.bookFacility();
				break;
			case 3:
				client.changeBooking();
				break;
			case 4:
				client.monitorAvailability();
				break;
			case 5:
				client.listAllFacilities();
				break;
			case 6:
				exitProgram = true;
				break;
			default:
				System.out.println("Invalid choice! Please try again.");
				break;
			}	
		}
		System.out.println("Shutting down!");
		UserInputTools.closeScanner();
	}

}
