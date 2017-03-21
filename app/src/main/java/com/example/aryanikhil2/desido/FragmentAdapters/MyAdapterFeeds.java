package com.example.aryanikhil2.desido.FragmentAdapters;

/**
 * Created by Nikhil on 02-07-2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.aryanikhil2.desido.LogIn.LoginActivity;
import com.example.aryanikhil2.desido.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyAdapterFeeds extends RecyclerView.Adapter<MyAdapterFeeds.ViewHolder> {
    private List<Integer> listPId = new ArrayList<Integer>();
    private List<Integer> listRating = new ArrayList<Integer>();
    private List<String> listTitle = new ArrayList<String>();
    private List<String> listName = new ArrayList<String>();
    private List<String> listDesc = new ArrayList<String>();
    private List<Bitmap> listImg = new ArrayList<Bitmap>();
    private List<Bitmap> listImgThumb = new ArrayList<Bitmap>();
    private List<List<String>> listComments = new ArrayList<List<String>>();

    LayoutInflater inflater;
    Context context;

    public MyAdapterFeeds(List<Integer> listPId, List<String> listTitle, List<String> listName, List<String> listDesc, List<Integer> listRating,List<Bitmap> listImg, List<Bitmap> listImgThumb, List<List<String>> listComments,Context context){
        this.listPId = listPId;
        this.listRating = listRating;
        this.listTitle = listTitle;
        this.listName = listName;
        this.listDesc = listDesc;
        this.listImg = listImg;
        this.listImgThumb = listImgThumb;
        this.listComments = listComments;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView postImageThumb;
        public TextView postTitle;
        public TextView postDesc;
        public TextView postName;
        public RatingBar postRating;
        public Button comment;

        public ViewHolder(View itemView) {
            super(itemView);
            postImageThumb = (ImageView) itemView.findViewById(R.id.imageView2);
            postTitle = (TextView)itemView.findViewById(R.id.textView2);
            postDesc = (TextView)itemView.findViewById(R.id.textView3);
            postName = (TextView)itemView.findViewById(R.id.textView);
            postRating = (RatingBar)itemView.findViewById(R.id.ratingBar);
            comment = (Button)itemView.findViewById(R.id.button6);

            postImageThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    View mView= inflater.inflate(R.layout.popup_image,null);
                    final PopupWindow mPopupWindow = new PopupWindow(mView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
                    ImageView postImage = (ImageView) mView.findViewById(R.id.imageView3);
                    postImage.setImageBitmap(listImg.get(position));
                    mPopupWindow.showAtLocation(postImage, Gravity.CENTER, 45, 0);
                    postImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPopupWindow.dismiss();
                        }
                    });
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_layout_feeds, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.postTitle.setText(listTitle.get(i));
        viewHolder.postName.setText("@" + listName.get(i));
        viewHolder.postDesc.setText(listDesc.get(i));
        viewHolder.postImageThumb.setImageBitmap(listImgThumb.get(i));


        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                Log.e("position Comment", ""+position);
                View mView= inflater.inflate(R.layout.popup_comment,null);
                Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
                final Point size = new Point();
                display.getSize(size);

                ListView commentsList = (ListView)mView.findViewById(R.id.commentsListView);
                EditText writeNew = (EditText)mView.findViewById(R.id.writeComment);
                Button postComment = (Button)mView.findViewById(R.id.button11);

                ArrayAdapter<String> adapter;
                adapter = new ArrayAdapter<String>(context,
                        R.layout.comment_list, android.R.id.text1,listComments.get(position));

                commentsList.setAdapter(adapter);

                //setListViewHeightBasedOnChildren(commentsList);
                postComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(hasContent(writeNew)){
                            SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.MyPrefs, Context.MODE_PRIVATE);
                            int uId = sharedPreferences.getInt("userid",0);
                            new AddComment().execute(Integer.toString(uId), listPId.get(i).toString(), writeNew.getText().toString());
                            listComments.get(position).add(writeNew.getText().toString());
                            writeNew.setText("");
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

                final PopupWindow mPopupWindow = new PopupWindow(mView, size.x*90/100 , size.y*80/100,true);
                mPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.comment_popup_style));
                // make it focusable to show the keyboard to enter in `EditText`
                mPopupWindow.setFocusable(true);
                // make it outside touchable to dismiss the popup window
                mPopupWindow.setOutsideTouchable(true);

                // show the popup at bottom of the screen and set some margin at bottom ie,
                mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0,100);
            }
        });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    @Override
    public int getItemCount() {
        return listTitle.size();
    }

    public boolean hasContent(EditText et){
        if(et.getText().toString().trim().length()>0) return true;
        else{
            et.setError("Fill This Field");
            return false;
        }
    }

    class AddComment extends AsyncTask<String, Void, Void> {

        public Void doInBackground(String... params){
            try {
                Class.forName("org.postgresql.Driver");
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try{

                Connection conn = DriverManager.getConnection("jdbc:postgresql://172.16.40.26:5432/student?currentSchema=desido","student","student");
                PreparedStatement ps = conn.prepareStatement("INSERT INTO feedback(uid, pid, info, timefeed) VALUES(?, ?, ?, ?)");
                ps.setInt(1, Integer.parseInt(params[0]));
                ps.setInt(2, Integer.parseInt(params[1]));
                ps.setString(3, params[2]);
                ps.setTimestamp(4, new Timestamp(new Date().getTime()));

                ps.executeUpdate();

                ps.close();
                conn.close();
                Log.e("Success","Commented Successfully.");
                //Toast.makeText(context, "Comment Posted!!", Toast.LENGTH_SHORT).show();
            }
            catch(SQLException e) {
                Log.e("Error", "Error in posting comments");
                //Toast.makeText(context, "Comment Not Posted!!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(Void result){
        }
    }
}
