package com.example.sadeep.winternightd.misc;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import com.example.sadeep.winternightd.spans.RichText;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Sadeep on 7/5/2017.
 */

public class Utils {

    static Activity activity;

    public static void initialize(Activity context){
        activity=context;
    }

    public static CharSequence duplicateCharSequence(CharSequence seq){
        return RichText.generateRichText((Spanned) seq).getCharSequence();
    }

    public static byte[] bitmapToByteArray(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    public static Bitmap byteArrayToBitmap(byte[] arr){
        return BitmapFactory.decodeByteArray(arr,0,arr.length);
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

    public static ArrayList<Byte>  arrayToArrayList(byte[] bytes){
        ArrayList<Byte> list= new ArrayList<>();
        for(byte x:bytes)list.add(x);
        return list;
    }
    public static LinkedList<Byte> arrayToLinkedList(byte[] bytes){
        LinkedList<Byte> list= new LinkedList<>();
        for(byte x:bytes)list.add(x);
        return list;
    }

    public static int[] byte2int(byte[]src) {
        int dstLength = src.length >>> 2;
        int[]dst = new int[dstLength];

        for (int i=0; i<dstLength; i++) {
            int j = i << 2;
            int x = 0;
            x += (src[j++] & 0xff) << 0;
            x += (src[j++] & 0xff) << 8;
            x += (src[j++] & 0xff) << 16;
            x += (src[j++] & 0xff) << 24;
            dst[i] = x;
        }
        return dst;
    }
    public static byte[] int2byte(int[]src) {
        int srcLength = src.length;
        byte[]dst = new byte[srcLength << 2];

        for (int i=0; i<srcLength; i++) {
            int x = src[i];
            int j = i << 2;
            dst[j++] = (byte) ((x >>> 0) & 0xff);
            dst[j++] = (byte) ((x >>> 8) & 0xff);
            dst[j++] = (byte) ((x >>> 16) & 0xff);
            dst[j++] = (byte) ((x >>> 24) & 0xff);
        }
        return dst;
    }
    public static byte[] toByteArray(ArrayList<Byte> in) {
        final int n = in.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }
    public static byte[] toByteArray(LinkedList<Byte> in) {
        final int n = in.size();
        byte ret[] = new byte[n];
        for (int i = 0; i < n; i++) {
            ret[i] = in.get(i);
        }
        return ret;
    }
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }


    }
    public static String getRealPathFromURI(Uri contentUri) {

        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static String getNewUUID(){
        return java.util.UUID.randomUUID().toString().replaceAll("-","");
    }

    public static void downloadFile(String url,OnSuccessListener onSuccess,OnProgressListener onProgress,int bytes){
        new DownloadFileFromURL(url,onSuccess,onProgress,bytes).execute();
    }
    public static void downloadFile(String url,OnSuccessListener onSuccess,OnProgressListener onProgress){
        new DownloadFileFromURL(url,onSuccess,onProgress,1000000).execute();
    }
    public static interface OnSuccessListener{
        void onSuccess(byte[] data);
    }
    public static interface OnProgressListener{
        void onProgress(int progress);
    }

    private static ArrayList<String> progressingDownloads = new ArrayList<>();
    private static ArrayList<String> successDownloads = new ArrayList<>();
    private static class DownloadFileFromURL extends AsyncTask<String, String, String> {
        private OnSuccessListener onSuccess;
        private OnProgressListener onProgress;
        private String link = null;
        private int fileSize;

        public DownloadFileFromURL(String link, OnSuccessListener onSuccess, OnProgressListener onProgress, int fileSize) {
            this.onProgress = onProgress;
            this.onSuccess = onSuccess;
            this.link = link;
            this.fileSize = fileSize;
        }


        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            try {

                for ( int i = 0;  i < progressingDownloads.size(); i++){
                    String tempName = progressingDownloads.get(i);
                    if(tempName.equals(link)) return null;
                }
                for ( int i = 0;  i < successDownloads.size(); i++){
                    String tempName = successDownloads.get(i);
                    if(tempName.equals(link)) return null;
                }

                URL url = new URL(link);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream

                byte data[] = new byte[8192+10];

                byte[] fa = new byte[fileSize+10];
                int o=0;
                progressingDownloads.add(link);
                while (true) {
                    int len=input.read(data,0,8192);
                    if(len==-1) break;
                    for(int v=0;v<len;v++) {
                        fa[o++] =  data[v];
                    }
                    if (onProgress != null) onProgress.onProgress(o / 1024);
                }
                byte [] kk = new byte[o];
                for(int c=0;c<o;c++)kk[c]=fa[c];
                input.close();

                for ( int i = 0;  i < progressingDownloads.size(); i++){
                    String tempName = progressingDownloads.get(i);
                    if(tempName.equals(link)){
                        progressingDownloads.remove(i);
                    }
                }
                successDownloads.add(link);
                if(onSuccess!=null)onSuccess.onSuccess(kk);

            } catch (Exception e) {
                Log.e("File Download Error: ", e.getMessage());
                for ( int i = 0;  i < progressingDownloads.size(); i++){
                    String tempName = progressingDownloads.get(i);
                    if(tempName.equals(link)){
                        progressingDownloads.remove(i);
                    }
                }
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded

        }

    }


    public static boolean writeToFile(byte[] data,String file){
        try {
            FileOutputStream fout = new FileOutputStream(file, false);
            fout.write(data);
            fout.close();
            return true;
        }catch (Exception e){return false;}
    }
    public static void createDir(String path){
        try {
            File createDir = new File(path + File.separator);
            if (!createDir.exists()) createDir.mkdir();
        }catch (Exception e){}
    }
    public static byte[] readFromFile(String filepath){
        File file=new File(filepath);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;

    }


    public static void openFile(Context context, File url)  {
        try {


            // Create URI
            File file = url;
            Uri uri = Uri.fromFile(file);

            Intent target = new Intent(Intent.ACTION_VIEW);
            // Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                target.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                target.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                target.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                target.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // WAV audio file
                target.setDataAndType(uri, "application/x-wav");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                target.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                target.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                target.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                target.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                target.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                target.setDataAndType(uri, "video/*");
            } else {
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                target.setDataAndType(uri, "*/*");
            }

            target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent = Intent.createChooser(target, "Open File");
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
            }

        }catch (Exception e){}


    }
}
