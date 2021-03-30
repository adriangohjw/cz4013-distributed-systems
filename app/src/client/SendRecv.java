package cz4013.facilitybooking.client;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.util.UUID;

import server.serialization;
import server.deserialization;

public class SendRecv {
	DatagramSocket clientSocket;
	byte[] buf;
	
	public SendRecv(DatagramSocket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public void sendRequest(SocketAddress serverAddress, String requestType, String requestFacility, String requestContent) {
		//TODO add timeout and retransmit
		//TODO handle response based on request type
		try {
			byte[] request = null;
			
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
