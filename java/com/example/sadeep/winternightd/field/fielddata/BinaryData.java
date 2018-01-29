package com.example.sadeep.winternightd.field.fielddata;

import com.example.sadeep.winternightd.misc.Globals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.microedition.khronos.opengles.GL;

/**
 * Created by Sadeep on 1/25/2018.
 */

public class BinaryData extends FieldData {

    private byte[] data;

    public BinaryData(byte[] data) {
        this.data = data;

        File file=new File(Globals.respath, getId());
        FileOutputStream fout= null;
        try {
            fout = new FileOutputStream(file, false);
            fout.write(data);
            fout.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }
    public BinaryData(String id){
        super(id);

        File file=new File(Globals.respath, getId());
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        data=bytes;

    }

    public byte[] getData() {
        return data;
    }
}
