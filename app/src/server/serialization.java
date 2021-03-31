package src.server;

import java.io.*;

public class serialization {

	
    /** 
     * @param obj				Receives an Object to be cast to a byte array
     * @return byte[]			Returns a serialized byte array
     * @throws IOException		If an I/O error occurs while writing stream header
     */
    public static byte[] serialize(Object obj) throws IOException {
        try(ByteArrayOutputStream byteAray = new ByteArrayOutputStream()){
            try(ObjectOutputStream objectArray = new ObjectOutputStream(byteAray)){
            	objectArray.writeObject(obj);
            }
            return byteAray.toByteArray();
        }
    }
}
