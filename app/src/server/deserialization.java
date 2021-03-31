package server;

import java.io.*;

public class deserialization {

	
    /** 
     * @param bytes
     * @return Object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream byteAray = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream objectArray = new ObjectInputStream(byteAray)){
                return objectArray.readObject();
            }
        }
    }
}
