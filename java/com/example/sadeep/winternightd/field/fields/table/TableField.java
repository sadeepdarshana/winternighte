package com.example.sadeep.winternightd.field.fields.table;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.sadeep.winternightd.dumping.FieldDataStream;
import com.example.sadeep.winternightd.field.fields.Field;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.misc.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sadeep on 2/1/2018.
 */

public class TableField extends Field {

    public static final int classFieldType = 1786423781;


    private boolean firstRowIsHeader = false;

    private int rowCount=0,columnCount;

    private HorizontalScrollView hsv;
    GridLayout grid;
    private TextView[][] textviews;
    private boolean listenTextChanges =false;

    private Timer timer = new Timer();
    TimerTask saveNoteTask = new TimerTask() {
        @Override
        public void run() {
            getNote().saveIfInNotebookViewMode();
        }
    };

    public TableField(Context context) {
        super(context);
        init();
    }

    //new
    public TableField(Context context,TableFieldParams params) {
        super(context);
        this.firstRowIsHeader = params.firstRowIsHeader;
        init();
        initNewTextBoxes(params.columnCount,firstRowIsHeader?2:1,firstRowIsHeader);
        textviews[0][0].requestFocus();
    }

    private void addNewRow(){
        grid.setRowCount(rowCount+1);
        for(int x=0;x<columnCount;x++){
            textviews[x][rowCount]=get(x,rowCount);
            textviews[x][rowCount].setTag(rowCount);
            //textviews[x][rowCount].setHint("*");
            grid.addView(textviews[x][rowCount]);
        }
        rowCount++;
    }

    private void initNewTextBoxes(int finalColumnCount,int finalRowCount,boolean firstRowIsHeader){
        listenTextChanges=false;
        textviews = new TextView[finalColumnCount][1000];

        grid.setColumnCount(finalColumnCount);
        columnCount = finalColumnCount;

        for(int y=0;y<finalRowCount;y++)addNewRow();

        if(firstRowIsHeader)
            for(int x=0;x<finalColumnCount;x++){
                textviews[x][0].setBackgroundColor(0xff4472C4);
                textviews[x][0].setTextColor(0xffffffff);
                textviews[x][0].setTypeface(Typeface.DEFAULT_BOLD);
        }
        listenTextChanges = true;
    }

    private TextView get(int x,int y){
        final TextView tv= new EditText(getContext());
        tv.setText("");
        tv.setGravity(Gravity.CENTER_HORIZONTAL);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                GridLayout.spec(y, GridLayout.FILL,1f),
                GridLayout.spec(x, GridLayout.FILL,1f));
        int borderWidth = Math.max((int) (Globals.dp2px*.5),1);
        params.setMargins(borderWidth,borderWidth,borderWidth,borderWidth);
        tv.setPadding(Globals.dp2px*3,Globals.dp2px*3,Globals.dp2px*3,Globals.dp2px*3);
        tv.setLayoutParams(params);
        tv.setMinEms(4);
        tv.setBackgroundColor(0xffffffff);

        tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!listenTextChanges)return;
                getNote().saveIfInNotebookViewMode();
                if(s.length()!=0){
                    int i = (int) tv.getTag();
                    if(i==rowCount-1)addNewRow();
                }
            }
        });

        return tv;
    }

    private void init(){
        fieldType = classFieldType;
        hsv=new HorizontalScrollView(getContext());
        hsv.setBackgroundColor(0xff4466ff);
        //hsv.setVerticalScrollBarEnabled(true);
        //hsv.setScrollbarFadingEnabled(false);
        hsv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //grid.setLayoutParams(new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        hsv.setFillViewport(true);
        hsv.setPadding(Globals.dp2px*1,Globals.dp2px*1,Globals.dp2px*1,Globals.dp2px*1);
        addView(hsv);
        grid = new GridLayout(getContext());
        hsv.addView(grid);


    }

    @Override
    public void writeToFieldDataStream(FieldDataStream stream) {
        super.writeToFieldDataStream(stream);

        stream.putInt(rowCount);        //0
        stream.putInt(columnCount);     //1
        stream.putInt(firstRowIsHeader?1:0);    //2

        for(int y=0;y<rowCount;y++)for(int x=0;x<columnCount;x++)stream.putString(textviews[x][y].getText().toString());//3
    }

    @Override
    public void readFromFieldDataStream(FieldDataStream stream) {
        super.readFromFieldDataStream(stream);
        //if(true)return;
        int finalRowCount=stream.getInt();                //0
        int finalColumnCount = stream.getInt();             //1
        firstRowIsHeader = stream.getInt()==1;  //2

        initNewTextBoxes(finalColumnCount,finalRowCount,firstRowIsHeader);
        listenTextChanges=false;
        for(int y=0;y<rowCount;y++)for(int x=0;x<columnCount;x++)textviews[x][y].setText(stream.getString());   //3
        listenTextChanges=true;

    }
}
