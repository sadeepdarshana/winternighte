package com.example.sadeep.winternightd.catalog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.activities.CatalogActivity;
import com.example.sadeep.winternightd.activities.NotebookActivity;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.misc.NotebookIcon;
import com.example.sadeep.winternightd.misc.Utils;
import com.example.sadeep.winternightd.notebook.Notebook;
import com.example.sadeep.winternightd.notebook.NotebookInfo;
import com.example.sadeep.winternightd.temp.d;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Sadeep on 6/17/2017.
 */

final class CatalogViewHolderUtils {
    private CatalogViewHolderUtils(){}//static class

    public static final int VIEWTYPE_NOTEBOOK_ROOT = 0;

    static class CatalogViewHolder extends RecyclerView.ViewHolder{
        public ViewGroup holder;
        public CatalogViewHolder(ViewGroup holder) {
            super(holder);
            this.holder = holder;
        }
    }


    static class NotebookRootHolder extends LinearLayout{

        private TextView title;
        private Context context;
        private NotebookInfo notebookInfo;
        private NotebookIcon icon;

        public NotebookRootHolder(Context context) {
            super(context);
            this.context = context;

            setOrientation(VERTICAL);
            //setBackgroundColor(Color.WHITE);
            setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotebookRootHolder.this.onClick();
                }
            });

            LinearLayout l1 =new LinearLayout(getContext());
            addView(l1);

            icon = new NotebookIcon(getContext(),"home");
            l1.addView(icon);
            LinearLayout.LayoutParams params = (LayoutParams) icon.getLayoutParams();
            params.setMargins(Globals.dp2px*15,Globals.dp2px*15,Globals.dp2px*15,Globals.dp2px*15);

            title = new TextView(context);
            title.setTextSize(TypedValue.COMPLEX_UNIT_FRACTION, Globals.defaultFontSize*1.2f);
            title.setTextColor(0xff000000);
            title.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            ((LinearLayout.LayoutParams)title.getLayoutParams()).setMargins(0,Globals.dp2px*20,0,0);
            l1.addView(title);

            View bottomLine = new View(getContext());
            LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(MATCH_PARENT,1);
            p1.setMargins(85*Globals.dp2px,0,0,0);
            bottomLine.setLayoutParams(p1);
            bottomLine.setBackgroundColor(0xffdddddd);
            addView(bottomLine);


            final GestureDetector.SimpleOnGestureListener longTapDetector = new GestureDetector.SimpleOnGestureListener(){
                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);
                    //todo
                    CatalogActivity activity = (CatalogActivity)getContext();
                    Utils.showKeyboard(title);
                    activity.displayAddEditDialog(notebookInfo,true);
                }
            };

            final GestureDetector detector = new GestureDetector(longTapDetector);

            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return detector.onTouchEvent(event);
                }


            });

            int[] attrs = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = context.obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            setBackgroundResource(backgroundResource);
            typedArray.recycle();

        }

        public void bind(NotebookInfo info){
            title.setText(info.title);
            notebookInfo = info;
            icon.bindImage(info.icon);
        }

        protected void onClick() {
            Intent intent = new Intent(context, NotebookActivity.class);
            Bundle b = new Bundle();
            b.putString("notebookUUID",notebookInfo.notebookUUID);
            b.putString("title",notebookInfo.title);
            intent.putExtras(b);
            context.startActivity(intent);
            ((Activity)getContext()).finish();
        }

    }


    static class Footer extends LinearLayout{
        //// TODO: 7/13/2017 following code is copy from Notebook's codes, should delete this method and rewrite it
        public Footer(Context context, Notebook notebook) {
            super(context);

            LayoutParams params = new LayoutParams(MATCH_PARENT,1);
            setLayoutParams(params);

            notebook.bottomBar.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View xv, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    {
                        Footer.this.getLayoutParams().height = bottom;
                        Footer.this.requestLayout();
                    }
                }
            });
        }
    }
}


