package com.example.sadeep.winternightd.dumping;

import com.example.sadeep.winternightd.misc.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Sadeep on 11/28/2016.
 */

/**   An instance of FieldDataStream stores one or more Fields (and thus it can store a Note).
 *
 *
 *    A Field can be stored using 5 Arrays (here we use ArrayLists to store)
 *
 *    strings[0]  :  to store strings that are not searchable
 *    strings[1]  :  to store strings that are searchable       //user typed texts etc.
 *    ints[0]  :  to store integers that are not searchable     //indent of an indented Field, starts & ends of Spans, whether a CheckedField is ticked or not etc.
 *    ints[1]  :  to store integers that are searchable
 *    bindata  :  to store binary data
 *    fieldTypes : to store the type (see Field.fieldType) of each of the Field stored (the length of this array is equal to the no of Fields stored)
 *
 *    When dumping data writeToFieldDataStream method of the respective Field puts(appends) data to the 5 streams
 *    When generating the Fields readFromFieldDataStream of the respective Field reads data and changes itself accordingly
 *    Information is read in readFromFieldDataStream in the same order as they are written in writeToFieldDataStream(look at the 2 functions in a Field, they have the same structure)
 *    This ensures the integer written to represent a certain property is correctly perceived when reading and is used to identify the
 *    same property when generating the note.
 *
 *    There is no separator to separate Fields in the 5 arrays, a Field only reads the number of data needed to generate itself from
 *    the arrays inside its readFromFieldDataStream method, thus placing the 5 cursors at the start of the next Field.
 */
public class FieldDataStream  {

    ArrayList<String>[] strings = new ArrayList[2];//       the 6
    ArrayList<Integer>[] ints = new ArrayList[2]; //          data
    ArrayList<Integer> fieldTypes;//                            ArrayLists
    ArrayList<Byte> bindata;


    //the index of the value that should be read next in the each of the above ArrayLists (this is like the cursor position)
    private int[] stringsPos = new int[2];
    private int[] intsPos = new int[2];
    private int fieldTypesPos = 0;
    private int binDataPos;

    public FieldDataStream(){
        strings[0] = new ArrayList<>();         //initialize our data ArrayLists
        strings[1] = new ArrayList<>();

        ints[0] = new ArrayList<>();
        ints[1] = new ArrayList<>();

        bindata = new ArrayList<>();

        fieldTypes = new ArrayList<>();
    }

    public FieldDataStream(RawFieldDataStream rawStream){
        for(int i=0;i<2;i++)strings[i]=elementsFromDelimitedString(rawStream.strings[i],false);
        ints[0] = integersFromByteArray(rawStream.ints0);
        ints[1] = elementsFromDelimitedString(rawStream.ints1,true);
        bindata = Utils.arrayToArrayList(rawStream.bindata);
        fieldTypes = integersFromByteArray(rawStream.fieldTypes);

    }

    public int getInt(boolean searchable){
        int arr = 0;
        if(searchable)arr = 1;

        return ints[arr].get(intsPos[arr]++);
    }
    public void putInt(boolean searchable,int value){
        int arr = 0;
        if(searchable)arr = 1;

        ints[arr].add(value);
    }


    public byte getByte(){
        return bindata.get(binDataPos++);
    }
    public void putByte(byte value){
        bindata.add(value);
    }

    public void putByteArray(byte[] bytes){
        for(byte b:bytes)putByte(b);
    }

    public byte[] getByteArray(int size){
        byte[] bytes = new byte[size];
        for(int i=0;i<size;i++)bytes[i]=getByte();
        return bytes;
    }



    public int getFieldType(){
        return fieldTypes.get(fieldTypesPos++);
    }
    public void putFieldType(int value){
        fieldTypes.add(value);
    }

    public String getString(boolean searchable){
        int arr = 0;
        if(searchable)arr = 1;

        return strings[arr].get(stringsPos[arr]++);
    }
    public void putString(boolean searchable,String value){
        int arr = 0;
        if(searchable)arr = 1;

        strings[arr].add(value);
    }


    public boolean endOfStream(){
        return stringsPos[0]==strings[0].size() &&
                stringsPos[1]==strings[1].size() &&
                 intsPos[0]==ints[0].size() &&
                  intsPos[1]==ints[1].size() &&
                   fieldTypesPos==fieldTypes.size();
    }

    public void resetCursor(){
        stringsPos[0] = 0;
        stringsPos[1] = 0;
        intsPos[0] = 0;
        intsPos[1] = 0;
        fieldTypesPos = 0;
    }


    private ArrayList<Integer> integersFromByteArray(byte[] bytes){
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        ArrayList<Integer> integers = new ArrayList<Integer>();

        while(buffer.hasRemaining())integers.add(buffer.getInt());

        return integers;
    }

    private ArrayList elementsFromDelimitedString(String string, boolean convertToIntegers){
        ArrayList<String> strings = split(string,RawFieldDataStream.STRING_DELIMITER);

        if(!convertToIntegers)return strings;

        ArrayList<Integer> list = new ArrayList<Integer>();

        for(String x:strings){
            int num = 0;
            try{num=Integer.parseInt(x);}
            catch (Exception e){}
            list.add(num);
        }
        return list;

    }

    private ArrayList<String> split(String string,char deli){
        ArrayList<String> list = new ArrayList<String>();

        int currentPos = 0;

        for(int i=0;i<string.length();i++){
            if(string.charAt(i)==deli){
                list.add(string.substring(currentPos,i));
                currentPos=i+1;
            }
        }

        return list;
    }

}
