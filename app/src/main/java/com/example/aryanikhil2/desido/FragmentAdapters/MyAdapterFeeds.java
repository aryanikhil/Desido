package com.example.aryanikhil2.desido.FragmentAdapters;

/**
 * Created by Nikhil on 02-07-2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aryanikhil2.desido.LogIn.LoginActivity;
import com.example.aryanikhil2.desido.MainActivity;
import com.example.aryanikhil2.desido.R;
import com.squareup.picasso.Picasso;
import com.uploadcare.android.library.api.UploadcareClient;
import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.callbacks.UploadcareFileCallback;
import com.uploadcare.android.library.exceptions.UploadcareApiException;
import com.uploadcare.android.library.urls.CdnPathBuilder;
import com.uploadcare.android.library.urls.Urls;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyAdapterFeeds extends RecyclerView.Adapter<MyAdapterFeeds.ViewHolder> {
    private List<Integer> listPId = new ArrayList<>();
    private List<Integer> listRating = new ArrayList<>();
    private List<String> listTitle = new ArrayList<>();
    private List<String> listName = new ArrayList<>();
    private List<String> listDesc = new ArrayList<>();
    private List<String> listImg = new ArrayList<>();
    private List<String> listImgThumb = new ArrayList<>();
    private List<List<String>> listComments = new ArrayList<>();

    LayoutInflater inflater;
    Context context;

    UploadcareClient client;

    public MyAdapterFeeds(List<Integer> listPId, List<String> listTitle, List<String> listName, List<String> listDesc, List<Integer> listRating, List<String> listImg, List<List<String>> listComments, Context context){
        this.listPId = listPId;
        this.listRating = listRating;
        this.listTitle = listTitle;
        this.listName = listName;
        this.listDesc = listDesc;
        this.listImg = listImg;
        this.listComments = listComments;
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.client = new UploadcareClient(MainActivity.PUBLICKEY, MainActivity.PRIVATEKEY);
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

                    client.getFileAsync(context, listImg.get(position), new UploadcareFileCallback() {
                        @Override
                        public void onFailure(UploadcareApiException e) {
                            Toast.makeText(context, "Image Fetch Error", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onSuccess(UploadcareFile file) {
                            CdnPathBuilder builder = file.cdnPath();
                            URI url = Urls.cdn(builder);
                            //builder.resizeWidth(250);
                            //builder.cropCenter(250, 250);
                            Picasso.with(context).load(url.toString()).into(postImage);
                        }
                    });

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

        client.getFileAsync(context, listImg.get(i), new UploadcareFileCallback() {
            @Override
            public void onFailure(UploadcareApiException e) {
                Toast.makeText(context, "Image Fetch Error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(UploadcareFile file) {
                CdnPathBuilder builder = file.cdnPath();
                URI url = Urls.cdn(builder);
                //builder.resizeWidth(250);
                //builder.cropCenter(250, 250);
                Picasso.with(context).load(url.toString()).into(viewHolder.postImageThumb);
            }
        });

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

                final PopupWindow mPopupWindow = new PopupWindow(mView, size.x*90/100 , size.y*55/100,true);
                //mPopupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.comment_popup_style));
                mPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.comment_popup_style));
                // make it outside touchable to dismiss the popup window
                mPopupWindow.setOutsideTouchable(true);
                // show the popup at bottom of the screen and set some margin at bottom ie,
                mPopupWindow.showAtLocation(view, Gravity.CENTER, 0,100);
            }
        });
    }

    /*public static void setListViewHeightBasedOnChildren(ListView listView) {
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
    }*/
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
                //Connection con = DriverManager.getConnection("jdbc:postgresql://172.16.40.26:5432/student?currentSchema=desido","student","student");
                Connection con = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/desido","postgres","5438");
                PreparedStatement ps = con.prepareStatement("INSERT INTO feedback(uid, pid, info, timefeed) VALUES(?, ?, ?, ?)");
                ps.setInt(1, Integer.parseInt(params[0]));
                ps.setInt(2, Integer.parseInt(params[1]));
                ps.setString(3, params[2]);
                ps.setTimestamp(4, new Timestamp(new Date().getTime()));

                ps.executeUpdate();

                ps.close();
                con.close();
                Log.e("Success","Commented Successfully.");
            }
            catch(SQLException e) {
                Log.e("Error", "Error in posting comments");
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(Void result){
        }
    }
}
