package com.example.sadeep.winternightd.factories;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

/**
 * Created by Sadeep on 10/13/2016.
 */

/***
 * All instantiating of bold, italic etc spans are done in this class.
 * Here we introduce our own types of spans used throughout the WN extending the built-in ones.
 *
 * Span Types Alone with the Type
 * ------------------------------ *
 * XBoldSpan        0
 * XItalicSpan      1
 * XUnderlineSpan   2
 * XHighlightSpan   3
 */
final public class SpansFactory {
    private SpansFactory(){} //cannot be instantiated


    /***
     * RecognizedSpan: to be used as a common entity to easily query and select our considered span types. (when you look at a very normal Text in an EditText there are many other platform generated spans too, god only knows what they do)
     *                 Also includes our important method getSpanType.
     */
    public interface RecognizedSpan{
        int getSpanType();
    }

    //region Spans Dictionary
    public static class XBoldSpan extends StyleSpan implements RecognizedSpan {
        private XBoldSpan() {
            super(Typeface.BOLD);
        }
        @Override
        public int getSpanType() {
            return 0;
        }
    }
    public static class XItalicSpan extends StyleSpan implements RecognizedSpan {
        private XItalicSpan() {
            super(Typeface.ITALIC);
        }
        @Override
        public int getSpanType() {
            return 1;
        }
    }
    public static class XUnderlineSpan extends UnderlineSpan implements RecognizedSpan {
        private XUnderlineSpan() {
            super();
        }
        @Override
        public int getSpanType() {
            return 2;
        }
    }
    public static class XHighlightSpan extends BackgroundColorSpan implements RecognizedSpan {
        private XHighlightSpan() {
            super(Color.GREEN);
        }
        @Override
        public int getSpanType() {
            return 3;
        }
    }
    //endregion


    //this is the method that creates spans for the entire WN.
    public static Object createSpan(int type){

        switch (type)
        {
            case 0: return new XBoldSpan();
            case 1: return new XItalicSpan();
            case 2: return new XUnderlineSpan();
            case 3: return new XHighlightSpan();
        }

        return null;
    }
}
