package com.example.sadeep.winternightd.misc;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.widget.EditText;

/**
 * Created by Sadeep on 10/12/2016.
 */
final public  class Globals {

    public static float defaultFontSize;

    private Globals(){}

    public static int dp2px;

    public static int defaultHighlightColor = Color.rgb(95,158,160);

    public static void initialize(Context context){
        dp2px = (int)((float)context.getResources().getDisplayMetrics().densityDpi / (float) DisplayMetrics.DENSITY_DEFAULT);
        defaultFontSize = new EditText(context).getTextSize();
    }

}
