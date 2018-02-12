package com.example.sadeep.winternightd.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.activities.NoteContainingActivity;
import com.example.sadeep.winternightd.activities.NotebookActivity;
import com.example.sadeep.winternightd.attachbox.AttachBoxManager;
import com.example.sadeep.winternightd.dumping.FieldDataStream;
import com.example.sadeep.winternightd.field.FieldFactory;
import com.example.sadeep.winternightd.field.SingleText;
import com.example.sadeep.winternightd.field.fielddata.BitmapData;
import com.example.sadeep.winternightd.field.fields.BulletedField;
import com.example.sadeep.winternightd.field.fields.CheckedField;
import com.example.sadeep.winternightd.field.fields.Field;
import com.example.sadeep.winternightd.field.fields.H1Field;
import com.example.sadeep.winternightd.field.fields.ImageField;
import com.example.sadeep.winternightd.field.fields.NumberedField;
import com.example.sadeep.winternightd.field.fields.SimpleIndentedField;
import com.example.sadeep.winternightd.field.fields.table.TableField;
import com.example.sadeep.winternightd.field.fields.table.TableFieldParams;
import com.example.sadeep.winternightd.notebook.NoteHolderModes;
import com.example.sadeep.winternightd.notebook.Notebook;
import com.example.sadeep.winternightd.notebook.NotebookViewHolderUtils;
import com.example.sadeep.winternightd.selection.CursorPosition;
import com.example.sadeep.winternightd.selection.XSelection;
import com.example.sadeep.winternightd.spans.RichText;
import com.example.sadeep.winternightd.textboxes.XEditText;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Sadeep on 10/18/2016.
 */

/**
 * A note in WN, this is a collection of Fields. (a Note may only have Fields as children)
 */
public class Note extends LinearLayout {


    private boolean isEditable;
    public static final int defaultFieldType = SimpleIndentedField.classFieldType;


    private View scrollableParent;

    public NoteInfo noteInfo;

    public int noteState = STATE_NORMAL;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_EDITED = 1;
    public static final int STATE_DELETED = 2;

    Note(Context context,boolean isEditable, boolean isNewNote, View scrollableParent) {
        super(context);
        this.scrollableParent = scrollableParent;

        init(isEditable);
        if(isNewNote) convertToNewNoteWithOneDefaultField();
    }

    private void init(boolean isEditable) {

        setOrientation(VERTICAL);

        setBackgroundColor(Color.TRANSPARENT);
        setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setClipChildren(false);

        this.isEditable = isEditable;




    }


//general methods

    public View getScrollableParent() {
        return scrollableParent;
    }

    public void convertToNewNoteWithOneDefaultField(){
        removeAllViews();
        addView(FieldFactory.createNewField(getContext(), defaultFieldType,true, null));
    }

    public Field getFieldAt(int index){
        Field f = null;
        try{
            f = (Field)getChildAt(index);
        }catch (Exception e){}
        return f;
    }

    public int getFieldCount(){
        return getChildCount();
    }

    /**
     * We make sure that the added child is a Field, because a Note is intended to hold only Fields as child views.
     * It would cause bugs in various classes if a non-Field child is available as a child in the Note. So here we prevent this.
     *
     * We make the added Field is editable if the note is editable and vice versa.
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params){

        if(XSelection.isSelectionAvailable())XSelection.clearSelections();

        Field f = null;
        try{
            f = (Field)child;
        }catch (Exception e){}

        if(f == null) return; //if child type is not Field stop the procedure

        f.setIsEditable(isEditable);


        super.addView(child,index,params);

        f.onAttachedToNote();
        f.refreshThisAndAllFieldsBelowToAccountForNoteChanges();
    }

    @Override
    public void removeView(View view) {
        int index = indexOfChild(view);
        removeViewAt(index);
    }
    @Override
    public void removeViewAt(int index) {
        Notebook.suspendScrollTemporary();

        super.removeViewAt(index);

        if(getFieldCount()-1>=index)//if the removed is not the bottom most Field
            getFieldAt(index).refreshThisAndAllFieldsBelowToAccountForNoteChanges();//refresh the new comer to the formers index and below
    }

    public boolean isEmpty(){
        if(getFieldCount()!=1)return false;
        Field f = getFieldAt(0);
        if(f.getFieldType()!=Note.defaultFieldType)return false;
        SimpleIndentedField s = (SimpleIndentedField)f;
        if(s.getMainTextBox().getText().length()!=0)return false;

        return true;
    }

    public FocusListener focusListener=null;
    public void onFocused() {
        if(focusListener!=null)focusListener.onFocused();
    }

    public void attachboxRequests(int attachButtonId) {
        if(attachButtonId == AttachBoxManager.ATTACH_BUTTON_ID_CHECKEDFIELD){
            CheckedField field = (CheckedField) FieldFactory.createNewField(getContext(),CheckedField.classFieldType,true, null);
            CursorPosition cpos = getCurrentCursorPosition();
            int newFieldPos;
            if(cpos==null)newFieldPos = getFieldCount();
            else newFieldPos = cpos.fieldIndex+1;

            if(getFieldAt(newFieldPos-1)instanceof SimpleIndentedField){
                if(((SimpleIndentedField)getFieldAt(newFieldPos-1)).isEmpty()){
                    removeView(getFieldAt(newFieldPos-1));
                    newFieldPos--;
                }
            }

            addView(field,newFieldPos);
            field.getMainTextBox().requestFocus();
        }
        if(attachButtonId == AttachBoxManager.ATTACH_BUTTON_ID_BULLETEDFIELD){
            BulletedField field = (BulletedField) FieldFactory.createNewField(getContext(),BulletedField.classFieldType,true, null);
            CursorPosition cpos = getCurrentCursorPosition();
            int newFieldPos;
            if(cpos==null)newFieldPos = getFieldCount();
            else newFieldPos = cpos.fieldIndex+1;

            if(getFieldAt(newFieldPos-1)instanceof SimpleIndentedField){
                if(((SimpleIndentedField)getFieldAt(newFieldPos-1)).isEmpty()){
                    removeView(getFieldAt(newFieldPos-1));
                    newFieldPos--;
                }
            }

            addView(field,newFieldPos);
            field.getMainTextBox().requestFocus();
        }
        if(attachButtonId == AttachBoxManager.ATTACH_BUTTON_ID_NUMBEREDFIELD){


            NumberedField field = (NumberedField) FieldFactory.createNewField(getContext(),NumberedField.classFieldType,true, null);
            CursorPosition cpos = this.getCurrentCursorPosition();
            int newFieldPos;
            if(cpos==null)newFieldPos = getFieldCount();
            else newFieldPos = cpos.fieldIndex+1;

            if(getFieldAt(newFieldPos-1)instanceof SimpleIndentedField){
                if(((SimpleIndentedField)getFieldAt(newFieldPos-1)).isEmpty()){
                    removeView(getFieldAt(newFieldPos-1));
                    newFieldPos--;
                }
            }

            addView(field,newFieldPos);
            field.getMainTextBox().requestFocus();
        }



        if(attachButtonId == AttachBoxManager.ATTACH_BUTTON_ID_CAMERA){

            new Thread(){
                @Override
                public void run() {
                    dispatchTakePictureIntent();
                }
            }.start();




                ((NoteContainingActivity)getContext()).onActivityResultListener= new PreferenceManager.OnActivityResultListener() {
                    @Override
                    public boolean onActivityResult(final int requestCode, final int resultCode, Intent data) {
                        ((NoteContainingActivity) Note.this.getContext()).onActivityResultListener = null;
                        //d.wow((NoteContainingActivity)Note.this.getContext());
                        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                            byte[] bytes = null;
                            try {
                                FileInputStream fis = new FileInputStream(mCurrentPhotoPath);
                                bytes = new byte[fis.available()];
                                fis.read(bytes, 0, fis.available());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            final byte[] finalBytes = bytes;
                            addNewImageField(new BitmapData(finalBytes));

                        }
                        return true;
                    }
                };
            }

        if(attachButtonId == AttachBoxManager.ATTACH_BUTTON_ID_GALLERY){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            ((Activity)getContext()).startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

            ((NoteContainingActivity)getContext()).onActivityResultListener= new PreferenceManager.OnActivityResultListener() {
                @Override
                public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
                    ((NoteContainingActivity)Note.this.getContext()).onActivityResultListener=null;
                    //d.wow((NoteContainingActivity)Note.this.getContext());


                    if (requestCode == 1 && resultCode == RESULT_OK) {



                        try {
                            Uri uri = data.getData();

                            Bitmap photo = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                            addNewImageField(photo);
                        } catch (IOException e) { }




                    }
                    return true;
                }
            };
        }
        if(attachButtonId == AttachBoxManager.ATTACH_BUTTON_ID_H1){
            H1Field field = (H1Field) FieldFactory.createNewField(getContext(),H1Field.classFieldType,true, null);
            CursorPosition cpos = getCurrentCursorPosition();
            int newFieldPos;
            if(cpos==null)newFieldPos = getFieldCount();
            else {
                newFieldPos = cpos.fieldIndex+1;
                if(cpos.characterIndex==0)newFieldPos=cpos.fieldIndex;
            }

            if(getFieldAt(newFieldPos-1)instanceof SimpleIndentedField){
                if(((SimpleIndentedField)getFieldAt(newFieldPos-1)).isEmpty()){
                    removeView(getFieldAt(newFieldPos-1));
                    newFieldPos--;
                }
            }

            addView(field,newFieldPos);
            field.getMainTextBox().requestFocus();

        }
        if(attachButtonId == AttachBoxManager.ATTACH_BUTTON_ID_TABLE){
            CursorPosition cpos = getCurrentCursorPosition();
            int newFieldPos;
            if(cpos==null)newFieldPos = getFieldCount();
            else newFieldPos = cpos.fieldIndex + 1;


            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            final View promptView = layoutInflater.inflate(R.layout.tablefield_insert, null);

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setView(promptView);

            final AlertDialog ad = alertDialogBuilder.create();

            ((NumberPicker)promptView.findViewById(R.id.numberPicker)).setMinValue(1);
            ((NumberPicker)promptView.findViewById(R.id.numberPicker)).setMaxValue(20);
            ((NumberPicker)promptView.findViewById(R.id.numberPicker)).setValue(3);
            ((NumberPicker)promptView.findViewById(R.id.numberPicker)).clearFocus();


            final int finalNewFieldPos = newFieldPos;
            promptView.findViewById(R.id.ok).requestFocus();
            promptView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int columnCount = ((NumberPicker)promptView.findViewById(R.id.numberPicker)).getValue();
                    boolean firstRowIsHeader = ((CheckBox)promptView.findViewById(R.id.firstRowHeader)).isChecked();
                    final TableField field = (TableField) FieldFactory.createNewField(getContext(),TableField.classFieldType,true, new TableFieldParams(columnCount,firstRowIsHeader));
                    Note.this.addView(field,finalNewFieldPos);
                    SimpleIndentedField textField = (SimpleIndentedField) FieldFactory.createNewField(Note.this.getContext(),SimpleIndentedField.classFieldType,true, null);
                    Note.this.addView(textField, finalNewFieldPos +1);
                    ad.cancel();
                }
            });
            promptView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ad.cancel();
                }
            });
            ad.show();


        }
    }

    public interface FocusListener{void onFocused();}

//editability related methods

    public void setEditable(boolean isEditable){

        if(this.isEditable == isEditable)return;
        this.isEditable = isEditable;

        for(int c = 0; c < getChildCount(); c++){
            Field f = getFieldAt(c);
            if(f != null)f.setIsEditable(isEditable);
        }

    }

    public boolean getIsEditable() {
        return isEditable;
    }




//selection and clipboard related methods

    public CursorPosition getCurrentCursorPosition(){
        return XSelection.getCurrentCursorPosition(this);
    }

    public void setCursor(CursorPosition pos) {
        try {

            Field f = getFieldAt(pos.fieldIndex);
            if (f instanceof SimpleIndentedField && f.getIsEditable()) {
                XEditText tv = (XEditText) ((SimpleIndentedField) f).getMainTextBox();
                tv.requestFocus();
                tv.setSelection(pos.characterIndex);
            }
        }catch (Exception e){}
    }

    public void setCursorVisible(boolean visible){
        //for each Field in Note set the visibility to the expected value
        for(int c=0;c<getChildCount();c++){
            getFieldAt(c).setCursorVisible(visible);
        }
    }

    /**
     *  Insert a CharSequence to the note at the CursorPosition specified by start.
     */
    public void insertCharSequenceAt(CursorPosition start, CharSequence text) {
        //todo here accounting only for when start is at a SingleText Field && isInternal

        SingleText f;
        try{
            f = ((SingleText) getFieldAt(start.fieldIndex));
        }catch(Exception e){return;}
        if (!start.isInternal())return;

        CharSequence seq1 = f.getMainTextBox().getText().subSequence(0,start.characterIndex);
        CharSequence seq2 = f.getMainTextBox().getText().subSequence(start.characterIndex,f.getMainTextBox().length());

        f.getMainTextBox().setText(android.text.TextUtils.concat(seq1,text,seq2));

    }

    public void eraseContent(CursorPosition start, CursorPosition end){
        if(start.equals(end))return;

        while(end.fieldIndex >= start.fieldIndex+2){
            removeViewAt(start.fieldIndex+1);
            end.fieldIndex--;
        }

        if(start.fieldIndex==end.fieldIndex){
            if(!start.isInternal()){
                //not implemented
            }
            else{
                TextView tv =((SimpleIndentedField) getFieldAt(start.fieldIndex)).getMainTextBox();
                CharSequence oldtext = tv.getText();
                CharSequence seq1 =  oldtext.subSequence(0,start.characterIndex);
                CharSequence seq2 =  oldtext.subSequence(end.characterIndex,oldtext.length());

                seq1 = RichText.getCharSequence(RichText.generateRichText((Spanned)seq1));
                seq2 = RichText.getCharSequence(RichText.generateRichText((Spanned)seq2));
                CharSequence newtext = android.text.TextUtils.concat(seq1,seq2);
                tv.setText(newtext);
            }
        }
        else if (start.fieldIndex==end.fieldIndex-1){
            if(start.isInternal()&&end.isInternal()){

                TextView tv1 =((SimpleIndentedField) getFieldAt(start.fieldIndex)).getMainTextBox();
                CharSequence text1 = tv1.getText().subSequence(0,start.characterIndex);
                TextView tv2 =((SimpleIndentedField) getFieldAt(end.fieldIndex)).getMainTextBox();
                CharSequence text2 = tv2.getText().subSequence(end.characterIndex,tv2.length());

                tv1.setText(android.text.TextUtils.concat(text1,text2));

                removeViewAt(end.fieldIndex);
            }
        }

    }

    public Point getAbsoluteCoordinatesForCursorPosition(CursorPosition cpos) {
        return (getFieldAt(cpos.fieldIndex)).getAbsoluteCoordinatesForCharacterIndex(cpos.characterIndex);
    }

    public CursorPosition cursorPositionForCoordinate(Point absoluteCoordinate) {
        int rawX=absoluteCoordinate.x, rawY=absoluteCoordinate.y;

        for(int c = 0; c < getChildCount(); c++)
        {
            Field childField = getFieldAt(c);

            int[] xy = new int[2];
            childField.getLocationInWindow(xy);

            Rect rect = new Rect(xy[0], xy[1], xy[0]+childField.getWidth(), xy[1]+childField.getHeight());

            if (rect.contains(rawX, rawY))
            {
                int characterIndex =  childField.characterPositionForCoordinate(new Point(rawX, rawY));
                return new CursorPosition(c,characterIndex);
            }

            if(c==0){
                if(rawY<xy[1])return new CursorPosition(c,childField.characterPositionForCoordinate(new Point(rawX, rawY)));
            }
            if(c==getChildCount()-1){
                if(rawY>xy[1]+childField.getHeight())return new CursorPosition(c,childField.characterPositionForCoordinate(new Point(rawX, rawY)));
            }
        }

        return new CursorPosition(-1, CursorPosition.CHARACTERINDEX_ERROR);
    }

    public void setScrollableParent(View scrollableParent) {
        this.scrollableParent = scrollableParent;
    }

    public boolean isInNotebook(){
        if(getParent()==null)return false;
        return (((View)getParent()).getId()== R.id.notespace);
    }

    public NotebookViewHolderUtils.NoteHolder getNoteHolder(){
        try{
            return (NotebookViewHolderUtils.NoteHolder) getParent().getParent().getParent();
        }
        catch (Exception e){
            return null;
        }
    }


//dumping related methods

    public FieldDataStream getFieldDataStream(){
        FieldDataStream stream = new FieldDataStream(getWritableByteArraySize());
        writeToFieldDataStream(stream);
        return stream;
    }

    public void writeToFieldDataStream(FieldDataStream stream){
        for(int c=0;c<getFieldCount();c++){
            Field field = getFieldAt(c);
            field.writeToFieldDataStream(stream);
        }
    }

    public int getWritableByteArraySize() {
        int sum=0;
        for(int c=0;c<getFieldCount();c++)sum+=getFieldAt(c).getWritableByteArraySize();
        return sum;
    }

    public void readFromFieldDataStream(FieldDataStream stream){
        try {
            while (!stream.endOfStream())
                addView(FieldFactory.fromFieldDataStream(getContext(), stream, isEditable));
        }catch (Exception e){
            turnToErrorNote();
        }
    }

    public void saveIfInNotebookViewMode(){

        if(isInNotebook() && getNoteHolder().getMode()== NoteHolderModes.MODE_VIEW) {
            ((NotebookActivity)getContext()).notebook.getNotebookDataHandler().addExistingNote(this);
            noteState = STATE_EDITED;
            ((NoteHolderModes.ModeView.ViewLower)getNoteHolder().getLowerChamber().getChamberContent()).setDateTime(System.currentTimeMillis());
        }
    }

    private void turnToErrorNote() {
        removeAllViews();
        SimpleIndentedField text = (SimpleIndentedField) FieldFactory.createNewField(getContext(),SimpleIndentedField.classFieldType,false,null);
        text.getMainTextBox().setText("Error occurred while loading the note");
        text.getMainTextBox().setTextColor(0xffee2222);
        addView(text);
    }

    public void revertTo(FieldDataStream stream){
        removeAllViews();
        readFromFieldDataStream(stream);
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {}
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),"com.whatsnoted.beta.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                ((Activity)getContext()).startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    private void addNewImageField(Bitmap photo){

        CursorPosition cpos = getCurrentCursorPosition();
        if(cpos==null)cpos= new CursorPosition(0,0);
        ImageField imageField = (ImageField) FieldFactory.createNewField(Note.this.getContext(),ImageField.classFieldType,true, null);
        imageField.setImageBitmap(new BitmapData(photo));
        Note.this.addView(imageField,cpos.fieldIndex+1);
        SimpleIndentedField textField = (SimpleIndentedField) FieldFactory.createNewField(Note.this.getContext(),SimpleIndentedField.classFieldType,true, null);
        Note.this.addView(textField,cpos.fieldIndex+2);
        textField.getMainTextBox().requestFocus();
    }
    private void addNewImageField(BitmapData photo){

        CursorPosition cpos = getCurrentCursorPosition();
        if(cpos==null)cpos= new CursorPosition(0,0);
        ImageField imageField = (ImageField) FieldFactory.createNewField(Note.this.getContext(),ImageField.classFieldType,true, null);
        imageField.setImageBitmap(photo);
        Note.this.addView(imageField,cpos.fieldIndex+1);
        SimpleIndentedField textField = (SimpleIndentedField) FieldFactory.createNewField(Note.this.getContext(),SimpleIndentedField.classFieldType,true, null);
        Note.this.addView(textField);
        textField.getMainTextBox().requestFocus();
    }
    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getCacheDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
