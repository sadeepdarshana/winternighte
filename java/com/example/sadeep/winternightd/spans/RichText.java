package com.example.sadeep.winternightd.spans;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.example.sadeep.winternightd.dumping.FieldDataStream;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Sadeep on 10/13/2016.
 */

/**
 * A class to store text containing spans.
 *
 * A spannable text cannot directly be stored or communicated, so this class made of more primitive data types works as a middle man.
 */
public class RichText {

    public String text = ""; //the simple String without formatting
    public int spansCount = 0;
    private int[] starts, ends, types; // start of the span , end of the span , type of the span.

    private RichText(int n)
    {
        spansCount = n;

        starts = new int[n];
        ends = new int[n];
        types = new int[n];
    }

    /**
     * Notice that the constructor is private, it leaves the following method the only way of creating RichText instances.
     *
     * getText() from a TextView derived class can be directly used as the text argument here.
     */
    public static RichText generateRichText(Spanned text)
    {
        SpansFactory.RecognizedSpan[] spans = text.getSpans(0, text.length(), SpansFactory.OrdinarySpan.class);

        RichText richText = new RichText(spans.length);

        richText.text=text.toString();

        for (int c=0;c<spans.length;c++)
        {
            richText.starts[c] = text.getSpanStart(spans[c]);
            richText.ends[c] = text.getSpanEnd(spans[c]);
            richText.types[c] = spans[c].getSpanType();
        }

        return richText;
    }

    /**
     * get the spannable string for the RichText.
     * you can directly use the returned CharSequence in a setText() of a child class of TextView.
     *
     * It's recommended this be done in a non-UI thread because the operation is a bit expensive.
     */
    public static SpannableStringBuilder getCharSequence(RichText richText){

        SpannableStringBuilder text = new SpannableStringBuilder(richText.text);

        for (int c = 0; c < richText.spansCount; c++){
            text.setSpan(SpansFactory.createSpan(richText.types[c]), richText.starts[c], richText.ends[c],  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return text;
    }

    public  SpannableStringBuilder getCharSequence(){
        return RichText.getCharSequence(this);
    }

    public void writeToFieldDataStream(FieldDataStream fieldDataStream){

        fieldDataStream.putInt(false,spansCount);

        for(int c=0;c<spansCount;c++){
            fieldDataStream.putInt(false,starts[c]);
            fieldDataStream.putInt(false,ends[c]);
            fieldDataStream.putInt(false,types[c]);
        }

        fieldDataStream.putString(true,text);
    }
    public static RichText readFromFieldDataStream(FieldDataStream stream){

        RichText rt = new RichText(stream.getInt(false));

        for(int c=0;c<rt.spansCount;c++){
            rt.starts[c] = stream.getInt(false);
            rt.ends[c] = stream.getInt(false);
            rt.types[c] = stream.getInt(false);
        }

        rt.text = stream.getString(true);

        return rt;
    }

}
