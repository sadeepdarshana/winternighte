package com.example.sadeep.winternightd.spans;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import com.example.sadeep.winternightd.misc.Globals;

/**
 * Created by Sadeep on 10/13/2016.
 */

/***
 * All instantiating of bold, italic etc spans are done in this class.
 * Here we introduce our own types of spans used throughout the WN extending the built-in ones.
 * DO NOT USE ANY SPAN OTHER THAN FOLLOWING SPANS IN WN. (ie. DO NOT USE UnderlineSpan USE XUnderlineSpan INSTEAD)
 *
 * Span Types Alone with the Type
 * ------------------------------ *
 *
 * XSelectionSpan   -1 *(this one is a little bit different in the way is used)
 *
 * XBoldSpan        0
 * XItalicSpan      1
 * XUnderlineSpan   2
 * XHighlightSpan   3
 *
 */
final public class SpansFactory {
    private SpansFactory(){} //cannot be instantiated

    public static final int NO_OF_ORDINARY_SPAN_TYPES = 3;

    /***
     * RecognizedSpan: to be used as a common entity to easily query and select our considered span types. (when you look at a very normal Text in an EditText there are many other platform generated spans too, god only knows what they do)
     *                 Also includes our important method getSpanType.
     */
    public interface RecognizedSpan{
        int getSpanType();
    }
    public interface OrdinarySpan extends RecognizedSpan{}

    //region Spans Dictionary
    public static class XSelectionSpan extends BackgroundColorSpan implements RecognizedSpan {
        public static final int  spanType = -1;
        private XSelectionSpan() {
            super(Globals.defaultHighlightColor);
        }
        @Override
        public int getSpanType() {
            return spanType;
        }
        @Override
        public boolean equals(Object o) {
            return o.getClass()==getClass();
        }
    }
    public static class XBoldSpan extends StyleSpan implements OrdinarySpan {
        public static final int spanType = 0;
        private XBoldSpan() {
            super(Typeface.BOLD);
        }
        @Override
        public int getSpanType() {
            return spanType;
        }

        @Override
        public boolean equals(Object o) {
            return o.getClass()==getClass();
        }
    }
    public static class XItalicSpan extends StyleSpan implements OrdinarySpan {
        public static final int spanType = 1;
        private XItalicSpan() {
            super(Typeface.ITALIC);
        }
        @Override
        public int getSpanType() {
            return spanType;
        }
        @Override
        public boolean equals(Object o) {
            return o.getClass()==getClass();
        }
    }
    public static class XUnderlineSpan extends UnderlineSpan implements OrdinarySpan {
        public static final int spanType = 2;
        private XUnderlineSpan() {
            super();
        }
        @Override
        public int getSpanType() {
            return spanType;
        }
        @Override
        public boolean equals(Object o) {
            return o.getClass()==getClass();
        }
    }
    public static class XHighlightSpan extends BackgroundColorSpan implements OrdinarySpan {
        public static final int spanType = 3;
        private XHighlightSpan() {
            super(Color.GREEN);
        }
        @Override
        public int getSpanType() {
            return spanType;
        }
        @Override
        public boolean equals(Object o) {
            return o.getClass()==getClass();
        }
    }
    //endregion


    /**
     * Creates spans given the span type.
     */
    public static RecognizedSpan createSpan(int type){

        switch (type)
        {
            case XSelectionSpan.spanType: return new XSelectionSpan();
            case XBoldSpan.spanType: return new XBoldSpan();
            case XItalicSpan.spanType: return new XItalicSpan();
            case XUnderlineSpan.spanType: return new XUnderlineSpan();
            case XHighlightSpan.spanType: return new XHighlightSpan();
        }

        return null;
    }

    /**
     *  Returns an identical new span to the given span
     */
    public static RecognizedSpan duplicateSpan(RecognizedSpan span){
        return createSpan(span.getSpanType());
    }


}
