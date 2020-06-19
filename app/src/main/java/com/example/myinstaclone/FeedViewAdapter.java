package com.example.myinstaclone;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class FeedViewAdapter extends RecyclerView.Adapter<FeedViewAdapter.ViewHolder> {
    private static final String TAG = "FeedViewAdapter";
    private List<FeedModel> mDataset;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private UserModel user;

    // data is passed into the constructor
    public FeedViewAdapter(Context context, List<FeedModel> dataset ) {
        this.mInflater = LayoutInflater.from(context);
        this.mDataset = dataset;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder");

        final FeedModel data = mDataset.get(position);
        if (data == null) {
            return;
        }

        holder.mItem = data;
        DatabaseManager.addListener(new DatabaseListener() {
            @Override
            public void onQueryUsersResult(List<UserModel> result) {

                String currentUserId = data.getAuthor();
                String newUserId = result.get(0).getId();
                Log.d(TAG, "onQueryUsersResult " + newUserId + " " + currentUserId);
                if (newUserId.equals(currentUserId)) {
                    user = result.get(0);
                    Log.d(TAG, "onQueryUsersResult - user object received: " + data.getAuthor());
                    URL url = null;
                    try {
                         url = new URL(user.getAvatarName());
                    } catch (MalformedURLException e) {
                        // e.printStackTrace();
                    }
                    if(url != null && (url.getProtocol() == "http" || url.getProtocol() == "https")){
                        loadImageFromUrl(user.getAvatarName(), holder.avatarImage);
                    }else{
                        loadImageFromStorage(user.getAvatarName(), holder.avatarImage);
                    }
                    holder.pseudoText.setText(user.getFirst() + " " + user.getLast());
                    DatabaseManager.removeListener(this);

                }

            }

            @Override
            public void onQueryFeedsResult(List<FeedModel> result) {

            }
        });
        DatabaseManager.getInstance().getUserById(data.getAuthor());
        holder.locationText.setText(data.getLocation());
        loadImageFromStorage(data.getImageName(), holder.feedImage);
        holder.descriptionText.setText(data.getDescription());
    }


    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        Log.i(TAG, "onViewRecycled");
        holder.cleanup();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.i(TAG, "onAttached");
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Log.i(TAG, "onDetached");
    }

    private void loadImageFromStorage(String name, ImageView imageView) {
        Log.d(TAG, "loadImage - name:" + name);
        if(name == null || name == "") return;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageReference.child(name);
        try{
            GlideApp.with(ApplicationContext.get())
                    .load(imageRef)
                    .into(imageView);
        } catch (Exception e){

        }
    }

    private void loadImageFromUrl(String url, ImageView imageView){
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);
        try{
            Glide.with(ApplicationContext.get()).load(url).apply(options).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView avatarImage;
        TextView pseudoText;
        TextView locationText;
        ImageView feedImage;
        TextView descriptionText;

        public FeedModel mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarImage = itemView.findViewById(R.id.avatarImage);
            pseudoText = itemView.findViewById(R.id.pseudoText);
            locationText = itemView.findViewById(R.id.locationText);
            feedImage = itemView.findViewById(R.id.feedImage);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }

        public void cleanup() {

        }
    }

    // convenience method for getting data at click position
    FeedModel getItem(int id) {
        return mDataset.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }



}
