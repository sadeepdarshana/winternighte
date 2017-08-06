package com.example.sadeep.winternightd.field;

import android.graphics.Point;
import android.widget.TextView;

/**
 * Created by Sadeep on 10/20/2016.
 */
public interface SingleText
{
    TextView getMainTextBox(); //the View that contains the main text. this would probably be a XEditText or a XTextView

    Point getAbsoluteCoordinatesForSingleTextCharacterIndex(int characterIndex);

    int getSingleTextCharacterIndexForAbsoluteCoordinates(int rawX, int rawY);
}
