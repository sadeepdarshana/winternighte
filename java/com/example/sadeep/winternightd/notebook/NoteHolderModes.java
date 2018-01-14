package com.example.sadeep.winternightd.notebook;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.activities.NotebookActivity;
import com.example.sadeep.winternightd.animation.XAnimation;
import com.example.sadeep.winternightd.animation.XAnimationListener;
import com.example.sadeep.winternightd.attachbox.AttachBoxManager;
import com.example.sadeep.winternightd.attachbox.OnAttachBoxItemClick;
import com.example.sadeep.winternightd.bottombar.ExtendedToolbar;
import com.example.sadeep.winternightd.buttons.customizedbuttons.AttachBoxOpener;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.misc.NotebookItemChamber;
import com.example.sadeep.winternightd.notebook.NotebookViewHolderUtils.NoteHolder;

import java.text.Format;
import java.text.SimpleDateFormat;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.example.sadeep.winternightd.note.Note.STATE_DELETED;
import static com.example.sadeep.winternightd.note.Note.STATE_EDITED;
import static com.example.sadeep.winternightd.notebook.NoteHolderModes.ModeView.ViewLower.DATETIME_COLOR_DEFAULT;
import static com.example.sadeep.winternightd.notebook.NoteHolderModes.ModeView.ViewLower.DATETIME_COLOR_SPECIAL;

/**
 * Created by Sadeep on 7/24/2017.
 */

public class NoteHolderModes {

    public static final int MODE_VIEW = 0;
    public static final int MODE_EDIT = 1;
    public static final int MODE_SETTINGS = 2;
    public static final int DEFAULT_MODE = MODE_VIEW;

    public static class ModeView{

        public static void setAsNoteHolderMode(final NoteHolder noteHolder, boolean animate){
            if(noteHolder.getMode()== MODE_VIEW)return;


            if(noteHolder.getNote()!=null)noteHolder.setVisibility(
                    noteHolder.getNote().noteState==STATE_DELETED ? View.GONE:View.VISIBLE);

            if(noteHolder.getNote()!=null)noteHolder.getNote().setEditable(false);
            noteHolder.noteEditable = false;

            ViewLower viewLower = new ViewLower(noteHolder.getContext());
            ViewUpper viewUpper = new ViewUpper(noteHolder.getContext());
            noteHolder.getUpperChamber().setChamberContent(viewUpper,animate);
            noteHolder.getLowerChamber().setChamberContent(viewLower,animate);

            noteHolder.setRadius(Globals.dp2px*2);

            final GestureDetector gestureDetector = new GestureDetector(noteHolder.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    noteHolder.getNotebook().editor.cancelActiveNote();
                    noteHolder.getNoteSpace().setOnTouchListener(null);
                    noteHolder.setMode(MODE_EDIT,true);
                    setAsActiveNote(noteHolder);

                    return true;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    noteHolder.setMode(MODE_SETTINGS,true);
                    return true;
                }
            });

            noteHolder.getNoteSpace().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
            viewLower.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });

            noteHolder.mode = MODE_VIEW;

            if(noteHolder.getNote()!=null) {
                viewLower.setDateTime(noteHolder.getNote().noteInfo.currentVersionTime);
                viewLower.dateTimeTextView.setTextColor(
                        noteHolder.getNote().noteState==STATE_EDITED ? DATETIME_COLOR_SPECIAL:DATETIME_COLOR_DEFAULT);
            }
        }

        public static void onBind(NoteHolder noteHolder) {
            ViewLower viewLower = (ViewLower) noteHolder.getLowerChamber().getChamberContent();
            viewLower.setDateTime(noteHolder.getNote().noteInfo.currentVersionTime);
            viewLower.dateTimeTextView.setTextColor(
                    noteHolder.getNote().noteState==STATE_EDITED ? DATETIME_COLOR_SPECIAL:DATETIME_COLOR_DEFAULT);
        }

        public static void setAsActiveNote(NoteHolder noteHolder) {
            noteHolder.getNotebook().noteHolderController.setAllNoteHoldersModeExcept(DEFAULT_MODE,noteHolder,true);
            noteHolder.getNotebook().editor.setActiveNote(noteHolder.getNote());
            noteHolder.getNotebook().notebookActivity.refreshBottomBar();
        }

        public static class ViewLower extends FrameLayout implements NotebookItemChamber.ChamberContentView{
            private long dateTime;
            private TextView dateTimeTextView;

            public static final int DATETIME_COLOR_DEFAULT = 0xff999999;
            public static final int DATETIME_COLOR_SPECIAL = 0xff66cc66;

            public void setDateTime(long dateTime){
                this.dateTime = dateTime;

                try {
                    NoteHolder noteHolder = (NoteHolder) getParent().getParent().getParent();
                        dateTimeTextView.setTextColor(
                                noteHolder.getNote().noteState == STATE_EDITED ? DATETIME_COLOR_SPECIAL : DATETIME_COLOR_DEFAULT);
                }catch (Exception e){}
                updateDateTimeTextView();
            }

            public ViewLower(Context context) {
                super(context);
                setLayoutParams(new NotebookItemChamber.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

                dateTimeTextView = new TextView(context);
                dateTimeTextView.setTextColor(DATETIME_COLOR_DEFAULT);
                dateTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_FRACTION,  Globals.defaultFontSize*.67f);

                LayoutParams params=new LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
                params.gravity = Gravity.RIGHT;
                dateTimeTextView.setLayoutParams(params);

                addView(dateTimeTextView);
                updateDateTimeTextView();

                setPadding(Globals.dp2px*2,Globals.dp2px*4,Globals.dp2px*2,Globals.dp2px*4);

                //setVisibility(GONE);
            }

            private void updateDateTimeTextView() {
                Format dateFormat =new SimpleDateFormat("MMM d, ''yy h:mm a");
                dateTimeTextView.setText(dateFormat.format(dateTime));
                //todo omit year if same year etc + today,yesterday,6mins ago
            }

            @Override
            public void onRemoved() {

            }

            @Override
            public void onAttached() {

            }
        }
        public static class ViewUpper extends FrameLayout{
            public ViewUpper(Context context) {
                super(context);
                setLayoutParams(new NotebookItemChamber.LayoutParams(MATCH_PARENT, 10*Globals.dp2px));

            }

        }
    }
    public static class ModeEdit{

        public static void setAsNoteHolderMode(NoteHolder noteHolder, boolean animate){
            if(noteHolder.getMode()== MODE_EDIT)return;

            if(noteHolder.getNote()!=null)noteHolder.setVisibility(
                    noteHolder.getNote().noteState==STATE_DELETED ? View.GONE:View.VISIBLE);

            if(noteHolder.getNote()!=null)noteHolder.getNote().setEditable(true);
            noteHolder.noteEditable = true;

            noteHolder.getUpperChamber().setChamberContent(new EditUpper(noteHolder.getContext()),animate);
            noteHolder.getLowerChamber().setChamberContent(new EditLower(noteHolder.getContext(),noteHolder),animate);

            noteHolder.setRadius(Globals.dp2px*23);

            noteHolder.mode = MODE_EDIT;
        }

        public static void onBind(NoteHolder noteHolder) {

        }

        public static class EditUpper extends View {

            public EditUpper(Context context) {
                super(context);
                NotebookItemChamber.LayoutParams params = new NotebookItemChamber.LayoutParams(MATCH_PARENT, Globals.dp2px*25);
                setLayoutParams(params);
            }
        }

        public static class EditLower extends LinearLayout implements NotebookItemChamber.ChamberContentView{
            public PopupWindow toolbarPopupWindow;
            CountDownTimer timer;

            public EditLower(Context context, final NoteHolder noteHolder) {
                super(context);
                NotebookItemChamber.LayoutParams params1 = new NotebookItemChamber.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                setLayoutParams(params1);

                final ExtendedToolbar extendedToolbar = new ExtendedToolbar(context,true,true, true){
                    @Override
                    protected void onAttachClick(View v) {
                        super.onAttachClick(v);
                        if(!((AttachBoxOpener)v).isAttachboxOpen()) {
                            ((AttachBoxOpener) v).setAttachboxOpened(true);
                            AttachBoxManager.display(v,((NotebookActivity)v.getContext()).rootView.bottomLeftMarker, new OnAttachBoxItemClick() {
                                @Override
                                public void buttonClicked(int attachButtonId) {
                                    Notebook.suspendScrollTemporary();
                                    noteHolder.getNotebook().editor.getActiveNote().attachboxRequests(attachButtonId);
                                }
                            });
                        }else{
                            try {
                                AttachBoxManager.popupWindow.dismiss();
                            }catch (Exception e){}
                        }
                    }

                    @Override
                    protected void onCancelClick(View v) {
                        super.onCancelClick(v);
                        noteHolder.getNotebook().editor.cancelActiveNote();
                    }

                    @Override
                    protected void onSendClick(View v) {
                        super.onSendClick(v);
                        noteHolder.getNotebook().editor.pushActiveNote();
                    }
                };
                addView(extendedToolbar);

                ExtendedToolbar popupToolbar = new ExtendedToolbar(context,true,true, true){
                    @Override
                    protected void onAttachClick(View v) {
                        super.onAttachClick(v);
                        if(!((AttachBoxOpener)v).isAttachboxOpen()) {
                            ((AttachBoxOpener) v).setAttachboxOpened(true);
                            AttachBoxManager.display(v,((NotebookActivity)v.getContext()).rootView.bottomLeftMarker, new OnAttachBoxItemClick() {
                                @Override
                                public void buttonClicked(int attachButtonId) {
                                    Notebook.suspendScrollTemporary();
                                    noteHolder.getNotebook().editor.getActiveNote().attachboxRequests(attachButtonId);
                                }
                            });
                        }else{
                            try {
                                AttachBoxManager.popupWindow.dismiss();
                            }catch (Exception e){}
                        }
                    }

                    @Override
                    protected void onCancelClick(View v) {
                        super.onCancelClick(v);
                        noteHolder.getNotebook().editor.cancelActiveNote();
                    }

                    @Override
                    protected void onSendClick(View v) {
                        super.onSendClick(v);
                        noteHolder.getNotebook().editor.pushActiveNote();
                    }
                };
                final LinearLayout popupLayout = new LinearLayout(getContext());
                popupLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                final CardView popupCard = new CardView(getContext());
                popupCard.setPadding(Globals.dp2px*4,Globals.dp2px*4,Globals.dp2px*4,Globals.dp2px*4);
                popupLayout.addView(popupCard);
                popupCard.setCardBackgroundColor(0xffeeeeee);
                popupCard.setRadius(Globals.dp2px*23);
                popupCard.setCardElevation(Globals.dp2px*2);
                popupCard.addView(popupToolbar);
                final int[]xy=new int[2];
                noteHolder.getNotebook().getLocationOnScreen(xy);
                ((LinearLayout.LayoutParams)popupCard.getLayoutParams()).setMargins(Globals.dp2px*4,Globals.dp2px*4,Globals.dp2px*4,Globals.dp2px*4);
                toolbarPopupWindow = new PopupWindow(popupLayout,MATCH_PARENT,WRAP_CONTENT){
                    @Override
                    public void showAtLocation(View parent, int gravity, int x, int y) {
                        try {
                            if (!this.isShowing())
                                super.showAtLocation(EditLower.this, Gravity.TOP, 0, xy[1] );
                        }catch (Exception e){}
                    }


                    @Override
                    public void dismiss() {
                        try {
                            if (this.isShowing())
                                super.dismiss();
                        }catch (Exception e){}
                    }

                };
                timer= new CountDownTimer(100000000000L,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        if(noteHolder.getNotebook().notebookActivity.rootView.viewTopWithinRootViewBounds(extendedToolbar))
                            toolbarPopupWindow.dismiss();
                        else
                            toolbarPopupWindow.showAtLocation(null,0,0,0);
                    }

                    @Override
                    public void onFinish() {

                    }
                };
                timer.start();

                EditLower.LayoutParams params2= new EditLower.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                extendedToolbar.setLayoutParams(params2);
            }

            @Override
            public void onRemoved() {
                try{toolbarPopupWindow.dismiss();}catch (Exception e){}
                timer.cancel();
            }
            @Override
            public void onAttached() {
                timer.start();
            }
        }
    }
    public static class ModeSettings{

        public static void setAsNoteHolderMode(final NoteHolder noteHolder, boolean animate){
            if(noteHolder.getMode()== MODE_SETTINGS)return;
            if(noteHolder.getNotebook().editor.getActiveNote()!=null)return;


            if(noteHolder.getNote()!=null)noteHolder.setVisibility(
                    noteHolder.getNote().noteState==STATE_DELETED ? View.GONE:View.VISIBLE);

            noteHolder.getNotebook().noteHolderController.setAllNoteHoldersModeExcept(MODE_VIEW,null,true);

            if(noteHolder.getNote()!=null)noteHolder.getNote().setEditable(false);
            noteHolder.noteEditable = false;

            SettingsLower settingsLower = new SettingsLower(noteHolder.getContext(),noteHolder);
            SettingsUpper settingsUpper = new SettingsUpper(noteHolder.getContext());
            noteHolder.getUpperChamber().setChamberContent(settingsUpper,animate);
            noteHolder.getLowerChamber().setChamberContent(settingsLower,animate);

            noteHolder.setRadius(Globals.dp2px*2);

            final GestureDetector gestureDetector = new GestureDetector(noteHolder.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    noteHolder.getNotebook().editor.cancelActiveNote();
                    noteHolder.getNoteSpace().setOnTouchListener(null);
                    noteHolder.setMode(MODE_EDIT,true);
                    setAsActiveNote(noteHolder);

                    return true;
                }
            });

            noteHolder.getNoteSpace().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
            settingsLower.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });

            noteHolder.mode = MODE_SETTINGS;

            if(noteHolder.getNote()!=null) {
                settingsUpper.setDateTime(noteHolder.getNote().noteInfo.currentVersionTime);
                settingsUpper.dateTimeTextView.setTextColor(
                        noteHolder.getNote().noteState==STATE_EDITED ? DATETIME_COLOR_SPECIAL:DATETIME_COLOR_DEFAULT);
            }

            noteHolder.postDelayed(new Runnable() {
                @Override
                public void run() {
                    noteHolder.getNotebook().notebookActivity.enableBottomBarToGlassModeIfNecessary();
                }
            },200);
        }

        public static void onBind(NoteHolder noteHolder) {
            SettingsUpper settingsUpper = (SettingsUpper) noteHolder.getLowerChamber().getChamberContent();
            settingsUpper.setDateTime(noteHolder.getNote().noteInfo.currentVersionTime);
            settingsUpper.dateTimeTextView.setTextColor(
                    noteHolder.getNote().noteState==STATE_EDITED ? DATETIME_COLOR_SPECIAL:DATETIME_COLOR_DEFAULT);
        }

        public static void setAsActiveNote(NoteHolder noteHolder) {
            noteHolder.getNotebook().noteHolderController.setAllNoteHoldersModeExcept(DEFAULT_MODE,noteHolder,true);
            noteHolder.getNotebook().editor.setActiveNote(noteHolder.getNote());
            noteHolder.getNotebook().notebookActivity.refreshBottomBar();
        }

        public static class SettingsUpper extends FrameLayout implements NotebookItemChamber.ChamberContentView{
            private long dateTime;
            private TextView dateTimeTextView;

            public void setDateTime(long dateTime){
                this.dateTime = dateTime;
                updateDateTimeTextView();
            }

            public SettingsUpper(Context context) {
                super(context);
                setLayoutParams(new NotebookItemChamber.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

                dateTimeTextView = new TextView(context);
                dateTimeTextView.setTextColor(DATETIME_COLOR_DEFAULT);
                dateTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_FRACTION,  Globals.defaultFontSize*.67f);

                LayoutParams params=new LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
                params.gravity = Gravity.RIGHT;
                dateTimeTextView.setLayoutParams(params);

                addView(dateTimeTextView);
                updateDateTimeTextView();

                setPadding(Globals.dp2px*2,Globals.dp2px*4,Globals.dp2px*2,Globals.dp2px*4);

                //setVisibility(GONE);
            }

            private void updateDateTimeTextView() {
                Format dateFormat =new SimpleDateFormat("MMM d, ''yy h:mm a");
                dateTimeTextView.setText(dateFormat.format(dateTime));
                //todo omit year if same year etc + today,yesterday,6mins ago
            }

            @Override
            public void onRemoved() {

            }

            @Override
            public void onAttached() {

            }
        }
        public static class SettingsLower extends LinearLayout{
            public SettingsLower(Context context, final NoteHolder noteHolder) {
                super(context);

                LinearLayout.LayoutParams params = new NotebookItemChamber.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
                setLayoutParams(params);
                params.setMargins(0,Globals.dp2px*10,0,0);

                LayoutInflater.from(context).inflate(R.layout.noteholder_settingsmode,this,true);

                findViewById(R.id.edit).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        noteHolder.getNotebook().editor.cancelActiveNote();
                        noteHolder.getNoteSpace().setOnTouchListener(null);
                        noteHolder.setMode(MODE_EDIT,true);
                        setAsActiveNote(noteHolder);
                    }
                });
                findViewById(R.id.delete).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        noteHolder.getNotebook().getNotebookDataHandler().deleteNote(noteHolder.getNote().noteInfo.noteUUID);
                        XAnimation.changeDimension(noteHolder, 300, XAnimation.DIMENSION_HEIGHT, noteHolder.getHeight(), 0, new XAnimationListener() {
                            @Override
                            public void onEnd() {
                                noteHolder.setVisibility(GONE);
                                noteHolder.getNote().noteState = STATE_DELETED;

                                Notebook.LayoutParams params = (RecyclerView.LayoutParams)noteHolder.getLayoutParams();
                                int margin = 0;
                                params.setMargins(margin,margin,margin,margin);
                                noteHolder.requestLayout();
                            }

                            @Override
                            public void onStep() {
                                
                            }
                        });
                    }
                });
            }

        }
    }
}
