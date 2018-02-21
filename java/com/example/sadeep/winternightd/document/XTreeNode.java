package com.example.sadeep.winternightd.document;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sadeep.winternightd.R;
import com.example.sadeep.winternightd.activities.DocumentViewer;
import com.example.sadeep.winternightd.misc.Globals;
import com.example.sadeep.winternightd.misc.Utils;
import com.unnamed.b.atv.model.TreeNode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by Sadeep on 2/14/2018.
 */

public class XTreeNode {

    public int type=0;//0 folder, 1 pdf
    public String name;
    private Context context;
    public ArrayList<XTreeNode>children = new ArrayList<>();

    public XTreeNode(String name,int type,Context context) {
        this.name = name;
        this.context = context;
        this.type=type;
    }

    public void addContent(String s){
        s=s.trim();
        String[]ss = s.split("/");
        boolean newChild = true;
        for(XTreeNode x:children){
            if(x.name.equals(ss[0])&&x.type==0){
                newChild = false;
                if(ss.length>1) x.addContent(s.substring(ss[0].length() + 1));
            }
        }
        XTreeNode newnode = new XTreeNode(ss[0],ss.length==1?1:0,context);

        if(newChild)children.add(newnode);
        if(ss.length>1)newnode.addContent(s.substring(ss[0].length()+1));
    }

    public TreeNode build(){
        return build(0);
    }
    public TreeNode build(int level){
        TreeNode tn = new TreeNode(new IconTreeItem(name,level,type)).setViewHolder(new MyHolder(context));
        for(XTreeNode child:children) tn.addChild(child.build(level+1));
        return tn;
    }



    class MyHolder extends TreeNode.BaseNodeViewHolder<IconTreeItem> {

        public MyHolder(Context context) {
            super(context);
        }

        @Override
        public View createNodeView(TreeNode node, IconTreeItem value) {
            final LayoutInflater inflater = LayoutInflater.from(context);
            final View view = inflater.inflate(R.layout.nodetree, null, false);
            int topP=0,bottomP=0;

            TextView tvValue = (TextView) view.findViewById(R.id.node_value);
            tvValue.setText(value.text);



            if(type==1) {
                String[] qq = value.text.split("%");

                if(qq[1].contains(".doc"))value.type=2;
                if(qq[1].contains(".ppt"))value.type=3;
                if(value.type==1){
                    ((ImageView)view.findViewById(R.id.image)).setImageResource(R.drawable.pdf);
                    topP=Globals.dp2px*5;
                    bottomP=Globals.dp2px*5;
                }
                if(value.type==2){
                    ((ImageView)view.findViewById(R.id.image)).setImageResource(R.drawable.docx);
                    topP=Globals.dp2px*5;
                    bottomP=Globals.dp2px*5;
                }
                if(value.type==3){
                    ((ImageView)view.findViewById(R.id.image)).setImageResource(R.drawable.pptx);
                    topP=Globals.dp2px*5;
                    bottomP=Globals.dp2px*5;
                }

                tvValue.setText(qq[0]);
                view.setTag(qq[1]);


                String path = Environment.getExternalStorageDirectory()+"/WhatsNoteDocs";
                File createDir = new File(path+ File.separator);
                if(!createDir.exists()) createDir.mkdir();

                File file=new File(path, qq[1]);

                if(file.exists())tvValue.setTextColor(0xff117711);

                view.setOnClickListener(v -> {
                    if(!file.exists()) {
                        Utils.downloadFile(DocumentViewer.server + view.getTag(), data -> {
                            FileOutputStream fout = null;
                            try {
                                fout = new FileOutputStream(file, false);
                                fout.write(data);
                                fout.close();

                                Intent target = new Intent(Intent.ACTION_VIEW);
                                if(value.type==1)target.setDataAndType(Uri.fromFile(file), "application/pdf");
                                if(value.type==2)target.setDataAndType(Uri.fromFile(file), "application/pdf");
                                if(value.type==3)target.setDataAndType(Uri.fromFile(file), "application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                ((Activity)context).runOnUiThread(()->
                                {
                                    tvValue.setText(qq[0]);
                                    tvValue.setTextColor(0xff117711);

                                });
                                Intent intent = Intent.createChooser(target, "Open File");
                                try {
                                    context.startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    // Instruct the user to install a PDF reader here, or something
                                }

                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }
                        },val->{
                            ((Activity)context).runOnUiThread(() -> {
                                tvValue.setText(qq[0]+"  "+(val<1000?(val+"KB"):(val/1000.0f+"MB")));
                            });
                        });
                    }else{


                        Intent target = new Intent(Intent.ACTION_VIEW);
                        target.setDataAndType(Uri.fromFile(file), "application/pdf");
                        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                        Intent intent = Intent.createChooser(target, "Open File");
                        try {
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            // Instruct the user to install a PDF reader here, or something
                        }
                    }
                });
            }
            view.setPadding(Globals.dp2px*20*value.level,topP,0,bottomP);

            return view;
        }

    }

    public static class IconTreeItem {
        public int icon;
        public String text;
        public int level;
        public int type;

        public IconTreeItem(String text, int level,int type) {
            this.text = text;
            this.level = level;
            this.type = type;
        }
    }
}
