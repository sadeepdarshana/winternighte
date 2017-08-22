package com.example.sadeep.winternightd.misc;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.InputStream;

/**
 * Created by Sadeep on 8/19/2017.
 */

public class NotebookIcon extends CardView {


    private  Drawable drawable;
    ImageView image = new ImageView(getContext());


    public NotebookIcon(Context context, String drawableName) {
        super(context);

        try {
            InputStream is = getContext().getAssets().open("pic/" + drawableName+".png");
            drawable = Drawable.createFromResourceStream(getResources(), null, is, null);
        }catch (Exception e){}

        init();
    }

    public NotebookIcon(Context context, Drawable drawable) {
        super(context);
        this.drawable = drawable;
        init();
    }

    public NotebookIcon(Context context) {
        super(context);
        init();
    }

    public NotebookIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NotebookIcon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Drawable getIcon(String icon){
        return getIcon(getContext(),icon);
    }

    public static Drawable getIcon(Context context, String icon){
        try {
            InputStream is = context.getAssets().open("pic/" + icon+".png");
            return Drawable.createFromResourceStream(context.getResources(), null, is, null);
        }catch (Exception e){
            return null;
        }
    }

    private void init() {
        setCardBackgroundColor(0xffebebeb);
        setCardElevation(0);
        setRadius(Globals.dp2px * 25);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Globals.dp2px * 50, Globals.dp2px * 50);
        setLayoutParams(params);

        CardView.LayoutParams imageParams = new CardView.LayoutParams(Globals.dp2px * 32, Globals.dp2px * 32);
        imageParams.gravity = Gravity.CENTER;
        imageParams.setMargins(Globals.dp2px*1,0,0,0);
        image.setLayoutParams(imageParams);
        addView(image);

        if(drawable!=null)image.setImageDrawable(drawable);
    }

    public void bindImage(String icon){
        image.setImageDrawable(getIcon(icon));
    }
}
