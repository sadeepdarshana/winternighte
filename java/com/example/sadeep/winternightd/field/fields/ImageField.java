package com.example.sadeep.winternightd.field.fields;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.sadeep.winternightd.textboxes.EditTextView;

/**
 * Created by Sadeep on 1/10/2018.
 */

public class ImageField extends IndentedField {

    private ImageView imageView;

    public ImageField(Context context) {
        super(context);

        imageView = new ImageView(getContext());
        addView(imageView);
    }

    public ImageField(Context context, Bitmap bmp){
        this(context);
        imageView.setImageBitmap(bmp);
    }
}
