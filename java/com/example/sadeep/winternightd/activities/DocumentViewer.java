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


    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    // File url to download
    private static String directoryURL = "https://winterproductionserver.azurewebsites.net/list.txt";
    public static String server = "https://winterproductionserver.azurewebsites.net/";



    HorizontalScrollView hsv ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_viewer);

        hsv = (HorizontalScrollView) findViewById(R.id.root);


        Globals.initialize(this);


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(XColors.actionbarColor));

        String path = Environment.getExternalStorageDirectory()+"/WhatsNoteDocs/ghjk/fghjk/ghj/cvbn/rtyui";
        Utils.createDir(path);
        String listpath = Environment.getExternalStorageDirectory()+"/WhatsNoteDocs/list";

        try {

            TreeNode root = TreeNode.root();
            XTreeNode xroot = new XTreeNode("Notes",0,this);
            String s = new String(Utils.readFromFile(listpath));
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

        Utils.downloadFile(directoryURL, data->{

            runOnUiThread(() -> {

                TreeNode root = TreeNode.root();
                XTreeNode xroot = new XTreeNode("Notes",0,this);
                hsv.postDelayed(()->Utils.writeToFile(data,listpath),2000);
                String s = new String(data);
                s.replaceAll("\r","");
                String h[]=s.split("\n");
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
}
