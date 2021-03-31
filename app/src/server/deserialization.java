package src.server;

import java.io.*;

public class deserialization {

	
    /** 
     * @param bytes							byte array to be converted back to object
     * @return Object						Returns object from byte array
     * @throws IOException					If an I/O error occurs while reading stream header
     * @throws ClassNotFoundException		If class of Object cannot be determined
     */
    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream byteAray = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream objectArray = new ObjectInputStream(byteAray)){
                return objectArray.readObject();
            }
        }
    }
}
