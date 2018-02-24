package com.example.sadeep.winternightd.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.document.XTreeNode;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.misc.Utils;
import com.example.sadeep.winternightd.misc.XColors;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.io.File;

public class DocumentViewer extends AppCompatActivity {


    // File url to download
    private static String directoryURL = "https://winterproductionserver.azurewebsites.net/list.txt";
    public static String server = "https://winterproductionserver.azurewebsites.net/";



    HorizontalScrollView hsv ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_viewer);

        hsv = (HorizontalScrollView) findViewById(R.id.root);
        TextView tvv= new TextView(this);
        tvv.setText("Please wait while the list of notes is loaded");
        Globals.initialize(this);


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(XColors.actionbarColor));
        String listpath = Environment.getExternalStorageDirectory()+"/WhatsNoteDocs/list";

        if(!new File(listpath).exists()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Wish you all the best for the coming exams, there already are some great materials here. We'll be updating more soon!!!")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) -> {});
            AlertDialog alert = builder.create();
            alert.show();
        }

        String s="";
        try {

            TreeNode root = TreeNode.root();
            XTreeNode xroot = new XTreeNode("Notes",0,this);
            s = new String(Utils.readFromFile(listpath));
            s.replaceAll("\r", "");
            if(s.length()>0) {
                String h[] = s.split("\n");
                for (String xx : h) xroot.addContent(xx);
                root.addChild(xroot.build());
                AndroidTreeView ttv = new AndroidTreeView(DocumentViewer.this, root);

                hsv.removeAllViews();
                hsv.addView(ttv.getView());
            }

        }catch (Exception e){}

        String finalS1 = s;
        Utils.downloadFile(directoryURL, data->{

            runOnUiThread(() -> {

                TreeNode root = TreeNode.root();
                XTreeNode xroot = new XTreeNode("Notes",0,this);
                hsv.postDelayed(()->Utils.writeToFile(data,listpath),2000);
                String ss = new String(data);
                if(ss.equals(finalS1))return;
                ss.replaceAll("\r","");
                String h[]=ss.split("\n");
                for(String xx:h)xroot.addContent(xx);
                root.addChild(xroot.build());
                AndroidTreeView ttv = new AndroidTreeView(DocumentViewer.this,root);

                HorizontalScrollView.LayoutParams params = new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                hsv.removeAllViews();
                hsv.addView(ttv.getView());
                Toast.makeText(DocumentViewer.this, "Refreshed notes list",
                        Toast.LENGTH_SHORT).show();

            });


        },null);


        ActivityCompat.requestPermissions(DocumentViewer.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode!=KeyEvent.KEYCODE_BACK)return false;

        //System.exit(0);
        finish();
        return true;
    }
}
