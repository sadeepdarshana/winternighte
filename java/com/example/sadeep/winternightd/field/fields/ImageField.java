package com.example.sadeep.winternightd.field.fields;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadeep.winternightd.dumping.FieldDataStream;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.misc.Utils;

/**
 * Created by Sadeep on 1/10/2018.
 */

public class ImageField extends IndentedField {



    public static final int classFieldType = 786419732;

    private ImageView image;
    private Bitmap bmp;

    public ImageField(Context context) {
        this(false,context);
    }

    public ImageField(boolean isEditable,Context context){
        super(context);
        init();
    }

    private void init(){

        fieldType = classFieldType;


        image = new ImageView(getContext());
        addView(image);




    }

    public void setImageBitmap(Bitmap bmp){
        this.bmp = bmp;

        image.setImageBitmap(bmp);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setAdjustViewBounds(true);
        image.setPadding(Globals.dp2px*10,0,Globals.dp2px*10,0);
    }





    @Override
    public void writeToFieldDataStream(FieldDataStream stream) {
        super.writeToFieldDataStream(stream);

        byte[] arr = Utils.bitmapToByteArray(bmp);

        stream.putInt(false,arr.length);        // 0 size of int array

        stream.putByteArray(arr);   //1 bytearray
    }
    @Override
    public void readFromFieldDataStream(FieldDataStream stream) {
        super.readFromFieldDataStream(stream);

        int size = stream.getInt(false);    // 0 size of int array

        byte[] arr = stream.getByteArray(size); //1 bytearray

        setImageBitmap(Utils.byteArrayToBitmap(arr));

    }

}
