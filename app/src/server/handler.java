package server;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.stream.Stream;
import java.time.LocalTime;

import databaseServices.*;
import databaseServices.caches.*;
import models.Availability;
import models.Booking;
import models.Facility;
import models.Monitor;
import client.*;

public class handler {
	
	private boolean atLeastOnce;
	private HashMap<String, byte[]> request_response = new HashMap<String, byte[]>();

	
	public handler(boolean atLeastOnce) {
		this.atLeastOnce = atLeastOnce;
		DatabaseSetup.main(null);
		DatabaseSeed.main(null);
		try {
			Connect.setupConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] getResponse(InetAddress address, int port, String request) {
		
		byte[] response = null;
		String[] requestArray = request.split("/");
		String requestId = requestArray[0];
		String requestType = requestArray[1];
		String requestKey = address+":"+port+"/"+requestId;
		String requestFacility = requestArray[2];
		String[] requestContent = requestArray[3].split(",");
		Integer[] int_requestContent = new Integer[requestContent.length];
		for(int i=0; i<requestContent.length; i++) {
			int_requestContent[i] = Integer.parseInt(requestContent[i]);
		}
		
		if(checkRequest(requestKey)) {
			if(!atLeastOnce)
				return request_response.get(requestKey);
		}
		
		switch(requestType) {		
			case "Availability":
				Integer facilityId = Facility.getIdFromName(requestFacility);
				List<Availability> availability = Availability.getAvailabilitiesForFacility(facilityId, int_requestContent);
				try {
					response = serialization.serialize(availability);
				} catch (IOException e) {
					try {
						response = serialization.serialize(e.getMessage());
					}
					catch (IOException ee){
						ee.printStackTrace();
					}
				}
				break;
				
			case "Book":
				Integer bookingId = Booking.create(requestFacility, int_requestContent[0], 
						LocalTime.of(int_requestContent[1],int_requestContent[2],int_requestContent[3]), 
						LocalTime.of(int_requestContent[4],int_requestContent[5],int_requestContent[6]));
				try {
					response = serialization.serialize(bookingId);
				} catch (IOException e) {
					try {
						response = serialization.serialize(e.getMessage());
					}
					catch (IOException ee){
						ee.printStackTrace();
					}
				}
				break;
				
			case "Change":
				Boolean changed = Booking.updateTiming(int_requestContent[0], int_requestContent[1], int_requestContent[2]);
				try {
					response = serialization.serialize(changed);
				} catch (IOException e) {
					try {
						response = serialization.serialize(e.getMessage());
					}
					catch (IOException ee){
						ee.printStackTrace();
					}
				}
				break;
				
			case "Monitor":
				Boolean monitoring = Monitor.create(requestFacility, address.toString(), port, int_requestContent[0]);
				try {
					response = serialization.serialize(monitoring);
				} catch (IOException e) {
					try {
						response = serialization.serialize(e.getMessage());
					}
					catch (IOException ee){
						ee.printStackTrace();
					}
				}
				break;
		}
		request_response.put(requestKey, response);
		return response;
	}
	
	public boolean checkRequest(String requestKey) {
		if(request_response.get(requestKey)!=null) return true;
		return false;
	}
	
}
