package com.example.sadeep.winternightd.dumping;

/**
 * Created by Sadeep on 6/15/2017.
 */

import com.example.sadeep.winternightd.misc.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 *  FieldDataStream  represented in a way that can directly be stored in a SQLite database and can be used in communication.
 *
 *  The 5 arrays(see FieldDataStream) are represented as follows
 *
 *    strings[0]  :  strings concatenated into a single string separated by delimiter \uEB92
 *    strings[1]  :  strings concatenated into a single string separated by delimiter \uEB92
 *    ints[0]  :  a byte array (4 bytes for an integer)
 *    ints[1]  :  ints converted to texts(strings) which are then concatenated into a single string separated by delimiter \uEB92
 *    fieldTypes : ints[0]  :  a byte array (4 bytes for an integer)
 *
 */
public class RawFieldDataStream {

    static final char STRING_DELIMITER = '\uEB92';

    public String [] strings = new String[2];

    public byte[]  ints0;
    public String  ints1;

    public byte[] bindata;

    public byte[]  fieldTypes;

    public RawFieldDataStream(String strings0, String strings1, byte[] ints0,String ints1,byte[]bindata, byte[] fieldTypes){
        this.strings[0] = strings0;
        this.strings[1] = strings1;
        this.ints0 = ints0;
        this.ints1 = ints1;
        this.bindata = bindata;
        this.fieldTypes = fieldTypes;
    }

    public RawFieldDataStream(FieldDataStream stream){

        for(int i=0;i<2;i++)   strings[i] = delimitedStringFromElements(stream.strings[i]);

        ints0 = byteArrayFromIntegers(stream.ints[0]);
        ints1 = delimitedStringFromElements(stream.ints[1]);

        bindata = Utils.toByteArray(stream.bindata);

        fieldTypes = byteArrayFromIntegers(stream.fieldTypes);
    }

    private byte[] byteArrayFromIntegers(ArrayList<Integer> ints){
        ByteBuffer buffer = ByteBuffer.allocate(ints.size()*4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        for(int x:ints)buffer.putInt(x);

        return buffer.array();
    }

    private String delimitedStringFromElements(ArrayList objects){

        StringBuffer buffer = new StringBuffer("");

        for (Object x :objects) {
            buffer.append(x);
            buffer.append(STRING_DELIMITER);
        }

        return buffer.toString();
    }
}
