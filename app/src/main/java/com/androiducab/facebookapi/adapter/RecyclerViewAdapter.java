package com.androiducab.facebookapi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androiducab.facebookapi.R;
import com.androiducab.facebookapi.model.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<Post> mItemList ;
    private Context context;

    public RecyclerViewAdapter(ArrayList<Post> mItemList, Context context) {
        this.mItemList = mItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup , false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        Log.e("onBindViewHolder: ","called");




        //load item attributes
        Post temp = mItemList.get(position);
        viewHolder.post = temp;

        //load image here
        if(temp.getPicture() != null){
            Picasso.with(context).load(temp.getPicture()).into(viewHolder.itemImage);
            viewHolder.itemImage.setVisibility(View.VISIBLE);
        }

        if(temp.getFrom() != null)
            viewHolder.itemFrom.setText(temp.getFrom());
        if(temp.getTime() != null)
            viewHolder.itemTime.setText(temp.getTime());
        if(temp.getCaption() != null)
            viewHolder.itemCaption.setText(temp.getCaption());
        if(temp.getDescription() != null)
            viewHolder.itemDescription.setText(temp.getDescription());
        if(temp.getType() != null)
            viewHolder.itemType.setText(temp.getType());

        viewHolder.itemLikes.setText(String.valueOf(temp.getLikes()));
        viewHolder.itemComments.setText(String.valueOf(temp.getComments()));
        viewHolder.itemShares.setText(String.valueOf(temp.getShares()));

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Post post;
        CircleImageView itemProfile;
        TextView itemFrom;
        TextView itemTime;
        TextView itemCaption;
        TextView itemDescription;
        TextView itemType;
        ImageView itemImage;
        TextView itemLikes;
        TextView itemComments;
        TextView itemShares;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemProfile = itemView.findViewById(R.id.item_profile_img);
            itemFrom = itemView.findViewById(R.id.item_from);
            itemTime = itemView.findViewById(R.id.item_time);
            itemCaption = itemView.findViewById(R.id.item_caption);
            itemDescription = itemView.findViewById(R.id.item_description);
            itemType = itemView.findViewById(R.id.item_type);
            itemImage = itemView.findViewById(R.id.item_image);
            itemLikes = itemView.findViewById(R.id.item_likes);
            itemComments = itemView.findViewById(R.id.item_comments);
            itemShares = itemView.findViewById(R.id.item_shares);


        }
    }

    public void deleteItem(int position){

        mItemList.remove(position);
        notifyItemRemoved(position);

        //String message = "Size: "+mItemList.size()+", Deleted an item.";
        //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();


    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItemList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItemList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

}
