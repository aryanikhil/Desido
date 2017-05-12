package com.example.aryanikhil2.desido.FragmentsMain;

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
    private List<String> listImg = new ArrayList<>();
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

    class DatabaseConnectivity extends AsyncTask<Integer, Void, Integer> {

        public Integer doInBackground(Integer... params){
            try {
                Class.forName("org.postgresql.Driver");
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try{
                //Connection con = DriverManager.getConnection("jdbc:postgresql://172.16.40.26:5432/student?currentSchema=desido","student","student");
                Connection con = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/desido","postgres","5438");
                ResultSet rs;
                PreparedStatement ps = con.prepareStatement("SELECT * FROM posts limit ? offset ?");
                ps.setInt(1, params[0]);
                ps.setInt(2, params[1]);
                rs = ps.executeQuery();
                if(rs!=null) {
                    while (rs.next()) {
                        listPId.add(rs.getInt("pid"));
                        listUId.add(rs.getInt("uid"));
                        listTitle.add(rs.getString("title"));
                        listDesc.add(rs.getString("info"));
                        listRating.add(rs.getInt("rating"));
                        listImg.add(rs.getString("pic"));
                        //Log.e("Fetched", listImg.toString());
                    }
                }
                for(int i = 0; i < listUId.size(); ++i) {
                    ps = con.prepareStatement("Select name from users where uid = ?");
                    ps.setInt(1,listUId.get(i));
                    rs = ps.executeQuery();
                    if(rs!=null){
                        while(rs.next()){
                            listName.add(rs.getString("name"));
                        }
                    }
                    rs.close();
                }
                listComments.clear();
                for(int i = 0; i < listPId.size(); ++i){
                    ps = con.prepareStatement("Select info from feedback where pid = ?");
                    ps.setInt(1,listPId.get(i));
                    rs = ps.executeQuery();
                    List<String> listSubComments = new ArrayList<String>();
                    if(rs != null){
                        while(rs.next()){
                            listSubComments.add(rs.getString("info"));
                        }
                    }
                    listComments.add(listSubComments);
                }
                flag = offset;
                offset = listPId.size()/3;
                loading = true;
                //Log.e("Comments Fetched", listComments.toString());
                ps.close();
                con.close();
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
                adapter = new MyAdapterFeeds(listPId, listTitle, listName, listDesc, listRating, listImg, listComments, getActivity());
                recyclerView.setAdapter(adapter);
            }
            else{
                adapter.notifyDataSetChanged();
            }
        }
    }
}
