package client;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.InetSocketAddress;

/**
 * The main menu of the client application. Initializes the client socket and instantiates instances of Client and SendRecv.
 */
public class Main {

	
	/** 
	 * @param args
	 * @throws SocketException
	 */
	public static void main(String[] args) throws SocketException {
		int argPort = 65535;
		String argServerAddr = "127.0.0.1";
		int argServerPort = 50001;
		
		if (args.length > 0) {
			try {
				if (args.length == 1) {
					argPort = Integer.parseInt(args[0]);
					System.out.println("Client port set to: " + argPort);
				}
				if (args.length == 2) {
					argPort = Integer.parseInt(args[0]);
					argServerAddr = args[1];
					System.out.println("Client port set to " + argPort);
					System.out.println("Server address set to " + argServerAddr);
				}
				if (args.length == 3) {
					argPort = Integer.parseInt(args[0]);
					argServerAddr = args[1];
					argServerPort = Integer.parseInt(args[2]);
					System.out.println("Client port set to " + argPort);
					System.out.println("Server address set to " + argServerAddr);
					System.out.println("Server port set to " + argServerPort);
				}
			} catch (Exception e) {
				System.err.println("Invalid arguments.");
				System.exit(1);
			}
		}
		
		String serverIpAddr = argServerAddr;
		int clientPort = argPort;
		int serverPort = argServerPort;
		
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
