package com.example.sadeep.winternightd.spans;

import android.text.Spannable;
import android.text.Spanned;

import java.util.ArrayList;
import java.util.Comparator;

import static com.example.sadeep.winternightd.spans.SpansFactory.*;

/**
 * Created by Sadeep on 6/10/2017.
 */

public class SpanWrapper {

    public RecognizedSpan span;
    public int start;
    public int end;

    public SpanWrapper(RecognizedSpan span, int start, int end) {
        this.span = span;
        this.start = start;
        this.end = end;
    }

    public static class CompareSpanWrapperByStart implements Comparator<SpanWrapper>{
        @Override
        public int compare(SpanWrapper lhs, SpanWrapper rhs) {
            return lhs.start-rhs.start;
        }
    }

    public SpanWrapper duplicate(){
        return new SpanWrapper(SpansFactory.duplicateSpan(span),start,end);
    }

    /**
     * Returns the SpanWrapper that that has a lower start value
     */
    public static SpanWrapper minByStart(SpanWrapper a, SpanWrapper b){
        if(a.start<=b.start)return a;
        return b;
    }

    /**
     * Returns the SpanWrapper that that has a higher start value
     */
    public static SpanWrapper maxByStart(SpanWrapper a, SpanWrapper b){
        if(a.start<=b.start)return b;
        return a;
    }


    /**
     * Does this SpanWrapper lie inside the region of outer?
     */
    public boolean isInside(SpanWrapper outer){
        SpanWrapper inner = this;

        if(inner.start<outer.start)return false;
        if(inner.end>outer.end)return false;

        return true;
    }

    /**
     * Does this SpanWrappers region overlap with that of a?
     */
    public boolean overlapsWith(SpanWrapper a){
        if(a.isInside(this)||this.isInside(a))return true;

        SpanWrapper min = minByStart(a,this);
        SpanWrapper max = maxByStart(a,this);
        if(min.end <= max.start)return false;

        return true;
    }

    public boolean neighbourOf(SpanWrapper a){
        return ( a.end == this.start  ||  a.start == this.end );
    }

    /**
     * Does part of this overlap with part of a?
     */
    public boolean overlapsPartiallyWith(SpanWrapper a){
        if(a.isInside(this)||this.isInside(a))return false;
        return overlapsWith(a);
    }

    /**
     *  Attempts to create a new SpanWrapper(which contains a new span) by combining the given 2 SpanWrappers.
     *  This is possible when the 2 SpanWrappers neighbour each other or overlap with each other.
     *  Returns the new SpanWrapper or null if unable to combine.
     */
    private static SpanWrapper combineIfPossible(SpanWrapper a,SpanWrapper b){
        //if(!a.span.equals(b.span))return null; //only same type of spans(wrappers) can be combined

        if(!a.overlapsWith(b) && !a.neighbourOf(b))return null;

        if(a.isInside(b))return b.duplicate();
        if(b.isInside(a))return a.duplicate();

        if(a.neighbourOf(b)){
            if (a.end == b.start) return new SpanWrapper(SpansFactory.duplicateSpan(a.span),a.start,b.end);
            return new SpanWrapper(SpansFactory.duplicateSpan(a.span),b.start,a.end);
        }

        if(a.overlapsPartiallyWith(b)){
            return new SpanWrapper(a.span,minByStart(a,b).start,maxByStart(a,b).end);
        }

        return null; //assert: this statement should not be reached, should have returned already
    }

    public static void combinePossibleSpanWrappers(ArrayList<SpanWrapper> list,Spannable text){
        for(int i=0;i<list.size()-1;i++){
            SpanWrapper newSpanWrapper = combineIfPossible(list.get(i),list.get(i+1));
            if(newSpanWrapper == null)continue;

            for(int c=0;c<2;c++) {
                text.removeSpan(list.get(i).span);
                list.remove(i);
            }

            text.setSpan(newSpanWrapper.span,newSpanWrapper.start,newSpanWrapper.end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            list.add(i,newSpanWrapper);

            i--;
        }
    }

    /**
     *  Makes the given region free of spans by erasing or trimming existing spans
     */
    public static void eraseSpanWrappersFromRegion(ArrayList<SpanWrapper> list,Spannable text,int start,int end){
        SpanWrapper region = new SpanWrapper(null,start,end);//just because isInside() etc. are defined only for 2 SpanWrappers

        for(int i=0;i<list.size();i++) {
            SpanWrapper spanWrapper = list.get(i);

            if(!spanWrapper.overlapsWith(region))continue;

            list.remove(spanWrapper);
            i--;
            text.removeSpan(spanWrapper.span);

            SpanWrapper [] wrappers = new SpanWrapper[2];
            wrappers[0] = new SpanWrapper(SpansFactory.duplicateSpan(spanWrapper.span),spanWrapper.start,region.start);
            wrappers[1] = new SpanWrapper(SpansFactory.duplicateSpan(spanWrapper.span),region.end,spanWrapper.end);

            for(SpanWrapper wrapper:wrappers){
                if(wrapper.start>=wrapper.end)continue;

                list.add(i+1,wrapper);
                text.setSpan(wrapper.span,wrapper.start,wrapper.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                i++;
            }
        }
    }
}
