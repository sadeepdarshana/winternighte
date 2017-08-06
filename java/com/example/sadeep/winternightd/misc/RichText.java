package com.example.sadeep.winternightd.misc;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.example.sadeep.winternightd.factories.SpansFactory;

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

    public static RichText generateRichText(Spanned text)
    {
        SpansFactory.RecognizedSpan[] spans = text.getSpans(0, text.length(), SpansFactory.RecognizedSpan.class);

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

    public static SpannableStringBuilder getCharSequence(RichText richText){
        SpannableStringBuilder text = new SpannableStringBuilder(richText.text);
        for (int c = 0; c < richText.spansCount; c++)
            {
            text.setSpan(SpansFactory.createSpan(richText.types[c]), richText.starts[c], richText.ends[c],  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return text;
    }

    public  SpannableStringBuilder getCharSequence(){
        return RichText.getCharSequence(this);
    }
}
