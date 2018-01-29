package com.example.sadeep.winternightd.field.fielddata;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.sadeep.winternightd.misc.Utils;

/**
 * Created by Sadeep on 1/25/2018.
 */

public class BitmapData extends BinaryData {

    private Bitmap bmp;
    private Bitmap sampleBmp;

    public BitmapData(Bitmap data) {
        super(Utils.bitmapToByteArray(data));
        bmp = data;
    }

    public BitmapData(String id) {
        super(id);
        //bmp = Utils.byteArrayToBitmap(getData());
    }
    public BitmapData(byte [] bytes) {
        super(bytes);
        //bmp = Utils.byteArrayToBitmap(getData());
    }

    public Bitmap getBitmap() {
        if(bmp!=null)return bmp;
        bmp = Utils.byteArrayToBitmap(getData());
        return bmp;
    }

    public Bitmap getSampleBmp() {
        if(sampleBmp!=null)return sampleBmp;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = 5;
        Bitmap sampleBmp = BitmapFactory.decodeByteArray(getData(),0,getData().length,bmOptions);
        if(sampleBmp.getWidth()*sampleBmp.getHeight()<50000){
            bmOptions.inSampleSize = 1;
            sampleBmp = BitmapFactory.decodeByteArray(getData(),0,getData().length,bmOptions);
        }
        return sampleBmp;
    }

}
