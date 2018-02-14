package com.example.sadeep.winternightd.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.sadeep.winternightd.R;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;

public class DocumentViewer extends AppCompatActivity {


    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    // File url to download
    private static String file_url = "http://192.168.43.2/list.txt";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TreeNode root = TreeNode.root();
        TreeNode parent = new TreeNode(new IconTreeItem("parent")).setViewHolder(new MyHolder(this));
        TreeNode child0 = new TreeNode(new IconTreeItem("ChildNode0")).setViewHolder(new MyHolder(this));
        TreeNode child1 = new TreeNode(new IconTreeItem("ChildNode1")).setViewHolder(new MyHolder(this));
        parent.addChildren(child0, child1);
        root.addChild(parent);

        AndroidTreeView ttv = new AndroidTreeView(this,root);


        setContentView(ttv.getView());

        new DownloadFileFromURL().execute(file_url);


    }

    /**
     * Showing Dialog
     * */

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream

                byte data[] = new byte[1024];

                long total = 0;

                byte[] fa = new byte[lenghtOfFile];
                int o=0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    for(int c=0;c<count;c++)fa[o++]=data[c];
                }

                String s = new String(fa);
                s.split("\n");
                getSupportActionBar().setTitle(s);
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);

        }

    }



    class MyHolder extends TreeNode.BaseNodeViewHolder<IconTreeItem> {

        public MyHolder(Context context) {
            super(context);
        }

        @Override
        public View createNodeView(TreeNode node, IconTreeItem value) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.nodetree, null, false);
            TextView tvValue = (TextView) view.findViewById(R.id.node_value);
            tvValue.setText(value.text);

            return view;
        }

    }

    public static class IconTreeItem {
        public int icon;
        public String text;

        public IconTreeItem(String text) {
            this.text = text;
        }
    }

}
