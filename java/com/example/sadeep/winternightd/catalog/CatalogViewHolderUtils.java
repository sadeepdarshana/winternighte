package com.example.sadeep.winternightd.catalog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sadeep.winternightd.activities.NotebookActivity;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.notebook.Notebook;
import com.example.sadeep.winternightd.notebook.NotebookInfo;

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

        public NotebookRootHolder(Context context) {
            super(context);
            this.context = context;

            setBackgroundColor(Color.WHITE);
            setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotebookRootHolder.this.onClick();
                }
            });

            title = new TextView(context);
            title.setTextSize(TypedValue.COMPLEX_UNIT_FRACTION, Globals.defaultFontSize*1.5f);
            title.setLayoutParams(new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,100*Globals.dp2px));
            addView(title);
        }

        public void bind(NotebookInfo info){
            title.setText(info.title);
            notebookInfo = info;
        }

        protected void onClick() {
            Intent intent = new Intent(context, NotebookActivity.class);
            Bundle b = new Bundle();
            b.putString("notebookUUID",notebookInfo.notebookUUID);
            b.putString("title",notebookInfo.title);
            intent.putExtras(b);
            context.startActivity(intent);
        }

    }


    static class Footer extends LinearLayout{
        //// TODO: 7/13/2017 following code is copy from Notebook's codes, should delete this method and rewrite it
        public Footer(Context context, Notebook notebook) {
            super(context);

            LayoutParams params = new LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,1);
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


