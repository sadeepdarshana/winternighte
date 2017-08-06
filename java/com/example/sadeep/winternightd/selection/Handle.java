package com.example.sadeep.winternightd.selection;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.note.Note;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.notebook.Notebook;

/**
 * Created by Sadeep on 10/22/2016.
 */
public class Handle extends PopupWindow implements View.OnTouchListener {

    private ImageView handleImage;
    private Context context;
    private Note note;
    private Handler noteScroller;
    private CursorPosition cursorPosition=null;

    private int handleWidth=0;

    public Handle(Note note){
        super(-2,-2);

        context = note.getContext();
        this.note = note;

        init();
    }

    private void init() {

        handleImage = new ImageView(context);
        handleImage.setImageResource(R.drawable.text_select_handle);
        handleImage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        handleImage.setOnTouchListener(this);
        setContentView(handleImage);

        setSplitTouchEnabled(true);
        setClippingEnabled(false);
        setTouchable(true);
        setOutsideTouchable(true);
        setFocusable(false);


        noteScroller = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(note.getScrollableParent()instanceof RecyclerView)((RecyclerView)note.getScrollableParent()).smoothScrollBy(0, scrollDirection*25);
                if(note.getScrollableParent()instanceof ScrollView)((ScrollView)note.getScrollableParent()).smoothScrollBy(0, scrollDirection*25);

                if(XSelection.isSelectionAvailable())noteScroller.sendEmptyMessageDelayed(0,50);
            }
        };
        noteScroller.sendEmptyMessage(0);

        new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(XSelection.isSelectionAvailable()) {
                    sendEmptyMessageDelayed(0, 500);
                    XSelection.refreshHandlePositions();
                }
            }
        }.sendEmptyMessage(0);

    }

    private float dy=0;
    private int scrollDirection=0;
    @Override//we implement onTouch for ImageView, not PopupWindow
    public boolean onTouch(View v, MotionEvent e) {
        //scrolling if neccassary code below
        //boolean a =true;if(a)return true;
        Rect coords = new Rect();
        note.getGlobalVisibleRect(coords);

        int threshold;
        if(note.getScrollableParent()instanceof Notebook)threshold = Globals.dp2px*45;
        else threshold=0;

        if (coords.bottom +threshold< e.getRawY()) scrollDirection=1;
        else if (coords.top > e.getRawY()) scrollDirection = -1;
        else scrollDirection = 0;

        if (e.getAction() == MotionEvent.ACTION_DOWN)
        {
            dy = e.getY();
            return true;
        }
        else if (e.getAction() == MotionEvent.ACTION_MOVE)
        {
            tryMoveTo((int)e.getRawX(), (int)e.getRawY()-(int)dy);
        }
        else // Event Canceled
        {
            dy = 0;
            scrollDirection = 0;
        }

        return true;
    }

    private void tryMoveTo(int rawX, int rawY) {
        CursorPosition cpos = note.cursorPositionForCoordinate(new Point(rawX, rawY - Globals.dp2px*14));
        if(cpos.characterIndex!=CursorPosition.CHARACTERINDEX_ERROR)updatePosition(cpos, true);
    }

    void updatePosition(CursorPosition cpos, boolean handleCursorPositionChanged){
        Point pos = note.getAbsoluteCoordinatesForCursorPosition(cpos);
        updatePosition(pos);
        cursorPosition = cpos;

        XSelection.handlePositionUpdated(handleCursorPositionChanged);
    }

    private void updatePosition(Point absolutePos){
        int x = absolutePos.x, y = absolutePos.y;
        if(handleWidth==0)handleWidth = handleImage.getDrawable().getIntrinsicWidth();

        int finalX = x - handleWidth/2; //because handle needs to center
        int finalY = y;

        if (!isShowing()) showAtLocation(note, Gravity.NO_GRAVITY, finalX, finalY);
        else update(finalX, finalY, -1, -1);
    }

    CursorPosition getCursorPosition(){
        if(cursorPosition==null)return null;
        return cursorPosition;
    }
}
