package udp_server;

import java.net.*;
import java.util.*;

public class handler {
	
	private boolean atLeastOnce;
	private LRUMap cache = new LRUMap(10);
	
	public handler(boolean atLeastOnce) {
		this.atLeastOnce = atLeastOnce;
	}

	public byte[] getResponse(InetAddress address, int port, String request) {
		String[] requestArray = request.split("/");
		String requestId = requestArray[0];
		String requestType = requestArray[1];
		String requestKey = address+":"+port+"/"+requestId;
		
		if(checkCache(requestKey)) {
			if(atLeastOnce) {
				return cache.get(requestKey).orElse(new byte[0]);
			}
			else
				return ("Repeat request for non-idempotent operation").getBytes();
		}
		
		return response;
	}
	
	public boolean checkCache(String key) {
		if(cache.get(key) != null) return true;
		return false;
	}
}
