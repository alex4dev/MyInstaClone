package com.example.myinstaclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FeedsActivity extends AppCompatActivity {
    private static final String TAG = "FeedsActivity";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton cameraButton;

    private ArrayList<FeedModel> feedsList = new ArrayList<FeedModel>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds);

        recyclerView = (RecyclerView) findViewById(R.id.feedsRecyclerView);
        cameraButton = findViewById(R.id.cameraButton);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new FeedViewAdapter(this, feedsList);
        recyclerView.setAdapter(mAdapter);
        ((FeedViewAdapter) mAdapter).setClickListener(new FeedViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                // showFullArticle(details.url);
            }
        });


        DatabaseManager.addListener(new DatabaseListener() {
            @Override
            public void onQueryUsersResult(List<UserModel> result) {

            }

            @Override
            public void onQueryFeedsResult(List<FeedModel> result) {
                if(result != null && result.size() > 0){
                    feedsList.clear();
                    feedsList.addAll(0, result);
                    Collections.sort(feedsList, new Comparator<FeedModel>() {
                        @Override
                        public int compare(FeedModel r1, FeedModel r2) {
                            return r2.getDate().compareTo(r1.getDate());
                        }
                    });
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        DatabaseManager.getInstance().subscribe();
        DatabaseManager.getInstance().getFeeds();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "camera button clicked");
                Intent intent = new Intent(ApplicationContext.get(), PhotoActivity.class);
                startActivity(intent);
            }
        });

    }






}
