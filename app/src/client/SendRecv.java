package cz4013.facilitybooking.client;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.UUID;
import java.util.List;

import server.serialization;
import server.deserialization;
import models.Availability;

public class SendRecv {
	DatagramSocket clientSocket;
	byte[] buf;
	
	public SendRecv(DatagramSocket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public void sendRequest(SocketAddress serverAddress, String requestType, String requestFacility, String requestContent) {
		//TODO add timeout and retransmit
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
				if (recvData instanceof List) {
					List<Availability> availabilityList = (List<Availability>) deserialization.deserialize(responsePacket.getData());
					System.out.println("Availability:");
					for (Availability dayAvail : availabilityList) {
						System.out.println(dayAvail.toString());
					}
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
					if (success) System.out.println("Change successful.");
				}
				break;
			case "Monitor":
				if (recvData instanceof Boolean) {
					boolean ready = (Boolean) deserialization.deserialize(responsePacket.getData());
					if (ready) System.out.println("Ready to receive.");
				}
				break;
			default:
				break;
			}
			if (recvData instanceof String) {
				String message = deserialization.deserialize(responsePacket.getData()).toString();
				System.out.println("Server Message: " + message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
