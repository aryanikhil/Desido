package com.example.aryanikhil2.desido.FragmentsMain;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.aryanikhil2.desido.FragmentAdapters.MyAdapterFeeds;
import com.example.aryanikhil2.desido.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arya.nikhil2 on 15-03-2017.
 */

public class FragmentFeeds extends Fragment {
    private List<Integer> listPId = new ArrayList<Integer>();
    private List<Integer> listUId = new ArrayList<Integer>();
    private List<String> listTitle = new ArrayList<String>();
    private List<String> listName = new ArrayList<String>();
    private List<String> listDesc = new ArrayList<String>();
    private List<Integer> listRating = new ArrayList<Integer>();
    private List<Bitmap> listImg = new ArrayList<Bitmap>();
    private List<Bitmap> listImgThumb = new ArrayList<Bitmap>();
    private List<List<String>> listComments = new ArrayList<List<String>>();

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    public ProgressBar progressBar,loadProgressBar;
    public TextView textView, loadMore;

    boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount, offset = 0, flag = -1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.feeds_frag, container, false);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        textView = (TextView)rootView.findViewById(R.id.textView15);

        loadProgressBar = (ProgressBar)rootView.findViewById(R.id.progressBar2);
        loadProgressBar.setProgress(0);
        loadMore = (TextView) rootView.findViewById(R.id.textView4);
        loadProgressBar.setVisibility(View.GONE);
        loadMore.setVisibility(View.GONE);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        //recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(48));
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        //recyclerView.setAdapter(null);

        new DatabaseConnectivity().execute(3,0);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            if(flag < offset) {
                                recyclerView.setVisibility(View.GONE);
                                loadProgressBar.setVisibility(View.VISIBLE);
                                loadMore.setVisibility(View.VISIBLE);

                                new DatabaseConnectivity().execute(3, offset * 3);
                            }
                        }
                    }
                }
            }
        });

        return rootView;
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;
        }
    }
    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{android.R.attr.listDivider};

        private Drawable mDivider;

        /**
         * Default divider will be used
         */
        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            mDivider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }
    }

    class DatabaseConnectivity extends AsyncTask<Integer, Void, Integer> {

        public Integer doInBackground(Integer... params){
            try {
                Class.forName("org.postgresql.Driver");
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try{
                Connection conn = DriverManager.getConnection("jdbc:postgresql://172.16.40.26:5432/student?currentSchema=desido","student","student");
                //Connection conn = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/desido","postgres","5438");
                ResultSet rs;
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM posts limit ? offset ?");
                ps.setInt(1, params[0]);
                ps.setInt(2, params[1]);
                rs = ps.executeQuery();
                if(rs!=null) {
                    while (rs.next()) {
                        listPId.add(rs.getInt(1));
                        listUId.add(rs.getInt(2));
                        listTitle.add(rs.getString(4));
                        listDesc.add(rs.getString(5));
                        listRating.add(rs.getInt(7));
                        byte[] imgbytes = rs.getBytes(3);
                        Bitmap img = BitmapFactory.decodeByteArray(imgbytes,0,imgbytes.length);
                        listImg.add(img);
                        img = Bitmap.createScaledBitmap(img,250,250,false);
                        listImgThumb.add(img);
                        //Log.e("Fetched", listPId.toString()+listTitle.toString());
                    }
                }
                for(int i = 0; i < listUId.size(); ++i) {
                    ps = conn.prepareStatement("Select name from users where uid = ?");
                    ps.setInt(1,listUId.get(i));
                    rs = ps.executeQuery();
                    if(rs!=null){
                        while(rs.next()){
                            listName.add(rs.getString(1));
                        }
                    }
                    rs.close();
                }
                listComments.clear();
                for(int i = 0; i < listPId.size(); ++i){
                    ps = conn.prepareStatement("Select info from feedback where pid = ?");
                    ps.setInt(1,listPId.get(i));
                    rs = ps.executeQuery();
                    List<String> listSubComments = new ArrayList<String>();
                    if(rs != null){
                        while(rs.next()){
                            listSubComments.add(rs.getString(1));
                        }
                    }
                    listComments.add(listSubComments);
                }
                flag = offset;
                offset = listPId.size()/3;
                loading = true;
                //Log.e("Comments Fetched", listComments.toString());
                ps.close();
                conn.close();
                //Log.e("Success","Feeds retrieved successfully. && offset = " + offset);
            }
            catch(SQLException e) {
                Log.e("Error", "Error in getting feeds");
                e.printStackTrace();
            }
            return params[1];
        }

        public void onPostExecute(Integer result){
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            loadProgressBar.setVisibility(View.GONE);
            loadMore.setVisibility(View.GONE);
            if(result == 0) {
                adapter = new MyAdapterFeeds(listPId, listTitle, listName, listDesc, listRating, listImg, listImgThumb, listComments, getActivity());
                recyclerView.setAdapter(adapter);
            }
            else{
                adapter.notifyDataSetChanged();
            }
        }
    }
}
