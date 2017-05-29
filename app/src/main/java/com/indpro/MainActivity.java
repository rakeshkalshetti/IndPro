package com.indpro;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.indpro.android.ApplicationLoader;
import com.indpro.android.ImageDatabase;
import com.indpro.android.ImageListAdapter;
import com.indpro.android.SpacesItemDecoration;
import com.indpro.android.WrapStaggeredGridLayoutManager;
import com.indpro.model.ImageDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView homeImageListView;
    private ImageListAdapter imageListAdapter;

    private List<ImageDetails> fromDatabaseImageList;

    private List<ImageDetails> imageDetailsList;

    private View mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        ApplicationLoader.lockOrientation (this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences (this);
        boolean isDatabaseCopied = sharedPreferences.getBoolean ("isDatabaseCopied", false);
        if (!isDatabaseCopied) {
            try {
                ImageDatabase.CopyDataBaseFromAsset ();
                sharedPreferences.edit ().putBoolean ("isDatabaseCopied", true).apply ();
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }

        loadFromDatabase ();

        mainLayout = findViewById(R.id.mainLayout);
        homeImageListView = (RecyclerView) findViewById (R.id.homeImageList);
        imageDetailsList = new ArrayList<> (fromDatabaseImageList);

        WrapStaggeredGridLayoutManager lm =
                new WrapStaggeredGridLayoutManager (2, StaggeredGridLayoutManager.VERTICAL);


        homeImageListView.setHasFixedSize (false);
        homeImageListView.setLayoutManager (lm);

        imageListAdapter = new ImageListAdapter (imageDetailsList, homeImageListView);
        imageListAdapter.setOnLoadMoreListener (new ImageListAdapter.OnLoadMoreListener () {
            @Override
            public void onLoadMore() {
                imageDetailsList.add (null);
                homeImageListView.post (new Runnable () {
                    @Override
                    public void run() {
                        imageListAdapter.notifyItemInserted (imageDetailsList.size () - 1);
                    }
                });

                Handler handler = new Handler ();
                handler.postDelayed (new Runnable () {
                    @Override
                    public void run() {
                        imageDetailsList.remove (imageDetailsList.size () - 1);
                        imageListAdapter.notifyItemRemoved (imageDetailsList.size ());

                        imageDetailsList.addAll (fromDatabaseImageList);
                        imageListAdapter.notifyItemInserted (imageDetailsList.size ());
                        imageListAdapter.setLoaded ();
                    }
                }, 200);
            }
        });

        int spacing = (int) TypedValue.applyDimension (TypedValue.COMPLEX_UNIT_DIP, 1, getResources ().getDisplayMetrics ());
        ;
        boolean includeEdge = true;
        homeImageListView.addItemDecoration (new SpacesItemDecoration (2, spacing, includeEdge));

        homeImageListView.setItemAnimator (new DefaultItemAnimator ());

        homeImageListView.setAdapter (imageListAdapter);
    }

    private void loadFromDatabase() {
        fromDatabaseImageList = ImageDatabase.getInstance ().loadImageList ();
    }

    @Override
    protected void onResume() {
        super.onResume ();
        if (imageDetailsList != null) {
            loadFromDatabase ();
            imageDetailsList.clear ();
            imageDetailsList.addAll (fromDatabaseImageList);
            if (imageListAdapter != null) {
                imageListAdapter.notifyDataSetChanged ();
            }

            if(!isNetworkAvailable()) {
                Snackbar snackbar = Snackbar
                        .make(mainLayout, "No internet connection!", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });

                snackbar.setActionTextColor(Color.RED);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
