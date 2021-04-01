package client;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.List;

import server.serialization;
import server.deserialization;
import models.Availability;

/**
 * Handles networking I/O between client and server
 */
public class SendRecv {
	DatagramSocket clientSocket;
	byte[] buf;
	
	public SendRecv(DatagramSocket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	
	/** 
	 * Sends a request to the server based on requestType and processes the response accordingly
	 * @param serverAddress The SocketAddress of the server containing its IP address and port
	 * @param requestType Type of request made
	 * @param requestFacility Name of facility
	 * @param requestContent Content to be sent in the request
	 */
	public void sendRequest(SocketAddress serverAddress, String requestType, String requestFacility, String requestContent) {
		try {
			byte[] request = null;
			clientSocket.setSoTimeout(5000); //timeout of 5 seconds
			
			String requestId = UUID.randomUUID().toString().substring(0,8); //not truly random! beware
			String requestString = requestId + "/" + requestType + "/" + requestFacility + "/" + requestContent;
			System.out.println("Request: " + requestString);
			request = serialization.serialize(requestString);
			
			//send packet
			buf = request;
			DatagramPacket requestPacket = new DatagramPacket(buf, buf.length, serverAddress);
			clientSocket.send(requestPacket);
			System.out.println("Request sent to: " + serverAddress.toString().substring(1));
			
			//receive packet
			DatagramPacket responsePacket = new DatagramPacket(buf, buf.length);
			boolean received = false;
			while (!received) {
				try {
					clientSocket.receive(responsePacket);
					received = true;
				} catch (SocketTimeoutException e) {
					System.out.println("Timeout. Resending request");
					clientSocket.send(requestPacket);
				}
			}
			Object recvData = deserialization.deserialize(responsePacket.getData());
			switch(requestType) {
			case "Availability":
				//if (recvData instanceof List) {
				List<Availability> availabilityList = (List<Availability>) recvData;
				System.out.println("Availability:");
				for (Availability dayAvail : availabilityList) {
					System.out.println(dayAvail.toString());
				//	}
				/*if (recvData instanceof Availability) {
					Availability availability = (Availability) recvData;
					System.out.println("Availability:");
					System.out.println(availability.toString());
					}*/
				}
				break;
			case "Book":
				if (recvData instanceof Integer) {
					int bookingId = (Integer) deserialization.deserialize(responsePacket.getData());
					System.out.println("Booking successful. Your unique booking ID is: " + Integer.toString(bookingId));
				}
				break;
			case "Change":
				if (recvData instanceof Boolean) {
					boolean success = (Boolean) deserialization.deserialize(responsePacket.getData());
					if (success) {
						System.out.println("Change successful.");
					} else {
						System.out.println("Change unsuccessful.");
					}
				}
				break;
			case "Monitor":
				if (recvData instanceof Boolean) {
					boolean ready = (Boolean) deserialization.deserialize(responsePacket.getData());
					if (ready) {
						System.out.println("Starting monitor.");
						int monitorMinutes = Integer.parseInt(requestContent);
						long durationMs = (long) monitorMinutes * 60 * 1000;
						long startTime = System.currentTimeMillis();
						long endTime = startTime + durationMs;
						long currentTime;
						try {
							while ((currentTime = System.currentTimeMillis()) < endTime) {
								DatagramPacket callbackPacket = new DatagramPacket(buf, buf.length);
								long remainingTimeout = endTime - currentTime;
								clientSocket.setSoTimeout((int) remainingTimeout);
								clientSocket.receive(callbackPacket);
								Object callbackRecvData = deserialization.deserialize(callbackPacket.getData());
								if (callbackRecvData instanceof String) {
									String callbackMessage = callbackRecvData.toString();
									System.out.println(callbackMessage);
								}
							}
						} catch (SocketTimeoutException e) {
							System.out.println("Monitoring period ended.");
						}
					} else {
						System.out.println("Server not ready. Please try again later.");
					}
				}
				break;
			default:
				break;
			}
			if (recvData instanceof String) {
				String message = recvData.toString();
				System.out.println("Server Message: " + message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/** 
	 * A test function to send a message to the server
	 * @param serverAddress The SocketAddress of the server containing its IP address and port
	 * @param message The string to be sent to the server
	 */
	public void sendMessage(SocketAddress serverAddress, String message) {
		//for testing only.
		try {
			buf = serialization.serialize(message);
			DatagramPacket messagePacket = new DatagramPacket(buf, buf.length, serverAddress);
			clientSocket.send(messagePacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
