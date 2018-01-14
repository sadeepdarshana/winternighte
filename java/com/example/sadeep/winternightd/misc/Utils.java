package com.example.sadeep.winternightd.misc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spanned;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.example.sadeep.winternightd.spans.RichText;

import java.io.ByteArrayOutputStream;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Sadeep on 7/5/2017.
 */

public class Utils {
    public static CharSequence duplicateCharSequence(CharSequence seq){
        return RichText.generateRichText((Spanned) seq).getCharSequence();
    }

    public static byte[] bitmapToByteArray(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static int[] byteArrayToIntArray(byte buf[]) {
        int intArr[] = new int[buf.length / 4];
        int offset = 0;
        for(int i = 0; i < intArr.length; i++) {
            intArr[i] = (buf[3 + offset] & 0xFF) | ((buf[2 + offset] & 0xFF) << 8) |
                    ((buf[1 + offset] & 0xFF) << 16) | ((buf[0 + offset] & 0xFF) << 24);
            offset += 4;
        }
        return intArr;
    }

    public static int getWidth(View view){
        if(view.getWidth()!=0)return view.getWidth();
        view.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredWidth();
    }
    public static int getHeight(View view){
        if(view.getHeight()!=0)return view.getHeight();
        view.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    public static void showKeyboard(View view){

        InputMethodManager inputMethodManager =
                (InputMethodManager)view.getContext().getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public static void hideKeyboard(Context activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = ((Activity)activity).getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public interface KeyboardVisibilityListener {
        void onKeyboardVisibilityChanged(boolean keyboardVisible,int size);
    }
    public static void setKeyboardVisibilityListener(Activity activity, final KeyboardVisibilityListener keyboardVisibilityListener) {
        final View contentView = activity.findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int mPreviousHeight;

            @Override
            public void onGlobalLayout() {
                int newHeight = contentView.getHeight();
                if (mPreviousHeight != 0) {
                    if (mPreviousHeight > newHeight) {
                        // Height decreased: keyboard was shown
                        keyboardVisibilityListener.onKeyboardVisibilityChanged(true,mPreviousHeight-newHeight);
                    } else if (mPreviousHeight < newHeight) {
                        // Height increased: keyboard was hidden
                        keyboardVisibilityListener.onKeyboardVisibilityChanged(false,mPreviousHeight-newHeight);
                    } else {
                        // No change
                    }
                }
                mPreviousHeight = newHeight;
            }
        });
    }
}
