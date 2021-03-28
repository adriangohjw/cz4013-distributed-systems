package server;

import java.io.*;

public class deserialization {

	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream byteAray = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream objectArray = new ObjectInputStream(byteAray)){
                return objectArray.readObject();
            }
        }
    }
}
