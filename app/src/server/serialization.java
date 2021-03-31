package server;

import java.io.*;

public class serialization {

	
    /** 
     * @param obj
     * @return byte[]
     * @throws IOException
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
