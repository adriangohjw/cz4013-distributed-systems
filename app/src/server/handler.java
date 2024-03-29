package server;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.time.LocalTime;

import databaseServices.*;
import databaseServices.exceptions.*;
import models.Availability;
import models.Booking;
import models.Facility;
import models.Monitor;
import client.*;

public class handler {
	
	private boolean atLeastOnce;
	private HashMap<String, byte[]> request_response = new HashMap<String, byte[]>();
	public List<Monitor> activeListeners;
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

	
	/** 
	 * Gets the response given the client address, port and request
	 * 
	 * @param address		Address of client that sent the request
	 * @param port			Port of client that sent the request
	 * @param request		Concatenated string request containing the type of request and parameters required for the request
	 * @return byte[]		Returns a serialized response 
	 */
	public byte[] getResponse(InetAddress address, int port, String request) {
		
		byte[] response = null;
		activeListeners = null;
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
				List<Booking> bookings = Booking.getForFacilityOnGivenDays(requestFacility, int_requestContent);
				String output = "";
				for(Availability i :availability) {
					List<LocalTime> timerange = new ArrayList<LocalTime>();
					boolean start_is_booked = false;
					timerange.add(i.startTime);
					timerange.add(i.endTime);
					output = output + "[Facility: " + requestFacility + " Day: " + i.day + " Available Timeslot: ";
					for(Booking j: bookings) {
						if(i.day.equals(j.day)) {
							if(j.startTime.equals(LocalTime.of(8, 0))) start_is_booked = true;
							timerange.add(j.startTime);
							timerange.add(j.endTime);
						}
					}
					
					Collections.sort(timerange);
					
					if(start_is_booked) {
						timerange.remove(0);
						timerange.remove(0);
						while(timerange.size()>1) {
							if(!timerange.get(0).equals(timerange.get(1))) {
								output = output + timerange.get(0) + " - " + timerange.get(1) + " ";
								timerange.remove(0);
								timerange.remove(0);
							}
							else {
								timerange.remove(0);
								timerange.remove(0);
							}
						}
						output+="]\n";
					}
					else {
						while(timerange.size()>1) {
							if(!timerange.get(0).equals(timerange.get(1))) {
								output = output + timerange.get(0) + " - " + timerange.get(1) + " ";
								timerange.remove(0);
								timerange.remove(0);
							}
							else {
								timerange.remove(0);
								timerange.remove(0);
							}
						}
						output+="]\n";
					}
				}
				System.out.println(availability);
				try {
					response = serialization.serialize(output);
				} catch (IOException e) {
					try {
						response = serialization.serialize(e.getMessage().toString());
					}
					catch (IOException ee){
						ee.printStackTrace();
					}
				}
				break;
				
			case "Book":
				Booking booking = Booking.create(requestFacility, int_requestContent[0], 
						LocalTime.of(int_requestContent[1],int_requestContent[2],int_requestContent[3]), 
						LocalTime.of(int_requestContent[4],int_requestContent[5],int_requestContent[6]));
				if(booking != null) {
					activeListeners = Monitor.getActiveListeners(booking.facilityId);
					try {
						response = serialization.serialize(booking.id);
					} catch (IOException e) {
						try {
							response = serialization.serialize(e.getMessage().toString());
						}
						catch (IOException ee){
							ee.printStackTrace();
						}
					}
				}
				else {
					System.out.println("Booking Error");
					try {
						response = serialization.serialize("Booking Error");
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
				
			case "Change":
				Boolean changed = Booking.updateTiming(int_requestContent[0], int_requestContent[1], int_requestContent[2]);
				try{
					activeListeners = Monitor.getActiveListeners(Booking.getById(int_requestContent[0]).facilityId);
				}
				catch (RecordNotFoundException e) {
					e.printStackTrace();
				}
				try {
					response = serialization.serialize(changed);
				} catch (IOException e) {
					try {
						response = serialization.serialize(e.getMessage().toString());
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
						response = serialization.serialize(e.getMessage().toString());
					}
					catch (IOException ee){
						ee.printStackTrace();
					}
				}
				break;
			default:
				break;
		}
		request_response.put(requestKey, response);
		return response;
	}
	
	
	/** 
	 * @param requestKey	Key for checking against the Hashmap to find previous records of response 
	 * @return boolean		Returns true if there is a record of response for the requestKey, false if no record is found
	 */
	public boolean checkRequest(String requestKey) {
		if(request_response.get(requestKey)!=null) return true;
		return false;
	}
	
}
