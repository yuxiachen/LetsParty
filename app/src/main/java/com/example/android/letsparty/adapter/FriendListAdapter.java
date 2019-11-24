package com.example.android.letsparty.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.letsparty.R;
import com.example.android.letsparty.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendItemViewHolder> implements View.OnClickListener{
    private List<User> mFriends;
    private List<String> mFriendsKeys;
    private onFriendItemClickedListener mListener;
    private ArrayList<User> tempFriendList = new ArrayList<>();
    private ArrayList<String> tempFriendKeys = new ArrayList<>();

    public FriendListAdapter(List<User> friends, List<String> friendKeys, onFriendItemClickedListener listener) {
        this.mFriends = friends;
        mFriendsKeys = friendKeys;
        this.mListener = listener;
        tempFriendList.addAll(friends);
        tempFriendKeys.addAll(friendKeys);
    }

    @NonNull
    @Override
    public FriendItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        FriendItemViewHolder holder = new FriendItemViewHolder(view);
        holder.itemView.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendItemViewHolder holder, int position) {
        User currFriend = mFriends.get(position);
        holder.updateUi(currFriend);
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    @Override
    public void onClick(View v) {
        int pos = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);
        mListener.onFriendItemClicked(mFriendsKeys.get(pos));
    }

     static class FriendItemViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView friendProfileImg;

        FriendItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_friend_name);
            friendProfileImg = itemView.findViewById(R.id.iv_friend_img);
        }

         void updateUi(User friend) {
            name.setText(friend.getUserName());
            if (friend.getProfileImageUrl() != null) {
                Picasso.get().load(friend.getProfileImageUrl())
                        .fit()
                        .into(friendProfileImg);
            }
        }
    }

    public void filter(String query) {
            mFriends.clear();
            mFriendsKeys.clear();
            for (int i = 0; i < tempFriendList.size(); i++) {
                if (tempFriendList.get(i).getUserName().toLowerCase().contains(query)) {
                    mFriends.add(tempFriendList.get(i));
                    mFriendsKeys.add(tempFriendKeys.get(i));
                }
            }
            notifyDataSetChanged();
        }

    public interface onFriendItemClickedListener {
        void onFriendItemClicked(String userKey);
    }

}
