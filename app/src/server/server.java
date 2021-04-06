package server;

import java.io.*;
import java.net.*;
import java.util.*;
import models.*;

public class server {
	
	public final static int SERVICE_PORT=50001;
    
	
	/** 
	 * 
	 * 
	 * @param args
	 * @throws IOException		If an I/O error occurs while reading or writing stream header
	 * @throws SocketException	If a socket error occurs when receiving or sending on socket connection
	 */
	public static void main(String[] args) throws IOException, SocketException{
		
        //determines whether the server uses at-least-once or at-most-once semantics, use ALO semantics as default
        boolean atLeastOnce = true;
        //set probability of packet loss to simulate loss of message during transmission, default is 0 
        double packetLossProb = 0;
		handler serverHandle = new handler(atLeastOnce);
        //set localhost address
		InetAddress local = InetAddress.getByName("0.0.0.0");
		
		try{
	      // Instantiate a new DatagramSocket to receive responses from the client
	      DatagramSocket serverSocket = new DatagramSocket(SERVICE_PORT,local);
	      System.out.println(serverSocket.getInetAddress());
	      while(true) {
	    	  byte[] receivingDataBuffer = new byte[1024];
		      
	    	  DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
		      System.out.println("Waiting for a client to connect...");
		      
		      serverSocket.receive(inputPacket);
		      
		      InetAddress senderAddress = inputPacket.getAddress();
		      int senderPort = inputPacket.getPort();
		      
		      if(Math.random() < packetLossProb){
		    	  System.out.printf("Simulated REQUEST message loss from  %s.\n", senderAddress.toString());
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
		    	  System.out.printf("Simulated REPLY message loss to  %s.\n", senderAddress.toString());
		    	  continue;
              }
		      
		      serverSocket.send(outputPacket);
		      
		      if(serverHandle.activeListeners != null) {
		    	  
		    	  Integer[] boxedArray = new Integer[7];		    	  
		          for (int i = 0; i < 7; i++) {
		              boxedArray[i] = Integer.valueOf(i+1);
		          }
		   
		    	  
		    	  for(int i=0; i<serverHandle.activeListeners.size(); i++) {
		    		  Monitor monitor = serverHandle.activeListeners.get(i);
		    		  List<Availability> availabilities = Availability.getAvailabilitiesForFacility(monitor.facilityId, boxedArray);
		    		  String message = "The monitored slot for "+monitor.startTime.toString()+" to "+monitor.endTime.toString()+" has been taken. The available slots are: \n";
		    		  for(int j=0; j<availabilities.size(); j++) {
		    			  message = message+availabilities.get(j).day+" from "+availabilities.get(j).startTime.toString()+" to "+availabilities.get(j).endTime.toString()+"\n";
		    		  }
		    		  try {
		    			  byte [] monitorSendingDataBuffer = serialization.serialize(message);
		    			  String monitor_address = monitor.address.substring(1);
			    		  
			    		  InetSocketAddress to_send = new InetSocketAddress(monitor_address, monitor.host);
			    		  DatagramPacket monitorPacket = new DatagramPacket(
			    				  monitorSendingDataBuffer, monitorSendingDataBuffer.length,
			    			      to_send
			    			      );
			    		  serverSocket.send(monitorPacket);
		    		  }
		    		  catch (IOException e) {
		    			  e.printStackTrace();
		    		  }
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
