package Util;

import java.nio.ByteBuffer;

public class TypeTransfer {

    // Convert Integer to Byte array
    public static byte[] convertIntToByte(int num){
        return ByteBuffer.allocate(4).putInt(num).array();
    }

    // Convert Byte array to Integer
    public static int convertByteToInt(byte[] bytes){
        return ByteBuffer.wrap(bytes).getInt();
    }


}
