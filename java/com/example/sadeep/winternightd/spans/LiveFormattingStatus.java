package com.example.sadeep.winternightd.spans;

/**
 * Created by Sadeep on 6/10/2017.
 */

import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.toolbar.ToolbarController;

import java.util.ArrayList;
import java.util.Collections;

/**
 *  Stores the current state of Bold(whether bold is on or off), italic, underline, font color(which color is selected font color) etc.
 */
final public class LiveFormattingStatus {
    private LiveFormattingStatus() {}

    public static final int OFF = -1;
    public static final int ON =  1;

    /**
     *  format[i] is the status of the text formatting i, i is as given below
     *
     *  i   name                            possible values and statuses related to each value
     *
     *  0   bold                            -1=off , 1=on
     *  1   italic                          -1=off , 1=on
     *  2   underline                       -1=off , 1=on
     *  3   highlight                       -1=off , color as an integer
     *
     */
    //public static int format[] = new int[SpansFactory.NO_OF_ORDINARY_SPAN_TYPES];
    public static int format[] = {-1,-1,-1};


    public static void update(int[] spanStatus) {
        for(int i=0;i< SpansFactory.NO_OF_ORDINARY_SPAN_TYPES;i++)format[i]=spanStatus[i];
        ToolbarController.dispatchToolbarUpdates(spanStatus);
    }
}
