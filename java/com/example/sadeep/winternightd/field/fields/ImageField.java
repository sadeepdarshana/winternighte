package com.example.sadeep.winternightd.field.fields;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.sadeep.winternightd.misc.Globals;

/**
 * Created by Sadeep on 1/10/2018.
 */

public class ImageField extends IndentedField {

    private ImageView image;

    public ImageField(Context context) {
        super(context);

        image = new ImageView(getContext());
        addView(image);
    }

    public ImageField(Context context, Bitmap bmp){
        this(context);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setAdjustViewBounds(true);
        image.setPadding(Globals.dp2px*10,0,Globals.dp2px*10,0);
        image.setImageBitmap(bmp);
    }
}
