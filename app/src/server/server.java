package server;

import java.io.*;
import java.net.*;
import java.util.*;
import models.*;

public class server {
	
	public final static int SERVICE_PORT=50001;
    
	public static void main(String[] args) throws IOException, SocketException{
		
        //determines whether the server uses at-least-once or at-most-once semantics, use ALO semantics as default
        boolean atLeastOnce = true;
        //set probability of packet loss to simulate loss of message during transmission, default is 0 
        double packetLossProb = 0;
		handler serverHandle = new handler(atLeastOnce);
        
		try{
	      // Instantiate a new DatagramSocket to receive responses from the client
	      DatagramSocket serverSocket = new DatagramSocket(SERVICE_PORT);
	      
	      while(true) {
	    	  byte[] receivingDataBuffer = new byte[1024];
		      
	    	  DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
		      System.out.println("Waiting for a client to connect...");
		      
		      serverSocket.receive(inputPacket);
		      
		      InetAddress senderAddress = inputPacket.getAddress();
		      int senderPort = inputPacket.getPort();
		      
		      if(Math.random() < packetLossProb){
		    	  System.out.printf("Simulated REQUEST message loss from  %s.\n", senderAddress);
		    	  continue;
              }
		      
		      String receivedData = deserialization.deserialize(inputPacket.getData()).toString();
		      System.out.println("Sent from client ("+senderAddress+") on port "+senderPort+" : "+receivedData);
		      
		      if ("QUIT".equals(receivedData))
		    	  break;		      

		      byte[] responseData = serverHandle.getResponse(senderAddress, senderPort, receivedData);
		            
		      DatagramPacket outputPacket = new DatagramPacket(
		        responseData, responseData.length,
		        senderAddress,senderPort
		      );
		      
		      if(Math.random() < packetLossProb){
		    	  System.out.printf("Simulated REPLY message loss to  %s.\n", senderAddress);
		    	  continue;
              }
		      
		      serverSocket.send(outputPacket);
		      
		      if(serverHandle.activeListeners != null) {
		    	  for(int i=0; i<serverHandle.activeListeners.size(); i++) {
		    		  Monitor monitor = serverHandle.activeListeners.get(i);
		    		  String message = "The monitored slot for "+monitor.startTime.toString()+" to "+monitor.endTime.toString()+" has been taken.";
		    		  byte [] monitorSendingDataBuffer = message.getBytes();
		    		  DatagramPacket monitorPacket = new DatagramPacket(
		    				  monitorSendingDataBuffer, monitorSendingDataBuffer.length,
		    			      InetAddress.getByName(monitor.address),monitor.host
		    			      );
		    		  serverSocket.send(monitorPacket);
		    	  }
		      }
	      }
	      
	      // Close the socket connection
	      serverSocket.close();
	    }
	
	    catch (SocketException e){
	      e.printStackTrace();
	    } 
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
