package com.example.sadeep.winternightd.field.fields;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.dumping.FieldDataStream;
import com.example.sadeep.winternightd.field.fielddata.BinaryData;
import com.example.sadeep.winternightd.field.fielddata.BitmapData;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.misc.Utils;

/**
 * Created by Sadeep on 1/10/2018.
 */

public class ImageField extends IndentedField {



    public static final int classFieldType = 786419732;

    private ImageView image;
    private Bitmap bmp;
    private BitmapData bitmapData;

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
        image.setPadding(0,Globals.dp2px*2,0,Globals.dp2px*2);
        image.setBackgroundColor(0xff888888);




    }

    public void setImageBitmap(final BitmapData bitmapData){
        this.bmp = bitmapData.getSampleBmp();
        this.bitmapData = bitmapData;

        image.setImageBitmap(bmp);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        image.setScaleType(ImageView.ScaleType.MATRIX);
        image.setAdjustViewBounds(true);
        //image.setPadding(Globals.dp2px*10,0,Globals.dp2px*10,0);


    }





    @Override
    public void writeToFieldDataStream(FieldDataStream stream) {
        super.writeToFieldDataStream(stream);
        stream.putString(false,bitmapData.getId());     //0
    }
    @Override
    public void readFromFieldDataStream(FieldDataStream stream) {
        super.readFromFieldDataStream(stream);

        String bitmapDataId = stream.getString(false);
        bitmapData = new BitmapData(bitmapDataId);
        setImageBitmap(bitmapData);
    }

    @Override
    public int getWritableByteArraySize() {
        return 0;
    }
}
